/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.mergers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kdmanalytics.toif.assimilator.TripleStatementWriter;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.SeenStatement;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.ToifRdfBnode;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.ToifRdfFile;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.ToifRdfResource;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.ToifRdfValue;
import com.kdmanalytics.toif.assimilator.toifRdfTypes.ToifStatement;

/**
 * Class used to merge the toif data together. Merging integrates multiple equivalent elements into
 * one single element.
 * 
 * @author adam
 *         
 */
public class ToifMerger {
  
  private static final String HTTP_TOIF = "http://toif/";
  
  private static final boolean DEBUG = false;
  
  /**
   * The logger for this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ToifMerger.class);
  
  private File currentFile;
  
  private Document doc;
  
  /**
   * factory to create new objects for the repository.
   */
  private ValueFactory factory;
  
  /**
   * whether to continue the merge when a file fails.
   */
  private final boolean force;
  
  /**
   * This is a map of ids and their values as a string. This should enable us to find equivalent
   * elements an use their ids.
   */
  private final HashMap<ToifRdfResource, Long> globalResources;
  
  private HashMap<Long, ToifRdfResource> localResources;
  
  /**
   * The offset makes sure that the next file's IDs continue where the previous file left off. It is
   * incremented every time a new toif element is created.
   */
  private Long offset;
  
  /**
   * the repository in which to output the completed statements.
   */
  private final Repository repository;
  
  /**
   * list of all the statements before being deposited.
   */
  private List<ToifStatement> statements;
  
  /**
   * the method of output.
   */
  private final PrintWriter output;
  
  /**
   * the connection to the repository.
   */
  private RepositoryConnection con;
  
  private String currentToolName;
  
  private final HashMap<String, ToifRdfResource> segments;
  
  private Element currentSegment;
  
  private int noName;
  
  /**
   * the lowest available number in the long scale when counting from the end. this is used for
   * bnodes.
   */
  private Long smallestBigNumber = Long.MAX_VALUE;
  
  /**
   * the statements that have been seen.
   */
  private Set<SeenStatement> seenStatements = new HashSet<SeenStatement>();
  
  /**
   * the map of the path elements that are to be removed.
   */
  private String blacklist;
  
  /**
   * mapping of the local statement to a global statement
   */
  private StatementMapping statementMapping;
  
  /**
   * creates a new toif merger. outputs to the output-printwriter as a stream.
   * 
   * @param output
   *          the output printwriter for the output stream.
   * @param force
   *          force the continuation of the parse, even if there is a failure on one of the files.
   * @param startID
   * @param smallestBigNumber2
   */
  public ToifMerger(PrintWriter output, boolean force, Long startID, Long smallestBigNumber2, String blacklistPath) {
    statementMapping = new StatementMapping();
    blacklist = blacklistPath;
    
    // farce the parse to continue even on a failed file?
    this.force = force;
    
    this.output = output;
    this.smallestBigNumber = smallestBigNumber2;
    
    // initialize the elements map and the types map.
    globalResources = new HashMap<ToifRdfResource, Long>();
    
    segments = new HashMap<String, ToifRdfResource>();
    
    // make sure that the offset is set
    offset = startID;
    
    repository = new SailRepository(new MemoryStore());
    
  }
  
  /**
   * get the repository
   * 
   * @return returns the repository.
   */
  public Repository getRepository() {
    return repository;
  }
  
  /**
   * create a complext fact and add it to the repository.
   * 
   * @param fact
   *          the fact we are using to create the statements
   * @param doc
   *          the doc that the fact belongs to .
   * @param predicate
   *          the initial prediacte for the first statement, this is probably the type.
   * @param statement1
   *          the attribute name for the first element
   * @param statement2
   *          the attribute name for the second element
   * @param statement3
   *          the attribute name for the third element
   */
  private void createComplexFact(Element fact, Document doc, String predicate, String statement1, String statement2,
                                 String statement3) {
                                 
    /*
     * create the initial statement. this is the one that sets up the blank node as the main subject
     * of the fact.
     */
    ToifRdfResource subject = createResource(statement1, doc, fact);
    createElementFacts(subject);
    
    URI predicateURI = factory.createURI(HTTP_TOIF + predicate);
    
    ToifRdfBnode bnode = new ToifRdfBnode(factory.createBNode(), currentFile);
    
    ToifStatement toifStatement1 = new ToifStatement(subject, predicateURI, bnode);
    statements.add(toifStatement1);
    
    // create statement2
    ToifRdfResource object1 = createResource(statement2, doc, fact);
    createElementFacts(object1);
    URI predicateURI2 = factory.createURI(HTTP_TOIF + trimNumber(statement2));
    
    ToifStatement toifStatement2 = new ToifStatement(bnode, predicateURI2, object1);
    statements.add(toifStatement2);
    
    // create statement3
    ToifRdfResource object2 = createResource(statement3, doc, fact);
    createElementFacts(object2);
    URI predicateURI3 = factory.createURI(HTTP_TOIF + trimNumber(statement3));
    
    ToifStatement toifStatement3 = new ToifStatement(bnode, predicateURI3, object2);
    statements.add(toifStatement3);
  }
  
  /**
   * I consider facts with references to more then 2 elements complex. These need to use a blank
   * node.
   * 
   * @param doc
   *          the document that contains this fact.
   * @param fact
   *          the fact that we wish to analyse.
   */
  private void createComplexFacts(Document doc, Element fact) {
    final Node predicateNode = fact.getAttributeNode("xsi:type");
    String predicate = null;
    
    if (predicateNode != null) {
      predicate = predicateNode.getTextContent();
      predicate = trimNumber(predicate);
    } else {
      return;
    }
    
    if ("toif:OrganizationIsPartOfOrganizationAsRole".equals(predicate)) {
      createComplexFact(fact, doc, predicate, "organization1", "organization2", "role");
      
    }
    if ("toif:PersonIsEmployedByOrganizationAsRole".equals(predicate)) {
      createComplexFact(fact, doc, predicate, "person", "organization", "role");
      
    }
    if ("toif:PersonIsInvolvedInProjectAsRole".equals(predicate)) {
      createComplexFact(fact, doc, predicate, "person", "project", "role");
      
    }
    if ("toif:OrganizationIsInvolvedInProjectAsRole".equals(predicate)) {
      createComplexFact(fact, doc, predicate, "organization", "project", "role");
      
    }
    
  }
  
  /**
   * create all the facts regarding this resource
   * 
   * @param subjectResource
   *          the resource for which to create facts about.
   * @return the local resource.
   */
  private ToifRdfResource createElementFacts(ToifRdfResource resource) {
    
    if (resource == null) {
      return null;
    }
    
    Long localId = resource.getLocalId();
    
    if (localResources.containsKey(localId)) {
      
      return localResources.get(localId);
    } else {
      localResources.put(localId, resource);
    }
    
    Element resourceElement = resource.getElement();
    
    // get the makings to construct a type statement.
    // ToifRdfResource resource = resource;
    URI predicateURI = factory.createURI(HTTP_TOIF + "type");
    String typeAttribute = resourceElement.getAttribute("xsi:type");
    ToifRdfValue objectResource = new ToifRdfValue(typeAttribute, resourceElement, currentFile);
    
    // this element has type...
    ToifStatement type = new ToifStatement(resource, predicateURI, objectResource);
    
    // do the segment contains this element.
    statements.add(new ToifStatement(new ToifRdfResource(currentSegment, currentFile), factory.createURI(HTTP_TOIF
                                                                                                         + "contains"),
                                     resource));
                                     
    // ///////////////////// Now to make the children///////////////////
    NodeList children = resourceElement.getChildNodes();
    
    if ("toif:Date".equals(typeAttribute)) {
      String value = resourceElement.getAttribute("date");
      ToifRdfValue statementValue = new ToifRdfValue(value, resourceElement, currentFile);
      URI typeURI = factory.createURI(HTTP_TOIF + "date");
      ToifStatement statement = new ToifStatement(resource, typeURI, statementValue);
      statements.add(statement);
    }
    
    if ("toif:WeaknessDescription".equals(typeAttribute)) {
      String value = resourceElement.getAttribute("text");
      ToifRdfValue statementValue = new ToifRdfValue(value, resourceElement, currentFile);
      URI typeURI = factory.createURI(HTTP_TOIF + "description");
      ToifStatement statement = new ToifStatement(resource, typeURI, statementValue);
      statements.add(statement);
    }
    
    // for each of the children of the node
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      
      String nodeName = child.getNodeName();
      
      if ("#text".equals(nodeName)) {
        continue;
      }
      
      NamedNodeMap childAtributes = child.getAttributes();
      String value = null;
      
      if ("description".equals(nodeName)) {
        value = childAtributes.getNamedItem("text").getTextContent();
      } else if ("toif:Date".equals(typeAttribute)) {
        value = resourceElement.getAttribute("date");
      } else {
        
        Node namedItem = childAtributes.getNamedItem(nodeName);
        if (namedItem == null) {
          System.err.println("No named element found: " + nodeName + " " + noName++);
          value = "none";
        } else if (namedItem.getTextContent() == null) {
          System.err.println("No named element found: " + nodeName + " " + noName++);
          value = "none";
          
        } else {
          value = namedItem.getTextContent();
        }
      }
      
      URI typeURI = factory.createURI(HTTP_TOIF + nodeName);
      ToifRdfValue statementValue = new ToifRdfValue(value, resourceElement, currentFile);
      ToifStatement statement = new ToifStatement(resource, typeURI, statementValue);
      statements.add(statement);
    }
    
    statements.add(type);
    
    return resource;
    
  }
  
  /**
   * create a fact that only has 2 elements in its attributes.
   * 
   * @param subject
   *          the subject attribute name
   * @param predicate
   *          the predicate attribute name
   * @param object
   *          the object attribute name
   * @param doc
   *          the facts document
   * @param element
   *          the fact.
   */
  private void createFact(String subject, String predicate, String object, Document doc, Element element) {
    // ///////////////////// subject /////////////////////////////////
    
    // get the id value
    ToifRdfResource subjectResource = createResource(subject, doc, element);
    
    // //////////////////////// predicate ///////////////////////////////
    final URI predicateURI = factory.createURI(HTTP_TOIF + predicate);
    
    // //////////////////////// object //////////////////////////////////
    // this follows a similar approach to the subject above.
    ToifRdfResource objectResource = createResource(object, doc, element);
    
    // Add a path to the code location.
    if ("http://toif/toif:CodeLocationReferencesFile".equals(predicateURI.stringValue())) {
      if (objectResource instanceof ToifRdfFile) {
        ToifRdfFile file = (ToifRdfFile) objectResource;
        
        List<String> details = subjectResource.getDetails();
        
        String directoryStructure = file.getDirectoryStructure(blacklist);
        
        details.add("path=" + directoryStructure);
        subjectResource.setDetails(details);
        
      }
    }
    
    if ("http://toif/toif:StatementHasCodeLocation".equals(predicateURI.stringValue())) {
      statementMapping.setNewLocalStatement(subjectResource.getLocalId(), objectResource.getLocalId());
    }
    
    /*
     * if the resource is to do with the files and directories, set the object's next-node as the
     * subject. this allows newly found directories to be propagated back through all the files and
     * directories. creating paths for each one.
     * 
     * next node is the objects child. dir nextnode file.
     * 
     * remember: subject - predicate -object file containedin dir
     */
    if ((subjectResource instanceof ToifRdfFile) && (objectResource instanceof ToifRdfFile) && (predicate.contains(
                                                                                                                   "IsContainedIn"))) {
                                                                                                                   
      ((ToifRdfFile) objectResource).setNextNode((ToifRdfFile) subjectResource);
      
      // just some debug.
      if (DEBUG) {
        System.err.println(objectResource.getLocalId() + ": " + ((ToifRdfFile) objectResource).getDirectoryStructure(
                                                                                                                     null)
                           + " -> setting next node -> " + subjectResource.getLocalId() + ": "
                           + ((ToifRdfFile) subjectResource).getDirectoryStructure(null));
      }
    }
    
    // put statement in the statements list
    ToifStatement toifStatement = new ToifStatement(subjectResource, predicateURI, objectResource);
    statements.add(toifStatement);
    
  }
  
  /**
   * create the regular facts.
   * 
   * @param doc
   *          the ducument the fact is in
   * @param element
   *          the fact.
   */
  private void createFacts(Document doc, Element element) {
    
    final String predicate = element.getAttributeNode("xsi:type").getTextContent();
    
    // for each of the fact types.
    if ("toif:TOIFSegmentIsCreatedAtDate".equals(predicate)) {
      createFact("segment", predicate, "date", doc, element);
    }
    if ("toif:TOIFSegmentIsRelatedToProject".equals(predicate)) {
      createFact("segment", predicate, "project", doc, element);
    }
    if ("toif:FileIsContainedInDirectory".equals(predicate)) {
      createFact("file", predicate, "directory", doc, element);
    }
    if ("toif:DirectoryIsContainedInDirectory".equals(predicate)) {
      createFact("directory1", predicate, "directory2", doc, element);
    }
    if ("toif:FindingIsDescribedByWeaknessDescription".equals(predicate)) {
      createFact("finding", predicate, "description", doc, element);
    }
    if ("toif:FindingHasCWEIdentifier".equals(predicate)) {
      createFact("finding", predicate, "cwe", doc, element);
    }
    if ("toif:FindingHasSFPIdentifier".equals(predicate)) {
      createFact("finding", predicate, "sfp", doc, element);
    }
    if ("toif:FindingHasClusterIdentifier".equals(predicate)) {
      createFact("finding", predicate, "cluster", doc, element);
    }
    if ("toif:CodeLocationReferencesFile".equals(predicate)) {
      createFact("codeLocation", predicate, "file", doc, element);
    }
    if ("toif:FindingHasCodeLocation".equals(predicate)) {
      createFact("finding", predicate, "location", doc, element);
    }
    if ("toif:StatementIsInvolvedInFinding".equals(predicate)) {
      createFact("statement", predicate, "finding", doc, element);
    }
    if ("toif:StatementIsSinkInFinding".equals(predicate)) {
      createFact("statement", predicate, "finding", doc, element);
    }
    if ("toif:StatementHasCodeLocation".equals(predicate)) {
      createFact("statement", predicate, "location", doc, element);
    }
    if ("toif:StatementIsProceededByStatement".equals(predicate)) {
      createFact("statement1", predicate, "statement2", doc, element);
    }
    if ("toif:DataElementIsInvolvedInFinding".equals(predicate)) {
      createFact("data", predicate, "finding", doc, element);
    }
    if ("toif:FindingIsDescribedByWeaknessDescription".equals(predicate)) {
      createFact("finding", predicate, "description", doc, element);
    }
    
    // general housekeeping
    if ("toif:TOIFSegmentIsProducedByOrganization".equals(predicate)) {
      createFact("segment", predicate, "organization", doc, element);
    }
    if ("toif:TOIFSegmentIsSupervisedByPerson".equals(predicate)) {
      createFact("segment", predicate, "person", doc, element);
    }
    if ("toif:GeneratorIsSuppliedByVendor".equals(predicate)) {
      createFact("generator", predicate, "vendor", doc, element);
    }
    if ("toif:AdaptorSupportsGenerator".equals(predicate)) {
      createFact("adaptor", predicate, "vendor", doc, element);
    }
    if ("toif:TOIFSegmentIsRelatedToProject".equals(predicate)) {
      createFact("segment", predicate, "project", doc, element);
    }
    if ("toif:TOIFSegmentIsGeneratedByGenerator".equals(predicate)) {
      createFact("segment", predicate, "generator", doc, element);
    }
    if ("toif:TOIFSegmentIsProcessedByAdaptor".equals(predicate)) {
      createFact("segment", predicate, "adaptor", doc, element);
    }
    if ("toif:TOIFSegmentIsOwnedByOrganization".equals(predicate)) {
      createFact("segment", predicate, "organization", doc, element);
    }
    if ("toif:AdaptorIsSuppliedByVendor".equals(predicate)) {
      createFact("adaptor", predicate, "vendor", doc, element);
    }
    if ("toif:TOIFSegmentIsGeneratedByPerson".equals(predicate)) {
      createFact("segment", predicate, "person", doc, element);
    }
    
  }
  
  /**
   * Create the resource that is referenced by the fact. this also creates the child elements for
   * this resource.
   * 
   * @param resourceAttributeName
   *          the name of the attribute as it appears in the fact.
   * @param doc
   *          the document that these elements belong to
   * @param element
   *          the fact.
   * @return
   */
  private ToifRdfResource createResource(String resourceAttributeName, Document doc, Element element) {
    // get the id value.
    String subjectId = element.getAttribute(resourceAttributeName);
    // get the element that has this value for its id.
    Element subjectElement = getElementById(doc, subjectId);
    
    // element is probably the segment if it cannot be obtained by its id.
    if (subjectElement == null) {
      subjectElement = generateSegment(doc);
      currentSegment = subjectElement;
    }
    
    /*
     * the resource will either be a toifRdfFile or ToifRdfResource. The resources will be stored
     * the same way either way.
     */
    ToifRdfResource subjectResource = null;
    
    /*
     * if the subject is some form of file, use the toifRdfFile. this is so that we can create a
     * directory structure for it later.
     */
    if (resourceAttributeName.contains("file") || resourceAttributeName.contains("directory")) {
      subjectResource = new ToifRdfFile(subjectElement, currentFile);
    }
    // otherwise, create a straight-up ToifRdfResource.
    else {
      subjectResource = new ToifRdfResource(subjectElement, currentFile);
    }
    
    /*
     * create the elements that are children of this resource. this also returns the actual
     * subject-resource object that we should be using. IE the one that is already in the
     * localResources.
     */
    subjectResource = createElementFacts(subjectResource);
    
    return subjectResource;
  }
  
  /**
   * Deposit the resources into the repository.
   */
  private void depositResources() {
    try {
      con = repository.getConnection();
      
      /*
       * write all the local resources and statements to the repository. make sure that you use
       * global resources if possible.
       */
      for (ToifStatement statement : statements) {
        // subject
        ToifRdfResource subject = statement.getSubject();
        
        // predicate.
        URI predicate = statement.getPredicate();
        
        // object
        ToifRdfResource object = statement.getObject();
        
        // Add a path to the code location.
        if ("http://toif/toif:CodeLocationReferencesFile".equals(predicate.stringValue())) {
          if (object instanceof ToifRdfFile) {
            ToifRdfFile file = (ToifRdfFile) object;
            writeFact(subject, factory.createURI(HTTP_TOIF + "path"), new ToifRdfValue(file.getDirectoryStructure(
                                                                                                                  blacklist),
                                                                                       object.getElement(),
                                                                                       currentFile));
          } else {
            LOG.error("This should have not happened.");
            //System.err.println("This should have not happened.");
          }
        }
        
        // write statement
        writeFact(subject, predicate, object);
      }
      // export the toif repositoty as a stream.
      con.export(new NTriplesWriter(output), (Resource) null);
      
      con.clear((Resource) null);
      
      con.close();
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (RDFHandlerException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * Filter the elements out of the parent element.
   * 
   * @param parent
   *          the node containing the elements that you wish to filter
   * @param filter
   *          the tag that you wish to filter.
   */
  private void filterElements(Node parent, String filter) {
    final NodeList children = parent.getChildNodes();
    
    for (int i = 0; i < children.getLength(); i++) {
      final Node child = children.item(i);
      
      // only interested in elements
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        
        // remove elements whose tag name = filter
        // otherwise check its children for filtering with a recursive
        // call
        if (child.getNodeName().equals(filter)) {
          parent.removeChild(child);
        } else {
          filterElements(child, filter);
        }
      }
    }
  }
  
  /**
   * Get the Segment from the document and its description. These get placed in the hashmap of all
   * the collected elements.
   * 
   * @param doc
   *          the document from which to get the segment.
   */
  private Element generateSegment(Document doc) {
    
    // don't want to affect the original doc at this stage.
    final Document clonedDoc = (Document) doc.cloneNode(true);
    
    // get the root element.
    Element segment = clonedDoc.getDocumentElement();
    
    filterElements(segment, "fact");
    
    segment.setAttribute("id", Long.toString(0));
    // segment.setAttribute("parent", currentParent);
    
    String fileName = currentFile.getName();
    fileName = fileName.replace(".toif.xml", "");
    String[] nameArray = fileName.split("[.]");
    currentToolName = nameArray[nameArray.length - 1];
    
    segment.setAttribute("parent", currentToolName);
    
    clonedDoc.normalize();
    
    ToifRdfResource resource = new ToifRdfResource(segment, currentFile);
    
    if (!segments.containsKey(currentToolName)) {
      segments.put(currentToolName, resource);
    }
    
    resource = segments.get(currentToolName);
    
    makeGlobal(resource);
    
    return resource.getElement();
    
  }
  
  /**
   * return the dom document for this file
   * 
   * @param file
   *          the file to be parsed
   * @return the dom document
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  private Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {
    final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    final Document doc = docBuilder.parse(file);
    
    doc.getDocumentElement().normalize();
    return doc;
  }
  
  /**
   * Get the element with the required id from the node.
   * 
   * @param node
   *          The node which contains the elements with ids.
   * @param id
   *          the id value to search for.
   * @return the element with the required id value.
   */
  private Element getElementById(Node node, String id) {
    try {
      final XPathFactory xPathfactory = XPathFactory.newInstance();
      final XPath xpath = xPathfactory.newXPath();
      
      final XPathExpression xPathexpression = xpath.compile("//*[@id = '" + id + "']");
      
      final Element result = (Element) xPathexpression.evaluate(node, XPathConstants.NODE);
      
      return result;
    } catch (final XPathExpressionException ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  /**
   * makeGlobal must check to make sure that if the element is already in the global resources map,
   * that it is the existing element that is returned.
   * 
   * @param subject
   * @return
   */
  private Long makeGlobal(ToifRdfResource resource) {
    
    // if the resource has already been seen (its in the globalResources).
    // Use it!
    if (globalResources.containsKey(resource)) {
      // System.err.println("contained: "+resource.getDetails());
      return globalResources.get(resource);
    }
    // otherwise make it a global resource.
    else {
      // System.err.println("added: " + resource.getDetails());
      long globalId = resource.getLocalId() + offset;
      
      globalResources.put(resource, globalId);
      // return globalResources.get(resource);
      return globalId;
    }
  }
  
  /**
   * Attempt to merge the toif files.
   * 
   * @param toifFiles
   *          The List of toif files to merge.
   * @param startID
   * @return
   */
  public Long merge(List<File> toifFiles) {
    
    int fileNum = 0;
    int listSize = toifFiles.size();
    try {
      repository.initialize();
      con = repository.getConnection();
      factory = repository.getValueFactory();
    } catch (final RepositoryException e) {
      LOG.error("There was a repository exception when trying to merge the TOIF data. Cannot continue!");
      e.printStackTrace();
    }
    
    // for each file in the list of toif files, parse it.
    for (final File file : toifFiles) {
      
      fileNum++;
      
      float fraction = (float)fileNum / (float)listSize;
      int percent = (int) Math.floor(fraction * 100f);
      
      consoleOutput("Processing TOIF files: \"" + file.toString() + "\"... " + percent + "%");
      
      currentFile = file;
      
      String fileName = currentFile.getName();
      fileName = fileName.replace(".toif.xml", "");
      String[] nameArray = fileName.split("[.]");
      currentToolName = nameArray[nameArray.length - 1];
      // currentToolName = currentFile.getParent();
      
      // initialize the local structures that only relate to this file.
      statements = new ArrayList<ToifStatement>();
      
      localResources = new HashMap<Long, ToifRdfResource>();
      
      try {
        // parse the file.
        parseFile(file);
      }
      /*
       * catch possible exceptions. try to continue the parse if force has been set.
       */
      catch (final ParserConfigurationException e) {
        if (force) {
          e.printStackTrace();
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a parserConfigurationException but the other files will be attempted");
          continue;
        } else {
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a parserConfigurationException no further files will be processd.");
          return null;
        }
      } catch (final SAXException e) {
        if (force) {
          e.printStackTrace();
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a SAXException but the other files will be attempted");
          continue;
        } else {
          e.printStackTrace();
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a SAXException no further files will be processd.");
          return null;
        }
      } catch (final IOException e) {
        if (force) {
          e.printStackTrace();
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a IOException but the other files will be attempted");
          continue;
        } else {
          e.printStackTrace();
          LOG.error("File: " + file.getAbsolutePath()
                    + " has encountered a IOException no further files will be processd.");
          return null;
        }
      }
      
      ArrayList<Long> keys = new ArrayList<Long>(globalResources.values());
      if (keys.isEmpty()) {
        continue;
      }
      Collections.sort(keys);
      Collections.reverse(keys);
      offset = keys.get(0) + 1;
      depositResources();
      
      if (DEBUG) {
        LOG.error("End of file: {}",file);
      }
      // end of file.
    }
    consoleOutput("");
    
    try {
      con.close();
    } catch (RepositoryException e) {
      LOG.error("Repository exception during merge. {}", e);
    }
    
    return offset;
    // after files
  }
  
  /**
   * parse each file and extract the elements from it.
   * 
   * @param file
   *          the file which is to be parsed.
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private void parseFile(File file) throws ParserConfigurationException, SAXException, IOException {
    if (DEBUG) {
      System.err.println("-----------------------------------------------------------------------------------------------------------------------------------");
      System.err.println("Parsing: " + file.getAbsolutePath());
      System.err.println("Offset: " + offset);
      System.err.println("Current Segment: " + new ToifRdfResource(currentSegment, currentFile).getDetails());
      System.err.println("Current File: " + currentFile.getName());
      System.err.println("Current Tool Name: " + currentToolName + "\n");
    }
    
    doc = getDocument(file);
    
    // just get all the elements.
    final NodeList nodeList = doc.getElementsByTagName("*");
    
    /*
     * for each of the elements in the document, find the facts relating to the relationships
     * between the other entities. ie: "toif:FileIsContainedInDirectory".
     */
    for (int i = 0; i < nodeList.getLength(); i++) {
      // element is a subinterface of node.
      final Element element = (Element) nodeList.item(i);
      
      /*
       * figure out if this is a fact that relates multiple entities. These kinds of facts have 4
       * attributes. It would be nice if the spec differentiated between these two kinds of
       * elements.
       */
      final int attributesLength = element.getAttributes().getLength();
      
      /*
       * if the attribute length is not 4 then just continue until we find one.
       */
      if (attributesLength > 4) {
        createComplexFacts(doc, element);
      }
      
      /*
       * if the attribute length is 4 then we can just use the xsi:type as the predicate.
       */
      if (attributesLength == 4) {
        createFacts(doc, element);
      }
      
    }
  }
  
  /**
   * prints the contents of the repository. mainly for debug purposes.
   * 
   * @param repository
   *          the repository to print.
   */
  void printDB() {
    RepositoryConnection con;
    
    try {
      con = repository.getConnection();
      // get all statements.
      final RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
      
      // for all the statements.
      while (statements.hasNext()) {
        final Statement st = statements.next();
        // print statements.
        System.err.println(st.toString());
      }
      
      statements.close();
    } catch (final RepositoryException e) {
      e.printStackTrace();
      LOG.error("There was a repository error while printing the database. " + e);
    }
    
  }
  
  /**
   * print the document (useful for debug)
   * 
   * @param doc
   *          the XML document to print
   * @throws TransformerFactoryConfigurationError
   */
  @SuppressWarnings("unused")
  private void printXML(Document doc) throws TransformerFactoryConfigurationError {
    final Source source = new DOMSource(doc);
    final Result dest = new StreamResult(System.err);
    final TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer tFormer;
    try {
      tFormer = tFactory.newTransformer();
      tFormer.transform(source, dest);
    } catch (final TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (final TransformerException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * trim the number off the string if it is there
   * 
   * @param string
   *          the string to trim
   * @return the trimmed string
   */
  private String trimNumber(String string) {
    String end = string.substring(string.length() - 1);
    
    try {
      Integer.parseInt(end);
      return string.substring(0, string.length() - 1);
    } catch (NumberFormatException e) {
      return string;
    }
  }
  
  /**
   * write the statement to the repository.
   * 
   * @param subject
   *          the subject of the statement
   * @param predicateURI
   *          the predicate of the statement
   * @param object
   *          the object of the statement.
   */
  private void writeFact(ToifRdfResource subject, URI predicateURI, ToifRdfResource object) {
    
    if (subject instanceof ToifRdfFile) {
      
      ToifRdfFile file = (ToifRdfFile) subject;
      
      if (subject.getType().equals("toif:Directory")) {
        if (!file.getDirectoryStructure(null).contains(blacklist)) {
          return;
        }
      }
      
    }
    
    if (object instanceof ToifRdfFile) {
      ToifRdfFile file = (ToifRdfFile) object;
      
      final String directoryStructure = file.getDirectoryStructure(null);
      if (!directoryStructure.contains(blacklist)) {
        return;
      }
      
    }
    
    try {
      Long subjectId = (long) 0;
      
      if ("toif:Statement".equals(subject.getType())) {
        final Long globalStatement = statementMapping.getGlobalStatement(subject.getLocalId());
        if (globalStatement != null) {
          subjectId = globalStatement;
        } else {
          subjectId = makeGlobal(subject);
        }
      } else {
        subjectId = makeGlobal(subject);
        
      }
      
      URI subjectURI = factory.createURI(HTTP_TOIF + subjectId);
      if ((object instanceof ToifRdfBnode)) {
        
        Long objectId = makeGlobal(object);
        
        URI objectURI = factory.createURI(HTTP_TOIF + (smallestBigNumber - objectId));
        
        // returns true if the statement has been seen.
        boolean seen = seenStatement(subjectURI.stringValue(), predicateURI.stringValue(), objectURI.stringValue());
        
        // if it has not been seen.
        if (!seen) {
          TripleStatementWriter.addOrWrite(output, con, subjectURI, predicateURI, objectURI);
        }
        
      } else if (object instanceof ToifRdfValue) {
        Value valueURI = factory.createLiteral(((ToifRdfValue) object).getValue());
        boolean seen = seenStatement(subjectURI.stringValue(), predicateURI.stringValue(), valueURI.stringValue());
        if (!seen) {
          TripleStatementWriter.addOrWrite(output, con, subjectURI, predicateURI, valueURI);
        }
        
      }
      
      else if ((subject instanceof ToifRdfBnode)) {
        subjectId = makeGlobal(subject);
        subjectURI = factory.createURI(HTTP_TOIF + (smallestBigNumber - subjectId));
        Long objectId = makeGlobal(object);
        URI objectURI = factory.createURI(HTTP_TOIF + objectId);
        
        boolean seen = seenStatement(subjectURI.stringValue(), predicateURI.stringValue(), objectURI.stringValue());
        
        if (!seen) {
          
          TripleStatementWriter.addOrWrite(output, con, subjectURI, predicateURI, objectURI);
        }
        
      } else {
        Long objectId = makeGlobal(object);
        
        if ("toif:CodeLocation".equals(object.getType())) {
          statementMapping.setNewCodeLocation(object.getLocalId(), objectId);
        }
        
        if ("toif:CodeLocation".equals(object.getType()) && "toif:Statement".equals(subject.getType())) {
          statementMapping.setNewGlobalStatement(objectId, subjectId);
        }
        
        URI objectURI = factory.createURI(HTTP_TOIF + objectId);
        
        boolean seen = seenStatement(subjectURI.stringValue(), predicateURI.stringValue(), objectURI.stringValue());
        
        if (!seen) {
          TripleStatementWriter.addOrWrite(output, con, subjectURI, predicateURI, objectURI);
        }
        
      }
      
    } catch (final RepositoryException e) {
      System.err.println("Repository exception while writing the fact to the repository. " + e);
    }
    
  }
  
  /**
   * checks to see if this statement
   * 
   * @param subject
   * @param predicate
   * @param object
   * @return
   */
  private boolean seenStatement(String subject, String predicate, String object) {
    SeenStatement seenStatement = new SeenStatement(subject, predicate, object);
    
    // returns true if the statement has been seen
    final boolean seen = !seenStatements.add(seenStatement);
    return seen;
  }
  
  
  /**
   * Console output.
   *
   * @param consoleOutput the console output
   */
  private void consoleOutput(String consoleOutput) {
    System.out.println(consoleOutput);
    if (LOG.isDebugEnabled()) {
      LOG.debug(consoleOutput);
    }
  }
  
}
