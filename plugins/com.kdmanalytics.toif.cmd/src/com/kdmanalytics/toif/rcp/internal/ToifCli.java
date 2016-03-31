
package com.kdmanalytics.toif.rcp.internal;

/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

import java.io.File;
import java.util.Set;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

@CommandLineInterface(application="toif")
public interface ToifCli {
  
  @Option(description = "Display TOIF version and exit")
  boolean isVersion();
  
  @Option (description = "Runs toif assimilator to merge inputfile(s)")
  boolean isMerge();
  
  @Option(shortName = "a", description = "Defines the name of the adaptor class")
  Set<String> getAdaptor();
  
  boolean isAdaptor();
  
  @Option(shortName = "v",description = "Enable verbose TOIF progress output")
  boolean isVerbose();
  
  // --------------------------------------------
  // Adaptor specific options
  // --------------------------------------------
  @Option(shortName = "i", description = "Defines the path to the toif file(s) to be merged")
  Set<File> getInputfile();
  
  boolean isInputfile();
  
  @Option(shortName = "o",description = "Directory to contain TOIF output")
  File getOutputdirectory();
  
  boolean isOutputdirectory();
  
  @Option(shortName = "H", description = "Defines the path to the file containing the facts about the project's housekeeping")
  File getHousekeeping();
  
  boolean isHousekeeping();
  
  // --------------------------------------------
  // Merge specific options
  // --------------------------------------------
//  @Option(shortName = "r")
//  File getRepository();
//  
//  boolean isRepository();
  
  @Option(shortName = "k",description = "Path to generated KDM file. Note: KDM file is zipped.")
  File getKdmfile();
  
  boolean isKdmfile();
  
//  @Option
//  String getRootname();
//  
//  boolean isRootname();
  
  @Option(shortName = "e", longName = "exec", description = "Override path to the vulnerability detection tool executable.")
  File getExecutablePath();
  
  boolean isExecutablePath();
  
  /**
   * Help message.
   * 
   * @return returns true?
   */
  @Option(description = "Display this help message and exit", helpRequest = true, shortName = "h")
  boolean getHelp();
  
}
