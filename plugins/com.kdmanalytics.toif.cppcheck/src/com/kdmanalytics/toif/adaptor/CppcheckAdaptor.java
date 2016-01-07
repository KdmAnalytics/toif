/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.adaptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.kdmanalytics.toif.cppcheck.CppCheckParser;
import com.kdmanalytics.toif.framework.files.IFileResolver;
import com.kdmanalytics.toif.framework.parser.StreamGobbler;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.INiceable;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;

/**
 * Create the implementation of the adaptor.
 * 
 * @author Adam Nunn
 *         
 */
public class CppcheckAdaptor extends AbstractAdaptor implements INiceable {
  
  /**
   * By default we expect the executable to be in path
   */
  private String execPath = "cppcheck";
  
  public CppcheckAdaptor() {
  
  }
  
  /**
   * Create the command array to run the tool.
   */
  @Override
  public String[] runToolCommands(AdaptorOptions options, String[] otherOpts) {
    String execPath = this.execPath;
    if (options.isExecutablePath()) execPath = options.getExecutablePath().getAbsolutePath();
    
    // The basic commands to run the tool.
    final String[] commands = {
                                execPath, "--xml", "-q", options.getInputFile().toString()
    };
    
    /*
     * the optional arguments are inserted in to the command array before the inputfile.
     */
    List<String> commandList;
    commandList = new ArrayList<String>();
    commandList.addAll(Arrays.asList(commands));
    commandList.addAll(commands.length - 1, Arrays.asList(otherOpts));
    String[] s = commandList.toArray(new String[commandList.size()]);
    
    // return the commands.
    return s;
  }
  
  /**
   * parse the output of the tool. In this case, because cppcheck outputs xml on the error stream, I
   * am using an XMLreader and my own parser (CppParser) to generate the elements.
   */
  @Override
  public ArrayList<Element> parse(java.io.File process, AdaptorOptions options, IFileResolver resolver,
                                  boolean[] validLines, boolean unknownCWE) {
    com.kdmanalytics.toif.framework.xmlElements.entities.File file = resolver.getDefaultFile();
    InputStream inputStream;
    try {
      String path = process.getAbsolutePath();
      path = path.replace(".cppcheck", "-err.cppcheck");
      java.io.File fileerr = new java.io.File(path);
      inputStream = new FileInputStream(fileerr);
      
      Thread stderr;
      final CppCheckParser parser = new CppCheckParser(getProperties(), file, getAdaptorName(), validLines, unknownCWE);
      
      final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
      
      /*
       * The act of using a streamGobbler to consume the error stream is probably not required if
       * redirectErrorStream() had been used.
       */
      
      stderr = new Thread(new StreamGobbler(inputStream, errStream));
      
      stderr.start();
      stderr.join();
      
      // process.waitFor();
      
      final XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
      rdr.setContentHandler(parser);
      rdr.parse(fileerr.toURI().toURL().toString());
      
      return parser.getElements();
      
    } catch (final SAXException e) {
      System.err.println(getAdaptorName() + ": Error parsing file.");
    } catch (final IOException e) {
      System.err.println(getAdaptorName() + ": Error parsing file.");
    } catch (final InterruptedException e) {
      System.err.println(getAdaptorName() + ": Error parsing file.");
    }
    
    return null;
  }
  
  /**
   * Return the name of this adaptor for housekeeping
   */
  @Override
  public String getAdaptorName() {
    return "Cppcheck";
  }
  
  /**
   * return the description of the adaptor for housekeeping
   */
  @Override
  public String getAdaptorDescription() {
    return "The cppCheck adaptor";
  }
  
  /**
   * get the name of the Adaptors Vendor.
   */
  @Override
  public String getAdaptorVendorName() {
    return "KDM Analytics";
  }
  
  /**
   * get the description of the adaptor's vendor
   */
  @Override
  public String getAdaptorVendorDescription() {
    return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
  }
  
  @Override
  public String getAdaptorVendorAddress() {
    return "3730 Richmond Rd, Suite 204, Ottawa, ON, K2H 5B9";
  }
  
  /**
   * get the adaptor vendor's phone number
   */
  @Override
  public String getAdaptorVendorPhone() {
    return "613-627-1011";
  }
  
  /**
   * get the adaptor vendor's email
   */
  @Override
  public String getAdaptorVendorEmail() {
    return "adam@kdmanalytics.com";
  }
  
  /**
   * get the description of the generator.
   */
  @Override
  public String getGeneratorDescription() {
    return "Cppcheck is an analysis tool for C/C++ code.";
  }
  
  /**
   * get the generator's name
   */
  @Override
  public String getGeneratorName() {
    return "cppcheck";
  }
  
  /**
   * get the address of the generator's vendor
   */
  @Override
  public String getGeneratorVendorAddress() {
    return "http://cppcheck.sourceforge.net/";
  }
  
  /**
   * get the description of the generator's vendor
   */
  @Override
  public String getGeneratorVendorDescription() {
    return "SourceForge is a web-based source code repository.";
  }
  
  /**
   * get the generator vendor's email.
   */
  @Override
  public String getGeneratorVendorEmail() {
    return "";
  }
  
  /**
   * get the generator vendor's name
   */
  @Override
  public String getGeneratorVendorName() {
    return "sourceforge";
  }
  
  /**
   * get the generator vendor's phone number
   */
  @Override
  public String getGeneratorVendorPhone() {
    return "";
  }
  
  /**
   * get the version from the tool. To do this, the tool is executed with the version option and the
   * version number is parsed from its output.
   */
  @Override
  public String getGeneratorVersion() {
    final String[] commands = {
                                "cppcheck", "--version"
    };
    ProcessBuilder cppcheck = new ProcessBuilder(commands);
    try {
      Process cppInstance = cppcheck.start();
      InputStream in = cppInstance.getInputStream();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      
      String strLine;
      
      while ((strLine = br.readLine()) != null) {
        String[] stringArray = strLine.split(" ");
        if (stringArray[1].trim().equals("1.60")) {
          return stringArray[1].trim();
        } else {
          // give a warning that a different version was found.
          System.err.println(getAdaptorName() + ": Generator " + stringArray[1]
                             + " found, only version 1.60 has been tested");
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
    return "cppcheck";
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
