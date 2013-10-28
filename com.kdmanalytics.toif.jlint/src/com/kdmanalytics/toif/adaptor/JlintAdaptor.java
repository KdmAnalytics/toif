package com.kdmanalytics.toif.adaptor;
/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * class for the jlint adaptor.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 * 
 */
public class JlintAdaptor extends AbstractAdaptor
{
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#getAdaptorName
     * ()
     */
    @Override
    public String getAdaptorName()
    {
        return "JLint Adaptor";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorDescription()
     */
    @Override
    public String getAdaptorDescription()
    {
        return "Jlint will check your Java code and find bugs, inconsistencies and synchronization problems by doing data flow analysis and building the lock graph.";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#getAdaptorVersion
     * ()
     */
    @Override
    public String getAdaptorVersion()
    {
        return "0.5";
    }
    
    /**
     * create the List using the FindingCreator of elements.
     */
    @Override
    public ArrayList<Element> parse(Process process, AdaptorOptions options, File file, boolean[] validLines, boolean unknownCWE)
    {
        
        // new finding creator
        FindingCreator creator = new FindingCreator(getProperties(), getAdaptorName(), unknownCWE);
        
        // get the stream from the process.
        InputStream inStream = process.getInputStream();
        
        // new buffered reader from the stream.
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        
        String line = null;
        
        try
        {
            // read each line one at a time
            while ((line = br.readLine()) != null)
            {
                // the different elements are divided by a colon
                String[] elements = line.split(":");
                
                // anything of length 3, is a finding
                if (elements.length == 3)
                {
                    
                    // create the parts of a finding.
                    String msg = elements[2].trim();
                    String id = deriveId(elements[2]);
                    int lineNumber = Integer.parseInt(elements[1]);
                    // String file = elements[0];
                    
                    String dataElement = getDataElement(id, msg);
                    // create the finding using the finding creator.
                    creator.create(msg, id, lineNumber, null, null, file, dataElement, null);
                }
                
            }
            
        }
        catch (Exception e)
        {
            System.err.println(getAdaptorName() + ": Error while reading input stream from tool");
            System.exit(1);
        }
        
        return creator.getElements();
    }
    
    /**
     * Since there are no weakness ID's for jlint, we need to create our own.
     * The ID's need to be unique for all weaknesses but the same for all
     * variants of the same weakness.
     * 
     * @param string
     * @return
     */
    private String deriveId(String description)
    {
        try
        {
            Scanner scan = new Scanner(getClass().getResourceAsStream("/config/idConfig"));
            
            String line = null;
            while (scan.hasNextLine())
            {
                line = scan.nextLine();
                String[] lineParts = line.split(";");
                String pattern = lineParts[0];
                String id = lineParts[1];
                
                Pattern r = Pattern.compile(pattern);
                
                Matcher m = r.matcher(description);
                
                if (m.find())
                {
                    return id;
                    
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(getAdaptorName() + ": Could not access the idConfig file.");
            System.exit(1);
        }
        return description;
    }
    
    /**
     * Commands to run the tool. In the form of a String array.
     */
    @Override
    public String[] runToolCommands(AdaptorOptions options, String[] otherOpts)
    {
        final String[] commands = { "jlint", options.getInputFile().toString() };
        return commands;
    }
    
    /**
     * Get the dataElement's name from the configuration file.
     * 
     * @param id
     *            the error's id.
     * @param msg
     *            the error's message.
     * @return The name as a string for the dataElement.
     */
    public String getDataElement(String id, String msg)
    {
        Properties props = getProperties();
        
        if (props.getProperty(id + "Element") == null)
        {
            return null;
        }
        
        // look for the property which defines where the element is.
        final String prop = props.getProperty(id + "Element");
        
        String reg = "";
        
        // choose which regex to use.
        if (prop.startsWith("#"))
        {
            final String text = msg.substring(msg.length() - prop.length() + 1);
            reg = ".*(?=" + text + ")";
        }
        else if (prop.endsWith("#"))
        {
            final String text = prop.split("#")[0];
            reg = "(?<=" + text + ").*";
        }
        else
        {
            final String[] text = prop.split("#");
            reg = "(?<=" + text[0] + ").*(?=" + text[1] + ")";
        }
        
        // match the pattern to the message
        final Pattern pat = Pattern.compile(reg, Pattern.DOTALL);
        final Matcher matcher = pat.matcher(msg);
        
        String name = "";
        
        // if the matcher makes a find, use this as the name
        if (matcher.find())
        {
            name = matcher.group();
        }
        else
        {
            return null;
        }
        
        return name;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorVendorAddress()
     */
    @Override
    public String getAdaptorVendorAddress()
    {
        return "3730 Richmond Rd, Suite 204, Ottawa, ON, K2H 5B9";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorVendorDescription()
     */
    @Override
    public String getAdaptorVendorDescription()
    {
        return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorVendorEmail()
     */
    @Override
    public String getAdaptorVendorEmail()
    {
        return "adam@kdmanalytics.com";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorVendorName()
     */
    @Override
    public String getAdaptorVendorName()
    {
        return "KDM Analytics";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getAdaptorVendorPhone()
     */
    @Override
    public String getAdaptorVendorPhone()
    {
        return "613-627-1011";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorDescription()
     */
    @Override
    public String getGeneratorDescription()
    {
        return "Jlint will check your Java code and find bugs, inconsistencies and synchronization problems by doing data flow analysis and building the lock graph.";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#getGeneratorName
     * ()
     */
    @Override
    public String getGeneratorName()
    {
        return "jlint";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVendorAddress()
     */
    @Override
    public String getGeneratorVendorAddress()
    {
        return "http://artho.com/index.shtml";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVendorDescription()
     */
    @Override
    public String getGeneratorVendorDescription()
    {
        return "We develop tools for web pages with dynamic content of medium size";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVendorEmail()
     */
    @Override
    public String getGeneratorVendorEmail()
    {
        return "cyrille@artho.com";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVendorName()
     */
    @Override
    public String getGeneratorVendorName()
    {
        return "artho";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVendorPhone()
     */
    @Override
    public String getGeneratorVendorPhone()
    {
        return "+81.8051731892";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
     * getGeneratorVersion()
     */
    @Override
    public String getGeneratorVersion()
    {
        return "Assumed 3.0";
    }
    
    @Override
    public String getRuntoolName()
    {
        return "jlint";
    }
    
    @Override
    public String getLanguage()
    {
        return "Java";
    }
    
    @Override
    public boolean acceptsDOptions()
    {
        return false;
    }
    
    @Override
    public boolean acceptsIOptions()
    {
        return false;
    }
}
