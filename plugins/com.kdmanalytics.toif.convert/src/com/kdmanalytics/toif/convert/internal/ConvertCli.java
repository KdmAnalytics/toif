
package com.kdmanalytics.toif.convert.internal;

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

@CommandLineInterface(application="tsvoutput")
public interface ConvertCli {
  
  @Option(description = "Display tsvoutput version and exit")
  boolean isVersion();
  
  @Option(shortName = "i", description = "Defines the path to the kdm file to be converted")
  File getInputfile();
  
  boolean isInputfile();
  
  @Option(shortName = "o",description = "Defines the path to the output file")
  File getOutputfile();
  
  boolean isOutputfile();
  
  /**
   * Help message.
   * 
   * @return returns true?
   */
  @Option(description = "Display this help message and exit", helpRequest = true, shortName = "h")
  boolean getHelp();
}
