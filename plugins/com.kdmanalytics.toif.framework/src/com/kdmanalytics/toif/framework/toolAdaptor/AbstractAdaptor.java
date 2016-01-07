/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////
 * ////////////////////////////////////////////////////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.toolAdaptor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.framework.files.IFileResolver;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;

/**
 * abstract class outlining the adaptor classes.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 *         
 */
public abstract class AbstractAdaptor
{
    
    public ArrayList<Element> parse(AbstractAdaptor abstractAdaptor, java.io.File process, AdaptorOptions options, IFileResolver resolver,
            boolean[] validLines, boolean unknownCWE) throws ToifException
    {
        ArrayList<Element> elements = abstractAdaptor.parse(process, options, resolver, validLines, unknownCWE);
        
        return elements;
    }
    
    /**
     * get the language this adaptor works on.
     */
    public abstract Language getLanguage();
    
    /**
     * get the name to be run from the command line.
     * 
     * @return return the string of the name of the tool to be run from the
     *         command line.
     */
    public abstract String getRuntoolName();
    
    /**
     * get the adaptor description for housekeeping
     * 
     * @return
     */
    public abstract String getAdaptorDescription();
    
    /**
     * Get the adaptor name for housekeeping
     * 
     * @return
     */
    public abstract String getAdaptorName();
    
    /**
     * get the address of the vendor0
     * 
     * @return the address of the vendor for this adaptor
     */
    public abstract String getAdaptorVendorAddress();
    
    /**
     * get the vendor's description.
     * 
     * @return the vendor's description for this adaptor
     */
    public abstract String getAdaptorVendorDescription();
    
    /**
     * get the vendors email address.
     * 
     * @return the email address for this adaptor.
     */
    public abstract String getAdaptorVendorEmail();
    
    /**
     * get the vendor's name
     * 
     * @return the name of the vendor for this adaptor
     */
    public abstract String getAdaptorVendorName();
    
    /**
     * get the vendor's phone number
     * 
     * @return the vendors phone number for this adaptor
     */
    public abstract String getAdaptorVendorPhone();
    
    /**
     * get the adaptor version for housekeeping.
     * 
     * @return the version
     */
    public String getAdaptorVersion()
    {
        return "1.8.7";
    }
    
    /**
     * get the generators description.
     * 
     * @return the description
     */
    public abstract String getGeneratorDescription();
    
    /**
     * get the generators name
     * 
     * @return the generators name.
     */
    public abstract String getGeneratorName();
    
    /**
     * get the address of the generator vendor0
     * 
     * @return the address of the generator vendor for this adaptor
     */
    public abstract String getGeneratorVendorAddress();
    
    /**
     * get the generator vendor's description.
     * 
     * @return the generator vendor's description for this adaptor
     */
    public abstract String getGeneratorVendorDescription();
    
    /**
     * get the generator vendors email address.
     * 
     * @return the email address for this adaptor.
     */
    public abstract String getGeneratorVendorEmail();
    
    /**
     * get the generator vendor's name
     * 
     * @return the name of the generator vendor for this adaptor
     */
    public abstract String getGeneratorVendorName();
    
    /**
     * get the generator vendor's phone number
     * 
     * @return the generator vendors phone number for this adaptor
     */
    public abstract String getGeneratorVendorPhone();
    
    /**
     * get the generators version
     * 
     * @return the version of the generator
     */
    public abstract String getGeneratorVersion();
    
    /**
     * Get the properties file. This needs to be given set in the
     * FindingCreator.
     * 
     * @return
     */
    public Properties getProperties()
    {
        // get the properties.
        Properties props = new Properties();
        // load the configuration into the properties
        URL url = null;
        try
        {
            props.load(getClass().getResourceAsStream("/config/" + getClass().getSimpleName() + "Configuration"));
        }
        catch (final IOException e)
        {
            System.err.println("Could not find configuration file! " + url);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.err.println("Could not find configuration file! " + url);
            e.printStackTrace();
        }
        return props;
    }
    
    /**
     * Create a specific parser for the tool. It must return an arraylist of the
     * elements. These can be generated using the FindingCreator.
     * 
     * @param adaptorImpl
     *            
     * @param file
     * @param validLines
     * @param unknownCWE
     *            
     * @return
     * @throws ToifException
     */
    public abstract ArrayList<Element> parse(java.io.File process, AdaptorOptions options, IFileResolver resolver, boolean[] validLines,
            boolean unknownCWE) throws ToifException;
            
    /**
     * construct the command to run the vulnerability detection tool.
     * 
     * @param options
     *            Options handed to the adaptor from the command line. Tool,
     *            Sources, Output.
     * @param otherOpts
     * @return The commands to run the tool as a String array.
     */
    public abstract String[] runToolCommands(AdaptorOptions options, String[] otherOpts);
    
    /**
     * does this adaptor accept -D options.
     * 
     * @return true if this adaptor accepts -D options
     */
    public abstract boolean acceptsDOptions();
    
    /**
     * does this adaptor accept -I options.
     * 
     * @return true if this adaptor accepts -I options.
     */
    public abstract boolean acceptsIOptions();
    
}
