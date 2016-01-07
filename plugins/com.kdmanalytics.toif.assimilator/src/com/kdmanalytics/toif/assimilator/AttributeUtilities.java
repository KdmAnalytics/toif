/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import java.util.HashSet;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class AttributeUtilities {
  
  static HashSet<String> notAttributes = initLookup();
  
  static HashSet<String> references = initReferenceLookup();
  
  /**
   * The elements in this set are KDM/RDF pre-defined elements and should not be listed as
   * attributes.
   * 
   * @return
   */
  private static HashSet<String> initLookup() {
    HashSet<String> lookup = new HashSet<String>();
    
    lookup.add("name");
    lookup.add("size");
    lookup.add("kind");
    lookup.add("pos");
    lookup.add("contains");
    lookup.add("snippet");
    lookup.add("language");
    lookup.add("kdmType");
    lookup.add("path");
    lookup.add("text");
    lookup.add("__index");
    lookup.add("__item");
    lookup.add("external");
    
    // Internally used by navigator
    // lookup.add("lastUID");
    // lookup.add("UID");
    
    // Names used to indicate references
    lookup.addAll(initReferenceLookup());
    return lookup;
  }
  
  /**
   * 
   * @return
   */
  private static HashSet<String> initReferenceLookup() {
    HashSet<String> lookup = new HashSet<String>();
    // Names used to indicate references
    lookup.add("stereotype");
    lookup.add("__group");
    lookup.add("implementation");
    lookup.add("__type");
    lookup.add("__groupedCode");
    lookup.add("__groupedBuild");
    lookup.add("__groupedComponent");
    lookup.add("__KDM_RELATION");
    lookup.add("type");
    lookup.add("to");
    lookup.add("from");
    lookup.add("file");
    return lookup;
  }
  
  public static boolean isReference(String value) {
    return references.contains(value);
  }
  
  public static boolean isAttribute(String value) {
    return !notAttributes.contains(value);
  }
  
  public static boolean isAttribute(URI tag, Value value) {
    if (!(value instanceof Literal)) return false;
    return !notAttributes.contains(tag.getLocalName());
  }
}
