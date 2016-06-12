
package com.kdmanalytics.toif.convert.internal;

/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;

public class UserConsole implements IToifImportListener {
  
  private static final Logger LOG = LoggerFactory.getLogger(UserConsole.class);
  
  /**
   * Output stream
   */
  private PrintWriter out;
  
  private String args[] = null;
  
  public UserConsole() {
  }
  
  public UserConsole(String args[]) {
    this.args = args;
  }
  
  public void execute() {
    
    Cli<ConvertCli> cli = CliFactory.createCli(ConvertCli.class);
    // Check out CLI options
    ConvertCli cmdCli = null;
    try {
      if (args.length == 0) {
        System.out.println(cli.getHelpMessage());
        return;
      }
      cmdCli = CliFactory.parseArguments(ConvertCli.class, args);
    } catch (HelpRequestedException ex) {
      System.out.println(cli.getHelpMessage());
      return;
    } catch (Exception ex) {
      LOG.error("Invalid Arguments: " + ex.getMessage());
      return;
    }
    
    // Are we doing Version?
    if (cmdCli.isVersion()) doVersion(cmdCli);
    run(cmdCli);
  }
  
  private void doVersion(ConvertCli cmdCli) {
    BuildInformation bi = new BuildInformation(this);
    System.out.println("Version=" + bi.getVersion());
  }
  
  /** Do argument validation and run the conversion
   * 
   * @param cmdCli
   */
  private void run(ConvertCli cmdCli) {
    // Ensure that output directory is specifed
    if (!cmdCli.isInputfile()) {
      LOG.error("Input file needs to be specified");
      return;
    }
    
    File ifile = cmdCli.getInputfile();
    if (!ifile.exists()) {
      LOG.error("Specified input file does not exist: " + ifile.getAbsolutePath());
      return;
    }
    
    // Ensure the output directory exists
    if (!cmdCli.isOutputfile()) {
      LOG.error("Output file needs to be specified");
      return;
    }
    
    File ofile = cmdCli.getOutputfile();
    try {
      Files.createParentDirs(ofile);
    } catch (IOException ex) {
      LOG.error("Unable to create parent directory of specified output file: " + ofile.getAbsolutePath());
      return;
    }
    
    // Prepare the output
    try {
      out = new PrintWriter(new FileWriter(ofile));
      out.println("SFP\tCWE\tCiting Status\tConfidence\tResource\tLine Number\tKDM Line Number\tSCA tool\tWeakness Description");
      
      
      // Run the import. There is a callback that gets called with the findings
      ToifImporter importer = new ToifImporter();
      importer.addFindingListener(this);
      importer.run(ifile);
      
    } catch (IOException e) {
      LOG.error("Exception running conversion: " + e.getMessage());
    }
    finally {
      if (out != null) out.close();
    }
  }
  
  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.importer.IToifImportListener#add(com.kdmanalytics.toif.report.items.IFindingEntry)
   */
  @Override
  public void add(IFindingEntry finding) {
    // Exporting the data to a tsv file
    out.print(finding.getSfp());
    out.print('\t');
    out.print(finding.getCwe());
    out.print('\t');
    out.print('\t');
    out.print(finding.getTrust());
    out.print('\t');
    out.print(finding.getPath());
    out.print('\t');
    out.print(finding.getLine());
    out.print('\t');
    out.print(finding.getLine());
    out.print('\t');
    out.print(finding.getTool());
    out.print('\t');
    out.print(finding.getDescription());
    out.println();
  }
}
