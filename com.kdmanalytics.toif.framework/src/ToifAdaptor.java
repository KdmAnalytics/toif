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
import java.util.List;

import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;
import com.lexicalscope.jewel.cli.ArgumentValidationException;

/**
 * kick off the toif adaptor.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 * 
 */
public class ToifAdaptor
{
    
    /**
     * Main. Entry point for the Adaptor.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        
        ToolAdaptor toolAdaptor = new ToolAdaptor();
                
        // get the options provided to main.
        try
        {
            toolAdaptor.setOptions(args);
        }
        catch (ArgumentValidationException e)
        {
            System.err.println("Invalid arguments");
            //e.printStackTrace();
        }
        
        
        // from options
        Class<?> adaptorClass = toolAdaptor.getAdaptorClass();
        
        toolAdaptor.setAdaptorImplementation(adaptorClass);
        
        toolAdaptor.createFacts(null);
        
        File file = toolAdaptor.createSegmentFile();
        
        // run the tool.
        final java.io.File process = toolAdaptor.runTool();
        
        if (process == null)
        {
            System.err.println("unable to run the scan tool, or no tool needs to be run.");
        }
        
        toolAdaptor.getElementsFromParse(process, file);
        
        // construct the xml.
        toolAdaptor.constructXml();
        
    }
    
    /**
     * Main. Entry point for the Adaptor.
     * 
     * @param args
     */
    public static void run(AbstractAdaptor adaptor, List<String> arguments)
    {
        // spoof the adaptor option at the beginning.
        arguments.add(0, "-a");
        arguments.add(1, adaptor.getAdaptorName());
        
        
        String[] args = arguments.toArray(new String[arguments.size()]);
        
        ToolAdaptor toolAdaptor = new ToolAdaptor();
        
        // get the options provided to main.
        try
        {
            toolAdaptor.setOptions(args);
        }
        catch (ArgumentValidationException e)
        {
            System.err.println("Invalid arguments "+e.getMessage());
            // e.printStackTrace();
        }
        
        toolAdaptor.setAdaptor(adaptor);
        
        toolAdaptor.createFacts(null);
        
        File file = toolAdaptor.createSegmentFile();
        
        // run the tool.
        final java.io.File process = toolAdaptor.runTool();
        
        if (process == null)
        {
            System.err.println("unable to run the scan tool, or no tool needs to be run.");
        }
        
        toolAdaptor.getElementsFromParse(process, file);
        
        // construct the xml.
        toolAdaptor.constructXml();
        
    }
    
}
