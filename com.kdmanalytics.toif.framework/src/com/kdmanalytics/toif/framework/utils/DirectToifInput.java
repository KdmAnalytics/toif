/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.utils;

import java.io.File;
import java.util.ArrayList;

import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;

/**
 * This class provides an interface by which tools can create findings and toif
 * files without an adaptor. This aproach is a bit different to how the toif
 * adaptor works with adaptors. Usually the ToifAdaptor Tool uses the adaptors
 * as plugins -- and drives them--. In this case, this adaptor is driving the
 * toifAdaptor.
 * 
 * @author Adam Nunn
 * 
 */
public class DirectToifInput extends AbstractAdaptor
{
    
    // adaptor name
    private static final String ADAPTOR_NAME = "Direct Input";
    
    // the finding creator
    private FindingCreator creator;
    
    // the tool adaptor
    private ToolAdaptor toolAdaptor;
    
    // the outputdirectory
    private File outputDirectory;
    
    // the file this segment is describing.
    private File segmentFile;
    
    /**
     * Create a toif segment.
     * 
     * @param ouputDirectory
     *            The directory where you wish the output files to go.
     * @param housekeepingFile
     *            The housekeeping file. This has all the information about the
     *            project stored in it. It should be formatted according to the
     *            toif housekeeping file format
     * @param segmentFile
     *            The source file which is being analyzed.
     */
    public DirectToifInput(File ouputDirectory, File housekeepingFile, File segmentFile)
    {
        this.outputDirectory = ouputDirectory;
        this.segmentFile = segmentFile;
        
        // new tool adaptor
        toolAdaptor = new ToolAdaptor(null);
        // using the mock adaptor
        toolAdaptor.setAdaptorImplementation(this);
        // create the housekeeping facts
        toolAdaptor.createFacts(housekeepingFile);
        // create the facts regarding the file the segment is referencing.
        toolAdaptor.createSegmentFile(segmentFile);
        
        // create a finding creator.
        creator = new FindingCreator(null, ADAPTOR_NAME);
    }
    
    /**
     * Create a finding. For the weakness.
     * 
     * @param msg
     *            The message of the weakness description
     * @param id
     *            The Id of the weakness description. Often a shortened version
     *            of the description. It is unique to the weakness.
     * @param lineNumber
     *            The line number that the weakness was found on.
     * @param offset
     *            The offset of the weakness
     * @param position
     *            The position of the weakness
     * @param file
     *            The file that the weakness was found in.
     * @param dataElement
     *            The dataElement the weakness was found on.
     * @param cwe
     *            the CWE name. for example CWE-123.
     */
    public void createFinding(String msg, String id, Integer lineNumber, Integer offset, Integer position, File file, String dataElement, String cwe)
    {
        //needs a toif file entity as the file. create this from the java.io file.
        com.kdmanalytics.toif.framework.xmlElements.entities.File toifFile = new com.kdmanalytics.toif.framework.xmlElements.entities.File(
                file.getPath());
        
        //create the finding.
        creator.create(msg, id, lineNumber, offset, position, toifFile, dataElement, cwe);
    }
    
    /**
     * construct the xml after creating all the findings.
     */
    public void constructXml()
    {
        //add the elements from the finding creator
        toolAdaptor.addElements(creator.getElements());
        
        //construct the toif xml.
        toolAdaptor.constructXml(outputDirectory, segmentFile);
    }
    
    /**
     * Once done creating all the elements, get them.
     * 
     * @return
     */
    private ArrayList<Element> getElements()
    {
        return creator.getElements();
    }
    
    @Override
    public String getAdaptorDescription()
    {
        return "This method uses a direct input to the TOIF Adaptor.";
    }
    
    @Override
    public String getAdaptorName()
    {
        return ADAPTOR_NAME;
    }
    
    @Override
    public String getAdaptorVendorAddress()
    {
        return "3730 Richmond Rd, Suite 204, Ottawa, ON K2H 5B9";
    }
    
    @Override
    public String getAdaptorVendorDescription()
    {
        return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
    }
    
    @Override
    public String getAdaptorVendorEmail()
    {
        return "info@kdmanalytics.com";
    }
    
    @Override
    public String getAdaptorVendorName()
    {
        return "KDM Analytics";
    }
    
    @Override
    public String getAdaptorVendorPhone()
    {
        return "613-627-1011";
    }
    
    @Override
    public String getAdaptorVersion()
    {
        return "0.5";
    }
    
    @Override
    public String getGeneratorDescription()
    {
        return "Generated by the analyzer";
    }
    
    @Override
    public String getGeneratorName()
    {
        return "Analyzer";
    }
    
    @Override
    public String getGeneratorVendorAddress()
    {
        return "3730 Richmond Rd, Suite 204, Ottawa, ON K2H 5B9";
    }
    
    @Override
    public String getGeneratorVendorDescription()
    {
        return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
    }
    
    @Override
    public String getGeneratorVendorEmail()
    {
        return "info@kdmanalytics.com";
    }
    
    @Override
    public String getGeneratorVendorName()
    {
        return "KDM Analytics";
    }
    
    @Override
    public String getGeneratorVendorPhone()
    {
        return "613 627 1011";
    }
    
    @Override
    public String getGeneratorVersion()
    {
        return "0.5";
    }
    
    @Override
    public String[] runToolCommands(AdaptorOptions options, String[] otherOpts)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ArrayList<Element> parse(Process process, AdaptorOptions options, com.kdmanalytics.toif.framework.xmlElements.entities.File file)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
