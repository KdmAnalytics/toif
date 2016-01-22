
package com.kdmanalytics.toif.rcp.internal;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

import java.io.File;
import java.util.Set;

import com.lexicalscope.jewel.cli.Option;

public interface ToifCli {
  
  @Option
  boolean isVersion();
  
  @Option
  boolean isMerge();
  
  @Option
  Set<String> getAdaptor();
  
  boolean isAdaptor();
  
  @Option(shortName = "v")
  boolean isVerbose();
  
  // --------------------------------------------
  // Adaptor specific options
  // --------------------------------------------
  @Option(shortName = "i")
  Set<File> getInputfile();
  
  boolean isInputfile();
  
  @Option(shortName = "o")
  File getOutputdirectory();
  
  boolean isOutputdirectory();
  
  @Option
  File getHousekeeping();
  
  boolean isHousekeeping();
  
  // --------------------------------------------
  // Merge specific options
  // --------------------------------------------
  @Option(shortName = "r")
  File getRepository();
  
  boolean isRepository();
  
  @Option(shortName = "k")
  File getKdmfile();
  
  boolean isKdmfile();
  
  @Option
  String getRootname();
  
  boolean isRootname();
  
  @Option(longName = "exec", description = "Override path to the vulnerability detection tool executable.")
  File getExecutablePath();
  
  boolean isExecutablePath();
  
  /**
   * Help message.
   * 
   * @return returns true?
   */
  @Option(description = "Display this help message", helpRequest = true, shortName = "h")
  boolean getHelp();
  
}
