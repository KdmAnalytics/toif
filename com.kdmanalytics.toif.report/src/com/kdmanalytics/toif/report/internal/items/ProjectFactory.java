/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the // accompanying materials are made available under the terms of the Open
 * Source // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
// /////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2012 KDM Analytics, Inc.
// All rights reserved.
// /////////////////////////////////////////////////////////////////////////////

package com.kdmanalytics.toif.report.internal.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * class to create the toif project
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class ProjectFactory
{
    
    /**
     * Used to cache results of a query on code locations and related
     * information.
     */
    public static Map<String, Map<Value, CachedCodeLocation>> codeLocationCache;
    
    // DEBUG
    static int countCodeLocations;
    
    static int countFindings;
    
    // DEBUG
    
    /**
     * Instantiates a new project factory.
     */
    private ProjectFactory()
    {
        // empty on purpose
    }
    
    /**
     * create a new toif project model.
     * 
     * @param kdmRepoFolder
     *            the repository folder
     * @param workbenchPresent
     *            is this view in the kdm workbench
     * @param monitor
     *            the progress monitor
     * @return the toif project
     */
    public static IToifProject createProjectModel(IFolder kdmRepoFolder, boolean workbenchPresent, IProgressMonitor monitor)
    {
        SubMonitor progress = SubMonitor.convert(monitor, 100);
        
        // starting from scratch, so create a new project. the root of the
        // defect model.
        Project project = new Project(kdmRepoFolder, workbenchPresent);
        
        try
        {
            initCodeLocationCache(project);
        }
        catch (QueryEvaluationException e1)
        {
            e1.printStackTrace();
        }
        catch (RepositoryException e1)
        {
            e1.printStackTrace();
        }
        catch (MalformedQueryException e1)
        {
            e1.printStackTrace();
        }
        
        try
        {
            // ReportContentProvider contentProvider = (ReportContentProvider)
            // viewer.getContentProvider();
            
            // the connection will be used to query the repository to get the
            // file-groupings, the location-groupings, and the findings.
            if (!project.getRepositoryConnection().isOpen())
            {
                return project;
            }
            // add the file to content provider. this imports the data into the
            // repository so that we can then query it.
            // addKdmFile(file);
            
            // query for the file-groupings, add them to the project and return
            // the results so that they can be use to query for its children.
            final List<FileGroup> fileGroupings = addFileGroupings(project);
            
            SubMonitor loopProgress = progress.newChild(100);
            loopProgress.beginTask("Building Defect Model", fileGroupings.size());
            
            for (FileGroup fileGroup : fileGroupings)
            {
                
                // query for the location-groupings, add them to the project and
                // return the results so that they can be use to query for its
                // children.
                for (LocationGroup locations : addLocationGroupings(project, fileGroup))
                {
                    progress.setTaskName("Processing findings for location: " + fileGroup.getName() + ":" + locations.getLineNumber());
                    // query for the findings based on the locations and add
                    // them to the project.
                    addFindings(project, locations);
                }
                loopProgress.worked(1);
            }
            
        }
        catch (RepositoryException e)
        {
            e.printStackTrace();
        }
        catch (MalformedQueryException e)
        {
            e.printStackTrace();
        }
        catch (QueryEvaluationException e)
        {
            e.printStackTrace();
        }
        
        // return the root of the model which should now be populated.
        return project;
        
    }
    
    /**
     * cache the code locations.
     * 
     * @param project
     *            the toif project
     * @throws QueryEvaluationException
     * @throws RepositoryException
     * @throws MalformedQueryException
     */
    private static void initCodeLocationCache(IToifProject project) throws QueryEvaluationException, RepositoryException, MalformedQueryException
    {
        Set<String> uniquePaths = new HashSet<String>();
        int count = 0;
        
        codeLocationCache = new HashMap<String, Map<Value, CachedCodeLocation>>();
        countFindings = 0;
        countCodeLocations = 0;
        int findingCount = 0;
        
        RepositoryConnection con = project.getRepositoryConnection();
        // make the query. get all the codelocations that have the
        // locationgroup's linenumber and path.
        String queryString = "SELECT ?codeLocation ?path ?lineno ?finding WHERE {" + "?codeLocation <http://toif/path> ?path . "
                + "?codeLocation <http://toif/lineNumber> ?lineno. " + "?finding <http://toif/toif:FindingHasCodeLocation> ?codeLocation ." + "}";
        
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        
        TupleQueryResult result = tupleQuery.evaluate();
        try
        {
            // for each of the results, get the findings at this codelocation.
            while (result.hasNext())
            {
                BindingSet bindingSet = result.next();
                Value finding = bindingSet.getValue("finding");
                Value codeLocation = bindingSet.getValue("codeLocation");
                Value path = bindingSet.getValue("path");
                uniquePaths.add(path.stringValue());
                Value lineno = bindingSet.getValue("lineno");
                String pathName = path.stringValue();
                if (!codeLocationCache.containsKey(pathName))
                {
                    codeLocationCache.put(pathName, new HashMap<Value, CachedCodeLocation>());
                }
                Map<Value, CachedCodeLocation> map = codeLocationCache.get(pathName);
                CachedCodeLocation loc = null;
                if (map.containsKey(codeLocation))
                    loc = map.get(codeLocation);
                else
                {
                    loc = new CachedCodeLocation(codeLocation, path.stringValue(), lineno.stringValue());
                    map.put(codeLocation, loc);
                    ++count;
                }
                loc.addFinding(finding);
                findingCount++;
            }
        }
        finally
        {
            result.close();
        }
    }
    
    /**
     * based on the parent project, query the repository for the file-groups.
     * once the file-groups are found, add them to the project. return the
     * file-groupings.
     * 
     * @param project
     *            this is the parent of all the file-groupings which will be
     *            returned.
     * @return the list of file-groupings in this project.
     * 
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static List<FileGroup> addFileGroupings(final Project project) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        
        List<String> fgroups = new ArrayList<String>();
        
        if (con == null)
        {
            return new ArrayList<FileGroup>();
        }
        else if (!con.isOpen())
        {
            return new ArrayList<FileGroup>();
        }
        // make a query. get all elements that are the object of statements with
        // the predicate "<http://toif/path>".
        String queryString = "SELECT ?x WHERE {?y <http://toif/path> ?x . ?z <http://toif/toif:FindingHasCodeLocation> ?y .}";
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        
        // evaluate the query.
        TupleQueryResult result = tupleQuery.evaluate();
        try
        {
            // for each of the results. make a new filegroup.
            while (result.hasNext())
            {
                // get the binding set.
                BindingSet bindingSet = result.next();
                Value valueOfX = bindingSet.getValue("x");
                if (!fgroups.contains(valueOfX.stringValue()))
                {
                    fgroups.add(valueOfX.stringValue());
                    // create the filegroup.
                    FileGroup files = new FileGroup(valueOfX.stringValue());
                    files.setParent(project);
                    
                    // add the files to the project.
                    project.AddFileGroup(files);
                }
            }
        }
        finally
        {
            result.close();
        }
        // return the list of filegroups.
        return project.getFileGroup();
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
    private static List<ToolGroup> addFindings(final IToifProject project, LocationGroup locationGroup) throws RepositoryException,
            MalformedQueryException, QueryEvaluationException
    {
        for (CachedCodeLocation ccl : codeLocationCache.get(locationGroup.getPath()).values())
        {
            if (ccl.matches(locationGroup.getPath(), locationGroup.getToifLineNumber()))
            {
                ++countCodeLocations;
                int findingCount = getFinding(project, locationGroup, ccl);
                if (findingCount != ccl.getFindings().size())
                {
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
     * query the repository for the location groups (lineNumbers). these are
     * added to the project and also returned as a list.
     * 
     * @param file
     *            the file-group which is the parent for this locationgroup.
     * @return a list of the locationgroups within this file group.
     * 
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static List<LocationGroup> addLocationGroupings(IToifProject project, FileGroup file) throws RepositoryException,
            MalformedQueryException, QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        
        // get all the code locations that fall within this file. A better way
        // to do this whole method would be to use the OPTIONAL query to only
        // select the kdm location if it is present.
        String codeLocationString = "SELECT ?codeLocation ?lineNumber  WHERE { ?codeLocation <http://toif/path> \"" + file.getPath()
                + "\" . ?codeLocation <http://toif/lineNumber> ?lineNumber}";
        
        TupleQuery codeLocationQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, codeLocationString);
        
        TupleQueryResult codeLocations = codeLocationQuery.evaluate();
        
        try
        {
            // for each of the codeLocations we need to check to see if there is
            // a kdm location for them. if not, just use the toif.
            while (codeLocations.hasNext())
            {
                BindingSet codeLocationsBindingSet = codeLocations.next();
                
                Value codeLocation = codeLocationsBindingSet.getValue("codeLocation");
                
                Value toifLineNumber = codeLocationsBindingSet.getValue("lineNumber");
                
                // this is the string to try to find a kdm location.
                String kdmQueryString = "SELECT ?line WHERE { <"
                        + codeLocation
                        + "> <http://org.omg.kdm/CommonView> ?commonView . ?commonView <http://org.omg.kdm/KdmView> ?kdmElement . ?kdmElement <http://org.omg.kdm/SourceRef> ?line . } ";
                TupleQuery kdmQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, kdmQueryString);
                
                TupleQueryResult result = kdmQuery.evaluate();
                
                // if the kdm query does not have any result, then use the toif
                // query to just use the toif data. replacing the result with
                // this new value.
                if (!result.hasNext())
                {
                    String toifQueryString = "SELECT ?line  WHERE { <" + codeLocation + "> <http://toif/lineNumber> ?line . }";
                    
                    TupleQuery toifQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, toifQueryString);
                    
                    // replace the result with the toif value.
                    result = toifQuery.evaluate();
                    
                    if (!result.hasNext())
                    {
                        continue;
                    }
                }
                
                try
                {
                    do
                    {
                        BindingSet lineNumberBindingSet = result.next();
                        Value toifOrKdmLineNumber = lineNumberBindingSet.getValue("line");
                        
                        // create a new location.
                        String toifOrKdmLineNumberValue = toifOrKdmLineNumber.stringValue();
                        
                        // get the line number value from the string.
                        if (toifOrKdmLineNumberValue.contains(";"))
                        {
                            String[] sourceRefValues = toifOrKdmLineNumberValue.split(";");
                            toifOrKdmLineNumberValue = sourceRefValues[1];
                        }
                        LocationGroup location = new LocationGroup(file.getPath(), toifLineNumber.stringValue());
                        
                        location.setRealLineNumber(toifOrKdmLineNumberValue);
                        
                        location.setParent(file);
                        
                        // add the location to the filegroup.
                        file.AddLocation(location);
                        
                    }
                    while (result.hasNext());
                }
                finally
                {
                    result.close();
                }
                
            }
            
        }
        finally
        {
            codeLocations.close();
        }
        // return the list of location groups.
        return file.getLocationGroup();
        
    }
    
    /**
     * Get the finding based on the fact "FindingHasCodeLocation" we do this
     * because we already have the locations.
     * 
     * This method also adds toolgroups to the locationgroups.
     * 
     * @param locationGroup
     *            The location grouping for this finding.
     * @param codeLocation
     *            The value of the location.
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static int getFinding(IToifProject project, LocationGroup locationGroup, CachedCodeLocation ccl) throws RepositoryException,
            MalformedQueryException, QueryEvaluationException
    {
        int count = 0;
        List<Value> findings = ccl.getFindings();
        for (Value finding : findings)
        {
            FindingEntry entry = new FindingEntry(finding, project.getRepository());
            
            initializeTrust(project, entry);
            initializeIsOk(project, entry);
            
            // get the tool.
            ToolGroup tool = getTool(project, finding);
            
            // add the tool group to the location group
            tool = locationGroup.AddToolGroup(tool);
            
            // add the finding entry to the tool group.
            entry.setParent(tool);
            
            tool.setParent(locationGroup);
            
            // give the findings a weakness description.
            entry.setDescription(getWeaknessDescription(project, finding));
            entry.setTraces(getTraces(project, finding));
            entry.setCwe(getCwe(project, finding));
            entry.setSfp(getSfp(project, finding));
            if (!tool.addFinding(entry))
            {
                System.err.println("  - " + ccl.getPath() + " " + ccl.getLineNumber());
            }
            ++count;
        }
        return count;
    }
    
    /**
     * get the cwe name for the finding
     * 
     * @param project
     *            the toif project
     * @param finding
     *            the finding that we wish to find the cwe for
     * @return the name of the cwe.
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static String getCwe(final IToifProject project, Value finding) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        
        // make a query. this finds the findings cwe id, then uses
        // its name.
        String descriptionQuery = "SELECT ?cwe WHERE {<" + finding
                + "> <http://toif/toif:FindingHasCWEIdentifier> ?id. ?id <http://toif/name> ?cwe .}";
        TupleQuery cweQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
        
        // evaluate the query.
        TupleQueryResult cweNameResult = cweQuery.evaluate();
        
        try
        {
            // use the first cwe name we come across. there should be only
            // one anyway.
            while (cweNameResult.hasNext())
            {
                BindingSet cweSet = cweNameResult.next();
                Value cweName = cweSet.getValue("cwe");
                
                // return the sfp name.
                return cweName.stringValue();
            }
        }
        finally
        {
            cweNameResult.close();
        }
        // there is no sfp name.
        return " - ";
    }
    
    /**
     * Find the weakness description for this finding value.
     * 
     * @param finding
     *            The finding value as found in getFinding().
     * @return The weakness description.
     * 
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static String getWeaknessDescription(final IToifProject project, Value finding) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        // make a query. this finds the findings weakness description, then uses
        // its description text.
        String descriptionQuery = "SELECT ?t WHERE {" + "<" + finding + "> <http://toif/toif:FindingIsDescribedByWeaknessDescription> ?d. "
                + "?d <http://toif/description> ?t." + "}";
        TupleQuery descriptionTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
        
        // evaluate the query.
        TupleQueryResult descriptionResult = descriptionTupleQuery.evaluate();
        
        try
        {
            // use the first description text we come across. there should be
            // only
            // one anyway.
            while (descriptionResult.hasNext())
            {
                BindingSet descriptionSet = descriptionResult.next();
                Value valueOfdescription = descriptionSet.getValue("t");
                
                // return the description text.
                return valueOfdescription.stringValue();
            }
        }
        finally
        {
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
     *            the finding that we are trying to find a toolgroup for.
     * @return the toolgroup. or 'Unknown Tool' if none can be found.
     * 
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static ToolGroup getTool(IToifProject project, Value finding) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        
        RepositoryConnection con = project.getRepositoryConnection();
        // make a query to find the adaptor name. we are looking for something
        // (the segment) that contains this finding, it also contains another
        // element (the adaptor) with the type 'toif:Adaptor'. this adaptor has
        // a name 'name'.
//         String adaptorQuery = "SELECT ?name WHERE { " +
//         "?id <http://toif/contains> <" + finding + ">. " +
//         "?id <http://toif/contains> ?adaptorId. "
//         + "?adaptorId <http://toif/type> \"toif:Adaptor\". " +
//         "?adaptorId <http://toif/name> ?name.}";
        
//        String adaptorQuery = "SELECT ?name WHERE { " + "?id <http://toif/contains> <" + finding + ">. "
//                + "?id <http://toif:TOIFSegmentIsProcessedByAdaptor> ?adaptorId. " + "?adaptorId <http://toif/name> ?name.}";
        
        String adaptorQuery = "SELECT ?name WHERE { " + "?id <http://toif/contains> <" + finding + ">. "
                + "?id <http://toif/toif:TOIFSegmentIsProcessedByAdaptor> ?adaptor. ?adaptor <http://toif/name> ?name}";
        
        TupleQuery adaptorTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, adaptorQuery);
        
        // query the repository.
        TupleQueryResult queryResult = adaptorTupleQuery.evaluate();
        
        try
        {
            // for each of the results, create a new toolgroup. return the
            // toolgroup
            // if there is one.
            while (queryResult.hasNext())
            {
                
                BindingSet adaptorSet = queryResult.next();
                Value adaptorName = adaptorSet.getValue("name");
                // return the tool group since we have one.
                return new ToolGroup(adaptorName.stringValue());
                
            }
        }
        finally
        {
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
     *            the toif project
     * @param finding
     *            the finding that we want to find the sfp for
     * @return
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static String getSfp(final IToifProject project, Value finding) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        // make a query. this finds the findings sfp id, then uses
        // its name.
        String descriptionQuery = "SELECT ?sfp WHERE {" + "<" + finding + "> <http://toif/toif:FindingHasSFPIdentifier> ?id. "
                + "?id <http://toif/name> ?sfp ." + "}";
        TupleQuery sfpQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
        
        // evaluate the query.
        TupleQueryResult descriptionResult = sfpQuery.evaluate();
        try
        {
            
            // use the first sfp name we come across. there should be only
            // one anyway.
            while (descriptionResult.hasNext())
            {
                BindingSet sfpSet = descriptionResult.next();
                Value sfpName = sfpSet.getValue("sfp");
                // return the sfp name.
                return sfpName.stringValue();
            }
        }
        finally
        {
            descriptionResult.close();
        }
        // there is no sfp name.
        return " - ";
    }
    
    /**
     * get the traces for this finding from the repository.
     * 
     * @param finding
     *            the finding we wwant to find traces for
     * @return list of traces.
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private static List<Trace> getTraces(final IToifProject project, Value finding) throws RepositoryException, MalformedQueryException,
            QueryEvaluationException
    {
        RepositoryConnection con = project.getRepositoryConnection();
        
        List<Trace> traces = new ArrayList<Trace>();
        
        String descriptionQuery = "SELECT ?lineNumber WHERE {" + "?segment <http://toif/contains> <" + finding + "> . "
                + "?segment <http://toif/contains> ?statement . " + "?statement <http://toif/toif:StatementIsInvolvedInFinding> <" + finding + "> . "
                + "?statement <http://toif/toif:StatementHasCodeLocation> ?codeLocation . "
                + "?codeLocation <http://toif/lineNumber> ?lineNumber . }";
        TupleQuery traceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, descriptionQuery);
        
        // evaluate the query.
        TupleQueryResult traceNameResult = traceQuery.evaluate();
        
        try
        {
            
            while (traceNameResult.hasNext())
            {
                BindingSet traceSet = traceNameResult.next();
                Value traceName = traceSet.getValue("lineNumber");
                
                Trace trace = new Trace(traceName.stringValue());
                traces.add(trace);
            }
        }
        finally
        {
            traceNameResult.close();
        }
        
        return traces;
    }
    
    /**
     * initialize if this finding is a real weakness or not.
     * 
     * @param project
     *            the toif project
     * @param entry
     *            the finding entry in the report view.
     */
    private static void initializeIsOk(final IToifProject project, IFindingEntry entry)
    {
        ValueFactory factory = project.getValueFactory();
        RepositoryConnection con = project.getRepositoryConnection();
        URI isWeaknessURI = factory.createURI("http://toif/isWeakness");
        URI findingURI = factory.createURI(entry.getFindingId());
        try
        {
            String isWeaknessQuery = "SELECT ?isWeakness WHERE {<" + entry.getValue() + "> <http://org.omg.kdm/isWeakness> ?isWeakness .}";
            TupleQuery isWeaknessTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, isWeaknessQuery);
            
            TupleQueryResult isWeaknessResults = isWeaknessTupleQuery.evaluate();
            
            while (isWeaknessResults.hasNext())
            {
                BindingSet findingSet = isWeaknessResults.next();
                Value isWeaknessValue = findingSet.getValue("isWeakness");
                entry.setIsOk(!Boolean.parseBoolean(isWeaknessValue.stringValue()));
                
                // there will be only one, return if we have reached this far.
                return;
            }
            
            con.add(findingURI, isWeaknessURI, factory.createLiteral("false"));
            
        }
        catch (RepositoryException e)
        {
            System.err.println("There was a repository exception while setting whether the weakness is true or not. " + e);
        }
        catch (MalformedQueryException e)
        {
            System.err.println("There is a mal formed query while setting whether the weakness is true or not. " + e);
        }
        catch (QueryEvaluationException e)
        {
            System.err.println("There is a query exception while finding out if the finding is true or not " + e);
        }
        
    }
    
    /**
     * make sure that the trust statement exists. if not, make the statement and
     * initialize it with the correct starting value.
     * 
     * @param entry
     *            the findings for which we are doing the trust operations.
     */
    private static void initializeTrust(final IToifProject project, IFindingEntry entry)
    {
        ValueFactory factory = project.getValueFactory();
        RepositoryConnection con = project.getRepositoryConnection();
        URI trustURI = factory.createURI("http://toif/trust");
        URI findingURI = factory.createURI(entry.getFindingId());
        try
        {
            String trustQuery = "SELECT ?trust WHERE {<" + entry.getValue() + "> <http://toif/trust> ?trust .}";
            TupleQuery trustTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, trustQuery);
            
            TupleQueryResult trustResults = trustTupleQuery.evaluate();
            
            while (trustResults.hasNext())
            {
                BindingSet findingSet = trustResults.next();
                Value trust = findingSet.getValue("trust");
                
                entry.setTrust(Integer.parseInt(trust.stringValue()));
                
                // there will be only one, return if we have reached this far.
                return;
            }
            
            // if we reach this far then there were no trusts.
            con.add(findingURI, trustURI, factory.createLiteral(0));
            entry.setTrust(0);
        }
        catch (MalformedQueryException e)
        {
            e.printStackTrace();
        }
        catch (QueryEvaluationException e)
        {
            e.printStackTrace();
        }
        catch (RepositoryException e)
        {
            e.printStackTrace();
        }
        
    }
    
}
