/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.toif.report.internal.views.ReportView;
import com.kdmanalytics.toif.report.util.IRepositoryMaker;

/**
 * class for merging spreadsheet datat to the report view.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class MergeTSV extends AbstractHandler
{
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection s = HandlerUtil.getCurrentSelection(event);
        
        if (s instanceof StructuredSelection)
        {
            StructuredSelection structuredSelection = (StructuredSelection) s;
            
            IProject project = null;
            
            Object firstElement = structuredSelection.getFirstElement();
            if (firstElement instanceof IProject) {
                project = (IProject) firstElement;
            }
            
            Shell shell = HandlerUtil.getActiveShell(event);
            System.err.println(s);
            // make the dialog
            final File file = displayFileDialog(shell);
            
            if (file == null)
            {
                return null;
            }
            
            Repository repo = getRepositoryFromSelection(structuredSelection);
            
            if (repo != null)
            {
                if (project != null) {
                    File f = new File(project.getLocation() + "/.toifProject.ser");
                    f.delete();
                }
                processTSVFile(shell, file, repo);
                ReportView view = (ReportView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ReportView.VIEW_ID);
                
                if (view != null) {
                    view.updateInput(null);
                }
                ModelUtil.buildModel(s);
                
            }
            
            
            
        }
        
        return null;
    }
    
    /**
     * Process the tsv file. this method steps through each line of the TSV
     * file, extracts the details for that finding, and applies it to a matching
     * finding in the repository.
     * 
     * @param shell2
     * 
     * @param file
     *            the tsvFile that we are parsing
     * @param repo
     *            the event from the handler.
     */
    private void processTSVFile(final Shell shell, final File file, final Repository repo)
    {
        // count the line numbers.
        final int lineNumbers = countLineNumbers(file);
        
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
        
        try
        {
            dialog.run(true, true, new IRunnableWithProgress() {
                
                public void run(IProgressMonitor monitor)
                {
                    monitor.beginTask("Importing TSV File...", lineNumbers);
                    
                    BufferedReader bufRdr = null;
                    try
                    {
                        bufRdr = new BufferedReader(new FileReader(file));
                        
                        String line = null;
                        
                        RepositoryConnection con = null;
                        
                        try
                        {
                            con = repo.getConnection();
                        }
                        catch (RepositoryException e)
                        {
                            e.printStackTrace();
                        }
                        
                        if (con == null)
                        {
                            System.err.println("Could not create the repository connection...");
                            return;
                        }
                        
                        org.openrdf.model.ValueFactory factory = con.getValueFactory();
                        
                        // read each line of text file=
                        while ((line = bufRdr.readLine()) != null)
                        {
                            // split on a tab (tsv file).
                            String[] tokens = line.split("\t");
                            
                            if (tokens.length != 9)
                            {
                                monitor.worked(1);
                                continue;
                            }
                            
                            // get the details
                            String valid = tokens[2];
                            String trust = tokens[3];
                            String resource = tokens[4];
                            String lineNumber = tokens[5];
                            String description = tokens[8];
                            
                            try
                            {
                                String query = "SELECT ?finding ?description WHERE { ?codeLocation <http://toif/path> \""
                                        + resource
                                        + "\". ?codeLocation <http://toif/lineNumber> \""
                                        + lineNumber
                                        + "\". ?finding <http://toif/toif:FindingHasCodeLocation> ?codeLocation . ?finding <http://toif/toif:FindingIsDescribedByWeaknessDescription> ?descriptionId . ?descriptionId <http://toif/description> ?description }";
                                
                                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
                                
                                // query the repository.
                                TupleQueryResult queryResult = tupleQuery.evaluate();
                                
                                while (queryResult.hasNext())
                                {
                                    String[] descriptionTokens = description.split(":");
                                    
                                    if (descriptionTokens.length < 1)
                                    {
                                        continue;
                                    }
                                    String ident = descriptionTokens[0];
                                    
                                    BindingSet adaptorSet = queryResult.next();
                                    Value finding = adaptorSet.getValue("finding");
                                    Value desc = adaptorSet.getValue("description");
                                    
                                    if (!desc.stringValue().startsWith(ident))
                                    {
                                        continue;
                                    }
                                    
                                    URI findingURI = factory.createURI(finding.stringValue());
                                    
                                    // set the trust
                                    URI trustURI = factory.createURI("http://toif/trust");
                                    
                                    con.remove(findingURI, trustURI, null);
                                    
                                    con.add(findingURI, trustURI, factory.createLiteral(trust));
                                    
                                    // set the validity
                                    URI validURI = factory.createURI("http://toif/isWeakness");
                                    
                                    con.remove(findingURI, validURI, null);
                                    
                                    con.add(findingURI, validURI, factory.createLiteral(valid));
                                    
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
                            
                            // update the row for the progress.
                            monitor.worked(1);
                        }
                        
                        // close the file
                        bufRdr.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (bufRdr != null)
                        {
                            try
                            {
                                bufRdr.close();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    monitor.done();
                }
                
            });
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * get the repository from the selection.
     * 
     * @param s
     *            the event from the handler.
     * @return the repository.
     */
    private Repository getRepositoryFromSelection(final IStructuredSelection selection)
    {
        Repository repository = null;
        
        Object element = selection.getFirstElement();
        
        if (element instanceof IProject)
        {
            IProject iProject = (IProject) element;
            IFolder folder = iProject.getFolder(".KDM/repository");
            repository = getRepository(folder);
            
        }
        return repository;
    }
    
    /**
     * Count the line numbers in the file.
     * 
     * @param file
     *            the file in which to count the line numbers
     * @return the number of lines as an int.
     */
    private int countLineNumbers(File file)
    {
        // count the libne numbers for the progress
        LineNumberReader lnr = null;
        int lineNumbers = 0;
        try
        {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            lineNumbers = lnr.getLineNumber();
            lnr.close();
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (lnr != null)
            {
                try
                {
                    lnr.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return lineNumbers;
    }
    
    /**
     * display the dialog to find the tsv file.
     * 
     * @param event
     *            the event from the handler execute method.
     * @return the file that has been selected by the user.
     */
    private File displayFileDialog(final Shell shell)
    {
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        
        dialog.setFilterNames(new String[] { "TSV Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.tsv", "*.*" });
        dialog.setFilterPath(System.getProperty("user.dir"));
        dialog.setFileName("");
        
        final String open = dialog.open();
        if (open == null)
        {
            return null;
        }
        
        File file = new File(open);
        
        if (!file.exists())
        {
            MessageDialog.openQuestion(shell, "No File", "An incorrect file has been choosen...\nNothing will happen.");
            return null;
        }
        
        System.err.println(file.toString());
        return file;
    }
    
    /**
     * get the repository. this may be complicated by the fact that we dont know
     * if we are in the workbench or not.
     * 
     * think about turning this method into a utility...
     * 
     * @param folder
     *            the folder that contains the repository.
     * @return the repository.
     */
    public Repository getRepository(IFolder folder)
    {
        
        if (folder == null)
        {
            return null;
        }
        
        IRepositoryMaker repMaker = getRepositoryMaker(folder);
        Repository repository = repMaker.getRepository();
        
        try
        {
            repository.initialize();
            
        }
        catch (RepositoryException e)
        {
            
            e.printStackTrace();
        }
        
        return repository;
        
    }
    
    /**
     * get the repository maker. this is so that we can retreive the repository
     * if we are in the workbench or stand alone.
     * 
     * think about turning this method into a utility...
     * 
     * @param folder
     *            the folder which contains the repository.
     * @return the repository maker.
     */
    private IRepositoryMaker getRepositoryMaker(IFolder folder)
    {
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "com.kdmanalytics.toif.report.repositoryMaker.extensionpoint");
        try
        {
            for (IConfigurationElement e : config)
            {
                System.out.println("Evaluating extension");
                
                final Object o = e.createExecutableExtension("class");
                
                if (o instanceof IRepositoryMaker)
                {
                    IRepositoryMaker repositoryMaker = (IRepositoryMaker) o;
                    repositoryMaker.setFolder(folder);
                    repositoryMaker.createRepository();
                    return repositoryMaker;
                }
            }
        }
        catch (CoreException e)
        {
            System.err.println("There was a core excption... " + e);
        }
        return null;
    }
    
}
