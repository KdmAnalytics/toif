
package com.kdmanalytics.toif.adaptor;

/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.framework.files.IFileResolver;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;
import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;

public class SplintAdaptor extends AbstractAdaptor {
  
  /**
   * the logger.
   */
  private static Logger LOG = Logger.getLogger(SplintAdaptor.class);
  
  private File tmpFile = null;
  
  /**
   * By default we expect the executable to be in path
   */
  private String execPath = "splint";
  
  /**
   * return this adaptors name
   */
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#getAdaptorName ()
   */
  @Override
  public String getAdaptorName() {
    return "Splint";
  }
  
  /**
   * return the adaptors description.
   */
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorDescription()
   */
  @Override
  public String getAdaptorDescription() {
    return "Splint is a tool for statically checking C programs for security vulnerabilities and common programming mistakes. With minimal effort, splint can be used as a better lint. If additional effort is invested adding annotations to programs, splint can perform stronger checks than can be done by any standard lint.";
  }
  
  /**
   * Create the commands to run the tool.
   */
  @Override
  public String[] runToolCommands(AdaptorOptions options, String[] otherOpts) {
    // this is where the output of the tool is going to be written in order
    // for us to collect it.
    
    // File tmpdir = new
    // File(options.getOutputDirectory(),options.getInputFile().getName()+"."+getGeneratorName());
    // tmpdir.mkdirs();
    tmpFile = new File(options.getOutputDirectory(), options.getInputFile().getName() + "." + getGeneratorName());
    
    // tmpFile.deleteOnExit();
    // the required commands to run the tool.
    String execPath = this.execPath;
    if (options.getExecutablePath() != null) execPath = options.getExecutablePath().getAbsolutePath();
    final String[] commands = {
                                execPath, options.getInputFile().toString(), "+csv", tmpFile.getPath(), "+csvoverwrite"
    };
    
    String[] allCommands = (String[]) ArrayUtils.addAll(commands, otherOpts);
    
    return allCommands;
  }
  
  public static void writeToFile(String sb) throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File tempFile = new File(tempDir, "toifLog");
    FileWriter fileWriter = new FileWriter(tempFile, true);
    // System.out.println(tempFile.getAbsolutePath());
    BufferedWriter bw = new BufferedWriter(fileWriter);
    bw.write(sb);
    bw.close();
  }
  
  @Override
  public ArrayList<Element> parse(File process, AdaptorOptions options, IFileResolver resolver, boolean[] validLines,
                                  boolean unknownCWE) throws ToifException {
    com.kdmanalytics.toif.framework.xmlElements.entities.File file = resolver.getDefaultFile();
    // new finding creator
    final FindingCreator creator = new FindingCreator(getProperties(), getAdaptorName(), unknownCWE);
    
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileInputStream(process));
      // read from the csv file.
      while (scanner.hasNextLine()) {
        final String csvLine = scanner.nextLine();
        
        final String[] csvElements = csvLine.split(",");
        
        // Check if line is valid for parsing
        if (csvElements.length <= 2) continue;
        
        if ("preproc".equals(csvElements[2])) {
          continue;
        }
        // the first line of these csv files have the information of
        // what the values are. We dont need this.
        if ("Warning".equals(csvElements[0])) {
          continue;
        }
        
        if (csvElements.length < 8) {
          continue;
        }
        
        // continue if not same file.
        String csvFileName = new File(csvElements[4]).getName();
        String inputFileName = new File(file.getPath()).getName();
        if (!csvFileName.endsWith(inputFileName)) {
          continue;
        }
        
        // get the weakness description
        String msg = csvElements[7];
        if (msg.startsWith("\"")) {
          msg = msg.substring(1, msg.length());
        }
        if (msg.endsWith("\"")) {
          msg = msg.substring(0, msg.length() - 1);
        }
        
        if (msg.trim().startsWith("Parse Error")) {
          try {
            writeToFile("splint: " + msg + "\n");
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          continue;
        }
        
        // get the weakness id.
        final String id = csvElements[2];
        // get the linenumber of the weakness.
        final Integer lineNumber = Integer.valueOf(csvElements[5]);
        // this tool has no offset.
        final Integer offset = null;
        // get the position of the weakness.
        final Integer position = Integer.valueOf(csvElements[6]);
        
        // if there is a dataElement, use it.
        String dataElement = null;
        if (msg.split(":").length == 2) {
          dataElement = msg.split(":")[1].trim();
        }
        
        // if there are valid lines
        if (validLines != null) {
          if (lineNumber > 0) {
            // return if the line number is greater than the array
            // size.
            if (lineNumber < validLines.length) {
              if (validLines[lineNumber]) {
                // pass the required information to the finding
                // creator.
                creator.create(StringEscapeUtils.unescapeHtml4(msg), id, lineNumber, offset, position, file,
                               dataElement, null);
              } else {
                try {
                  FindingCreator.writeToFile("Splint: Not a valid line (uncompiled) " + file.getPath() + ":"
                                             + lineNumber + "\n");
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            }
          }
          
        } else {
          // pass the required information to the finding creator.
          creator.create(StringEscapeUtils.unescapeHtml4(msg), id, lineNumber, offset, position, file, dataElement,
                         null);
        }
        
        // // pass the required information to the finding creator.
        // creator.create(StringEscapeUtils.unescapeHtml4(msg), id,
        // lineNumber, offset, position, file, dataElement, null);
        //
        //
        // // if there are valid lines
        // if (validLines != null)
        // {
        // // return if the line number is greater than the array size.
        // if (lineNumber < validLines.length)
        // {
        // if (validLines[lineNumber])
        // {
        // // create the finding.
        // creator.create(StringEscapeUtils.unescapeHtml4(msg), id,
        // lineNumber, offset, position, file, dataElement, null);
        // }
        // }
        //
        // }
        
      }
      
      // delete the file we made in the tmp directory.
      // tmpFile.delete();
    } catch (final FileNotFoundException e) {
      final String msg = "The tools output file could not be found. \"" + tmpFile.getAbsolutePath() + '"';
      LOG.error(msg, e);
      throw new ToifException(msg, e);
    } finally {
      if (scanner != null) scanner.close();
    }
    
    // return the elements from the finding creator.
    return creator.getElements();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorVendorAddress()
   */
  @Override
  public String getAdaptorVendorAddress() {
    return "3730 Richmond Rd, Suite 204, Ottawa, ON, K2H 5B9";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorVendorDescription()
   */
  @Override
  public String getAdaptorVendorDescription() {
    return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorVendorEmail()
   */
  @Override
  public String getAdaptorVendorEmail() {
    return "adam@kdmanalytics.com";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorVendorName()
   */
  @Override
  public String getAdaptorVendorName() {
    return "KDM Analytics";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getAdaptorVendorPhone()
   */
  @Override
  public String getAdaptorVendorPhone() {
    return "613-627-1011";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorDescription()
   */
  @Override
  public String getGeneratorDescription() {
    return "Splint is a tool for statically checking C programs for security vulnerabilities and coding mistakes.";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#getGeneratorName ()
   */
  @Override
  public String getGeneratorName() {
    return "splint";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorVendorAddress()
   */
  @Override
  public String getGeneratorVendorAddress() {
    return "http://www.splint.org";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor#
   * getGeneratorVendorDescription()
   */
  @Override
  public String getGeneratorVendorDescription() {
    return "University of Virginia Department of Computer Science";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorVendorEmail()
   */
  @Override
  public String getGeneratorVendorEmail() {
    return "info@splint.org";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorVendorName()
   */
  @Override
  public String getGeneratorVendorName() {
    return "David Evans";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorVendorPhone()
   */
  @Override
  public String getGeneratorVendorPhone() {
    return "";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor# getGeneratorVersion()
   */
  @Override
  public String getGeneratorVersion() {
    final String[] commands = {
                                "splint", "-help", "version"
    };
    ProcessBuilder splint = new ProcessBuilder(commands);
    try {
      Process splintInstance = splint.start();
      InputStream in = splintInstance.getErrorStream();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      
      String strLine;
      
      while ((strLine = br.readLine()) != null) {
        String[] stringArray = strLine.split(" ");
        
        if (stringArray[1].trim().equals("3.1.2")) {
          return stringArray[1].trim();
        } else {
          System.err.println(getAdaptorName() + ": Generator " + stringArray[1]
                             + " found, only version 3.1.2 has been tested");
          return stringArray[1].trim();
        }
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return "";
  }
  
  @Override
  public String getRuntoolName() {
    return "splint";
  }
  
  @Override
  public Language getLanguage() {
    return Language.C;
  }
  
  @Override
  public boolean acceptsDOptions() {
    return false;
  }
  
  @Override
  public boolean acceptsIOptions() {
    return true;
  }
  
}
