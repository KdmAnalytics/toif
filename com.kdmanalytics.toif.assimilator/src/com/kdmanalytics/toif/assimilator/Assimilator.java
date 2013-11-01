/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.xml.sax.SAXException;

import com.kdmanalytics.kdm.repositoryMerger.RepositoryMerger;
import com.kdmanalytics.kdm.repositoryMerger.linkconfig.LinkConfig;
import com.kdmanalytics.kdm.repositoryMerger.linkconfig.MergeConfig;
import com.kdmanalytics.toif.assimilator.FilePathTrie.Node;
import com.kdmanalytics.toif.assimilator.exceptions.AssimilatorArgumentException;
import com.kdmanalytics.toif.mergers.ToifMerger;

/**
 * This class takes a number of tkdm and toif files and integrates them into one
 * repository. Each domain is merged with its self first, then integrated with
 * each other.
 * 
 * @author adam
 * 
 */
public class Assimilator
{
    
    public static void debug(Logger logger, Object message)
    {
        logger.debug(message);
    }
    
    static public void deleteDirectory(File path)
    {
        if (path.exists())
        {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    deleteDirectory(files[i]);
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return;
    }
    
    /**
     * Entry point for the assimilator. Takes an array of strings. These are the
     * repository options -r <repository location>, and the unparsed options
     * (files) that need to be assimilated.
     * 
     * @param args
     *            the repository option and location, and the files to be
     *            assimilated.
     */
    public static void main(String[] args)
    {
        Assimilator assimilator = new Assimilator();
        // assimilate the files.
        assimilator.assimilate(args);
        
    }
    
    /**
     * do we continue if exceptions are found.
     */
    private final boolean force = true;
    
    /**
     * KDM name space
     */
    private static final String KDM_NS_HOST = "http://org.omg.kdm/";
    
    /**
     * the logger.
     */
    private static Logger LOG = Logger.getLogger(Assimilator.class);
    
    /**
     * the model namespace.
     */
    private static final String MODEL_NS = "http://kdmanalytics.com/";
    
    /**
     * tkdm extension
     */
    private static final String TKDM_EXTENSION = ".tkdm";
    
    /**
     * kdmo file extension.
     */
    private static final String KDMO_EXTENSION = ".kdmo";
    
    /**
     * The kdm extension for the kdm xml file.
     */
    private static final String KDM_EXTENSION = ".kdm";
    
    /**
     * toif extension.
     */
    private static final String TOIF_EXTENSION = ".toif.xml";
    
    private static final boolean debug = false;
    
    /**
     * the main repository for this assimilation. This repository contains both
     * the tkdm and the toif data together.
     */
    private Repository repository;
    
    private RepositoryConnection con;
    
    private ValueFactory factory;
    
    /**
     * A map of all the known sourcefiles for this project.
     */
    private final Map<String, Resource> sourceFiles;
    
    private File outputLocation = null;
    
    private Long offset;
    
    private Long nextId = 0L;
    
    private Long smallestBigNumber = Long.MAX_VALUE;
    
    private Set<Statement> toifPaths = new HashSet<Statement>();
    
    private List<Statement> lineNumbers = new ArrayList<Statement>();
    
    private List<Statement> kdmElements = new ArrayList<Statement>();
    
    private Set<Statement> kdmContains = new HashSet<Statement>();
    
    /**
     * the repository option. -r, -m, or -k. [R]epository output, repository
     * [M]igration, and [K]DM ouput respectively.
     */
    private String rOption;
    
    /**
     * constructor for the assimilator.
     */
    public Assimilator()
    {
        sourceFiles = new HashMap<String, Resource>();
    }
    
    /**
     * 
     * @param statement
     * @throws RepositoryException
     */
    public void add(Statement statement) throws RepositoryException
    {
        if (outputLocation.isFile())
        {
            addToFile(statement);
        }
        else if (repository != null)
        {
            addToRepository(statement);
        }
        
    }
    
    /**
     * add the statement to the specified kdm file. this is instead of adding to
     * the repository.
     * 
     * @param statement
     *            the statement to add to the kdm file.
     */
    private void addToFile(Statement statement)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation.getAbsolutePath(), true));
            writer.write("<" + statement.getSubject() + "> ");
            writer.write("<" + statement.getPredicate() + "> ");
            String object = statement.getObject().toString();
            if (!object.startsWith("\""))
            {
                object = "<" + object + ">";
            }
            writer.write(object + " ");
            writer.write(" .\n");
            writer.close();
        }
        catch (IOException e)
        {
            System.err.println("There was and IO Exception while writing to the file. " + e);
        }
        
    }
    
    /**
     * Ultimately add the statement to the repository. However, if the statement
     * is a codeLocation then try to bind it to the source-ref in the kdm model.
     * The binding will look like this:
     * 
     * [KDM SourceRef] <---> [Common Node] <---> [TOIF CodeLocation]
     * 
     * First we need to find the correct source file. We do this by: -Get all
     * the sourcefiles in the kdm model -get their path statements -compare
     * paths using Trie. -find best fit.
     * 
     * We then find all sourceRefs with this sourcefile ID.
     * 
     * Then, create the binding between this codeLocation and the sourceRef
     * using the common node.
     * 
     * @param statement
     * @throws RepositoryException
     */
    private void addToRepository(Statement statement) throws RepositoryException
    {
        if (con == null)
        {
            con = repository.getConnection();
        }
        
        if (debug)
        {
            System.err.println("repository: " + con.getRepository());
        }
        
        con.add(statement, (Resource) null);
    }
    
    /**
     * Assimilate the files into one repository.
     * 
     * @param args
     *            the arguments passed to main. These include the repository
     *            location and tkdm/toif file locations.
     * 
     */
    void assimilate(String[] args)
    {
        System.out.println("Running, this may take some time...");
        try
        {
            outputLocation = getOutputLocation(args);
            
            // get the tkdm files and the toif files.
            final List<File> kdmFiles = getFiles(args, KDM_EXTENSION);
            final List<File> tkdmFiles = getFiles(args, TKDM_EXTENSION, KDMO_EXTENSION);
            final List<File> toifFiles = getFiles(args, TOIF_EXTENSION);
            
            repository = createRepository(outputLocation);
            
            if (debug)
            {
                System.err.println("repository is: " + repository);
            }
            
            // if the migrate option has not been set. (future functionality)
            if (!"-m".equals(rOption))
            {
                // get the kdm file this writes directly to the repository.
                processKdmFile(kdmFiles);
                
                /*
                 * run the tkdm merger and pipe its output to the
                 * writeStatementLine() method.
                 */
                final RepositoryMerger kdmMerger = processTkdmFiles(tkdmFiles);
                setNextId(kdmMerger.getId());
                
            }
            else
            {
                
            }
            
            String blacklistPath = getBlackListPath(args);
            
            /*
             * run the toif merger and pipe its output to the
             * writeStatementLine() method.
             */
            processToifFiles(toifFiles, nextId, smallestBigNumber, blacklistPath);
            
            compareLocations();
            
        }
        catch (final AssimilatorArgumentException e)
        {
            LOG.error("Bad argument " + e);
            e.printStackTrace();
        }
        catch (RepositoryException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
                repository.shutDown();
            }
            catch (RepositoryException e)
            {
                System.err.println("There was a problem closing the connection to the repository. " + e);
            }
            
            System.out.println("\nComplete.");
        }
    }
    
    /**
     * Get the blacklist string from the args. The string is the name of the top
     * of the project.
     * 
     * @param args
     *            arguments from the command line
     * @return the directory name of the root of the project
     */
    private String getBlackListPath(String[] args)
    {
        int indexOfblacklistPath = 0;
        
        for (int i = 0; i < args.length; i++)
        {
            if ("-p".equals(args[i]))
            {
                indexOfblacklistPath = i;
                
                String blacklist = args[indexOfblacklistPath + 1];
                blacklist = blacklist.trim();
                
                return blacklist;
            }
        }
        
        return "";
    }
    
    /**
     * try to find matching locations in the toif and the kdm namespaces.
     * 
     * @return
     * 
     * @throws RepositoryException
     */
    private boolean compareLocations() throws RepositoryException
    {
        HashMap<String, Resource> bestFitMap = new HashMap<String, Resource>();
        
        int statements = 0;
        Map<Resource, FilePathTrie> tries = new HashMap<Resource, FilePathTrie>();
        // for all the toif statements that are a path
        for (Statement toifStatement : toifPaths)
        {
            Resource bestFitResource = bestFitMap.get(toifStatement.getSubject().stringValue());
            if (bestFitResource != null)
            {
                createCommonNode(toifStatement.getSubject(), bestFitResource);
                continue;
            }
            
            statements++;
            
            float fraction = 100f / toifPaths.size();
            int percent = (int) Math.ceil(fraction * statements);
            
            System.out.print("\rAssimilating TOIF and KDM Locations... " + percent + "%");
            
            // given a toif statement
            Resource kdmResourceFile = findBestFitSourceFile(tries, toifStatement);
            
            if (kdmResourceFile == null)
            {
                continue;
            }
            
            int reductionAmount = 0;
            
            while (bestFitResource == null)
            {
                // we tried, dont want an endless loop.
                if (reductionAmount == 200)
                {
                    break;
                }
                
                bestFitResource = findBestKdmElementForToifStatement(toifStatement, kdmResourceFile, reductionAmount);
                reductionAmount++;
            }
            
            if (bestFitResource == null)
            {
                bestFitResource = kdmResourceFile;
            }
            
            Resource toifSubject = toifStatement.getSubject();
            createCommonNode(toifSubject, bestFitResource);
            
        }
        
        return false;
        
    }
    
    /**
     * create the common node which is a blank node. this is doubly linked to
     * both the kdm blockunit and the toif code location.
     * 
     * @param sourceFile
     * @param blockUnit
     * @throws RepositoryException
     */
    private void createCommonNode(Resource toifSubject, Resource blockUnit) throws RepositoryException
    {
        
        URI bnodeURI = factory.createURI("http://toif/", (offset++).toString());
        Literal typeLiteral = factory.createLiteral("CommonNode");
        
        // create the blank common node.
        add(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "Type"), typeLiteral));
        
        // link the toif to it
        add(new StatementImpl(toifSubject, factory.createURI(KDM_NS_HOST + "CommonView"), bnodeURI));
        // link the toif from it.
        add(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "ToifView"), toifSubject));
        
        // link the kdm to it
        add(new StatementImpl(blockUnit, factory.createURI(KDM_NS_HOST + "CommonView"), bnodeURI));
        // link the kdm from it.
        add(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "KdmView"), blockUnit));
        
    }
    
    /**
     * create the disk repository which the files will be merged into.
     * 
     * @param repositoryLocation
     * 
     * @return the repository in its initialized state.
     */
    Repository createRepository(File repositoryLocation)
    {
        Repository repository = null;
        if (repositoryLocation.isDirectory())
        {
            // create the disk repository.
            repository = new SailRepository(new NativeStore(repositoryLocation));
        }
        else
        {
            repository = new SailRepository(new MemoryStore());
        }
        
        try
        {
            // initialize the repository.
            repository.initialize();
            Assimilator.debug(LOG, "Repository initialized in " + repositoryLocation.getAbsolutePath());
            
            con = repository.getConnection();
            // con.clear((Resource)null);
            factory = repository.getValueFactory();
        }
        catch (final RepositoryException e)
        {
            LOG.error("Repository could not be initialized. " + e);
            e.printStackTrace();
        }
        
        // return the repository.
        return repository;
    }
    
    /**
     * get the value of the object.
     * 
     * @param elements
     *            the subject predicate and object
     * @return the value of the object.
     */
    private Value determineObjectValue(String[] elements)
    {
        Value object;
        if (elements[2].startsWith("<"))
        {
            object = factory.createURI(trimForStatement(elements[2]));
        }
        else
        {
            object = factory.createLiteral(trimForStatement(elements[2]));
        }
        return object;
    }
    
    /**
     * Creates a trie (note: trie not tree) of all the known source files. The
     * toif path is matched against the tries. the tries return the longest path
     * that they can for each kdm resource, the resource with the longest path
     * is the resource which is returned.
     * 
     * @param tries
     *            the map of tries for all the kdm resources. the tries each
     *            start with a file as the root and parent directories extending
     *            from that.
     * @param st
     *            the toif statement. this is the statement which contains the
     *            path.
     * @return returns the kdm resource with the best fit.
     */
    private Resource findBestFitSourceFile(Map<Resource, FilePathTrie> tries, final Statement st)
    {
        Resource bestFit = null;
        int bestFitCount = 0;
        
        // get the OBJECT string value
        String toifPath = st.getObject().stringValue();
        
        // get rid of quotes and make a list.
        toifPath = toifPath.replace("\"", "");
        List<String> toifPathArray = Arrays.asList(toifPath.replace("\\", "/").split("/"));
        
        // we need the path array starting with the file.
        Collections.reverse(toifPathArray);
        
        // get the toif file name. which is the first element now.
        String toifFileName = toifPathArray.get(0);
        toifPathArray.set(0, getSourceFile(toifFileName));
        
        // work with all the kdm entries in the sourcefiles, reverse their
        // contents and create a mapping of the file as key and the Trie as
        // the value. Basically making the trie for each source file.
        for (String kdmPath : sourceFiles.keySet())
        {
            
            // reverse the kdm path so that it starts with the file.
            List<String> kdmPathArray = Arrays.asList(kdmPath.replace("\\", "/").split("/"));
            Collections.reverse(kdmPathArray);
            
            // there should be something in the path!!!
            if (kdmPathArray.isEmpty())
            {
                continue;
            }
            
            // the filename.
            String kdmFileName = kdmPathArray.get(0);
            Node currentKdmNode = null;
            
            // get the kdm resource based on its path.
            Resource kdmResource = sourceFiles.get(kdmPath);
            
            // if the filename doesn't exist, create a new trie for it.
            if (!tries.containsKey(kdmResource))
            {
                FilePathTrie filePathTrie = new FilePathTrie(kdmFileName);
                tries.put(kdmResource, filePathTrie);
            }
            
            // set the node as the sourcefile we are working with.
            currentKdmNode = tries.get(kdmResource).getFile();
            
            // for all the directories in this sourcefile's path, add them to
            // the trie.
            for (int i = 1; i < kdmPathArray.size(); i++)
            {
                // get the correct trie based on the resource.
                FilePathTrie trie = tries.get(kdmResource);
                // add the parent to the current kdm node. currentKdm Node gets
                // replaced with the parent once this is done.
                currentKdmNode = trie.addParentDirectory(currentKdmNode, kdmPathArray.get(i));
            }
        }
        
        for (Resource file : tries.keySet())
        {
            FilePathTrie trie = tries.get(file);
            // get the longest kdm path that we can find from the tries.
            List<String> path = trie.getBestPath(toifPathArray);
            
            // if this path is longer than the previous longest then set the
            // current resource (file) as the best fit.
            if (path.size() >= bestFitCount)
            {
                bestFit = file;
                bestFitCount = path.size();
            }
        }
        
        // return the best fit.
        return bestFit;
        
    }
    
    /**
     * Findind the best fitting source reference.
     * 
     * @param st
     * @param bestFit
     * @return the kdm element that best fits the toif resource
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private Resource findBestKdmElementForToifStatement(Statement st, Resource bestFit, int reductionAmount)
    {
        boolean searchForParent = false;
        
        if (st == null || bestFit == null)
        {
            return null;
        }
        
        if (reductionAmount > 100)
        {
            reductionAmount = (reductionAmount - 100) * -1;
            searchForParent = true;
        }
        
        String lineNumber = "";
        
        // figure out the reference for the source ref. Extract the id.
        String sourceFileRef = bestFit.stringValue().replace("http://kdmanalytics.com/", "");
        
        for (Statement lineNumberStatement : lineNumbers)
        {
            if (!st.getSubject().stringValue().equals(lineNumberStatement.getSubject().stringValue()))
            {
                continue;
            }
            
            lineNumber = lineNumberStatement.getObject().stringValue();
            
            int tempLineNumber = Integer.parseInt(lineNumber) - reductionAmount;
            
            if (tempLineNumber <= 0)
            {
                continue;
            }
            
            lineNumber = tempLineNumber + "";
            
            // make the literal for the sourceRef.
            String sourceRef = sourceFileRef + ";" + lineNumber;
            
            for (Statement kdmElement : kdmElements)
            {
                
                String object = kdmElement.getObject().stringValue();
                
                if (object.equals(sourceRef))
                {
                    if (searchForParent)
                    {
                        return getParent(kdmElement.getSubject().stringValue());
                    }
                    
                    return kdmElement.getSubject();
                }
                else if (object.equals(sourceRef + ";"))
                {
                    
                    if (searchForParent)
                    {
                        return getParent(kdmElement.getSubject().stringValue());
                    }
                    
                    return kdmElement.getSubject();
                }
                
            }
            
        }
        
        return null;
    }
    
    /**
     * get the parent of the kdm element.
     * 
     * @param kdm
     *            element
     * @return the parent of the kdm element.
     */
    private Resource getParent(String kdm)
    {
        for (Statement kdmElement : kdmContains)
        {
            if (kdmElement.getObject().stringValue().equals(kdm))
            {
                return (Resource) factory.createURI(kdmElement.getSubject().stringValue());
            }
        }
        return null;
    }
    
    /**
     * get all the files from the argument list.
     * 
     * @param args
     *            the arguments handed to main.
     * @return the list of files in the argument array with the given extension.
     */
    List<File> getFiles(String[] args, final String... extensions)
    {
        checkNotNull(args);
        checkNotNull(extensions);
        
        // set up a file filter.
        IOFileFilter fileFilter = new IOFileFilter() {
            
            @Override
            public boolean accept(File arg0)
            {
                for (String extension : extensions)
                {
                    if (arg0.getName().endsWith(extension))
                    {
                        return true;
                    }
                }
                
                return false;
            }
            
            @Override
            public boolean accept(File arg0, String arg1)
            {
                for (String extension : extensions)
                {
                    if (arg1.endsWith(extension))
                    {
                        return true;
                    }
                }
                
                return false;
            }
        };
        
        // the files of the specific extension
        final List<File> files = new ArrayList<File>();
        
        int startIndex = 2;
        for (String string : args)
        {
            if ("-p".equals(string))
            {
                startIndex = 4;
            }
        }
        
        // starting at the unparsed options, ie the files in the arguments.
        for (int i = startIndex; i < args.length; i++)
        {
            final String path = args[i];
            
            final File file = new File(path);
            
            // if the file doesnt exist, bail.
            if (!file.exists())
            {
                LOG.error("File does not exist.");
                System.err.println("File does not exit");
                System.exit(1);
            }
            
            if (file.isDirectory())
            {
                files.addAll(FileUtils.listFiles(file, fileFilter, TrueFileFilter.INSTANCE));
            }
            else
            {
                
                /*
                 * only add the file to the results list if the file has the
                 * extension and is a file.
                 */
                for (String extension : extensions)
                {
                    if (file.getAbsolutePath().endsWith(extension))
                    {
                        files.add(file);
                        Assimilator.debug(LOG, file.getName() + " added to the " + extension + " list");
                    }
                }
            }
            
        }
        
        if (debug)
        {
            for (File file : files)
            {
                System.err.println(file.getName());
            }
        }
        // return the list of files.
        return files;
    }
    
    /**
     * return the file that represents this repository location
     * 
     * @param args
     *            the command line arguments
     * @return the output location.
     */
    File getOutputLocation(String[] args) throws AssimilatorArgumentException
    {
        checkNotNull(args);
        
        // the location of the repository, this will be a directory.
        File location = null;
        
        if (args.length < 3)
        {
            throw new AssimilatorArgumentException("There are not enough arguments.");
        }
        
        int indexOfROption = 0;
        
        for (int i = 0; i < args.length; i++)
        {
            if ("-r".equals(args[i]) || "-k".equals(args[i]))
            {
                indexOfROption = i;
            }
        }
        
        rOption = args[indexOfROption];
        
        // the repository location, following the '-r' option
        final String locationParameter = args[indexOfROption + 1];
        
        if ((!rOption.equals("-r")) && (!rOption.equals("-k")))
        {
            throw new AssimilatorArgumentException("There should be an argument which should be the '-r' or the '-k' option.");
        }
        
        // if the option is merge but location is null.
        if ((locationParameter == null) && (!rOption.equals("-m")))
        {
            throw new AssimilatorArgumentException("The second argument which should be the workbench repository is null.");
        }
        
        // else if just the location is null.
        else if (locationParameter == null)
        {
            throw new AssimilatorArgumentException("The argument after the repository option, which should be the output location, is null.");
        }
        
        // check to make sure that there is an -r option
        if ("-r".equals(rOption))
        {
            // make sure the location of the repository is valid.
            location = getValidRepositoryLocation(locationParameter);
        }
        else if ("-k".equals(rOption))
        {
            location = getValidFileLocation(locationParameter);
        }
        else if ("-m".equals(rOption))
        {
            location = getValidRepositoryLocation(locationParameter);
        }
        
        // return the location.
        return location;
    }
    
    /**
     * The names need to be corrected to be able to find the source file.
     * 
     * @param name
     * @return
     */
    private String getSourceFile(String name)
    {
        String newName = name.replaceAll("$.*class", ".java");
        newName = name.replace(".class", ".java");
        return newName;
    }
    
    /**
     * create the kdm repository merger
     * 
     * @param out
     *            the print writer for the output.
     * @return the repository merger
     */
    RepositoryMerger getTkdmMerger(PrintWriter out)
    {
        final InputStream is = Assimilator.class.getResourceAsStream("config/cxx.cfg");
        
        final LinkConfig config = new LinkConfig(is);
        
        final MergeConfig mergeConfig = config.getMergeConfig();
        
        final RepositoryMerger merger = new RepositoryMerger(mergeConfig, out, RepositoryMerger.NTRIPLES, "Assembly");
        
        return merger;
    }
    
    /**
     * get the merger to merge the toif data.
     * 
     * @param printWriter
     *            the output print writer
     * @param startID
     *            the id to start from after the kdm
     * @param smallestBigNumber2
     *            the smallest number when starting from the other end of the
     *            long scale. (for the bnodes)
     * 
     * @return the toif merger instance.
     */
    ToifMerger getToifMerger(PrintWriter printWriter, Long startID, Long smallestBigNumber2, String blacklistPath)
    {
        return new ToifMerger(printWriter, force, startID, smallestBigNumber2, blacklistPath);
    }
    
    /**
     * get a valid location for this kdm file. the method checks to see if the
     * location can be used as a kdm file and then creates the file.
     * 
     * @param location
     *            path of the output file.
     * @return the output file
     * @throws AssimilatorArgumentException
     * @throws IOException
     */
    private File getValidFileLocation(String location) throws AssimilatorArgumentException
    {
        if (location == null)
        {
            throw new AssimilatorArgumentException("Kdm file location is null");
        }
        
        final File file = new File(location);
        // make the directory.
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new AssimilatorArgumentException("Kdm file could not be created");
                
            }
            Assimilator.debug(LOG, "Kdm File has been created.");
        }
        
        // if the file is a directory then it is good
        if (file.isFile())
        {
            return file;
        }
        else if (file.getName().toLowerCase().endsWith(".kdm"))
        {
            throw new AssimilatorArgumentException("This location is not a valid '.kdm' file");
        }
        else
        {
            /*
             * if the file is not a directory then it cant be used as a
             * repository location.
             */
            throw new AssimilatorArgumentException("This location is not a valid file");
            
        }
    }
    
    /**
     * check to make sure that this is a valid repository location.
     * 
     * @param location
     *            the location of the output repository.
     * 
     * @return the output file.
     * @throws AssimilatorArgumentException
     */
    File getValidRepositoryLocation(String location) throws AssimilatorArgumentException
    {
        if (location == null)
        {
            throw new AssimilatorArgumentException("Repository location is null");
        }
        
        final File file = new File(location);
        // make the direcotry
        if (file.mkdirs())
        {
            Assimilator.debug(LOG, "repository directory has been created.");
        }
        else
        {
            Assimilator.debug(LOG, "repository directory has already been created and will be used.");
        }
        
        // if the file is a directory then it is good
        if (file.isDirectory())
        {
            return file;
        }
        else
        {
            /*
             * if the file is not a directory then it cant be used as a
             * repository location.
             */
            throw new AssimilatorArgumentException("This location is not a valid directory");
            
        }
        
    }
    
    /**
     * Load the kdm file. parse the input file.
     * 
     * @param is
     *            the input file
     * @param out
     *            output stream
     * @return the xml handler for the kdm data.
     * @throws IOException
     * @throws RepositoryException
     */
    public KdmXmlHandler load(File is, PipedOutputStream out) throws IOException, RepositoryException
    {
        RepositoryConnection tempCon = null;
        KdmXmlHandler kdmXmlHandler = null;
        try
        {
            
            File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "KDMRepository");
            
            deleteDirectory(tempFile);
            
            tempFile.deleteOnExit();
            Repository tempRepository = new SailRepository(new NativeStore(tempFile));
            tempRepository.initialize();
            
            tempCon = tempRepository.getConnection();
            /**
             * The factory used to drive the parsing process.
             * 
             */
            SAXParserFactory factory = null;
            // Use the default (non-validating) parser
            factory = SAXParserFactory.newInstance();
            
            tempCon.setAutoCommit(false); // Control commit for speed
            
            kdmXmlHandler = new KdmXmlHandler(tempRepository);
            
            // Parse the input
            try
            {
                SAXParser saxParser = factory.newSAXParser();
                
                saxParser.parse(is, kdmXmlHandler);
            }
            catch (ParserConfigurationException ex)
            {
                throw new RepositoryException(ex);
            }
            catch (SAXException ex)
            {
                LOG.error(ex.getLocalizedMessage(), ex);
                throw new RepositoryException(ex);
            }
            
            // Commit postLoad data
            kdmXmlHandler.postLoad();
            
            tempCon.commit();
            
            tempCon.export(new NTriplesWriter(out), (Resource) null);
            
            tempCon.clear();
        }
        catch (RDFHandlerException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != tempCon)
            {
                tempCon.close();
            }
            
        }
        return kdmXmlHandler;
        
    }
    
    /**
     * load the rdf file into repository.
     * 
     * @param repository
     *            the repository
     * @param is
     *            the input stream from file
     * @throws IOException
     * @throws RepositoryException
     */
    void load(Repository repository, InputStream is) throws IOException, RepositoryException
    {
        int count = 0;
        final ValueFactory factory = repository.getValueFactory();
        final RepositoryConnection con = repository.getConnection();
        con.setAutoCommit(false);
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = in.readLine();
        URI lastSubject = null;
        
        while (line != null)
        {
            count++;
            
            if (line.length() <= 0)
            {
                line = in.readLine();
                continue;
            }
            
            if (line.charAt(0) == '#')
            {
                line = in.readLine();
                continue;
            }
            
            if (count == 1)
            {
                if (line.startsWith("KDM_Triple"))
                {
                    line = in.readLine();
                    continue;
                }
            }
            
            URI subject = null;
            URI predicate = null;
            Value object = null;
            
            try
            {
                final char bytes[] = line.toCharArray();
                
                // Skip the initial <
                int start = 1;
                int i = 1;
                
                // If the line starts with a space (indent) then reuse the last
                // subject,
                // otherwise parse the subject.
                if (bytes[0] == ' ')
                {
                    subject = lastSubject;
                }
                else
                {
                    // Parse the subject
                    while ((i < bytes.length) && (bytes[i] != '>'))
                    {
                        ++i;
                    }
                    if (i >= bytes.length)
                    {
                        LOG.error("Invalid subject URI on ntriples line " + count);
                        line = in.readLine();
                        continue;
                    }
                    
                    subject = factory.createURI(MODEL_NS + new String(line.substring(start, i)));
                    
                    // Buffer the subject in case we need it next time
                    lastSubject = subject;
                    ++i; // Skip the >
                }
                
                while ((i < bytes.length) && (bytes[i] != '<'))
                {
                    ++i;
                }
                if (i >= bytes.length)
                {
                    LOG.error("Invalid subject URI on ntriples line " + count);
                    line = in.readLine();
                    continue;
                }
                ++i; // Skip the >
                start = i;
                
                // Parse the predicate
                while ((i < bytes.length) && (bytes[i] != '>'))
                {
                    ++i;
                }
                if (i >= bytes.length)
                {
                    LOG.error("Invalid predicate URI on ntriples line " + count);
                    line = in.readLine();
                    continue;
                }
                
                predicate = factory.createURI(KDM_NS_HOST + new String(line.substring(start, i)));
                
                ++i; // Skip the >
                while ((i < bytes.length) && (bytes[i] != '<') && (bytes[i] != '\"'))
                {
                    ++i;
                }
                if (i >= bytes.length)
                {
                    LOG.error("Invalid predicate URI on ntriples line " + count);
                    line = in.readLine();
                    continue;
                }
                
                // Parse a URI
                if (bytes[i] == '<')
                {
                    ++i; // Skip the >
                    start = i;
                    while ((i < bytes.length) && (bytes[i] != '>'))
                    {
                        ++i;
                    }
                    if (i >= bytes.length)
                    {
                        LOG.error("Invalid object URI on ntriples line " + count);
                        line = in.readLine();
                        continue;
                    }
                    
                    object = factory.createURI(MODEL_NS + new String(line.substring(start, i)));
                    
                }
                // Parse a literal
                else
                {
                    final int lastIndex = line.lastIndexOf('"');
                    if ((lastIndex < 0) || (lastIndex <= i))
                    {
                        LOG.error("Invalid literal object on ntriples line " + count);
                        line = in.readLine();
                        continue;
                    }
                    object = factory.createLiteral(StringEscapeUtils.unescapeJava(new String(line.substring(++i, lastIndex))));
                    
                }
                
                // In non-lowmem mode everything gets loaded into the
                // database
                con.add(subject, predicate, object);
                
            }
            catch (final ArrayIndexOutOfBoundsException e)
            {
                LOG.error("Parse error on ntriples line " + count, e);
                e.printStackTrace();
            }
            line = in.readLine();
        }
        
        con.commit();
        con.close();
        
    }
    
    /**
     * Merge the tkdm files. with the kdm merger
     * 
     * @param kdmMerger
     *            the kdm merger
     * @param tkdmFiles
     *            the list of tkdm files collected from the commanline.
     */
    void mergeTkdm(RepositoryMerger kdmMerger, List<File> tkdmFiles)
    {
        int fileNum = 0;
        int listSize = tkdmFiles.size();
        
        /*
         * for each tkdm file collected, load it into the temporary repository
         * and merge that repository.
         */
        for (final File file : tkdmFiles)
        {
            fileNum++;
            
            float fraction = 100f / listSize;
            int percent = (int) Math.ceil(fraction * fileNum);
            System.out.print("\r" + file);
            System.out.print("\nprocessing TKDM... " + percent + "%");
            
            Repository tempRepository = new SailRepository(new MemoryStore());
            
            try
            {
                tempRepository.initialize();
                
                // load the data into the repository
                load(tempRepository, new FileInputStream(file));
                
                // merge the repository.
                kdmMerger.merge(file.getAbsolutePath(), tempRepository);
                tempRepository.shutDown();
                
            }
            catch (final RepositoryException e)
            {
                LOG.error("There was an exception when merging the tkdmFiles. " + e);
                e.printStackTrace();
            }
            catch (final FileNotFoundException e)
            {
                LOG.error("The Kdm file has not been found. " + e);
                e.printStackTrace();
            }
            catch (final IOException e)
            {
                LOG.error("There was an IO Exception when trying to merge the KDM files. " + e);
                e.printStackTrace();
            }
        }
        
        System.out.println("");
    }
    
    /**
     * merge the toif files using the toif merger
     * 
     * @param toifMerger
     *            the toif merger
     * @param toifFiles
     *            the toif files collected from the command line.
     * @param startID
     * @return
     */
    Long mergeToif(ToifMerger toifMerger, List<File> toifFiles)
    {
        return toifMerger.merge(toifFiles);
    }
    
    /**
     * prints the contents of the repository. mainly for debug purposes.
     * 
     * @param repository
     *            the repository to print.
     */
    void printDB(Repository rep)
    {
        
        try
        {
            RepositoryConnection connection = rep.getConnection();
            // get all statements.
            final RepositoryResult<Statement> statements = connection.getStatements(null, null, null, true);
            
            // for all the statements.
            while (statements.hasNext())
            {
                final Statement st = statements.next();
                // print statements.
                System.out.println(st.toString());
                
            }
            
            statements.close();
        }
        catch (final RepositoryException e)
        {
            LOG.error("There was a repository error while printing the database. " + e);
            e.printStackTrace();
        }
        
    }
    
    /**
     * process the kdm files.
     * 
     * @param kdmFiles
     *            the list of kdm files to process
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RepositoryException
     */
    private void processKdmFile(final List<File> kdmFiles) throws FileNotFoundException, IOException, RepositoryException
    {
        if (debug)
        {
            System.err.println("processing kdm file...");
        }
        
        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);
        
        new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                KdmXmlHandler handler = null;
                try
                {
                    if (kdmFiles.size() > 1)
                    {
                        System.err.println("There should only be one .kdm file.");
                        
                    }
                    else if (kdmFiles.size() == 1)
                    {
                        File kdmFile = kdmFiles.get(0); // get the head of
                                                        // thelist.
                        InputStream is = new FileInputStream(kdmFile);
                        handler = load(kdmFile, out);
                        is.close();
                    }
                    
                    out.close();
                    
                    if (handler == null)
                    {
                        return;
                    }
                    setNextId(handler.getNextId());
                    setSmallestBigNumber(handler.getSmallestBigNumber());
                    // increase
                }
                catch (IOException e)
                {
                    System.err.println("IO exception whilst processing kdm file. " + e + ". Possibly an existing kdm file is in your input path!");
                }
                catch (RepositoryException e)
                {
                    System.err.println("Repository Exception whilst processing kdm file. " + e
                            + ". Possibly an existing kdm file is in your input path!");
                }
            }
        }).start();
        
        writeStatementLine(in);
        
    }
    
    /**
     * process the tkdm files
     * 
     * @param tkdmFiles
     *            the list of tkdm files to process.
     * @return
     * @throws IOException
     */
    private RepositoryMerger processTkdmFiles(final List<File> tkdmFiles) throws IOException
    {
        final PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);
        
        final RepositoryMerger kdmMerger = getTkdmMerger(new PrintWriter(out));
        new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                mergeTkdm(kdmMerger, tkdmFiles);
                kdmMerger.close();
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        
        writeStatementLine(in);
        return kdmMerger;
    }
    
    /**
     * process the toif files.
     * 
     * @param toifFiles
     *            list of toif files to process.
     * @param smallestBigNumber2
     *            the smallest number from the end of the long scale. used for
     *            the bnodes at the end of the repository
     * @param blacklistPath
     *            string that is the name of the directory of the project root.
     * @return
     * @throws IOException
     */
    private void processToifFiles(final List<File> toifFiles, Long id, Long smallestBigNumber2, String blacklistPath) throws IOException
    {
        PipedInputStream toifIn = new PipedInputStream();
        final PipedOutputStream toifOut = new PipedOutputStream(toifIn);
        
        final ToifMerger toifMerger = getToifMerger(new PrintWriter(toifOut), id, smallestBigNumber2, blacklistPath);
        new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                Long offset = mergeToif(toifMerger, toifFiles);
                
                setOffset(offset);
                try
                {
                    toifOut.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
        }).start();
        
        writeStatementLine(toifIn);
    }
    
    /**
     * stores the next id
     * 
     * @param newId
     *            the new id
     */
    private void setNextId(Long newId)
    {
        if (newId > nextId)
        {
            nextId = newId;
        }
        
    }
    
    /**
     * sets the offset
     * 
     * @param offset
     *            the offset from the local id to be the global id.
     */
    private void setOffset(Long offset)
    {
        this.offset = offset;
        
    }
    
    /**
     * sets the smallest number near the end of the long scale.
     * 
     * @param smallest
     *            the smallest number when counting down from the end of the
     *            long scale.
     */
    private void setSmallestBigNumber(Long smallest)
    {
        if (smallest < smallestBigNumber)
        {
            smallestBigNumber = smallest;
        }
        
    }
    
    /**
     * trims the string for use with creating a url.
     * 
     * @param string
     *            string to trim.
     * @return
     */
    private String trimForStatement(String string)
    {
        
        string = string.replaceAll("<|>", "");
        
        if (string.startsWith("\""))
        {
            string = new String(string.substring(1).intern());
        }
        if (string.endsWith("\""))
        {
            string = new String(string.substring(0, string.length() - 1).intern());
        }
        return string;
    }
    
    /**
     * writes the stream of statements to the repository.
     * 
     * @param in
     *            the stream of statements for the repository.
     * @throws IOException
     */
    void writeStatementLine(InputStream in) throws IOException
    {
        try
        {
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter(" \\.\r?\n");
            
            String line = null;
            // Read File Line By Line
            while (scanner.hasNext())
            {
                line = scanner.next();
                
                line = line.trim();
                String[] elements = line.split(" ", 3);
                Resource subject = factory.createURI(trimForStatement(elements[0]));
                URI predicate = factory.createURI(trimForStatement(elements[1]));
                Value object = determineObjectValue(elements);
                
                Statement statement = new StatementImpl(subject, predicate, object);
                
                add(statement);
                
                // capture the toif points of interest.
                if ("http://toif/path".equals(predicate.stringValue()))
                {
                    toifPaths.add(statement);
                }
                if ("http://toif/lineNumber".equals(predicate.stringValue()))
                {
                    lineNumbers.add(statement);
                }
                
                // have to capture the kdm points of interest
                if ("http://org.omg.kdm/SourceRef".equals(predicate.stringValue()))
                {
                    kdmElements.add(statement);
                }
                if ("http://org.omg.kdm/contains".equals(predicate.stringValue()))
                {
                    kdmContains.add(statement);
                }
                // attempt to add the statement to the sourcefiles (this checks
                // to see if it is indeed a sourcefile).
                if ("http://org.omg.kdm/path".equals(statement.getPredicate().stringValue()))
                {
                    sourceFiles.put(statement.getObject().stringValue(), statement.getSubject());
                }
                
            }
            
        }
        catch (RepositoryException e)
        {
            System.err.println("Repository exception while writing the statement to the repository. " + e);
        }
    }
    
}
