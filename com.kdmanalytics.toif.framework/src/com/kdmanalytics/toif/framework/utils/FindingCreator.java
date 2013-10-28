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

package com.kdmanalytics.toif.framework.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.kdmanalytics.toif.framework.xmlElements.entities.CWEIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.ClusterIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.DataElement;
import com.kdmanalytics.toif.framework.xmlElements.entities.Directory;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;
import com.kdmanalytics.toif.framework.xmlElements.entities.SFPIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;
import com.kdmanalytics.toif.framework.xmlElements.entities.WeaknessDescription;
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
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementHasCodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsInvolvedInFinding;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsProceededByStatement;
import com.kdmanalytics.toif.framework.xmlElements.facts.StatementIsSinkInFinding;

/**
 * Creates the finding entities and fact regarding the findings found by the
 * vulnerability detection tool.
 * 
 * @author Adam Nunn
 * 
 */
public class FindingCreator
{
    
    // the config file properties.
    Properties sfps = null;
    
    // the Hashtable of all the findings found.
    ArrayList<Element> elements = null;
    
    // the adaptor name.
    String adaptorName = null;
    
    private boolean unknownCWE;

    private Object line;

    private String sourceFile;

    private String message;
    
    /**
     * Constructor of the findingCreator.
     * 
     * @param unknownCWE
     */
    public FindingCreator(Properties props, String adaptorName, boolean unknownCWE)
    {
        sfps = props;
        elements = new ArrayList<Element>();
        this.adaptorName = adaptorName;
        this.unknownCWE = unknownCWE;
    }
    
    
    public String getSourceFile()
    {
        return sourceFile;
    }
    
    
    public Object getLine()
    {
        return line;
    }
    
    /**
     * Creates the facts and entities relating to the findings.
     * 
     * @param msg
     *            the message produced by the vulnerability detection tool.
     * @param id
     *            the id of the error.
     * @param lineNumber
     *            the line number where the error was found.
     * @param offset
     * @param position
     * @param file
     *            the file in which the error was found.
     * @param dataElement
     */
    public void create(String msg, String id, Integer lineNumber, Integer offset, Integer position, File file, String dataElement, String cwe,
            CodeLocation... traces)
    {
        sourceFile = file.getPath();
        line = lineNumber;
        message = msg;
        // create a finding from the output.
        Finding finding = new Finding();
        
        file = (File) addToElements(file);
        
        containedIn(file);
        
        boolean cont = true;
        // create a CWE for the finding
        if (cwe != null)
        {
            cont = createCwe(finding, cwe);
        }
        else
        {
            cont = createCweFromId(finding, id);
        }
        
        if (!cont)
        {
            return;
        }
        
        // add the finding to the list of elements.
        // elements.add(finding);
        addToElements(finding);
        
        // Create a weakness description for the finding
        createWeaknessDescription(finding, id + ": " + msg);
        
        // create the entities and facts relating to the error location in the
        // code.
        CodeLocation location = createCodeLocation(finding, lineNumber, offset, position, file);
        
        Statement statement = null;
        
        // creates the statements for this finding.
        statement = createStatementIsInvolvedInFinding(finding, location);
        
        // creates the dataElement for this finding.
        if (dataElement != null)
            createDataElement(dataElement, finding, statement);
        
        Statement lastStatement = statement;
        
        for (CodeLocation codeLocation : traces)
        {
            if (codeLocation == null)
            {
                continue;
            }
            
            codeLocation = (CodeLocation) addToElements(codeLocation);
            
            // create a new statement for the finding.
            Statement traceStatement = new Statement();
            elements.add(traceStatement);
            
            // add the fact relating it to the finding
            StatementIsInvolvedInFinding involved = new StatementIsInvolvedInFinding(traceStatement, finding);
            elements.add(involved);
            
            // create the fact describing the location of the statement.
            StatementHasCodeLocation hasLocation = new StatementHasCodeLocation(traceStatement, codeLocation);
            elements.add(hasLocation);
            
            // do the trace back.
            StatementIsProceededByStatement preceedingFact = new StatementIsProceededByStatement(lastStatement, traceStatement);
            elements.add(preceedingFact);
            
            lastStatement = traceStatement;
        }
    }
    
    
    
    public String getMessage()
    {
        return message;
    }
    
    /**
     * @param file
     * @return
     */
    private Element addToElements(Element element)
    {
        if (elements.contains(element))
        {
            return elements.get(elements.indexOf(element));
        }
        else
        {
            elements.add(element);
            return element;
        }
        
    }
    
    public static void writeToFile(String sb) throws IOException
    {
        java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"));
        java.io.File tempFile = new java.io.File(tempDir, "toifLog");
        FileWriter fileWriter = new FileWriter(tempFile, true);
        //System.out.println(tempFile.getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write(sb);
        bw.close();
    }
    
    /**
     * Given that we are provided with the cwe string directly, just use it to
     * create the cwe element.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param finding
     * @param cwe
     */
    boolean createCwe(Finding finding, String cwe)
    {
        // if there is a matching cwe in the list, use it.
        if (cwe != null && (cwe.startsWith("CWE-")))
        {
            
            if (unknownCWE)
            {
                if ("CWE--1".equals(cwe))
                {
                    try
                    {
                        writeToFile(adaptorName + " finding filtered from results: "+getSourceFile()+":"+getLine()+" "+getMessage()+"\n");
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return false;
                }
            }
            
            CWEIdentifier cweId = new CWEIdentifier(cwe);
            
            elements.add(cweId);
            
            Fact fact2 = new FindingHasCWEIdentifier(finding, cweId);
            // add the fact.
            elements.add(fact2);
            
        }
        return true;
        
    }
    
    /**
     * Create the elements relating to the locations of the errors in the code.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param lineNumber
     *            line where the error was found
     * @param offset
     * @param position
     * @param file
     *            the file where the error was found
     * @return
     */
    CodeLocation createCodeLocation(Finding finding, final Integer lineNumber, Integer offset, Integer position, File file)
    {
        // there must be a linenumber
        if (lineNumber == null)
        {
            return null;
        }
        
        // create the code location entity.
        CodeLocation location = new CodeLocation(lineNumber, position, offset);
        elements.add(location);
        
        // create the fact which links the location and file.
        Fact fact = new CodeLocationReferencesFile(location, file);
        elements.add(fact);
        
        // create the fact which links the finding and location.
        FindingHasCodeLocation findingLocation = new FindingHasCodeLocation(finding, location);
        elements.add(findingLocation);
        
        // return the location.
        return location;
        
    }
    
    /**
     * Create a CWE if it doesn't already exit. Also create the fact which
     * references the CWE.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param finding
     *            The finding which has to be matched to a CWE
     * @param id
     *            The id of the error.
     */
    boolean createCweFromId(Finding finding, String id)
    {
        // there must be an id.
        if (id == null)
        {
            return false;
        }
        
        // there must be a configuration file.
        if (sfps == null)
        {
            System.err.println("No configuration file found!");
            return false;
        }
        
        // get the string which represents the cluster, sfp, and cwe
        String sfpProperty = sfps.getProperty(id);
        
        // if there is no SFP property for this error id, bail!
        if ((sfpProperty == null) || (sfpProperty.isEmpty()))
        {
            try
            {
                writeToFile(adaptorName + ": No properties found for " + id+"\n");
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sfpProperty = ";SFP--1;CWE--1";
            // return;
        }
        
        // split the property string into the individual properties.
        String[] clusterSfpCwe = sfpProperty.split(";");
        
        String cwe = "CWE--1";
        String sfp = "SFP--1";
        String cluster = null;
        
        // only one element in the array has to be a sfp
        if (clusterSfpCwe.length == 1)
        {
            cluster = clusterSfpCwe[0];
        }
        // two elements means that there should be a cwe too.
        else if (clusterSfpCwe.length == 2)
        {
            cluster = clusterSfpCwe[0];
            sfp = clusterSfpCwe[1];
        }
        // we have all three.
        else if (clusterSfpCwe.length == 3)
        {
            cluster = clusterSfpCwe[0];
            sfp = clusterSfpCwe[1];
            cwe = clusterSfpCwe[2];
        }
        else
        {
            System.err.println("Missing a configuration file or not enough values for " + id
                    + ". Hence, these Elements have not been created in the toif");
            
            return false;
        }
        
        /*
         * now make the cwe elements.
         */
        CWEIdentifier cweId = null;
        // if there is a matching cwe in the list, use it.
        if (cwe != null && (cwe.startsWith("CWE-") || cwe.startsWith("KDM-")))
        {
            if (unknownCWE)
            {
                if ("CWE--1".equals(cwe))
                {
                    try
                    {
                        writeToFile(adaptorName + " finding filtered from results: "+getSourceFile()+":"+getLine()+" "+getMessage()+"\n");
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return false;
                }   
            }
            
            cweId = new CWEIdentifier(cwe);
            elements.add(cweId);
            
            Fact fact2 = new FindingHasCWEIdentifier(finding, cweId);
            // add the fact.
            elements.add(fact2);
        }
        
        SFPIdentifier sfpId = null;
        
        if (sfp != null && sfp.startsWith("SFP-"))
        {
            // if there is a matching sfp in the list, use it.
            sfpId = new SFPIdentifier(sfp);
            elements.add(sfpId);
            
            // make the fact
            Fact fact = new FindingHasSFPIdentifier(finding, sfpId);
            // add the fact.
            elements.add(fact);
        }
        
        /*
         * give the cluster the value mapped from the cluster/sfp mapping to
         * initialize it.
         */
        if (sfp != null && sfp.startsWith("SFP-"))
        {
            cluster = ClusterMapping.getCluster(sfp);
        }
        
        // if an actual cluster is defined, then use that.
        if (cluster != null)
        {
            ClusterIdentifier clusterId = new ClusterIdentifier(cluster);
            elements.add(clusterId);
            
            if (finding != null)
            {
                // make the fact
                Fact fact3 = new FindingHasClusterIdentifier(finding, clusterId);
                // add the fact.
                elements.add(fact3);
            }
        }
        
        return true;
    }
    
    /**
     * Create the dataElement.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param name
     *            The name of the Element
     * @param finding
     *            The finding the element is related to .
     * @param statement
     *            the statement the element is involved with.
     * @return
     */
    DataElement createDataElement(String name, Finding finding, Statement statement)
    {
        // create the dataElement with this name.
        DataElement dataElement = new DataElement(name);
        elements.add(dataElement);
        
        // create the fact
        Fact fact = new DataElementIsInvolvedInFinding(dataElement, finding);
        // add the fact.
        elements.add(fact);
        
        DataElementIsInvolvedInStatement involved = new DataElementIsInvolvedInStatement(dataElement, statement);
        elements.add(involved);
        
        return dataElement;
    }
    
    /**
     * Create the statement entity and its facts.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param finding
     *            The finding.
     * @param location
     *            The line number where the statement is located. Usually it is
     *            assumed that this is the same as the error location, since
     *            there is no more information to go on.
     * @param element
     * @return
     */
    Statement createStatementIsInvolvedInFinding(Finding finding, CodeLocation location)
    {
        // create a new statement for the finding.
        Statement statement = new Statement();
        elements.add(statement);
        
        // add the fact relating it to the finding
        StatementIsInvolvedInFinding involved = new StatementIsInvolvedInFinding(statement, finding);
        elements.add(involved);
        
        // assumed that the statement is the sink in finding.
        StatementIsSinkInFinding sink = new StatementIsSinkInFinding(statement, finding);
        elements.add(sink);
        
        // create the fact describing the location of the statement.
        StatementHasCodeLocation hasLocation = new StatementHasCodeLocation(statement, location);
        elements.add(hasLocation);
        
        return statement;
    }
    
    /**
     * Create a weakness description for the finding, if it doesn't already
     * exist. Create a fact pointing to the description.
     * 
     * access level modifier missing for testing purposes
     * 
     * @param finding
     *            The finding for the error.
     * @param msg
     *            The id of the error.
     */
    void createWeaknessDescription(Finding finding, String msg)
    {
        // create the entity
        WeaknessDescription weaknessDescription = new WeaknessDescription(msg);
        elements.add(weaknessDescription);
        
        // make the fact
        Fact fact = new FindingIsDescribedByWeaknessDescription(finding, weaknessDescription);
        // add the fact
        elements.add(fact);
    }
    
    /**
     * Return the findings.
     * 
     * @return - An ArrayList of the findings.
     */
    public ArrayList<Element> getElements()
    {
        return elements;
    }
    
    /**
     * set the properties.
     * 
     * @param properties
     */
    void setProperties(Properties properties)
    {
        if (properties == null)
        {
            return;
        }
        sfps = properties;
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
        directory = (Directory) addToElements(directory);
        
        Fact fact = new FileIsContainedInDirectory(file, directory);
        fact = (Fact) addToElements(fact);
        
        // find all the directories containing directories.
        while (directory.getParent() != null)
        {
            // create the entities and facts.
            Directory directory2 = new Directory(directory.getParent());
            directory2 = (Directory) addToElements(directory2);
            
            DirectoryIsContainedInDirectory directoryContainedInDirectory = new DirectoryIsContainedInDirectory(directory, directory2);
            directoryContainedInDirectory = (DirectoryIsContainedInDirectory) addToElements(directoryContainedInDirectory);
            
            directory = directory2;
        }
    }
    
}
