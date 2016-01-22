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

import java.io.File;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

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
    @Option(shortName = "a", longName = "adaptor", description = "Name of the adaptor to run.")
    File getAdaptor();
    
    /**
     * class name of the adaptor to run.
     * 
     * @return
     */
    @Option(longName = "exec", description = "Override path to the vulnerability detection tool executable.")
    File getExecutablePath();
    
    boolean isExecutablePath();
    
    // /**
    // * class name of the adaptor to run.
    // *
    // * @return
    // */
    // @Option(longName = "extraPath", description = "Extra path for dependency
    // location.")
    // File getPaths();
    // boolean isPaths();
    
    /**
     * the housekeeping file to use.
     * 
     * @return
     */
    @Option(shortName = "h", longName = "housekeeping", description = "The location of the house-keeping file")
    File getHouseKeeping();
    
    @Option(shortName = "n", description = "Rename the file as this.")
    String getRename();
    
    boolean isRename();
    
    @Option
    boolean getUnknownCWE();
    
    boolean isUnknownCWE();
    
}
