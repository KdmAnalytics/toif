
package com.kdmanalytics.toif.adaptor;

/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.kdmanalytics.toif.framework.files.IFileResolver;
import com.kdmanalytics.toif.framework.parser.StreamGobbler;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.rats.RatsParser;

/**
 * the Rough audit tool for security adaptor.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 *         
 */
public class RatsAdaptor extends AbstractAdaptor {
  
  /**
   * By default we expect the executable to be in path
   */
  private String execPath = "rats";
  
  @Override
  public String getAdaptorName() {
    return "Rough Audit Tool for Security";
  }
  
  @Override
  public String getAdaptorDescription() {
    return "The Rough Audit Tool Adaptor";
  }
  
  @Override
  public String[] runToolCommands(AdaptorOptions options, String[] otherOpts) {
    String execPath = this.execPath;
    if (options.getExecutablePath() != null) execPath = options.getExecutablePath().getAbsolutePath();
    
    List<String> commands = new LinkedList<String>();
    commands.add(execPath);
    commands.add("--xml");
    commands.add("--quiet");
    commands.add(options.getInputFile().toString());
    if (otherOpts != null) {
      for (String opt : otherOpts) {
        commands.add(opt);
      }
    }
    
    return commands.toArray(new String[commands.size()]);
  }
  
  /**
   * parse the output of the rats tool
   */
  @Override
  public ArrayList<Element> parse(java.io.File process, AdaptorOptions options, IFileResolver resolver,
                                  boolean[] validLines, boolean unknownCWE) {
    com.kdmanalytics.toif.framework.xmlElements.entities.File file = resolver.getDefaultFile();
    try {
      final InputStream inputStream = new FileInputStream(process);
      Thread stderr;
      final RatsParser parser = new RatsParser(getProperties(), file, getAdaptorName(), validLines, unknownCWE);
      
      final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
      
      stderr = new Thread(new StreamGobbler(inputStream, errStream));
      
      stderr.start();
      stderr.join();
      
      // process.waitFor();
      
      final byte[] data = errStream.toByteArray();
      if (data.length > 10) {
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        
        final XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(parser);
        rdr.parse(new InputSource(in));
        
        return parser.getElements();
      }
      
      return new ArrayList<Element>();
    } catch (final SAXException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  @Override
  public String getAdaptorVendorAddress() {
    return "3730 Richmond Rd, Suite 204, Ottawa, ON, K2H 5B9";
  }
  
  @Override
  public String getAdaptorVendorDescription() {
    return "KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.";
  }
  
  @Override
  public String getAdaptorVendorEmail() {
    return "adam@kdmanalytics.com";
  }
  
  @Override
  public String getAdaptorVendorName() {
    return "KDM Analytics";
  }
  
  @Override
  public String getAdaptorVendorPhone() {
    return "613-627-1011";
  }
  
  @Override
  public String getGeneratorDescription() {
    return "RATS is a tool for scanning C, C++, Perl, PHP and Python source code and flagging common security related programming errors such as buffer overflows and TOCTOU (Time Of Check, Time Of Use) race conditions.";
  }
  
  @Override
  public String getGeneratorName() {
    return "RATS - Rough Auditing Tool for Security";
  }
  
  @Override
  public String getGeneratorVendorAddress() {
    return "2215 Bridgepointe Pkwy, Suite 400, San Mateo, CA, 94404";
  }
  
  @Override
  public String getGeneratorVendorDescription() {
    return "Fortify's Software Security Assurance products and services protect companies from the threats posed by security flaws in business-critical software applications.";
  }
  
  @Override
  public String getGeneratorVendorEmail() {
    return "contact@fortify.com";
  }
  
  @Override
  public String getGeneratorVendorName() {
    return "Fortify";
  }
  
  @Override
  public String getGeneratorVendorPhone() {
    return "650-358-5600";
  }
  
  @Override
  public String getGeneratorVersion() {
    final String[] commands = {
                                "rats", "-help"
    };
    ProcessBuilder rats = new ProcessBuilder(commands);
    try {
      Process ratsInstance = rats.start();
      InputStream in = ratsInstance.getInputStream();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      
      String strLine;
      
      while ((strLine = br.readLine()) != null) {
        String[] stringArray = strLine.split(" ");
        if (stringArray[1].trim().equals("v2.3")) {
          return stringArray[1].trim();
        } else {
          System.err.println(getAdaptorName() + ": Generator " + stringArray[1]
                             + " found, only version v2.3 has been tested");
          return stringArray[1].trim();
        }
      }
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return "";
  }
  
  @Override
  public String getRuntoolName() {
    return "rats";
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
    return false;
  }
  
}
