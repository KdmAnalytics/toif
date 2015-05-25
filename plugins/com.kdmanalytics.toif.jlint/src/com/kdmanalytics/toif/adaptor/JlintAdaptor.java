
package com.kdmanalytics.toif.adaptor;

/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;
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

/**
 * the logger.
 */
private static Logger LOG = Logger.getLogger(JlintAdaptor.class);
    
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
        return "JLint";
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
    
    /**
     * create the List using the FindingCreator of elements.
     * @throws ToifException 
     */
    @Override
    public ArrayList<Element> parse(java.io.File process, AdaptorOptions options, File file, boolean[] validLines, boolean unknownCWE) throws ToifException
    {
        InputStream inStream = null;
        BufferedReader br = null;
        String line = null;
        try
        
        {
            // new finding creator
            FindingCreator creator = new FindingCreator(getProperties(), getAdaptorName(), unknownCWE);
            
            // get the stream from the process.
            inStream = new FileInputStream(process);
            
            // new buffered reader from the stream.
            br = new BufferedReader(new InputStreamReader(inStream));
            
            
            
            // read each line one at a time
            while ((line = br.readLine()) != null)
            {
                    
            	//get the colon out the way of the windows side.
            	line = line.replaceFirst(":\\\\", "#");
            	
                // the different elements are divided by a colon
                String[] elements = line.split(":",3);
                
                
                
                // anything of length 3, is a finding
                if (elements.length == 3)
                {
                    
                    // create the parts of a finding.
                    String msg = line.split(": ", 2)[1].trim();
                    
                    //continue if the message is the verification message.
                    if (msg.startsWith("Verification completed")) {
                    	continue;
                    }
                    
                    String id = deriveId(msg);
                    int lineNumber = Integer.parseInt(elements[1]);
                    // String file = elements[0];
                    
                    String dataElement = getDataElement(id, msg);
                    // create the finding using the finding creator.
                    creator.create(msg, id, lineNumber, null, null, file, dataElement, null);
                }
                
            }
            // close resources
            br.close();
            br = null;
            inStream.close();
            inStream = null;
            return creator.getElements();
            
        }
        catch (Exception e)
        {
            final String msg = getAdaptorName() + ": Error while reading input stream from tool: file=" + process.getAbsolutePath() + " line=" +line;
            LOG.error( msg, e);
            throw new ToifException( msg, e );
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();
                
                if (inStream != null)
                    inStream.close();
                
            }
            catch (Exception e)
            {
                LOG.error(getAdaptorName() + ": Unable to close stream", e);
            }
        }
  
    }
    
    /**
     * Since there are no weakness ID's for jlint, we need to create our own.
     * The ID's need to be unique for all weaknesses but the same for all
     * variants of the same weakness.
     * 
     * @param string
     * @return
     * @throws ToifException 
     */
    protected String deriveId(String description) throws ToifException
    {
        
        Scanner scan = null;
        try
        
        {
            scan = new Scanner(getClass().getResourceAsStream("/config/idConfig"));
            
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
                    scan.close();
                    scan = null;
                    return id;
                }
            }
            
            // free resources
            scan.close();
            scan = null;
        }
        catch (Exception e)
        {
            final String msg = getAdaptorName() + ": Could not access the idConfig file.";
            LOG.error( msg , e);
            throw new ToifException( msg, e);
        }
        finally
        {
            try
            {
                if (scan != null)
                    scan.close();
            }
            catch (Exception e)
            {
                System.err.println(getAdaptorName() + ": Unable to close scanner.");
            }
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
        return "1956 Robertson Road, Suite 204, Ottawa, ON, K2H 5B9";
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
        return "info@kdmanalytics.com";
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
        return "1-613-627-1010";
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
        return "http://jlint.sourceforge.net/";
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
        return "c.artho@aist.go.jp";
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
        return "Cyrille Artho";
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
        return "+81 (0)6 6494 7813";
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
        return "Assumed 3.0.0";
    }
    
    @Override
    public String getRuntoolName()
    {
        return "jlint";
    }
    
    @Override
    public Language getLanguage()
    {
        return Language.JAVA;
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
