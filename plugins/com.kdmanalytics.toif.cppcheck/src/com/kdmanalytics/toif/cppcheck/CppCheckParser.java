/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.cppcheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * Create a parser to handle the specific output of the cppcheck tool
 * 
 * @author Adam Nunn
 *         
 */
public class CppCheckParser extends DefaultHandler {
  
  public FindingCreator findingCreator;
  
  public Properties props;
  
  private File file;
  
  private boolean[] validLines;
  
  /**
   * constructor for the cppcheck parser
   * 
   * @param properties
   * @param file
   * @param validLines
   * @param unknownCWE
   */
  public CppCheckParser(Properties properties, File file, String name, boolean[] validLines, boolean unknownCWE) {
    this.file = file;
    this.validLines = validLines;
    props = properties;
    findingCreator = new FindingCreator(properties, name, unknownCWE);
  }
  
  /**
   * This gets called every time there is an xml element found. The idea here is to extract the
   * required elements, such as the error id, message, line number, and file. This information
   * should be passed on to the FindingCreator which will then construct the entities in the toif
   * output.
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attrs) {
    if (attrs.getLength() == 0) {
      return;
    }
    
    // the error's message
    final String msg = attrs.getValue("msg");
    // the id of the error
    final String id = attrs.getValue("id");
    // the line number the error was found on
    Integer lineNumber = null;
    try {
      lineNumber = Integer.parseInt(attrs.getValue("line"));
    } catch (NumberFormatException e) {
      System.err.println("Unable to parse line number of finding from value: " + attrs.getValue("line"));
    }
    
    if (lineNumber == null) {
      return;
    }
    
    // if the line is not an error, skip.
    if (!"error".equals(localName)) {
      return;
    }
    
    // Parse errors shouldn't count against the code. That's just not fair.
    if (msg.contains("Can't process file")) {
      return;
    }
    
    final String dataElement = getDataElement(id, msg);
    
    // if there are valid lines
    if (validLines != null) {
      // return if the line number is greater than the array size.
      if (lineNumber >= validLines.length) {
        try {
          FindingCreator.writeToFile("Cppcheck: Not a valid line (uncompiled) " + file.getPath() + ":" + lineNumber
                                     + "\n");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return;
      }
      // return if the validity is false.
      else if (!validLines[lineNumber]) {
        try {
          FindingCreator.writeToFile("Cppcheck: Not a valid line (uncompiled) " + file.getPath() + ":" + lineNumber
                                     + "\n");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return;
      }
    }
    
    // pass the required information to the finding creator.
    findingCreator.create(msg, id, lineNumber, null, null, file, dataElement, null);
    
  }
  
  public ArrayList<Element> getElements() {
    return findingCreator.getElements();
  }
  
  /**
   * Get the dataElement's name from the configuration file.
   * 
   * @param id
   *          the error's id.
   * @param msg
   *          the error's message.
   * @return The name as a string for the dataElement.
   */
  public String getDataElement(String id, String msg) {
    // look for the property which defines where the element is.
    final String prop = props.getProperty(id + "Element");
    
    if (prop == null) {
      return null;
    }
    
    String reg = "";
    
    // choose which regex to use.
    if (prop.startsWith("#")) {
      final String text = msg.substring(msg.length() - prop.length() + 1);
      reg = ".*(?=" + text + ")";
    } else if (prop.endsWith("#")) {
      final String text = prop.split("#")[0];
      reg = "(?<=" + text + ").*";
    } else {
      final String[] text = prop.split("#");
      reg = "(?<=" + text[0] + ").*(?=" + text[1] + ")";
    }
    
    // match the pattern to the message
    final Pattern pat = Pattern.compile(reg, Pattern.DOTALL);
    final Matcher matcher = pat.matcher(msg);
    
    String name = "";
    
    // if the matcher makes a find, use this as the name
    if (matcher.find()) {
      name = matcher.group();
    } else {
      return null;
    }
    
    return name;
  }
}
