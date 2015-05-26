/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.kdmanalytics.kdm.repositoryMerger.RepositoryMerger;
import com.kdmanalytics.kdm.repositoryMerger.linkconfig.LinkConfig;
import com.kdmanalytics.kdm.repositoryMerger.linkconfig.MergeConfig;
import com.kdmanalytics.toif.assimilator.FilePathTrie.Node;
import com.kdmanalytics.toif.assimilator.exceptions.AssimilatorArgumentException;
import com.kdmanalytics.toif.common.exception.ToifException;
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
     *            assimilated. TODO: remove main since we are now using RCP app
     *            and can be a security hole/ public static void main(String[]
     *            args) { Assimilator assimilator = new Assimilator(); //
     *            assimilate the files. assimilator.assimilate(args);
     * 
     *            }
     * 
     *            /** do we continue if exceptions are found.
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
     * The kdm extension for the kdm keep file.
     */
    private static final String KDM_KEEP_EXTENSION = ".keep";
    
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
    
    private HashMap<String, Statement> lineNumbers = new HashMap<String, Statement>();
    
    private Map<String, Statement> sourceRefStatements = new HashMap<String, Statement>();
    
    private Map<String, String> kdmContains = new HashMap<String, String>();
    
    /**
     * the repository option. -r, -m, or -k. [R]epository output, repository
     * [M]igration, and [K]DM ouput respectively.
     */
    private String rOption;
    
    private ZipOutputStream zos;
    
    private PrintWriter writer = null;
    
    private boolean createZip = false;
    
    // private FileOutputStream fos;
    
    /**
     * constructor for the assimilator.
     */
    public Assimilator()
    {
        sourceFiles = new HashMap<String, Resource>();
    }
    
    /**
     * @param createZip2
     */
    public Assimilator(boolean createZip)
    {
        this.createZip = createZip;
        sourceFiles = new HashMap<String, Resource>();
    }
    
    /**
     * 
     * @param statement
     * @throws RepositoryException
     */
    public void addStatement(Statement statement) throws RepositoryException
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
        // BufferedWriter writer = new BufferedWriter(new
        // FileWriter(outputLocation.getAbsolutePath(), true));
        Resource subject = statement.getSubject();
        String subjectString = subject.toString();
        if ("-m".equals(rOption))
        {
            subjectString = subjectString.replace("http://kdmanalytics.com/", "");
            subjectString = subjectString.replace("http://toif/", "");
        }
        
        writer.write("<" + subjectString + "> ");
        
        URI predicate = statement.getPredicate();
        String predicateString = predicate.toString();
        if ("-m".equals(rOption))
        {
            predicateString = predicateString.replace("http://org.omg.kdm/", "");
            predicateString = predicateString.replace("http://toif/", "");
        }
        
        writer.write("<" + predicateString + "> ");
        
        String object = statement.getObject().toString();
        if ("-m".equals(rOption))
        {
            object = object.replace("http://kdmanalytics.com/", "");
            object = object.replace("http://toif/", "");
        }
        if (!object.startsWith("\""))
        {
            object = "<" + object + ">";
        }
        writer.write(object + " ");
        writer.write(" .\n");
        writer.flush();
        
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
     * main entry point mainly to aid in testing. tytpial use would be via the
     * RPC.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Assimilator ass = new Assimilator();
        try
        {
            ass.assimilate(args);
        }
        catch (Exception e)
        {
            System.err.println("running in stand alone context, exception occured.");
            e.printStackTrace();
        }
        
    }
    
    /**
     * Assimilate the files into one repository.
     * 
     * -k kdmoutputfile toiffiles.
     * 
     * @param args
     *            the arguments passed to main. These include the repository
     *            location and tkdm/toif file locations.
     * @throws ToifException
     * @throws IOException
     * 
     */
    public boolean assimilate(String[] args) throws ToifException, IOException
    {
        System.out.println("Running, this may take some time...");
        try
        {
            outputLocation = getOutputLocation(args);
            
            // get the tkdm files and the toif files.
            final List<File> kdmFiles = getFiles(args, KDM_EXTENSION, KDM_KEEP_EXTENSION);
            final List<File> tkdmFiles = getFiles(args, TKDM_EXTENSION, KDMO_EXTENSION);
            final List<File> toifFiles = getFiles(args, TOIF_EXTENSION);
            
            repository = createRepository(outputLocation);
            
            if (createZip)
            {
                createZipWriter();
            }
            else
            {
                createFileWriter();
            }
            
            if ("-m".equals(rOption))
            {
                
                if (!kdmFiles.isEmpty())
                {
                    InputStream is = new FileInputStream(kdmFiles.get(0));
                    
                    byte buf[] = new byte[1000];
                    int count = is.read(buf);
                    is.close();
                    
                    // If the file is empty, handle it special
                    if (count <= 0)
                    {
                        return false;
                    }
                    
                    // If it is a zip importer there is further processing that
                    // is done on the return to determine what type of data
                    // is contained within the zip file.
                    if (buf[0] == 'P' && buf[1] == 'K' && buf[2] == 3)
                    {
                        processKdmZip(kdmFiles);
                    }
                    else
                    {
                        processkdm(kdmFiles);
                    }
                }
                else
                {
                    LOG.error("There are no input kdm files");
                }
                
            }
            
            if (debug)
            {
                System.err.println("repository is: " + repository);
            }
            
            // if the migrate option has not been set. (future functionality)
            if (!"-m".equals(rOption))
            {
                // get the kdm file this writes directly to the repository.
                processkdm(kdmFiles);
                
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
            nextId++;
            processToifFiles(toifFiles, nextId, smallestBigNumber, blacklistPath);
            
            compareLocations();
            
        }
        catch (final AssimilatorArgumentException e)
        {
            final String msg = "Bad argument:";
            LOG.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
        catch (RepositoryException e)
        {
            e.printStackTrace();
        }
        /*
         * catch (IOException e) { e.printStackTrace(); }
         */
        finally
        {
            try
            {
                
                con.close();
                repository.shutDown();
                if (writer != null)
                {
                    writer.close();
                }
                outputLocation = null;
            }
            catch (RepositoryException e)
            {
                System.err.println("There was a problem closing the connection to the repository. " + e);
            }
            
            System.out.println("\nComplete.");
            
        }
        return true;
    }
    
    /**
     * process the kdm files. decides if its xml or triples.
     * 
     * @param kdmFiles
     * @throws ToifException
     */
    private void processkdm(List<File> kdmFiles) throws ToifException
    {
        if (kdmFiles.size() == 0)
        {
            return;
        }
        
        File kdmFile = kdmFiles.get(0);
        
        FileInputStream is = null;
        DataInputStream din = null;
        BufferedReader br = null;
        try
        {
            is = new FileInputStream(kdmFile);
            din = new DataInputStream(is);
            br = new BufferedReader(new InputStreamReader(din));
            
            String firstLine = br.readLine();
            
            if (firstLine.isEmpty())
            {
                return;
            }
            else if (firstLine.startsWith("<?xml"))
            {
                processKdmXmlFile(kdmFiles);
            }
            else
            {
                processKdmFile(kdmFiles);
            }
            
        }
        catch (FileNotFoundException e)
        {
            LOG.error("kdm file not found");
            return;
        }
        catch (IOException e)
        {
            LOG.error("error reading kdm file");
            return;
        }
        catch (RepositoryException e)
        {
            LOG.error("error accessing repository for writing xml nodes");
            return;
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();
                
                if (din != null)
                    din.close();
                
                if (is != null)
                    is.close();
                
            }
            catch (IOException e)
            {
                LOG.error("Unable to close stream: ", e);
            }
            
        }
        
    }
    
    /**
     * @param kdmFiles
     * @throws ToifException
     */
    private void processKdmFile(List<File> kdmFiles) throws ToifException
    {
        FileInputStream fis = null;
        File file = null;
        try
        {
            file = kdmFiles.get(0);
            
            fis = new FileInputStream(file);
            // Read from the ZipInputStream as you would normally from any other
            // input stream
            System.out.println("\n" + file.getAbsolutePath());
            streamStatementsToRepo(fis);
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * 
     */
    private void createFileWriter()
    {
        try
        {
            writer = new PrintWriter(outputLocation);
            
        }
        catch (FileNotFoundException e)
        {
            LOG.error("The file " + outputLocation + " has not been found.");
            
        }
        
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
            
            if (percent > 100)
            {
                percent = 100;
            }
            
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
                if (reductionAmount == 100)
                {
                    findBestKdmElementForToifStatement(toifStatement, kdmResourceFile, reductionAmount, true);
                    break;
                }
                
                bestFitResource = findBestKdmElementForToifStatement(toifStatement, kdmResourceFile, reductionAmount, false);
                
                // if (bestFitResource == null)
                // {
                // findBestKdmElementForToifStatement(toifStatement,
                // kdmResourceFile, reductionAmount * -1, false);
                // }
                
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
        addStatement(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "Type"), typeLiteral));
        
        // link the toif to it
        addStatement(new StatementImpl(toifSubject, factory.createURI(KDM_NS_HOST + "CommonView"), bnodeURI));
        // link the toif from it.
        addStatement(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "ToifView"), toifSubject));
        
        // link the kdm to it
        addStatement(new StatementImpl(blockUnit, factory.createURI(KDM_NS_HOST + "CommonView"), bnodeURI));
        // link the kdm from it.
        addStatement(new StatementImpl(bnodeURI, factory.createURI(KDM_NS_HOST + "KdmView"), blockUnit));
        
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
            try
            {
                MemoryStore memoryStore = new MemoryStore();
                repository = new SailRepository(memoryStore);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        
        try
        {
            // initialize the repository.
            if (repository == null)
            {
                throw new RepositoryException("Repository is null");
            }
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
     * addToFile
     * 
     */
    private void createZipWriter()
    {
        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream(outputLocation);
            zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(outputLocation.getName());
            zos.putNextEntry(ze);
            writer = new PrintWriter(new OutputStreamWriter(zos));
            
        }
        catch (FileNotFoundException e)
        {
            System.err.println("The file " + outputLocation + " has not been found.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("There has been a IO exception while creating the zip file.");
            e.printStackTrace();
        }
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
        
        Value object = null;
        
        if (elements.length < 2)
            return object;
        
        try
        {
            String objectString = trimForStatement(elements[2]);
            if (elements[2].startsWith("<"))
            {
                if (!objectString.startsWith("http"))
                {
                    objectString = "http://kdmanalytics.com/" + objectString;
                }
                object = factory.createURI(objectString);
            }
            else
            {
                object = factory.createLiteral(objectString);
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("index out of bouds " + e);
            System.err.println(elements);
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
    private Resource findBestKdmElementForToifStatement(Statement st, Resource bestFit, int reductionAmount, boolean searchForParent)
    {
        
        if (st == null || bestFit == null)
        {
            return null;
        }
        
        String lineNumber = "";
        
        // figure out the reference for the source ref. Extract the id.
        String sourceFileRef = bestFit.stringValue().replace("http://kdmanalytics.com/", "");
        
        Statement lineNumberStatement = lineNumbers.get(st.getSubject().stringValue());
        
        lineNumber = lineNumberStatement.getObject().stringValue();
        
        int tempLineNumber = Integer.parseInt(lineNumber) - reductionAmount;
        
        if (tempLineNumber <= 1)
        {
            tempLineNumber = 1;
        }
        
        lineNumber = tempLineNumber + "";
        
        // make the literal for the sourceRef.
        String sourceRef = sourceFileRef + ";" + lineNumber;
        
        Statement statmentWithSourceRef = sourceRefStatements.get(sourceRef);
        
        if (searchForParent)
        {
            return getParent(sourceFileRef);
        }
        else if (statmentWithSourceRef == null)
        {
            return null;
        }
        else
        {
            Resource subject = statmentWithSourceRef.getSubject();
            return subject;
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
     * get all the files from the argument list.
     * 
     * @param args
     *            the arguments handed to main.
     * @return the list of files in the argument array with the given extension.
     * @throws ToifException
     */
    List<File> getFiles(String[] args, final String... extensions) throws ToifException
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
        
        // int startIndex = 2;
        // for (String string : args)
        // {
        // if ("-p".equals(string))
        // {
        // startIndex = 4;
        // }
        // }
        
        // starting at the unparsed options, ie the files in the arguments.
        for (int i = 2; i < args.length; i++)
        {
            
            if ("-p".equals(args[i]))
            {
                i = i += 2;
            }
            
            final String path = args[i];
            
            final File file = new File(path);
            
            // if the file does not exist, bail.
            if (!file.exists())
            {
                final String msg = "File does not exist: " + file.getName();
                LOG.error(msg);
                throw new ToifException(msg);
                
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
        
        int indexOfROption = 0;
        
        for (int i = 0; i < args.length; i++)
        {
            if ("-r".equals(args[i]) || "-k".equals(args[i]))
            {
                indexOfROption = i;
            }
        }
        
        rOption = args[indexOfROption];
        
        if ("-m".equals(rOption))
        {
            if (args.length < 3)
            {
                throw new AssimilatorArgumentException("There are not enough arguments.");
            }
        }
        else
        {
            if (args.length < 3)
            {
                throw new AssimilatorArgumentException("There are not enough arguments.");
            }
        }
        
        // the repository location, following the '-r' option
        final String locationParameter = args[indexOfROption + 1];
        
        if ((!rOption.equals("-r")) && (!rOption.equals("-k")) && (!rOption.equals("-m")))
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
            location = getValidFileLocation(locationParameter);
        }
        
        // return the location.
        return location;
    }
    
    /**
     * get the parent of the kdm element.
     * 
     * @param sourceFileRef
     *            element
     * @return the parent of the kdm element.
     */
    private Resource getParent(String sourceFileRef)
    {
        return (Resource) factory.createURI("http://kdmanalytics.com/" + sourceFileRef);
        // String result = kdmContains.get(KdmElementWithSourceRef);
        //
        // if (result != null)
        // {
        // return (Resource) factory.createURI(result);
        // }
        // else
        // {
        // return null;
        // }
        // for (Statement containsStatement : kdmContains)
        // {
        // String containedObjectString =
        // containsStatement.getObject().stringValue();
        //
        // // if the contained element is the element with the source ref.
        // if (containedObjectString.equals(KdmElementWithSourceRef))
        // {
        // // return the element that contains the element with sourceRef.
        // return (Resource)
        // factory.createURI(containsStatement.getSubject().stringValue());
        // }
        // }
        // return null;
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
    RepositoryMerger getTkdmMerger(PrintWriter out, String assemblyName)
    {
        if (assemblyName == null)
        {
            assemblyName = "Assembly";
        }
        
        final InputStream is = Assimilator.class.getResourceAsStream("config/cxx.cfg");
        
        final LinkConfig config = new LinkConfig(is);
        
        final MergeConfig mergeConfig = config.getMergeConfig();
        
        final RepositoryMerger merger = new RepositoryMerger(mergeConfig, out, RepositoryMerger.NTRIPLES, assemblyName);
        
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
     * @param xmlFile
     *            the input file
     * @param out
     *            output stream
     * @return the xml handler for the kdm data.
     * @throws IOException
     * @throws ToifInternalException
     */
    public KdmXmlHandler load(File xmlFile, PipedOutputStream out) throws IOException, RepositoryException, ToifException
    {
        RepositoryConnection tempCon = null;
        KdmXmlHandler kdmXmlHandler = null;
        PrintWriter pw = null;
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
            
            SAXParser saxParser;
            saxParser = factory.newSAXParser();
            
            // Have to parse the file once to determine the maximum id;
            KdmXmiIdHandler idHandler = new KdmXmiIdHandler();
            
            // Need to set the stream to UTF-8 ti ensure that we correctly
            // handle
            // characters in Strings
            InputStream inputStream = new FileInputStream(xmlFile);
            InputStreamReader inputReader = new InputStreamReader(inputStream, "UTF-8");
            InputSource inputSource = new InputSource(inputReader);
            inputSource.setEncoding("UTF-8");
            
            saxParser.parse(inputSource, idHandler);
            
            tempCon.setAutoCommit(false); // Control commit for speed
            
            pw = new PrintWriter(out);
            kdmXmlHandler = new KdmXmlHandler(pw, repository, idHandler.getMaxId());
            
            // Parse the input
            saxParser.parse(xmlFile, kdmXmlHandler);
            
            // Commit postLoad data
            kdmXmlHandler.postLoad();
            
            tempCon.commit();
            tempCon.clear();
        }
        
        catch (ParserConfigurationException | SAXException ex)
        {
            final String msg = "Parser exception encountered:";
            LOG.error(msg, ex);
            throw new ToifException(msg, ex);
        }
        finally
        {
            if (pw != null)
            {
                pw.flush();
                pw.close();
            }
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
    public void load(Repository repository, InputStream is) throws IOException, RepositoryException
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
                    String string = new String(line.substring(++i, lastIndex));
                    string = string.replace("\\", "\\\\");
                    object = factory.createLiteral(StringEscapeUtils.unescapeJava(string));
                    
                }
                
                // In non-lowmem mode everything gets loaded into the
                // database
                // KdmXmlHandler.addOrWrite(con, subject, predicate, object);
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
    public void mergeTkdm(RepositoryMerger kdmMerger, List<File> tkdmFiles)
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
            
            if (percent > 100)
            {
                percent = 100;
            }
            
            System.out.print("\r" + file);
            System.out.print("\nprocessing TKDM... " + percent + "%");
            
            try
            {
                File temp = Files.createTempDir();
                
                SailRepository tempRepository = new SailRepository(new NativeStore(temp));
                
                tempRepository.initialize();
                
                // load the data into the repository
                FileInputStream istream = new FileInputStream(file);
                load(tempRepository, istream);
                istream.close();
                
                // merge the repository.
                kdmMerger.merge(file.getAbsolutePath(), tempRepository);
                tempRepository.shutDown();
                FileUtils.deleteDirectory(temp);
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
    
    class ThreadStatus
    {
        
        protected Exception exception;
        
        ThreadStatus()
        {
            exception = null;
        }
    }
    
    private void processKdmXmlFile(final List<File> kdmFiles) throws FileNotFoundException, IOException, RepositoryException, ToifException
    {
        if (debug)
        {
            System.err.println("processing kdm file...");
        }
        
        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);
        final ThreadStatus status = new ThreadStatus();
        
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                KdmXmlHandler handler = null;
                try
                {
                    if (kdmFiles.size() > 1)
                    {
                        final String msg = "There should only be one .kdm file.";
                        LOG.error(msg);
                        throw new ToifException(msg);
                    }
                    else if (kdmFiles.size() == 1)
                    {
                        File kdmFile = kdmFiles.get(0); // get the head of
                                                        // thelist.
                        handler = load(kdmFile, out);
                    }
                    out.flush();
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
                    final String msg = "IO exception whilst processing kdm file. " + ". Possibly an existing kdm file is in your input path!";
                    
                    LOG.error(msg, e);
                    status.exception = new ToifException(msg, e);
                }
                catch (RepositoryException e)
                {
                    final String msg = "Repository Exception whilst processing kdm file. " + ". Possibly an existing kdm file is in your input path!";
                    
                    LOG.error(msg, e);
                    status.exception = new ToifException(msg, e);
                }
                catch (ToifException e)
                {
                    // RJF final String msg =
                    // "Processing Exception whilst processing kdm file. "
                    // + ". Possibly that input file is invalid XML!";
                    
                    // LOG.error(msg, e);
                    status.exception = e;
                }
                finally
                {
                    if (out != null)
                        try
                        {
                            out.close();
                        }
                        catch (IOException e)
                        {
                            // Just leave it alone
                            LOG.error("unable to close stream");
                        }
                }
            }
        });
        
        // ---------------------------------------------------------
        // Unable to change logic within the short time frame given so
        // adding a means to catch unknown exceptions in thread
        // ----------------------------------------------------------
        Thread.UncaughtExceptionHandler tueh = new Thread.UncaughtExceptionHandler() {
            
            public void uncaughtException(Thread th, Throwable ex)
            {
                LOG.error("Uncaught exception: " + ex);
                status.exception = (Exception) ex;
            }
        };
        
        t.setUncaughtExceptionHandler(tueh);
        t.start();
        
        streamStatementsToRepo(in);
        try
        {
            t.join();
            
            // Check if we enoutered exception during processing and
            // proxy throw if we have one
            if (status.exception != null)
            {
                // Leave alone if already a ToifException
                if (status.exception instanceof ToifException)
                    throw (ToifException) status.exception;
                else throw new ToifException(status.exception);
                
            }
        }
        catch (InterruptedException e)
        {
            LOG.error("Interrupted");
            throw new ToifException("Interrupted");
        }
    }
    
    /**
     * @param kdmFiles2
     * @throws IOException
     * @throws ToifException
     */
    private void processKdmZip(List<File> kdmFiles2) throws IOException, ToifException
    {
        ZipInputStream zip = null;
        File file = null;
        try
        {
            file = kdmFiles2.get(0);
            zip = new ZipInputStream(new FileInputStream(file));
            zip.getNextEntry();
            
            // Read from the ZipInputStream as you would normally from any other
            // input stream
            System.out.println("\n" + file.getAbsolutePath());
            streamStatementsToRepo(zip);
        }
        
        finally
        {
            try
            {
                if (zip != null)
                {
                    zip.close();
                }
            }
            catch (IOException e)
            {
                // just leave it alone
                if (file == null)
                    file = new File(""); // just to be sure
                LOG.error("unable to close zip stream" + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * process the tkdm files
     * 
     * @param tkdmFiles
     *            the list of tkdm files to process.
     * @return
     * @throws IOException
     * @throws ToifException
     */
    private RepositoryMerger processTkdmFiles(final List<File> tkdmFiles) throws IOException, ToifException
    {
        final PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);
        
        String assemblyName = "Assembly";
        int possition = outputLocation.getName().lastIndexOf(".");
        if (possition != -1)
        {
            assemblyName = outputLocation.getName().substring(0, possition);
        }
        
        final RepositoryMerger kdmMerger = getTkdmMerger(new PrintWriter(out), assemblyName);
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
        
        streamStatementsToRepo(in);
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
     * @throws ToifException
     */
    private void processToifFiles(final List<File> toifFiles, Long id, Long smallestBigNumber2, String blacklistPath) throws IOException,
            ToifException
    {
        PipedInputStream toifIn = new PipedInputStream();
        final PipedOutputStream toifOut = new PipedOutputStream(toifIn);
        
        // final ToifMerger toifMerger = getToifMerger(new PrintWriter(toifOut),
        // id, smallestBigNumber2, blacklistPath);
        
        PrintWriter w = new PrintWriter(toifOut);
        final ToifMerger toifMerger = getToifMerger(w, id, smallestBigNumber2, blacklistPath);
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
        
        streamStatementsToRepo(toifIn);
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
        string = string.replace(" .", "");
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
    
    /*********************************************************
     * Writes the stream of statements to the repository.
     * 
     * @param in
     *            the stream of statements for the repository.
     * @throws IOException
     * @throws ToifException
     **********************************************************/
    private void streamStatementsToRepo(InputStream in) throws IOException, ToifException
    {
        try
        {
            // Scanner scanner = new Scanner(in);
            // scanner.useDelimiter(" \\.\r?\n");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            Long count = 0L;
            String line = null;
            
            Set<String> containers = new HashSet<String>();
            
            // Read File Line By Line
            // while (scanner.hasNext())
            while ((line = br.readLine()) != null)
            {
                
                if ("".equals(line))
                {
                    continue;
                }
                
                if (line.startsWith("#"))
                {
                    writer.write(line + "\n");
                    continue;
                }
                if (line.startsWith("KDM_Triple"))
                {
                    writer.write(line + "\n");
                    continue;
                }
                
                // line = scanner.next();
                line = line.trim();
                // System.err.println(line);
                
                if ("-m".equals(rOption) && (count % 10000 == 0))
                {
                    System.out.print("\rprocessing KDM file... statement: " + count + "          ");
                }
                count++;
                
                String[] elements = line.split(" ", 3);
                String subjectString = trimForStatement(elements[0]);
                
                String number = subjectString.replaceAll("\\D+", "");
                
                if (!number.isEmpty())
                {
                    Long id = Long.parseLong(number);
                    if (id > nextId)
                    {
                        nextId = id;
                    }
                }
                
                if (!subjectString.startsWith("http"))
                {
                    subjectString = "http://kdmanalytics.com/" + subjectString;
                }
                
                Resource subject = factory.createURI(subjectString);
                
                // ** Add guard in case we do have element
                if (elements.length < 2)
                    continue;
                
                String predicateString = trimForStatement(elements[1]);
                
                if (!predicateString.startsWith("http"))
                {
                    predicateString = "http://org.omg.kdm/" + predicateString;
                }
                
                URI predicate = factory.createURI(predicateString);
                
                Value object = determineObjectValue(elements);
                
                if (object == null)
                {
                    continue;
                }
                
                Statement statement = new StatementImpl(subject, predicate, object);
                
                addStatement(statement);
                
                // turn on storage if the type is correct. this is so that we
                // dont store more than we need.
                if ("http://org.omg.kdm/kdmType".equals(predicate.stringValue()))
                {
                    if ("code/SharedUnit".equals(object.stringValue()))
                    {
                        containers.add(subjectString);
                    }
                    if ("code/CompilationUnit".equals(object.stringValue()))
                    {
                        containers.add(subjectString);
                    }
                    if ("code/CallableUnit".equals(object.stringValue()))
                    {
                        containers.add(subjectString);
                    }
                    if ("code/MethodUnit".equals(object.stringValue()))
                    {
                        containers.add(subjectString);
                    }
                    if ("code/ClassUnit".equals(object.stringValue()))
                    {
                        containers.add(subjectString);
                    }
                }
                
                // capture the toif points of interest.
                if ("http://toif/path".equals(predicate.stringValue()))
                {
                    toifPaths.add(statement);
                }
                if ("http://toif/lineNumber".equals(predicate.stringValue()))
                {
                    lineNumbers.put(statement.getSubject().stringValue(), statement);
                }
                
                // have to capture the kdm points of interest
                String stringValue = statement.getObject().stringValue();
                if ("http://org.omg.kdm/SourceRef".equals(predicate.stringValue()))
                {
                    stringValue = stringValue.replace(";;||java", "");
                    sourceRefStatements.put(stringValue, statement);
                }
                if ("http://org.omg.kdm/contains".equals(predicate.stringValue()) && containers.contains(subjectString))
                {
                    kdmContains.put(object.stringValue(), subject.stringValue());
                }
                // attempt to add the statement to the sourcefiles (this checks
                // to see if it is indeed a sourcefile).
                if ("http://org.omg.kdm/path".equals(statement.getPredicate().stringValue()))
                {
                    sourceFiles.put(stringValue, statement.getSubject());
                }
                
            }
            
            // Close file to free resource
            if (br != null)
                br.close();
            
        }
        catch (RepositoryException e)
        {
            final String msg = "Repository exception while writing the statement to the repository.";
            LOG.error(msg, e);
            throw new ToifException(msg, e);
        }
    }
}
