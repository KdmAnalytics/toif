/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.convert.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

import com.kdmanalytics.toif.report.internal.items.CachedCodeLocation;
import com.kdmanalytics.toif.report.internal.items.FileGroup;
import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.internal.items.Trace;
import com.kdmanalytics.toif.report.items.IFindingEntry;

/** Imports a *.kdm TOIF file, without dependencies to the UI code. See
 *   o com.kdmanalytics.toif.report.internal.items.ProjectFactory
 * 
 * @author 
 *
 */
public class ToifImporter {
  
  private List<IToifImportListener> listeners = new LinkedList<IToifImportListener>();
  
  /**
   * Repository connection
   */
  private RepositoryConnection con;
  private ValueFactory factory;
  
  /**
   * Used to cache results of a query on code locations and related information.
   */
  public static Map<String, Map<Value, CachedCodeLocation>> codeLocationCache;
  
  // DEBUG
  static int countCodeLocations;
  
  static int countFindings;
  
  // DEBUG
  
  /** Add an event listener
   * 
   * @param listener
   */
  public void addFindingListener(IToifImportListener listener) {
    listeners.add(listener);
  }
  
  /** Remove an event listener
   * 
   * @param listener
   */
  public void removeFindingListener(IToifImportListener listener) {
    listeners.remove(listener);
  }
  
  public void run(File ifile) throws IOException {
    for (IToifImportListener listener : listeners) {
      listener.event(new ToifImportEvent(ToifImportEvent.IMPORT_REPO_START));
    }
    // Load the KDM data into a repository
    File root = importRepository(ifile);
    
    for (IToifImportListener listener : listeners) {
      listener.event(new ToifImportEvent(ToifImportEvent.IMPORT_REPO_DONE));
    }
    
    for (IToifImportListener listener : listeners) {
      listener.event(new ToifImportEvent(ToifImportEvent.IMPORT_FINDINGS_START));
    }

    // Look through the repository for findings
    findingScanner(root);
    
    for (IToifImportListener listener : listeners) {
      listener.event(new ToifImportEvent(ToifImportEvent.IMPORT_FINDINGS_DONE));
    }
    
    // Delete the temporary repository
    FileUtils.deleteDirectory(root);
  }
  
  /** Load the KDM data into a repository.
   * 
   * @param ifile
   * @return
   * @throws IOException 
   */
  private File importRepository(File ifile) throws IOException {
    Path path = Files.createTempDirectory("toif-import.");
    File root = path.toFile();
    File repoDir = new File(root, "repo");
    repoDir.mkdir();
    repoDir.deleteOnExit();
    
    Repository myRepository = new SailRepository(new NativeStore(repoDir));
    RepositoryConnection con = null;
    try {
      myRepository.initialize();
      
      con = myRepository.getConnection();
      
      try {
        String content = FileUtils.readFileToString(ifile, "UTF-8");
        
        content = content.replace("KDM_Triple:1", "");
        
        if (!content.contains("<http://toif/")) {
          content = content.replaceAll("<", "<http://toif/");
        }
        File tempFile = File.createTempFile("toif", "kdm");
        tempFile.deleteOnExit();
        FileUtils.writeStringToFile(tempFile, content, "UTF-8");
        con.add(tempFile, null, RDFFormat.NTRIPLES);
      } catch (IOException e) {
        // Simple exception handling, replace with what's necessary
        // for your use case!
        throw new RuntimeException("Generating file failed", e);
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (RDFParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (con != null) {
          con.close();
        }
        myRepository.shutDown();
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return root;
  }
  
  /** Save data from the repository into a tsv file
   * 
   * @param repo
   */
  private void findingScanner(File root) {
    File repo = new File(root, "repo");
    final Repository repository = new SailRepository(new NativeStore(repo));
    try {
      // Prepare the repository
      repository.initialize();
      
      con = repository.getConnection();
      factory = repository.getValueFactory();
      initCodeLocationCache();
      
      
      // query for the file-groupings, add them to the project and return
      // the results so that they can be use to query for its children.
      final List<FileGroup> fileGroupings = addFileGroupings();
      
      for (FileGroup fileGroup : fileGroupings) {
        
        // query for the location-groupings, add them to the project and
        // return the results so that they can be use to query for its
        // children.
        for (LocationGroup locations : addLocationGroupings(fileGroup)) {
          // query for the findings based on the locations and add
          // them to the project.
          addFindings(locations);
        }
      }
    } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e1) {
      e1.printStackTrace();
    } finally {
      if (con != null) {
        try {
          con.close();
          con = null;
          factory = null;
        } catch (RepositoryException e1) {
          e1.printStackTrace();
        }
        if (repository != null) {
          try {
            repository.shutDown();
          } catch (RepositoryException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  /**
   * cache the code locations.
   * 
   * @param project
   *          the toif project
   * @throws QueryEvaluationException
   * @throws RepositoryException
   * @throws MalformedQueryException
   */
  private void initCodeLocationCache() throws QueryEvaluationException, RepositoryException,
      MalformedQueryException {
    Set<String> uniquePaths = new HashSet<String>();
    
    codeLocationCache = new HashMap<String, Map<Value, CachedCodeLocation>>();
    countFindings = 0;
    countCodeLocations = 0;
    
    // make the query. get all the codelocations that have the
    // locationgroup's linenumber and path.
    String queryString = "SELECT ?codeLocation ?path ?lineno ?finding WHERE {"
                         + "?codeLocation <http://toif/path> ?path . "
                         + "?codeLocation <http://toif/lineNumber> ?lineno. "
                         + "?finding <http://toif/toif:FindingHasCodeLocation> ?codeLocation ." + "}";
                         
    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    
    TupleQueryResult result = tupleQuery.evaluate();
    try {
      // for each of the results, get the findings at this codelocation.
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        Value finding = bindingSet.getValue("finding");
        Value codeLocation = bindingSet.getValue("codeLocation");
        Value path = bindingSet.getValue("path");
        uniquePaths.add(path.stringValue());
        Value lineno = bindingSet.getValue("lineno");
        String pathName = path.stringValue();
        if (!codeLocationCache.containsKey(pathName)) {
          codeLocationCache.put(pathName, new HashMap<Value, CachedCodeLocation>());
        }
        Map<Value, CachedCodeLocation> map = codeLocationCache.get(pathName);
        CachedCodeLocation loc = null;
        if (map.containsKey(codeLocation)) loc = map.get(codeLocation);
        else {
          loc = new CachedCodeLocation(codeLocation, path.stringValue(), lineno.stringValue());
          map.put(codeLocation, loc);
        }
        loc.addFinding(finding);
      }
    } finally {
      result.close();
    }
  }
  
  /**
   * based on the parent project, query the repository for the file-groups. once the file-groups are
   * found, add them to the project. return the file-groupings.
   * 
   * @param project
   *          this is the parent of all the file-groupings which will be returned.
   * @return the list of file-groupings in this project.
   *         
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private List<FileGroup> addFileGroupings() throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    
    List<FileGroup> fileGroups = new LinkedList<FileGroup>();
    List<String> fgroups = new ArrayList<String>();
    
    if (con == null) {
      return new ArrayList<FileGroup>();
    } else if (!con.isOpen()) {
      return new ArrayList<FileGroup>();
    }
    // make a query. get all elements that are the object of statements with
    // the predicate "<http://toif/path>".
    String queryString = "SELECT ?x WHERE {?y <http://toif/path> ?x . ?z <http://toif/toif:FindingHasCodeLocation> ?y .}";
    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    
    // evaluate the query.
    TupleQueryResult result = tupleQuery.evaluate();
    try {
      // for each of the results. make a new filegroup.
      while (result.hasNext()) {
        // get the binding set.
        BindingSet bindingSet = result.next();
        Value valueOfX = bindingSet.getValue("x");
        if (!fgroups.contains(valueOfX.stringValue())) {
          fgroups.add(valueOfX.stringValue());
          // create the filegroup.
          FileGroup files = new FileGroup(valueOfX.stringValue());
          
          // add the files to the project.
          fileGroups.add(files);
        }
      }
    } finally {
      result.close();
    }
    return fileGroups;
  }
  
  /**
   * query the repository for the location groups (lineNumbers). these are added to the project and
   * also returned as a list.
   * 
   * @param file
   *          the file-group which is the parent for this locationgroup.
   * @return a list of the locationgroups within this file group.
   *         
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private List<LocationGroup> addLocationGroupings(FileGroup file)
      throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    
    // get all the code locations that fall within this file. A better way
    // to do this whole method would be to use the OPTIONAL query to only
    // select the kdm location if it is present.
    String codeLocationString = "SELECT ?codeLocation ?lineNumber  WHERE { ?codeLocation <http://toif/path> \"" + file
        .getPath()
    + "\" . ?codeLocation <http://toif/lineNumber> ?lineNumber}";
    
    TupleQuery codeLocationQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, codeLocationString);
    
    TupleQueryResult codeLocations = codeLocationQuery.evaluate();
    
    try {
      // for each of the codeLocations we need to check to see if there is
      // a kdm location for them. if not, just use the toif.
      while (codeLocations.hasNext()) {
        BindingSet codeLocationsBindingSet = codeLocations.next();
        
        Value codeLocation = codeLocationsBindingSet.getValue("codeLocation");
        
        Value toifLineNumber = codeLocationsBindingSet.getValue("lineNumber");
        
        // this is the string to try to find a kdm location.
        String kdmQueryString = "SELECT ?line WHERE { <" + codeLocation
            + "> <http://org.omg.kdm/CommonView> ?commonView . ?commonView <http://org.omg.kdm/KdmView> ?kdmElement . ?kdmElement <http://org.omg.kdm/SourceRef> ?line . } ";
        TupleQuery kdmQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, kdmQueryString);
        
        TupleQueryResult result = kdmQuery.evaluate();
        
        // if the kdm query does not have any result, then use the toif
        // query to just use the toif data. replacing the result with
        // this new value.
        if (!result.hasNext()) {
          String toifQueryString = "SELECT ?line  WHERE { <" + codeLocation + "> <http://toif/lineNumber> ?line . }";
          
          TupleQuery toifQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, toifQueryString);
          
          // replace the result with the toif value.
          result = toifQuery.evaluate();
          
          if (!result.hasNext()) {
            continue;
          }
        }
        
        try {
          do {
            BindingSet lineNumberBindingSet = result.next();
            Value toifOrKdmLineNumber = lineNumberBindingSet.getValue("line");
            
            // create a new location.
            String toifOrKdmLineNumberValue = toifOrKdmLineNumber.stringValue();
            
            // get the line number value from the string.
            if (toifOrKdmLineNumberValue.contains(";")) {
              String[] sourceRefValues = toifOrKdmLineNumberValue.split(";");
              toifOrKdmLineNumberValue = sourceRefValues[1];
            }
            LocationGroup location = new LocationGroup(file.getPath(), toifLineNumber.stringValue());
            
            location.setRealLineNumber(toifOrKdmLineNumberValue);
            
            location.setParent(file);
            
            // add the location to the filegroup.
            file.AddLocation(location);
            
          } while (result.hasNext());
        } finally {
          result.close();
        }
        
      }
      
    } finally {
      codeLocations.close();
    }
    // return the list of location groups.
    return file.getLocationGroup();
    
  }
  
  /**
   * query for the findings in the repository.
   * 
   * @param parentElement
   * @return
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private List<ToolGroup> addFindings(LocationGroup locationGroup)
      throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    for (CachedCodeLocation ccl : codeLocationCache.get(locationGroup.getPath()).values()) {
      if (ccl.matches(locationGroup.getPath(), locationGroup.getToifLineNumber())) {
        ++countCodeLocations;
        int findingCount = getFinding(locationGroup, ccl);
        if (findingCount != ccl.getFindings().size()) {
          System.err.println("wut");
        }
        countFindings += findingCount;
      }
    }
    
    // since there was a group that was missed (jumping from location to
    // finding) we will return the tool groups.
    return locationGroup.getToolGroups();
  }
  
  /**
   * Get the finding based on the fact "FindingHasCodeLocation" we do this because we already have
   * the locations.
   * 
   * This method also adds toolgroups to the locationgroups.
   * 
   * @param locationGroup
   *          The location grouping for this finding.
   * @param codeLocation
   *          The value of the location.
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private int getFinding(LocationGroup locationGroup, CachedCodeLocation ccl)
      throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    int count = 0;
    List<Value> findings = ccl.getFindings();
    for (Value finding : findings) {
      FindingEntry entry = new FindingEntry(finding);
      
      initializeTrust(entry);
      initializeIsOk(entry);
      
      // get the tool.
      ToolGroup tool = getTool(finding);
      
      // add the tool group to the location group
      tool = locationGroup.AddToolGroup(tool);
      
      // add the finding entry to the tool group.
      entry.setParent(tool);
      
      tool.setParent(locationGroup);
      
      // give the findings a weakness description.
      entry.setDescription(getWeaknessDescription(finding));
      entry.setTraces(getTraces(finding));
      entry.setCwe(getCwe(finding));
      entry.setSfp(getSfp(finding));
      entry.setPath(ccl.getPath());
      try {
        entry.setLineNumber(Integer.parseInt(ccl.getLineNumber()));
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      if (!tool.addFinding(entry)) {
        System.err.println("  - " + ccl.getPath() + " " + ccl.getLineNumber());
      }
      ++count;
      
      for (IToifImportListener listener: listeners) {
        listener.add(entry);
      }
    }
    return count;
  }
  
  /**
   * get the cwe name for the finding
   * 
   * @param project
   *          the toif project
   * @param finding
   *          the finding that we wish to find the cwe for
   * @return the name of the cwe.
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private String getCwe(Value finding) throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    
    // make a query. this finds the findings cwe id, then uses
    // its name.
    String descriptionQuery = "SELECT ?cwe WHERE {<" + finding
        + "> <http://toif/toif:FindingHasCWEIdentifier> ?id. ?id <http://toif/name> ?cwe .}";
    TupleQuery cweQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
    
    // evaluate the query.
    TupleQueryResult cweNameResult = cweQuery.evaluate();
    
    try {
      // use the first cwe name we come across. there should be only
      // one anyway.
      while (cweNameResult.hasNext()) {
        BindingSet cweSet = cweNameResult.next();
        Value cweName = cweSet.getValue("cwe");
        
        // return the sfp name.
        return cweName.stringValue();
      }
    } finally {
      cweNameResult.close();
    }
    // there is no sfp name.
    return " - ";
  }
  
  /**
   * Find the weakness description for this finding value.
   * 
   * @param finding
   *          The finding value as found in getFinding().
   * @return The weakness description.
   *         
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private String getWeaknessDescription(Value finding) throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    // make a query. this finds the findings weakness description, then uses
    // its description text.
    String descriptionQuery = "SELECT ?t WHERE {" + "<" + finding
        + "> <http://toif/toif:FindingIsDescribedByWeaknessDescription> ?d. "
        + "?d <http://toif/description> ?t." + "}";
    TupleQuery descriptionTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
    
    // evaluate the query.
    TupleQueryResult descriptionResult = descriptionTupleQuery.evaluate();
    
    try {
      // use the first description text we come across. there should be
      // only
      // one anyway.
      while (descriptionResult.hasNext()) {
        BindingSet descriptionSet = descriptionResult.next();
        Value valueOfdescription = descriptionSet.getValue("t");
        
        // return the description text.
        return valueOfdescription.stringValue();
      }
    } finally {
      descriptionResult.close();
    }
    // there is no description. however, there should always be a
    // description.
    return "None";
  }
  
  /**
   * query the repository to get the tool group for this finding.
   * 
   * @param finding
   *          the finding that we are trying to find a toolgroup for.
   * @return the toolgroup. or 'Unknown Tool' if none can be found.
   *         
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private ToolGroup getTool(Value finding) throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    
    // make a query to find the adaptor name. we are looking for something
    // (the segment) that contains this finding, it also contains another
    // element (the adaptor) with the type 'toif:Adaptor'. this adaptor has
    // a name 'name'.
    // String adaptorQuery = "SELECT ?name WHERE { " +
    // "?id <http://toif/contains> <" + finding + ">. " +
    // "?id <http://toif/contains> ?adaptorId. "
    // + "?adaptorId <http://toif/type> \"toif:Adaptor\". " +
    // "?adaptorId <http://toif/name> ?name.}";
    
    // String adaptorQuery = "SELECT ?name WHERE { " + "?id <http://toif/contains> <" + finding +
    // ">. "
    // + "?id <http://toif:TOIFSegmentIsProcessedByAdaptor> ?adaptorId. " + "?adaptorId
    // <http://toif/name> ?name.}";
    
    String adaptorQuery = "SELECT ?name WHERE { " + "?id <http://toif/contains> <" + finding + ">. "
        + "?id <http://toif/toif:TOIFSegmentIsProcessedByAdaptor> ?adaptor. ?adaptor <http://toif/name> ?name}";
    
    TupleQuery adaptorTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, adaptorQuery);
    
    // query the repository.
    TupleQueryResult queryResult = adaptorTupleQuery.evaluate();
    
    try {
      // for each of the results, create a new toolgroup. return the
      // toolgroup
      // if there is one.
      while (queryResult.hasNext()) {
        
        BindingSet adaptorSet = queryResult.next();
        Value adaptorName = adaptorSet.getValue("name");
        // return the tool group since we have one.
        return new ToolGroup(adaptorName.stringValue());
        
      }
    } finally {
      queryResult.close();
    }
    // if we are here, then there is no tool group. return the fall back
    // value.
    return new ToolGroup("Unknown Tool");
    
  }
  
  /**
   * get the sfp for the given finding
   * 
   * @param project
   *          the toif project
   * @param finding
   *          the finding that we want to find the sfp for
   * @return
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private String getSfp(Value finding) throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    // make a query. this finds the findings sfp id, then uses
    // its name.
    String descriptionQuery = "SELECT ?sfp WHERE {" + "<" + finding
        + "> <http://toif/toif:FindingHasSFPIdentifier> ?id. " + "?id <http://toif/name> ?sfp ."
        + "}";
    TupleQuery sfpQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
    
    // evaluate the query.
    TupleQueryResult descriptionResult = sfpQuery.evaluate();
    try {
      
      // use the first sfp name we come across. there should be only
      // one anyway.
      while (descriptionResult.hasNext()) {
        BindingSet sfpSet = descriptionResult.next();
        Value sfpName = sfpSet.getValue("sfp");
        // return the sfp name.
        return sfpName.stringValue();
      }
    } finally {
      descriptionResult.close();
    }
    // there is no sfp name.
    return " - ";
  }
  
  /**
   * get the traces for this finding from the repository.
   * 
   * @param finding
   *          the finding we wwant to find traces for
   * @return list of traces.
   * @throws RepositoryException
   * @throws MalformedQueryException
   * @throws QueryEvaluationException
   */
  private List<Trace> getTraces(Value finding) throws RepositoryException,
  MalformedQueryException, QueryEvaluationException {
    
    List<Trace> traces = new ArrayList<Trace>();
    
    String descriptionQuery = "SELECT ?lineNumber WHERE {" + "?segment <http://toif/contains> <" + finding + "> . "
        + "?segment <http://toif/contains> ?statement . "
        + "?statement <http://toif/toif:StatementIsInvolvedInFinding> <" + finding + "> . "
        + "?statement <http://toif/toif:StatementHasCodeLocation> ?codeLocation . "
        + "?codeLocation <http://toif/lineNumber> ?lineNumber . }";
    TupleQuery traceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
    
    // evaluate the query.
    TupleQueryResult traceNameResult = traceQuery.evaluate();
    
    try {
      
      while (traceNameResult.hasNext()) {
        BindingSet traceSet = traceNameResult.next();
        Value traceName = traceSet.getValue("lineNumber");
        
        Trace trace = new Trace(traceName.stringValue());
        traces.add(trace);
      }
    } finally {
      traceNameResult.close();
    }
    
    return traces;
  }
  
  /**
   * initialize if this finding is a real weakness or not.
   * 
   * @param project
   *          the toif project
   * @param entry
   *          the finding entry in the report view.
   */
  private void initializeIsOk(IFindingEntry entry) {
    URI isWeaknessURI = factory.createURI("http://toif/isWeakness");
    URI findingURI = factory.createURI(entry.getFindingId());
    try {
      String isWeaknessQuery = "SELECT ?isWeakness WHERE {<" + entry.getValue()
      + "> <http://toif/isWeakness> ?isWeakness .}";
      TupleQuery isWeaknessTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, isWeaknessQuery);
      
      TupleQueryResult isWeaknessResults = isWeaknessTupleQuery.evaluate();
      
      while (isWeaknessResults.hasNext()) {
        BindingSet findingSet = isWeaknessResults.next();
        Value isWeaknessValue = findingSet.getValue("isWeakness");
        entry.setIsOk(Citing.valueOf(isWeaknessValue.stringValue()));
        
        // there will be only one, return if we have reached this far.
        return;
      }
      
      String valid = Citing.UNKNOWN.toString();
      con.add(findingURI, isWeaknessURI, factory.createLiteral(valid));
      
    } catch (RepositoryException e) {
      System.err.println("There was a repository exception while setting whether the weakness is true or not. " + e);
    } catch (MalformedQueryException e) {
      System.err.println("There is a mal formed query while setting whether the weakness is true or not. " + e);
    } catch (QueryEvaluationException e) {
      System.err.println("There is a query exception while finding out if the finding is true or not " + e);
    }
    
  }
  
  /**
   * make sure that the trust statement exists. if not, make the statement and initialize it with
   * the correct starting value.
   * 
   * @param entry
   *          the findings for which we are doing the trust operations.
   */
  private void initializeTrust(IFindingEntry entry) {
    URI trustURI = factory.createURI("http://toif/trust");
    URI findingURI = factory.createURI(entry.getFindingId());
    try {
      String trustQuery = "SELECT ?trust WHERE {<" + entry.getValue() + "> <http://toif/trust> ?trust .}";
      TupleQuery trustTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, trustQuery);
      
      TupleQueryResult trustResults = trustTupleQuery.evaluate();
      
      while (trustResults.hasNext()) {
        BindingSet findingSet = trustResults.next();
        Value trust = findingSet.getValue("trust");
        
        entry.setTrust(Integer.parseInt(trust.stringValue()));
        
        // there will be only one, return if we have reached this far.
        return;
      }
      
      // if we reach this far then there were no trusts.
      con.add(findingURI, trustURI, factory.createLiteral(0));
      entry.setTrust(0);
    } catch (MalformedQueryException e) {
      e.printStackTrace();
    } catch (QueryEvaluationException e) {
      e.printStackTrace();
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    
  }
}
