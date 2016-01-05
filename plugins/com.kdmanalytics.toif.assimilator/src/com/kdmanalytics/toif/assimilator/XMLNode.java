/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class XMLNode {
  
  private static final Logger LOG = Logger.getLogger(XMLNode.class);
  
  /**
   * Init size for hashmaps, done for optimization reasons
   * 
   */
  private static final int HASHMAP_SIZE = 30;
  
  public static final String modelNSHost = "kdmanalytics.com";
  
  public static final String modelNS = "http://" + modelNSHost + "/";
  
  /**
   * simple name
   */
  private String sName;
  
  /**
   * Contains
   * 
   */
  List<XMLNode> children = null;
  
  /**
   * XML Attributes that indicate literal values
   * 
   */
  private Map<String, String> attributes = null;
  
  /**
   * Multiple references
   * 
   */
  Map<String, List<String>> references = null;
  
  private String id = null;
  
  /**
   * Map of sName to xmi:types
   * 
   */
  static Map<String, String> kdmTypes = new HashMap<String, String>(HASHMAP_SIZE);
  
  static Set<String> stringFields = new HashSet<String>();
  
  {
    kdmTypes.put("kdm:Segment", "kdm/Segment");
    kdmTypes.put("itemUnit", "code/ItemUnit");
    kdmTypes.put("indexUnit", "code/IndexUnit");
    kdmTypes.put("entryFlow", "action/EntryFlow");
    kdmTypes.put("attribute", "kdm/Attribute");
    kdmTypes.put("source", "source/SourceRef");
    kdmTypes.put("region", "source/SourceRegion");
    kdmTypes.put("parameterUnit", "code/ParameterUnit");
    kdmTypes.put("audit", "kdm/Audit");
    kdmTypes.put("abstraction", "action/ActionElement");
    kdmTypes.put("extensionFamily", "kdm/ExtensionFamily");
    kdmTypes.put("stereotype", "kdm/Stereotype");
    kdmTypes.put("tag", "kdm/TagDefinition");
    kdmTypes.put("dataElement", "data/DataEvent");
    kdmTypes.put("platformElement", "platform/PlatformElement");
    
    stringFields.add("name");
    stringFields.add("snippet");
  }
  
  public XMLNode() {
    id = "" + UniqueID.get();
  }
  
  /**
   * Parse the node information. Note that the "Attributes" here are XML Attributes, not KDM
   * attributes.
   * 
   * @param ns
   * @param sName
   * @param qName
   * @param attrs
   */
  public XMLNode(String ns, String sName, String qName, Attributes attrs) {
    
    children = new ArrayList<XMLNode>();
    this.sName = sName;
    if ("".equals(sName)) this.sName = qName; // Not namespace aware
    
    int size = attrs.getLength();
    for (int i = 0; i < size; i++) {
      String key = attrs.getLocalName(i); // Attr name
      if ("".equals(key)) key = attrs.getQName(i);
      
      // Special cases
      // Stereotype/tag "type" is an attribute, not a reference
      if ("stereotype".equals(this.sName) && "type".equals(key)) addAttribute(key, attrs.getValue(key));
      else if ("tag".equals(this.sName) && "type".equals(key)) addAttribute(key, attrs.getValue(key));
      
      // Some attributes are really references
      else if (AttributeUtilities.isReference(key)) addReference(key, attrs.getValue(key));
      
      // Unescape the fields which likely contain escaped HTML
      else if (stringFields.contains(key)) {
        String value = attrs.getValue(key);
        try {
          // value = StringEscapeUtils.unescapeHtml4(value);
          // value = StringEscapeUtils.unescapeXml(value);
          value = StringEscapeUtils.unescapeHtml3(value);
        } catch (StringIndexOutOfBoundsException e) {
          // String was most likely '&' which causes commons.lang3 to
          // throw... ignore it
          if (!value.contains("&")) {
            throw e;
          }
        }
        addAttribute(key, value);
      }
      // Normal attribute
      else {
        addAttribute(key, attrs.getValue(key));
      }
    }
    
    // Use the xmiLid if it exists
    id = getAttribute("xmi:id");
    if (id == null) id = "" + UniqueID.get();
  }
  
  /**
   * Return the index of the specified child.
   * 
   */
  public int indexOf(XMLNode node) {
    return children.indexOf(node);
  }
  
  /**
   * Return the index of the specified child, only counting nodes of the same kind ahead of it.
   * 
   */
  public int indexOfByType(XMLNode node) {
    if (node == null) return -1;
    
    int count = 0;
    String type = node.getName();
    
    for (Iterator<XMLNode> it = children.iterator(); it.hasNext();) {
      XMLNode nextNode = it.next();
      if (nextNode.equals(node)) return count;
      // If a node of the same type is found then increment the counter.
      if (nextNode.getName().equals(type)) count++;
    }
    return -1;
  }
  
  /**
   * Return the number of contained children
   * 
   * @return
   */
  public int size() {
    return children.size();
  }
  
  /**
   * Add the reference to the appropriate list
   * 
   * @param key
   * @param value
   */
  private void addReference(String key, String value) {
    if (references == null) references = new HashMap<String, List<String>>(HASHMAP_SIZE);
    if (!references.containsKey(key)) references.put(key, new ArrayList<String>());
    List<String> list = references.get(key);
    list.add(value);
  }
  
  /**
   * Get a list of the various reference types we found.
   * 
   * @return
   */
  public Set<String> getReferenceTypes() {
    if (references == null) return null;
    return references.keySet();
  }
  
  /**
   * Return the list of references of the specified type
   * 
   * @param type
   * @return
   */
  public List<String> getReferences(String type) {
    if (references == null) return null;
    return references.get(type);
  }
  
  /**
   * Add the specified attribute to the list of attributes
   * 
   * @param key
   * @param value
   */
  private void addAttribute(String key, String value) {
    if (attributes == null) attributes = new HashMap<String, String>(HASHMAP_SIZE);
    attributes.put(key, value);
  }
  
  /**
   * set the id.
   * 
   * @param id
   *          the id to set.
   */
  public void setId(Long id) {
    this.id = id.toString();
  }
  
  /**
   * get the simple name
   * 
   * @return the simple name
   */
  public String getName() {
    return sName;
  }
  
  /**
   * check if the simple name matches name
   * 
   * @param name
   *          the name to check
   * @return true if simple name matches name
   */
  public boolean isType(String name) {
    if (sName == name) return true;
    if (sName == null) return false;
    return sName.equals(name);
  }
  
  /**
   * get the type.
   * 
   * @return the simple name
   */
  public String getType() {
    return sName;
  }
  
  /**
   * get the attributes
   * 
   * @return the attributes.
   */
  public Map<String, String> getAttributes() {
    return attributes;
  }
  
  /**
   * get attribute based on key
   * 
   * @param key
   *          the key of the attribute whos value you want
   * @return the value of the attribute.
   */
  public String getAttribute(String key) {
    if (attributes == null) return null;
    return attributes.get(key);
  }
  
  /**
   * does the attribute have the value ...
   * 
   * @param key
   *          the attribute key
   * @param value
   *          the attribute value.
   * @return true if the attribute is contained.
   */
  public boolean hasAttribute(String key, String value) {
    if (attributes == null) return false;
    String myValue = attributes.get(key);
    return myValue.equals(value);
  }
  
  /**
   * Assemble the URI string for the node
   * 
   * @return
   */
  public String getURIString() {
    return modelNS + id;
  }
  
  /**
   * Return the detected KDM type
   * 
   * @return
   */
  protected String getKDMType() {
    String type = getAttribute("xmi:type");
    if (type == null) type = getAttribute("xsi:type");
    if (type != null) return type.replace(':', '/');
    
    type = kdmTypes.get(sName);
    if (type != null) return type;
    
    return "unknown";
  }
  
  /**
   * return as string.
   */
  public String toString() {
    return getStartString() + getEndString();
  }
  
  /**
   * Return the starting string. This is often used when we are echoing straight to output and do
   * not want to store the data.
   * 
   * @return
   */
  public String getStartString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    sb.append(sName);
    if (attributes != null) {
      for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext();) {
        String key = it.next();
        String value = attributes.get(key);
        sb.append(" " + key + "=\"" + value + "\"");
      }
    }
    sb.append(">");
    return sb.toString();
  }
  
  /**
   * Return the ending string. This is often used when we are echoing straight to output and do not
   * want to store the data.
   * 
   * @return
   */
  public String getEndString() {
    StringBuilder sb = new StringBuilder();
    sb.append("</");
    sb.append(sName);
    sb.append(">");
    return sb.toString();
  }
  
  /**
   * add a child
   * 
   * @param child
   *          the node to add
   */
  public void add(XMLNode child) {
    children.add(child);
  }
  
  public int hashCode() {
    return getURIString().hashCode();
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof XMLNode)) return false;
    if (o.hashCode() == hashCode()) return true;
    return false;
  }
  
  /**
   * print the children.
   * 
   * @param out
   *          the writer for output.
   */
  public void printChildrenRDF(PrintWriter out) {
    for (Iterator<XMLNode> it = children.iterator(); it.hasNext();) {
      XMLNode child = it.next();
      out.println("<kdm:contains rdf:resource=\"" + child.getURIString() + "\"/>");
    }
  }
  
  /**
   * Using the XMI path string, find the referenced node and return its URI.
   * 
   * @param token
   * @return
   */
  public String getURIByPath(String iPath) {
    // System.err.println(" * " + iPath);
    // Strip the preceding slashes.
    String path = iPath;
    while (path.startsWith("/"))
      path = path.substring(1);
      
    String token = null;
    int index = path.indexOf("/");
    if (index >= 0) {
      token = path.substring(0, index);
      path = path.substring(index + 1);
    } else {
      token = path;
      path = null;
    }
    
    index = token.indexOf(".");
    String type = null;
    // form: name.<count>
    if (index >= 0) {
      type = token.substring(0, index);
      String displacement = token.substring(index + 1);
      try {
        // index = Integer.parseInt(displacement);
        index = parseIntChecked(displacement);
      } catch (NumberFormatException ex) {
        LOG.error("Cannot parse fragment from (" + iPath + ")");
        LOG.error(ex.getLocalizedMessage(), ex);
        return null;
      }
    }
    // form: // or /0/
    else {
      if (token.equals("*")) index = 0;
      else {
        try {
          index = Integer.parseInt(token);
          LOG.warn("Cannot find node with id/path " + iPath);
        } catch (NumberFormatException ex) {}
        index = 0;
      }
    }
    
    int count = -1;
    for (Iterator<XMLNode> it = children.iterator(); it.hasNext();) {
      XMLNode node = it.next();
      if (type == null) count++;
      else if (type.equals("@" + node.getType())) count++;
      if (count == index) {
        if (path == null) return node.getURIString();
        return node.getURIByPath(path);
      }
    }
    LOG.error("could not get element by URI path " + iPath);
    return null;
  }
  
  /**
   * Parses the string argument as a signed decimal integer. Faster than Integer.parseInt since it
   * assumes radix 10
   * 
   * @param intString
   *          a String containing the int representation to be parsed
   *          
   * @return the integer value represented by the argument in decimal.
   */
  public static int parseIntChecked(final String intString) {
    // Ensure that we have
    checkNotNull(intString, "A null string cannot be parsed");
    
    // Check for a sign.
    int num = 0;
    int sign = -1;
    final int len = intString.length();
    final char ch = intString.charAt(0);
    if (ch == '-') {
      if (len == 1) {
        throw new NumberFormatException("Missing digits:  " + intString);
      }
      sign = 1;
    } else {
      final int d = ch - '0';
      if ((d < 0) || (d > 9)) {
        throw new NumberFormatException("Malformed:  " + intString);
      }
      num = -d;
    }
    
    // Build the number.
    final int max = (sign == -1) ? -Integer.MAX_VALUE : Integer.MIN_VALUE;
    final int multmax = max / 10;
    int i = 1;
    while (i < len) {
      final int d = intString.charAt(i++) - '0';
      if ((d < 0) || (d > 9)) {
        throw new NumberFormatException("Malformed:  " + intString);
      }
      if (num < multmax) {
        throw new NumberFormatException("Over/underflow:  " + intString);
      }
      num *= 10;
      if (num < (max + d)) {
        throw new NumberFormatException("Over/underflow:  " + intString);
      }
      num -= d;
    }
    
    return sign * num;
  }
}

/**
 * A class used to generate unique IDs. This is required on import into the RDF database, and is
 * VERY nice to have in the XML output as opposed to the nasty position dependent naming conventions
 * that EMF appears to use otherwise.
 * 
 * Not that I use EMF really anymore.
 * 
 */

class UniqueID {
  
  /**
   * This is the initial id number. All IDs are assigned sequentially from here.
   * 
   * Set the initial value to a known number for debugging (so numbers are consistent.
   * 
   */
  static long current = System.currentTimeMillis();
  
  /**
   * Get the next ID number in sequence.
   * 
   * @return
   */
  static public synchronized long get() {
    return current++;
  }
}
