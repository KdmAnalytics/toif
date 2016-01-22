/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.etoif.ccr;

import java.io.IOException;

/**
 * get the cwe name from the id.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 *         
 */
public class CweToName {
  
  public CweToName() {
  
  }
  
  /**
   * returns the name of the CWE based on its ID number
   * 
   * @param cweId
   *          the ID of the CWE. This is just a string of its numerical part. IE: if the CWE was
   *          "CWE-120" the value used here would be "120".
   * @return returns the name of the CWE-Id as a string. IE: providing "120" would produce,
   *         "Null pointer dereference".
   */
  public String getCweName(String cweId) {
    
    java.util.Properties props = new java.util.Properties();
    
    try {
      props.load(getClass().getResourceAsStream("/config/CweIdToName"));
      return props.getProperty(cweId);
    } catch (IOException e) {
      System.err.println("There was an error while accessing the CWE properties file. " + e);
    }
    
    return null;
  }
  
}
