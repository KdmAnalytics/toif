/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Open Source Initiative OSI - Open Software
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 *        
 */
public class KdmXmlHandler extends DefaultHandler {
  
  private static final String kdmNSHost = "org.omg.kdm";
  
  private static final String kdmNS = "http://" + kdmNSHost + "/";
  
  /** Logger for the KdmXmlHandler class. */
  private static final Logger LOG = Logger.getLogger(KdmXmlHandler.class);
  
  private Repository repository = null;
  
  private RepositoryConnection con = null;
  
  private XMLNode root = null;
  
  private final Map<String, String> nodeNames = new HashMap<String, String>();
  
  private final List<DelayedRelation> postLoad = new ArrayList<DelayedRelation>();
  
  private final Stack<XMLNode> nodes = new Stack<XMLNode>();
  
  private final boolean storeHierarchy = false;
  
  private final boolean debug = false;
  
  // private Long nextId = 0L;
  private long initialId;
  
  private long nextId;
  
  private long smallestBigNumber = Long.MAX_VALUE;
  
  private PrintWriter out;
  
  public KdmXmlHandler(Repository repository) {
    this.repository = repository;
  }
  
  /**
   * Construct KdmXmlHandler
   * 
   * @param initialMaxId
   *          the maximum id in the file to be parsed. Used as an initial subject id for those
   *          elements that are missing xmi:id's
   */
  public KdmXmlHandler(PrintWriter out, Repository repository, long initialMaxId) {
    this.out = out;
    this.repository = repository;
    nextId = initialMaxId + 1;
    initialId = nextId;
  }
  
  /**
   * Add the specified child to the specified parent
   * 
   * @param parent
   * @param child
   * @throws RepositoryException
   */
  protected void addChild(XMLNode parent, XMLNode child) throws RepositoryException {
    // I need the nested nodes to remember as well, for finding XMI path
    // information.
    // Only do this if we need to.
    if (storeHierarchy) {
      parent.add(child);
    }
    
    // Do not output attributes as children. They are being output as
    // direct rdf tuplets.
    // Since the node has not already been commited, the attribute is
    // added here.
    
    if ("attribute".equals(child.getName())) {
      ValueFactory f = repository.getValueFactory();
      URI key = f.createURI(kdmNS, child.getAttribute("tag"));
      Literal value = f.createLiteral(child.getAttribute("value"));
      TripleStatementWriter.addOrWrite(out, con, f.createURI(parent.getURIString()), key, value);
      return;
    }
    
    // Standard contained node
    
    ValueFactory f = repository.getValueFactory();
    URI predicate = f.createURI(kdmNS, "contains");
    
    TripleStatementWriter.addOrWrite(out, con, f.createURI(parent.getURIString()), predicate, f.createURI(child
                                                                                                               .getURIString()));
    doCommit();
    
  }
  
  /**
   * Write the XMLNode into the RDF database
   * 
   * @param source
   * @throws RepositoryException
   */
  protected void commitNode(XMLNode source) throws RepositoryException {
    // Attributes are handled differently to conserve space
    if ("attribute".equals(source.getName())) {
      return;
    }
    
    // This is a standard node, not an attribute.
    setRDFAttribute(source, "kdmType", source.getKDMType());
    
    Map<String, String> attrs = source.getAttributes();
    if (attrs != null) {
      for (Iterator<String> it = attrs.keySet().iterator(); it.hasNext();) {
        String key = it.next();
        // Ignore the special xmi values
        if (key.contains(":")) {
          continue;
        }
        String value = attrs.get(key);
        
        setRDFAttribute(source, key, value);
      }
    }
    
    // Add the references
    Set<String> referenceTypes = source.getReferenceTypes();
    if (referenceTypes != null) {
      for (Iterator<String> it = referenceTypes.iterator(); it.hasNext();) {
        String key = it.next();
        List<String> references = source.getReferences(key);
        for (Iterator<String> rit = references.iterator(); rit.hasNext();) {
          String ref = rit.next();
          ValueFactory f = repository.getValueFactory();
          URI subject = f.createURI(source.getURIString());
          URI predicate = f.createURI(kdmNS, key);
          
          postLoad.add(new DelayedRelation(subject, predicate, ref));
        }
      }
      // con.commit();
    }
  }
  
  /**
   * do the commit.
   * 
   * @throws RepositoryException
   */
  private void doCommit() throws RepositoryException {
    LOG.debug("commiting...");
    con.commit();
  }
  
  /**
   * Element ended. Pop the XMLNode off of the stack.
   * 
   */
  @Override
  public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    XMLNode node = nodes.pop();
    
    // Some nodes are represented by a special compressed format,
    // add these to the repository in a special way.
    // These nodes (and their children) are prevented from writing
    // in the conventional way in the "startElement" method.
    //
    // file;startline:startpos-endline:endpos;language;path|snippet|language
    if ("source/SourceRef".equals(node.getKDMType())) {
      List<XMLNode> regions = node.children;
      StringBuilder sb = new StringBuilder();
      for (XMLNode region : regions) {
        String type = region.getKDMType();
        if (!"source/SourceRegion".equals(type)) {
          continue;
        }
        List<String> file = region.getReferences("file");
        String startLine = region.getAttribute("startLine");
        String startPos = region.getAttribute("startPos");
        String endLine = region.getAttribute("endLine");
        String endPos = region.getAttribute("endPos");
        String language = region.getAttribute("language");
        String path = region.getAttribute("path");
        if (file != null && file.size() > 0) {
          sb.append(file.get(0));
        }
        sb.append(";");
        if (startLine != null) {
          sb.append(startLine);
          if (startPos != null) {
            sb.append(":").append(startPos);
          }
          if (endLine != null) {
            sb.append("-").append(endLine);
            if (endPos != null) {
              sb.append(":").append(endPos);
            }
          }
        }
        sb.append(";");
        if (language != null) {
          sb.append(language);
        }
        sb.append(";");
        if (path != null) {
          sb.append(path);
        }
      }
      String snippet = node.getAttribute("snippet");
      String language = node.getAttribute("language");
      sb.append("|");
      if (snippet != null) {
        sb.append(snippet.replaceAll(",", "&comma;").replaceAll("\\|", "&pipe;"));
      }
      sb.append("|");
      if (language != null) {
        sb.append(language);
      }
      
      if (!nodes.isEmpty()) {
        XMLNode parent = nodes.peek();
        try {
          setRDFAttribute(parent, "SourceRef", sb.toString());
        } catch (RepositoryException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  /**
   * get the next id
   * 
   * @return the next id
   */
  public Long getNextId() {
    return nextId;
  }
  
  /**
   * get the smallest available number at the end of the long scale.
   * 
   * @return the smallest number available at the end of the long scale.
   */
  public long getSmallestBigNumber() {
    return smallestBigNumber;
  }
  
  /**
   * Commit postload data
   * 
   */
  public void postLoad() throws RepositoryException {
    try {
      con = repository.getConnection();
      
      // Commit postLoad data
      for (Iterator<DelayedRelation> it = postLoad.iterator(); it.hasNext();) {
        DelayedRelation rel = it.next();
        
        rel.commit(out, repository, con, root, nodeNames);
        doCommit();
      }
    } finally {
      con.commit();
      con.close();
    }
    
  }
  
  /**
   * Add the specified RDF tuple
   * 
   * @param key
   *          attribute key
   * @param value
   *          attribute value
   */
  public void setRDFAttribute(XMLNode source, String name, String value) throws RepositoryException {
    
    ValueFactory f = repository.getValueFactory();
    Literal literal = f.createLiteral(value);
    URI predicate = f.createURI(kdmNS, name);
    
    if (con == null) {
      con = repository.getConnection();
    }
    
    if (debug) {
      System.err.println("==========================================================");
      System.err.println("the connection is: " + con);
      System.err.println("the source is: " + source);
      System.err.println("the predicate is: " + predicate);
      System.err.println("the literal is: " + literal);
      System.err.println("==========================================================");
    }
    
    TripleStatementWriter.addOrWrite(out, con, f.createURI(source.getURIString()), predicate, literal);
    doCommit();
  }
  
  /**
   * Beginning of the document.
   * 
   */
  @Override
  public void startDocument() throws SAXException {
  }
  
  /**
   * New element found.
   * 
   */
  @Override
  public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
    
    XMLNode node = new XMLNode(namespaceURI, sName, qName, attrs);
    String stringId = node.getAttribute("xmi:id");
    
    if (stringId == null) {
      node.setId(++nextId);
    } else {
      try {
        long id = Long.parseLong(stringId);
        if (id > initialId) {
          throw new SAXException("ID Overlap:" + id);
        }
      } catch (NumberFormatException e) {
        throw new SAXException(e);
      }
    }
    
    // else {
    // try {
    // long id = Long.parseLong(stringId);
    // if (id > nextId) {
    // nextId = id;
    // } else if (id == nextId) {
    // ++nextId;
    // }
    // } catch (NumberFormatException e) {
    // throw new SAXException(e);
    // }
    // }
    
    if ("source/SourceRef".equals(node.getKDMType()) || "source/SourceRegion".equals(node.getKDMType())) {
      // These elements are represented in the repository in
      // a special "compressed" format and should not be output
      // using the conventional methods.
      if (!nodes.isEmpty()) {
        XMLNode parent = nodes.lastElement();
        parent.add(node);
      }
      nodes.push(node);
      return;
    }
    
    // KDM Type needs to be output before any more information about the
    // node.
    try {
      commitNode(node);
    } catch (RepositoryException ex) {
      throw new SAXException(ex);
    }
    
    // Process the node
    XMLNode parent = null;
    if (!nodes.isEmpty()) {
      parent = nodes.lastElement();
    }
    nodes.push(node);
    
    try {
      if (parent != null) {
        addChild(parent, node);
      }
    } catch (RepositoryException ex) {
      throw new SAXException(ex);
    }
    
    // Remember IDs for postload and non xmi:id files (barf)
    if (stringId != null) {
      String uri = node.getURIString();
      nodeNames.put(stringId, uri);
    }
  }
  
  /**
   * End of the document. Write all postLoad elements
   * 
   */
  public void stopDocument() throws SAXException {
    // Nothing to do
  }
  
}
