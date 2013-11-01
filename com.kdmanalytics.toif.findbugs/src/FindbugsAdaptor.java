/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.kdmanalytics.toif.framework.parser.StreamGobbler;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * An example of how the findbugs adaptor could be made.
 * 
 * @author adam
 * 
 */
public class FindbugsAdaptor extends AbstractAdaptor
{
    
    /**
     * the name of the adaptor
     */
    @Override
    public String getAdaptorName()
    {
        return "FindBugs Adaptor";
    }
    
    /**
     * the description of the adaptor
     */
    @Override
    public String getAdaptorDescription()
    {
        return "Find Bugs in Java Programs";
    }
    
    /**
     * the adaptor version
     */
    @Override
    public String getAdaptorVersion()
    {
        return "0.5";
    }
    
    /**
     * the xml produced form the tool is parsed by a sax parser and our own
     * content handler.
     */
    @Override
    public ArrayList<Element> parse(Process process, AdaptorOptions options, File file)
    {
        final InputStream inputStream = process.getInputStream();
        Thread stderr;
        final FindBugsParser parser = new FindBugsParser(getProperties(), file, getAdaptorName());
        
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        
        /*
         * The two streams could probably be merged with redirectErrorStream(),
         * that was we would only have to deal with one stream.
         */
        try
        {
            stderr = new Thread(new StreamGobbler(inputStream, errStream));
            
            stderr.start();
            stderr.join();
            
            final byte[] data = errStream.toByteArray();
            final ByteArrayInputStream in = new ByteArrayInputStream(data);
            
            final XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            rdr.setContentHandler(parser);
            rdr.parse(new InputSource(in));
            
            // return the elements gathered during the parse.
            return parser.getElements();
            
        }
        catch (final SAXException e)
        {
            e.printStackTrace();
            System.err.println(getAdaptorName()
                    + ": Possibly the file the tool is run against is too large, the wrong kind of file, or not just one file.");
            System.exit(1);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        
        return null;
    }
    
    /**
     * get the commands required to run the tool. Unfortunately findbugs is a
     * bat under windows. This requires that the cmd.exe be called to execute
     * it. This means that we have to determine what system the adaptor is
     * running on.
     */
    @Override
    public String[] runToolCommands(AdaptorOptions options, String[] otherOpts)
    {
        // if the system is linux, findugs can b eexecuted on its own.
        if ("Linux".equals(System.getProperty("os.name")))
        {
            // the basic command to run the tool.
            final String[] commands = { "findbugs", "-xml", options.getInputFile().toString() };
            
            // inserting the optional arguments into that array.
            List<String> commandList;
            commandList = new ArrayList<String>();
            commandList.addAll(Arrays.asList(commands));
            commandList.addAll(commands.length - 1, Arrays.asList(otherOpts));
            String[] s = commandList.toArray(new String[commandList.size()]);
            
            // return the commands.
            return s;
        }
        /*
         * if the system is windows then findbugs must be run within the
         * cmd.exe.
         */
        else
        {
            // the basic commands to run the tool
            final String[] commands = { "cmd.exe", "/C", "findbugs.bat", "-textui", "-xml", options.getInputFile().toString() };
            
            // inserting the optional arguments into the commands array.
            List<String> commandList;
            commandList = new ArrayList<String>();
            commandList.addAll(Arrays.asList(commands));
            commandList.addAll(commands.length - 1, Arrays.asList(otherOpts));
            String[] s = commandList.toArray(new String[commandList.size()]);
            
            // return the commands.
            return s;
        }
        
    }
    
    /**
     * get the address of the adaptor's vendor.
     */
    @Override
    public String getAdaptorVendorAddress()
    {
        return "3730 Richmond Rd, Suite 204, Ottawa, ON, K2H 5B9";
    }
    
    /**
     * get the description of the adaptor's vendor.
     */
    @Override
    public String getAdaptorVendorDescription()
    {
        return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
    }
    
    /**
     * get the email address of the adaptor's vendor.
     */
    @Override
    public String getAdaptorVendorEmail()
    {
        return "adam@kdmanalytics.com";
    }
    
    /**
     * get the name of the adaptor's vendor.
     */
    @Override
    public String getAdaptorVendorName()
    {
        return "KDM Analytics";
    }
    
    /**
     * get the phone number of the adaptor's vendor.
     */
    @Override
    public String getAdaptorVendorPhone()
    {
        return "613-627-1011";
    }
    
    /**
     * get the description of the generator
     */
    @Override
    public String getGeneratorDescription()
    {
        return "Static code analysis tool that analyses Java bytecode and detects a wide range of problems.";
    }
    
    /**
     * get the name of the generator
     */
    @Override
    public String getGeneratorName()
    {
        return "findbugs";
    }
    
    /**
     * get the address of the vendor.
     */
    @Override
    public String getGeneratorVendorAddress()
    {
        return "http://findbugs.sourceforge.net/";
    }
    
    /**
     * get the vendors' description
     */
    @Override
    public String getGeneratorVendorDescription()
    {
        return "SourceForge is a web-based source code repository.";
    }
    
    /**
     * get email address of vendors email
     */
    @Override
    public String getGeneratorVendorEmail()
    {
        return "findbugs@cs.umd.edu";
    }
    
    /**
     * get name of generator's vendor
     */
    @Override
    public String getGeneratorVendorName()
    {
        return "sourceforge";
    }
    
    /**
     * get the phone number of the generator's vendor
     */
    @Override
    public String getGeneratorVendorPhone()
    {
        return "";
    }
    
    /**
     * get the generator's version. This is done by calling the generator with
     * its version options and parsing that output. Again to execute findbugs on
     * windows, it has to be run from within the cmd.exe shell
     */
    @Override
    public String getGeneratorVersion()
    {
        ProcessBuilder findbugs = null;
        
        // if the system is linux, run normally
        if ("Linux".equals(System.getProperty("os.name")))
        {
            String[] commands = { "findbugs", "-version" };
            findbugs = new ProcessBuilder(commands);
        }
        
        // if the system is windows, run the tool inside cmd.exe
        else
        {
            String[] commands = { "cmd.exe", "/C", "findbugs.bat", "-textui", "-version" };
            findbugs = new ProcessBuilder(commands);
        }
        
        // parse the output
        try
        {
            Process findbugsInstance = findbugs.start();
            InputStream in = findbugsInstance.getInputStream();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String strLine;
            
            while ((strLine = br.readLine()) != null)
            {
                if (strLine.trim().equals("1.3.9"))
                {
                    return strLine.trim();
                }
                else
                {
                    //give a warning if the versions do not match.
                    System.err.println(getAdaptorName() + ": Generator " + strLine + " found, only version 1.3.9 has been tested");
                    return strLine.trim();
                }
            }
            
        }
        catch (Exception e)
        {
            System.err.println("Could not run program to gather generator version.");
        }
        
        return "";
    }
}
