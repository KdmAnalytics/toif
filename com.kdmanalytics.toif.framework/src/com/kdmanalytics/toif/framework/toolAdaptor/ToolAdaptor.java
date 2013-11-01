/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////
 * ////////////////////////////////////////////////////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.toolAdaptor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.kdmanalytics.toif.framework.utils.ElementComparator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Adaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.Address;
import com.kdmanalytics.toif.framework.xmlElements.entities.CWEIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.Checksum;
import com.kdmanalytics.toif.framework.xmlElements.entities.ClusterIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.DataElement;
import com.kdmanalytics.toif.framework.xmlElements.entities.Date;
import com.kdmanalytics.toif.framework.xmlElements.entities.Description;
import com.kdmanalytics.toif.framework.xmlElements.entities.Directory;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.EmailAddress;
import com.kdmanalytics.toif.framework.xmlElements.entities.Entity;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;
import com.kdmanalytics.toif.framework.xmlElements.entities.Generator;
import com.kdmanalytics.toif.framework.xmlElements.entities.LineNumber;
import com.kdmanalytics.toif.framework.xmlElements.entities.Name;
import com.kdmanalytics.toif.framework.xmlElements.entities.Offset;
import com.kdmanalytics.toif.framework.xmlElements.entities.Organization;
import com.kdmanalytics.toif.framework.xmlElements.entities.Person;
import com.kdmanalytics.toif.framework.xmlElements.entities.Position;
import com.kdmanalytics.toif.framework.xmlElements.entities.Project;
import com.kdmanalytics.toif.framework.xmlElements.entities.Role;
import com.kdmanalytics.toif.framework.xmlElements.entities.SFPIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;
import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;
import com.kdmanalytics.toif.framework.xmlElements.entities.Text;
import com.kdmanalytics.toif.framework.xmlElements.entities.Vendor;
import com.kdmanalytics.toif.framework.xmlElements.entities.WeaknessDescription;
import com.kdmanalytics.toif.framework.xmlElements.facts.AdaptorIsSuppliedByVendor;
import com.kdmanalytics.toif.framework.xmlElements.facts.AdaptorSupportsGenerator;
import com.kdmanalytics.toif.framework.xmlElements.facts.CodeLocationReferencesFile;
import com.kdmanalytics.toif.framework.xmlElements.facts.DataElementIsInvolvedInFinding;
import com.kdmanalytics.toif.framework.xmlElements.facts.DataElementIsInvolvedInStatement;
import com.kdmanalytics.toif.framework.xmlElements.facts.DirectoryIsContainedInDirectory;
import com.kdmanalytics.toif.framework.xmlElements.facts.Fact;
import com.kdmanalytics.toif.framework.xmlElements.facts.FileIsContainedInDirectory;
import com.kdmanalytics.toif.framework.xmlElements.facts.FindingHasCWEIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.facts.FindingHasClusterIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.facts.FindingHasCodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.facts.FindingHasSFPIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.facts.FindingIsDescribedByWeaknessDescription;
import com.kdmanalytics.toif.framework.xmlElements.facts.GeneratorIsSuppliedByVendor;
import com.kdmanalytics.toif.framework.xmlElements.facts.OrganizationIsInvolvedInProjectAsRole;
import com.kdmanalytics.toif.framework.xmlElements.facts.OrganizationIsPartOfOrganizationAsRole;
import com.kdmanalytics.toif.framework.xmlElements.facts.PersonIsEmployedByOrganizationAsRole;
import com.kdmanalytics.toif.framework.xmlElements.facts.PersonIsInvolvedInProjectAsRole;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementHasCodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsInvolvedInFinding;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsProceededByStatement;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsSinkInFinding;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsCreatedAtDate;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsGeneratedByGenerator;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsGeneratedByPerson;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsOwnedByOrganization;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsProcessedByAdaptor;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsProducedByOrganization;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsRelatedToProject;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentIsSupervisedByPerson;
import com.kdmanalytics.toif.framework.xmlElements.facts.TOIFSegmentReferencesFile;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;

/**
 * The main guts of the Adaptor. Creates much of the housekeeping and calls of
 * the implementation of the specific adaptor to get the finding elements.
 * 
 * @author Adam Nunn
 * 
 */

public class ToolAdaptor
{
    
    /**
     * The implementation of the specific adaptor.
     */
    AbstractAdaptor adaptorImpl = null;
    
    /**
     * the created elements
     */
    ArrayList<Element> elements = new ArrayList<Element>();
    
    /**
     * housekeeping facts
     */
    Properties houseKeeping = null;
    
    /**
     * the table containing all the elements to be converted to xml
     */
    Hashtable<Integer, Element> housekeepingElements = new Hashtable<Integer, Element>();
    
    /**
     * the options given to main.
     */
    AdaptorOptions options = null;
    
    /**
     * the root segment
     */
    private Segment segment = new Segment();
    
    /**
     * additional options from command line
     */
    private String[] otherOpts = null;
    
    private java.io.File workingDirectory = null;
    
    private boolean[] validLines;
    
    /**
     * Constructor of the adaptor. Takes the arguments from main, extracts the
     * options from it, generates the translation table, and runs the tool.
     * 
     * @param args
     *            - The arguments from main.
     */
    public ToolAdaptor(String[] args)
    {
        
    }
    
    public boolean runToolAdaptor(String[] args)
    {
        // get the options provided to main.
        try
        {
            setOptions(args);
        }
        catch (ArgumentValidationException e)
        {
            System.err.println("Incorrect arguments");
            return false;
        }
        
        // from options
        Class<?> adaptorClass = getAdaptorClass();
        
        setAdaptorImplementation(adaptorClass);
        
        createFacts(null);
        
        File file = createSegmentFile();
        
        // run the tool.
        final Process process = runTool();
        
        if (process == null)
        {
            System.err.println("unable to run the scan tool, or no tool needs to be run.");
            return false;
        }
        
        getElementsFromParse(process, file);
        
        // construct the xml.
        constructXml();
        
        return true;
    }
    
    public boolean runToolAdaptor(AbstractAdaptor adaptor, List<String> arguments, java.io.File workingDirectory, boolean[] validLines)
    {
        // spoof the adaptor option at the beginning.
        arguments.add(0, "-a");
        arguments.add(1, adaptor.getAdaptorName());
        
        this.validLines = validLines;
        
        this.workingDirectory = workingDirectory;
        
        String[] args = arguments.toArray(new String[arguments.size()]);
        
        // get the options provided to main.
        try
        {
            setOptions(args);
        }
        catch (ArgumentValidationException e)
        {
            System.err.println("Incorrect arguments");
            return false;
        }
        
        setAdaptorImplementation(adaptor);
        
        createFacts(null);
        
        File file = createSegmentFile();
        
        // run the tool.
        final Process process = runTool();
        
        if (process == null)
        {
            System.err.println("unable to run the scan tool, or no tool needs to be run.");
            return false;
        }
        
        getElementsFromParse(process, file);
        
        // construct the xml.
        constructXml();
        
        return true;
    }
    
    /**
     * Get all the elements from the parsing of the input file by the adaptor.
     * 
     * @param process
     *            the scan tool process
     * @param file
     *            The file that the segment is working on.
     */
    public void getElementsFromParse(Process process, File file)
    {
        
        /*
         * put all the elements and facts generated from the parse phase into
         * the elements hashtable.
         */
        ArrayList<Element> parse = parse(process, file);
        
        elements.addAll(parse);
    }
    
    /**
     * create the house keeping facts.
     * 
     * @param housekeepingFile
     */
    public void createFacts(java.io.File housekeepingFile)
    {
        try
        {
            houseKeeping = getHousekeepingProperties(housekeepingFile);
            
            // creates the housekeeping facts and elements
            getHouseKeepingFacts();
            
            // creates the project facts and elements
            getProjectFacts();
            
            // creates the tool facts and elements
            getToolFacts();
            
            // creates the facts and elements about the organization
            getOrganizationFacts();
            
            // creates the facts and elements about the person
            getPersonFacts();
        }
        catch (final NullPointerException e)
        {
            if (options != null)
            {
                System.err.println(options.getAdaptor().toString() + ": The house-keeping file is missing some properties. ");
                // e.printStackTrace();
            }
            else
            {
                System.err.println("The house-keeping file is missing some properties. ");
                // e.printStackTrace();
            }
        }
    }
    
    /**
     * get an implementation of the adaptor class in use.
     * 
     * @param adaptorClass
     */
    public void setAdaptorImplementation(Class<?> adaptorClass)
    {
        // try to create an instance of the adaptor class.
        try
        {
            adaptorImpl = (AbstractAdaptor) adaptorClass.newInstance();
        }
        catch (final InstantiationException e1)
        {
            System.err.println(options.getAdaptor().toString() + ": Adaptor not found!");
            System.exit(1);
        }
        catch (final IllegalAccessException e1)
        {
            System.err.println(options.getAdaptor().toString() + ": Adaptor not found!");
            System.exit(1);
        }
    }
    
    public void setAdaptor(AbstractAdaptor adaptor)
    {
        adaptorImpl = adaptor;
    }
    
    /**
     * set the adaptor to use.
     * 
     * @param adaptor
     */
    public void setAdaptorImplementation(AbstractAdaptor adaptor)
    {
        adaptorImpl = adaptor;
    }
    
    public Class<?> getAdaptorClass()
    {
        Class<?> adaptorClass = null;
        String adaptor = null;
        try
        {
            // get the class name from the options.
            adaptor = options.getAdaptor().toString();
            
            adaptorClass = Class.forName(adaptor);
            
        }
        catch (final ClassNotFoundException e1)
        {
            System.err.println(adaptor + ": Adaptor not found!");
            System.exit(1);
        }
        catch (Exception e)
        {
            System.err.println("Error reading arguments.");
            System.exit(1);
        }
        return adaptorClass;
    }
    
    /**
     * Adds a description to the segment if a description is present in the
     * housekeeping file.
     * 
     * @param houseKeeping
     *            the housekeeping property file.
     */
    private void addSegmentDescription(Properties houseKeeping)
    {
        final String segmentDescription = houseKeeping.getProperty("SegmentDescription");
        
        if (segmentDescription != null)
        {
            segment.setDescription(segmentDescription);
        }
        
    }
    
    /**
     * Tries to find a matching element in the elements which have been created
     * already. If it does find one, it returns it. Otherwise, it returns the
     * elements which was passed into it.
     * 
     * @param element
     *            The element you want to check is in the elements list.
     * @return returns either the element or a matching element.
     */
    Element addToList(Element element)
    {
        // check to make sure the entity is not in the table.
        if (housekeepingElements.containsKey(element.hashCode()))
        {
            return housekeepingElements.get(element.hashCode());
        }
        else
        {
            housekeepingElements.put(element.hashCode(), element);
            return element;
        }
    }
    
    /**
     * Since all the entities and facts are in the elements hashtable, we can
     * add them to the root segment and generate the xml for the ouput.
     */
    public void constructXml()
    {
        // String outDirPath = options.getOutputDirectory().getPath() +
        // java.io.File.separator + adaptorImpl.getAdaptorName();
        String outDirPath = options.getOutputDirectory().getPath();
        java.io.File outDir = new java.io.File(outDirPath);
        outDir.mkdirs();
        
        java.io.File outFile = null;
        
        if (options.isRename())
        {
            outFile = new java.io.File(outDirPath, options.getRename() + "." + adaptorImpl.getRuntoolName() + ".toif.xml");
        }
        else
        {
            outFile = new java.io.File(outDirPath, options.getInputFile().getName() + "." + adaptorImpl.getRuntoolName() + ".toif.xml");
        }
        java.io.File houseKeepingFile = new java.io.File(outDirPath, "GENERAL_INFORMATION.toif.xml");
        
        // change the hashtable to an arrayList inorder to sort them.
        final ArrayList<Element> results = new ArrayList<Element>(housekeepingElements.values());
        // marshall(houseKeepingFile, results);
        results.addAll(elements);
        
        marshall(outFile, results);
        // results.addAll(elements);
        
    }
    
    /**
     * Since all the entities and facts are in the elements hashtable, we can
     * add them to the root segment and generate the xml for the ouput.
     */
    public void constructXml(java.io.File outputDir, java.io.File outputFile)
    {
        outputDir.mkdirs();
        java.io.File houseKeepingFile = new java.io.File(outputDir, "GENERAL_INFORMATION.toif.xml");
        java.io.File outFile = new java.io.File(outputDir, outputFile.getName() + ".toif.xml");
        
        // change the hashtable to an arrayList inorder to sort them.
        final ArrayList<Element> results = new ArrayList<Element>(housekeepingElements.values());
        marshall(houseKeepingFile, results);
        marshall(outFile, elements);
        // results.addAll(elements);
        
    }
    
    /**
     * Find the directories the files are in.
     * 
     * 
     * @param newFile
     *            the file for which the containment will be determined.
     */
    private void containedIn(File file)
    {
        if (file.getParent() == null)
        {
            return;
        }
        
        Directory directory = new Directory(file.getParent());
        elements.add(directory);
        
        Fact fact = new FileIsContainedInDirectory(file, directory);
        elements.add(fact);
        
        // find all the directories containing directories.
        while (directory.getParent() != null)
        {
            // create the entities and facts.
            Directory directory2 = new Directory(directory.getParent());
            elements.add(directory2);
            
            DirectoryIsContainedInDirectory directoryContainedInDirectory = new DirectoryIsContainedInDirectory(directory, directory2);
            elements.add(directoryContainedInDirectory);
            
            directory = directory2;
        }
    }
    
    /**
     * Create the adaptor entity and the TOIFSegmentIsProducedByAdaptor fact.
     */
    private void createAdaptor()
    {
        final Adaptor adaptor = new Adaptor();
        adaptor.setName(adaptorImpl.getAdaptorName());
        adaptor.setDescription(adaptorImpl.getAdaptorDescription());
        adaptor.setVersion(adaptorImpl.getAdaptorVersion());
        
        housekeepingElements.put(adaptor.hashCode(), adaptor);
        final Fact fact = new TOIFSegmentIsProcessedByAdaptor(segment, adaptor);
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * Create the AdaptorIsSuppliedByVendor fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createAdaptorIsSuppliedByVendor(Properties props)
    {
        // create the entities for the fact.
        final Adaptor adaptor = (Adaptor) addToList(new Adaptor(adaptorImpl.getAdaptorName(), adaptorImpl.getAdaptorDescription(),
                adaptorImpl.getAdaptorVersion()));
        final Vendor vendor = (Vendor) addToList(new Vendor(adaptorImpl.getAdaptorVendorName(), adaptorImpl.getAdaptorVendorDescription(),
                adaptorImpl.getAdaptorVendorAddress(), adaptorImpl.getAdaptorVendorPhone(), adaptorImpl.getAdaptorVendorEmail()));
        
        // create the fact
        final Fact fact = new AdaptorIsSuppliedByVendor(adaptor, vendor);
        // add the fact.
        housekeepingElements.put(fact.hashCode(), fact);
    }
    
    /**
     * create the AdaptorSupportsGenerator fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createAdaptorSupportsGenerator(Properties props) throws NullPointerException
    {
        // create the entities.
        final Generator gen = (Generator) addToList(new Generator(adaptorImpl.getGeneratorName(), adaptorImpl.getGeneratorDescription(),
                adaptorImpl.getGeneratorVersion()));
        final Adaptor adaptor = (Adaptor) addToList(new Adaptor(adaptorImpl.getAdaptorName(), adaptorImpl.getAdaptorDescription(),
                adaptorImpl.getAdaptorVersion()));
        
        // create the fact
        final Fact fact = new AdaptorSupportsGenerator(adaptor, gen);
        // add the fact.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * create the date facts and entities.
     */
    private void createDate()
    {
        final Date date = new Date();
        
        elements.add(date);
        final Fact fact = new TOIFSegmentIsCreatedAtDate(segment, date);
        elements.add(fact);
        
    }
    
    /**
     * Create the GeneratorIsSuppliedByVendor fact and its entities.
     * 
     * @param props
     *            the property file
     */
    private void createGeneratorIsSuppliedByVendor(Properties props)
    {
        // create the entities for this fact.
        final Generator gen = (Generator) addToList(new Generator(adaptorImpl.getGeneratorName(), adaptorImpl.getGeneratorDescription(),
                adaptorImpl.getGeneratorVersion()));
        final Vendor vendor = (Vendor) addToList(new Vendor(adaptorImpl.getGeneratorVendorName(), adaptorImpl.getGeneratorVendorDescription(),
                adaptorImpl.getGeneratorVendorAddress(), adaptorImpl.getGeneratorVendorPhone(), adaptorImpl.getGeneratorVendorEmail()));
        
        // add the fact.
        final Fact fact = new GeneratorIsSuppliedByVendor(gen, vendor);
        housekeepingElements.put(fact.hashCode(), fact);
    }
    
    /**
     * create the OrganizationIsInvolvedInProjectAsRole fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createOrganizationIsInvolvedInProjectAsRole(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String[] factDetails = props.getProperty("OrganizationIsInvolvedInProjectAsRole").split(";");
        final String[] organizationDetails = props.getProperty(factDetails[0]).split(";");
        final String[] projectDetails = props.getProperty(factDetails[1]).split(";");
        final String[] roleDetails = props.getProperty(factDetails[2]).split(";");
        
        // create the entities.
        final Organization organization = (Organization) addToList(new Organization(organizationDetails[0], organizationDetails[1],
                organizationDetails[2], organizationDetails[3], organizationDetails[4]));
        final Project project = (Project) addToList(new Project(projectDetails[0], projectDetails[1]));
        final Role role = (Role) addToList(new Role(roleDetails[0], roleDetails[1]));
        
        // create the fact.
        final Fact fact = new OrganizationIsInvolvedInProjectAsRole(organization, project, role);
        // add the fact.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * Create the OrganizationIsPartOfOrganizationAsRole fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createOrganizationIsPartOfOrganizationAsRole(Properties props) throws NullPointerException
    {
        // extract the information from the properties file.
        final String[] factDetails = props.getProperty("OrganizationIsPartOfOrganizationAsRole").split(";");
        final String[] org1Details = props.getProperty(factDetails[0]).split(";");
        final String[] org2Details = props.getProperty(factDetails[1]).split(";");
        final String[] roleDetails = props.getProperty(factDetails[2]).split(";");
        
        // create the entities relating to this fact.
        final Organization org1 = (Organization) addToList(new Organization(org1Details[0], org1Details[1], org1Details[2], org1Details[3],
                org1Details[4]));
        final Organization org2 = (Organization) addToList(new Organization(org2Details[0], org2Details[1], org2Details[2], org2Details[3],
                org2Details[4]));
        final Role role = (Role) addToList(new Role(roleDetails[0], roleDetails[1]));
        
        // create the fact
        final Fact fact = new OrganizationIsPartOfOrganizationAsRole(org1, org2, role);
        // add the fact to the table.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * Create the PersonIsEmployedByOrganizationAsRole fact and it's entities.
     * 
     * @param props
     *            The Properties file.
     */
    private void createPersonIsEmployedByOrganizationAsRole(Properties props) throws NullPointerException
    {
        // extract the information from the properties file.
        final String[] factDetails = props.getProperty("PersonIsEmployedByOrganizationAsRole").split(";");
        final String[] personDetails = props.getProperty(factDetails[0]).split(";");
        final String[] org1Details = props.getProperty(factDetails[1]).split(";");
        final String[] roleDetails = props.getProperty(factDetails[2]).split(";");
        
        // create the required entities.
        final Person person = (Person) addToList(new Person(personDetails[0], personDetails[1], personDetails[2]));
        final Organization org1 = (Organization) addToList(new Organization(org1Details[0], org1Details[1], org1Details[2], org1Details[3],
                org1Details[4]));
        final Role role = (Role) addToList(new Role(roleDetails[0], roleDetails[1]));
        
        // create the fact, and add it to the table.
        final Fact fact = new PersonIsEmployedByOrganizationAsRole(person, org1, role);
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * create the PersonIsInvolvedInProjectAsRole fact and entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createPersonIsInvolvedInProjectAsRole(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String[] factDetails = props.getProperty("PersonIsInvolvedInProjectAsRole").split(";");
        final String[] personDetails = props.getProperty(factDetails[0]).split(";");
        final String[] projectDetails = props.getProperty(factDetails[1]).split(";");
        final String[] roleDetails = props.getProperty(factDetails[2]).split(";");
        
        // create the entities.
        final Person person = (Person) addToList(new Person(personDetails[0], personDetails[1], personDetails[2]));
        final Project project = (Project) addToList(new Project(projectDetails[0], projectDetails[1]));
        final Role role = (Role) addToList(new Role(roleDetails[0], roleDetails[1]));
        
        // create the fact.
        final Fact fact = new PersonIsInvolvedInProjectAsRole(person, project, role);
        // add the fact.
        housekeepingElements.put(fact.hashCode(), fact);
    }
    
    /**
     * Creates the file that the segment is for.
     * 
     * @return the file-element.
     */
    public File createSegmentFile()
    {
        File file = new File(options.getInputFile().getPath());
        
        return file;
    }
    
    /**
     * Creates the file that the segment is for.
     * 
     * @return the file-element.
     */
    public File createSegmentFile(java.io.File segmentFile)
    {
        File file = new File(segmentFile.getPath());
        elements.add(file);
        
        Fact fact = new TOIFSegmentReferencesFile(segment, file);
        elements.add(fact);
        
        containedIn(file);
        
        return file;
    }
    
    /**
     * Create the fact and entities for the generator that generated the
     * segment.
     * 
     * @param props
     * @throws NullPointerException
     */
    private void createSegmentIsGeneratedByGenerator(Properties props) throws NullPointerException
    {
        // create a new generator. the addToList() method makes sure that there
        // is only one of this generator in the list.
        final Generator gen = (Generator) addToList(new Generator(adaptorImpl.getGeneratorName(), adaptorImpl.getGeneratorDescription(),
                adaptorImpl.getGeneratorVersion()));
        
        // create the fact linking the generator and segment.
        final Fact fact = new TOIFSegmentIsGeneratedByGenerator(segment, gen);
        
        // add the generator to the housekeeping elements.
        housekeepingElements.put(fact.hashCode(), fact);
    }
    
    /**
     * create the SegmentIsGeneratedByPerson fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createSegmentIsGeneratedByPerson(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String personName = props.getProperty("TOIFSegmentIsGeneratedByPerson");
        final String[] personDetails = props.getProperty(personName).split(";");
        
        // create the entity relating to this fact.
        final Person person = (Person) addToList(new Person(personDetails[0], personDetails[1], personDetails[2]));
        
        final Fact fact = new TOIFSegmentIsGeneratedByPerson(segment, person);
        
        // add the fact.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * create the SegmentIsOwnedByOrganization fact and it's entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createSegmentIsOwnedByOrganization(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String orgName = props.getProperty("TOIFSegmentIsOwnedByOrganization");
        final String[] orgDetails = props.getProperty(orgName).split(";");
        
        // create the entity
        final Organization organization = (Organization) addToList(new Organization(orgDetails[0], orgDetails[1], orgDetails[2], orgDetails[3],
                orgDetails[4]));
        
        final Fact fact = new TOIFSegmentIsOwnedByOrganization(segment, organization);
        
        // add the fact to the table.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * create the SegmentIsProducedByOrganization fact and entities.
     * 
     * @param props
     *            properties file
     */
    private void createSegmentIsProducedByOrganization(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String orgName = props.getProperty("TOIFSegmentIsProducedByOrganization");
        final String[] orgDetails = props.getProperty(orgName).split(";");
        
        // create the entity
        final Organization organization = (Organization) addToList(new Organization(orgDetails[0], orgDetails[1], orgDetails[2], orgDetails[3],
                orgDetails[4]));
        
        final Fact fact = new TOIFSegmentIsProducedByOrganization(segment, organization);
        
        // add the fact to the table.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * create the SegmentIsRelatedToProject fact and its entities.
     * 
     * @param housekeeping
     *            the properties file.
     */
    private void createSegmentIsRelatedToProject(Properties housekeeping) throws NullPointerException
    {
        // get the information from the properties file.
        final String projectName = housekeeping.getProperty("TOIFSegmentIsRelatedToProject");
        final String[] projectDetails = housekeeping.getProperty(projectName).split(";");
        
        // project is mandatory.
        if ((projectName == null) || (projectName.isEmpty()))
        {
            System.err.println(options.getAdaptor().toString() + ": No project defined in house-keeping file.");
            System.exit(1);
        }
        if (projectDetails == null)
        {
            System.err.println(options.getAdaptor().toString() + ": No project details defined in house-keeping file.");
            System.exit(1);
        }
        
        // create the project entity.
        final Project project = (Project) addToList(new Project(projectDetails[0], projectDetails[1]));
        elements.add(project);
        
        // check that the elements are not in the table already.
        final Fact fact = new TOIFSegmentIsRelatedToProject(segment, project);
        
        // add the fact to the table.
        housekeepingElements.put(fact.hashCode(), fact);
        elements.add(fact);
        
    }
    
    /**
     * create the SegmentIsSupervisedByPerson fact and its entities.
     * 
     * @param props
     *            the properties file.
     */
    private void createSegmentIsSupervisedByPerson(Properties props) throws NullPointerException
    {
        // get the information from the properties file.
        final String personName = props.getProperty("TOIFSegmentIsSupervisedByPerson");
        final String[] personDetails = props.getProperty(personName).split(";");
        
        // create the person entity
        final Person person = (Person) addToList(new Person(personDetails[0], personDetails[1], personDetails[2]));
        
        final Fact fact = new TOIFSegmentIsSupervisedByPerson(segment, person);
        // add the fact to the table.
        housekeepingElements.put(fact.hashCode(), fact);
        
    }
    
    /**
     * get the adaptor implementation
     * 
     * @return the adaptorImpl
     */
    public AbstractAdaptor getAdaptorImpl()
    {
        return adaptorImpl;
    }
    
    /**
     * get the elements
     * 
     * @return the elements
     */
    public ArrayList<Element> getElements()
    {
        return elements;
    }
    
    /**
     * @return the houseKeeping
     */
    public Properties getHouseKeeping()
    {
        return houseKeeping;
    }
    
    /**
     * @return the housekeepingElements
     */
    public Hashtable<Integer, Element> getHousekeepingElements()
    {
        return housekeepingElements;
    }
    
    /**
     * Get the housekeeping facts and elements. ie, all the facts not relating
     * to the finding facts and elements.
     */
    void getHouseKeepingFacts() throws NullPointerException
    {
        // create the date facts and entities
        createDate();
        
        // create the adaptor facts and entities.
        createAdaptor();
        
        addSegmentDescription(houseKeeping);
        
        // create the SegmentIsRelatedToProject fact and its entities
        createSegmentIsRelatedToProject(houseKeeping);
        
        // create the SegmentIsProducedByOrganization fact and its entities
        createSegmentIsProducedByOrganization(houseKeeping);
        
        // create the SegmentIsOwnedByOrganization fact and its entities
        createSegmentIsOwnedByOrganization(houseKeeping);
        
        // create the SegmentIsSupervisedByPerson fact and its entities
        createSegmentIsSupervisedByPerson(houseKeeping);
        
        // create the SegmentIsGeneratedByPerson fact and its entities
        createSegmentIsGeneratedByPerson(houseKeeping);
        
        createSegmentIsGeneratedByGenerator(houseKeeping);
        
    }
    
    /**
     * get the house keeping values
     * 
     * @return
     */
    Properties getHousekeepingProperties(java.io.File housekeepingFile)
    {
        /*
         * the property file is where all the information about the housekeeping
         * is stored.
         */
        final Properties props = new Properties();
        
        try
        {
            if (housekeepingFile != null)
            {
                props.load(new FileInputStream(housekeepingFile));
            }
            else
            {
                // load the property file
                props.load(new FileInputStream(options.getHouseKeeping()));
            }
        }
        catch (final FileNotFoundException e)
        {
            System.err.println(options.getAdaptor().toString() + ": Could not find the house-keeping file");
            System.exit(1);
        }
        catch (final IOException e)
        {
            System.err.println("Could not read from the house-keeping file");
            System.exit(1);
        }
        return props;
    }
    
    /**
     * get the adaptor options
     * 
     * @return the options
     */
    public AdaptorOptions getOptions()
    {
        return options;
    }
    
    /**
     * create the organization fact and entities.
     */
    void getOrganizationFacts() throws NullPointerException
    {
        /*
         * go on to create the fact and elements regarding
         * OrganizationIsPartOfOrganizationAsRole
         */
        createOrganizationIsPartOfOrganizationAsRole(houseKeeping);
        
    }
    
    /**
     * create the facts and entities about the person.
     */
    void getPersonFacts() throws NullPointerException
    {
        /*
         * go on to create the fact and elements regarding
         * PersonIsEmployedByOrganizationAsRole
         */
        createPersonIsEmployedByOrganizationAsRole(houseKeeping);
        
    }
    
    /**
     * create the project facts and entities.
     */
    void getProjectFacts() throws NullPointerException
    {
        // create the PersonIsInvolvedInProjectAsRole fact and it's entities
        createPersonIsInvolvedInProjectAsRole(houseKeeping);
        
        /*
         * create the OrganizationIsInvolvedInProjectAsRole fact and it's
         * entities
         */
        createOrganizationIsInvolvedInProjectAsRole(houseKeeping);
        
    }
    
    /**
     * get the segment
     * 
     * @return the segment
     */
    public Segment getSegment()
    {
        return segment;
    }
    
    /**
     * create the tool facts and its entities.
     */
    void getToolFacts() throws NullPointerException
    {
        // create the AdaptorSupportsGenerator fact and entities.
        createAdaptorSupportsGenerator(houseKeeping);
        
        // create the AdaptorIsSuppliedByVendor fact and entities.
        createAdaptorIsSuppliedByVendor(houseKeeping);
        
        // create the GeneratorIsSuppliedByVendor fact and entities.
        createGeneratorIsSuppliedByVendor(houseKeeping);
        
    }
    
    /**
     * marshall the elements to xml.
     * 
     * @param outFile
     * @param elementList
     */
    private void marshall(java.io.File outFile, final ArrayList<Element> elementList)
    {
        segment.clearSegment();
        /*
         * sort according to the custom comparator. Facts should always be after
         * entities.
         */
        Collections.sort(elementList, new ElementComparator());
        
        /*
         * now that we have all the elements, now would be a good time to
         * normalize and increment the id's. This could have been done when the
         * element was created but would cause the id's to be all over the
         * place.
         */
        int id = 0;
        
        // give the segment it's id.
        segment.setId(id++);
        
        // for each of the elements, increment and apply its id.
        for (final Element element : elementList)
        {
            element.setId(id++);
        }
        
        JAXBContext context;
        
        try
        {
            /*
             * There must be a shorter way to create the context. You can
             * probably do it by giving it the package all these are in.
             * However, this was not working for me.
             */
            context = JAXBContext.newInstance(Segment.class, Finding.class, Project.class, Organization.class, Person.class, Generator.class,
                    Adaptor.class, Date.class, WeaknessDescription.class, FindingIsDescribedByWeaknessDescription.class, CWEIdentifier.class,
                    FindingHasCWEIdentifier.class, CodeLocation.class, CodeLocationReferencesFile.class, File.class, Directory.class,
                    FileIsContainedInDirectory.class, DirectoryIsContainedInDirectory.class, Entity.class, Element.class,
                    TOIFSegmentIsGeneratedByGenerator.class, TOIFSegmentIsProcessedByAdaptor.class, TOIFSegmentIsRelatedToProject.class,
                    TOIFSegmentIsCreatedAtDate.class, TOIFSegmentIsProducedByOrganization.class, TOIFSegmentIsOwnedByOrganization.class,
                    TOIFSegmentIsGeneratedByPerson.class, TOIFSegmentIsSupervisedByPerson.class, Role.class, PersonIsInvolvedInProjectAsRole.class,
                    OrganizationIsInvolvedInProjectAsRole.class, AdaptorSupportsGenerator.class, Vendor.class, AdaptorIsSuppliedByVendor.class,
                    GeneratorIsSuppliedByVendor.class, OrganizationIsPartOfOrganizationAsRole.class, PersonIsEmployedByOrganizationAsRole.class,
                    DataElement.class, DataElementIsInvolvedInFinding.class, Statement.class, StatementIsInvolvedInFinding.class,
                    StatementIsSinkInFinding.class, StatementHasCodeLocation.class, FindingHasCodeLocation.class, LineNumber.class, Name.class,
                    Text.class, Description.class, Address.class, EmailAddress.class, Checksum.class, SFPIdentifier.class,
                    FindingHasSFPIdentifier.class, ClusterIdentifier.class, FindingHasClusterIdentifier.class, Offset.class, Position.class,
                    DataElementIsInvolvedInStatement.class, TOIFSegmentReferencesFile.class, StatementIsProceededByStatement.class);
            
            final Marshaller m = context.createMarshaller();
            
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Add all the elements to the segment.
            for (final Element element : elementList)
            {
                segment.addElement(element);
            }
            
            /*
             * marshal the segment (and its contained elements) to the output
             * xml.
             */
            m.marshal(segment, new FileOutputStream(outFile));
            
        }
        catch (final JAXBException e)
        {
            System.err.println(options.getAdaptor().toString() + ": Failed to create XML output \n");
            System.exit(1);
        }
        catch (final FileNotFoundException e)
        {
            System.err.println(options.getAdaptor().toString() + ": Not able to write to output file " + outFile);
            System.exit(1);
        }
    }
    
    /**
     * Parse the output of the tool and generate a list of the findings.
     * 
     * @param file
     * 
     * @param inputStream
     *            - The error-stream from the running process.
     * @return - An ArrayList of the found findings.
     */
    ArrayList<Element> parse(Process process, File file)
    {
        return adaptorImpl.parse(process, options, file, validLines, options.getUnknownCWE());
    }
    
    /**
     * Run the tool. A command needs to be constructed, the same as you would if
     * you were running it from the command line.
     * 
     * @return return the process which was created by running the tool.
     */
    public Process runTool()
    {
        
        /*
         * CppCheck command. Tool executable location (taken from the options
         * provided to main), enable style error reporting, output in xml,
         * location to run the tool on.
         */
        
        final String[] command = adaptorImpl.runToolCommands(options, otherOpts);
        // final String[] command = adaptorImpl.runToolCommands(options,
        // options.getAdditionalArgs().toArray(new
        // String[options.getAdditionalArgs().size()]));
        
        if (command == null)
        {
            return null;
        }
        
        final ProcessBuilder process = new ProcessBuilder(command);
        
        if (workingDirectory != null)
        {
            process.directory(workingDirectory);
        }
        
        try
        {
            return process.start();
        }
        catch (final IOException e)
        {
            System.err.println(options.getAdaptor().toString() + ": Failed to find or run the tool.");
            System.exit(1);
        }
        
        return null;
        
    }
    
    /**
     * set the adaptor implementation
     * 
     * @param adaptorImpl
     *            the adaptorImpl to set
     */
    public void setAdaptorImpl(AbstractAdaptor adaptorImpl)
    {
        this.adaptorImpl = adaptorImpl;
    }
    
    /**
     * add all the elements to elements list.
     * 
     * @param elements
     *            the elements to set
     */
    public void addElements(ArrayList<Element> elements)
    {
        this.elements.addAll(elements);
    }
    
    /**
     * @param houseKeeping
     *            the houseKeeping to set
     */
    public void setHouseKeeping(Properties houseKeeping)
    {
        this.houseKeeping = houseKeeping;
    }
    
    /**
     * @param housekeepingElements
     *            the housekeepingElements to set
     */
    public void setHousekeepingElements(Hashtable<Integer, Element> housekeepingElements)
    {
        this.housekeepingElements = housekeepingElements;
    }
    
    /**
     * @param options
     *            the options to set
     */
    public void setOptions(AdaptorOptions options)
    {
        this.options = options;
    }
    
    /**
     * Get the options from main's arguments. Parse the arguments.
     * 
     * @param args
     *            - The arguments from main. The tool, the source location, the
     *            output location.
     */
    public void setOptions(String[] args) throws ArgumentValidationException
    {
        // create the command line interface.
        final Cli<AdaptorOptions> CLI = CliFactory.createCli(AdaptorOptions.class);
        
        // List<String> options = Arrays.asList(args);
        String[] adaptorOpts = {};
        
        int argsLimit = 8;
        
        for (String string : args)
        {
            if ("--unknownCWE".equals(string))
            {
                argsLimit++;
            }
            if ("--rename".equals(string))
            {
                argsLimit += 2;
            }
            if ("-n".equals(string))
            {
                argsLimit += 2;
            }
            
        }
        
        if (args.length >= argsLimit)
        {
            List<String> argList = Arrays.asList(args);
            adaptorOpts = argList.subList(0, argsLimit).toArray(new String[argsLimit]);
            List<String> argsublist = argList.subList(argsLimit, args.length);
            otherOpts = argsublist.toArray(new String[args.length - argsLimit]);
            for (int i = 0; i < otherOpts.length; i++)
            {
                String string = otherOpts[i];
                
                // string = "\"" + string + "\"";
                
                otherOpts[i] = string;
            }
        }
        // Collect the arguments

        options = CLI.parseArguments(adaptorOpts);

        
    }
    
    /**
     * @param segment
     *            the segment to set
     */
    public void setSegment(Segment segment)
    {
        this.segment = segment;
    }
    
}
