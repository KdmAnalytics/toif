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
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

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
        
        ToolAdaptor toolAdaptor = new ToolAdaptor(args);
        
        // get the options provided to main.
        toolAdaptor.setOptions(args);
        
        // from options
        Class<?> adaptorClass = toolAdaptor.getAdaptorClass();
        
        toolAdaptor.setAdaptorImplementation(adaptorClass);
        
        toolAdaptor.createFacts(null);
        
        File file = toolAdaptor.createSegmentFile();
        
        // run the tool.
        final Process process = toolAdaptor.runTool();
        
        if (process == null)
        {
            System.err.println("unable to run the scan tool, or no tool needs to be run.");
        }
        
        toolAdaptor.getElementsFromParse(process, file);
        
        // construct the xml.
        toolAdaptor.constructXml();
        
    }
    
}
