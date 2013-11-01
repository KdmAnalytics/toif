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

import java.io.File;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;

/**
 * Define the commands which can be used.
 * 
 * @author adam
 * 
 */
@CommandLineInterface(application = "adaptor")
public interface AdaptorOptions
{
    
    /**
     * The output directory for the toif files.
     * 
     * @return
     */
    @Option(shortName = "o", description = "Where to output the toif files.")
    File getOutputDirectory();
    
    /**
     * The full path to the input files.
     * 
     * @return
     */
    @Option(shortName = "i", description = "Full path to the file or files to run the vulnerability dection tool on.")
    File getInputFile();
    
    /**
     * class name of the adaptor to run.
     * 
     * @return
     */
    @Option(shortName = "a", description = "Name of the adaptor to run.")
    File getAdaptor();
    
    /**
     * the housekeeping file to use.
     * 
     * @return
     */
    @Option(shortName = "h", description = "The location of the house-keeping file")
    File getHouseKeeping();
    
    @Option(shortName = "n", description = "Rename the file as this.")
    String getRename();
    boolean isRename();
    
    @Option
    boolean getUnknownCWE();
    boolean isUnknownCWE();
    
}
