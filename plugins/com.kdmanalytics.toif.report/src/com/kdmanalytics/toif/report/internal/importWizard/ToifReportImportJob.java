/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.importWizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 * job that actually imports the toif data.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ToifReportImportJob extends Job
{
    
    private final String name;
    
    private final IProject project;
    
    /**
     * new job
     * 
     * @param name
     * @param contentprovider
     */
    public ToifReportImportJob(String name, IProject project, String fname)
    {
        super(name);
        this.name = fname;
        this.project = project;
    }
    
    /**
     * copy the toif data.
     * 
     * @param sourceFile
     *            source
     * @param destinationFile
     *            destination file
     * @throws IOException
     */
    public void copy(File sourceFile, File destinationFile) throws IOException
    {
        
        FileUtils.copyDirectory(sourceFile, destinationFile);
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        File file = new File(name);
        
        File dataDir = new File(project.getFolder(".KDM/repository").getLocationURI());
        
        if (file.isDirectory())
        {
            
            monitor.beginTask("Running SFP/CWE Importer", IProgressMonitor.UNKNOWN);
            
            try
            {
                copy(file, dataDir);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
        else
        {
            
            try
            {
                FileUtils.deleteDirectory(dataDir);
                System.err.println("deleted");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            
            Repository myRepository = new SailRepository(new NativeStore(dataDir));
            RepositoryConnection con = null;
            try
            {
                monitor.beginTask("Running SFP/CWE Importer", IProgressMonitor.UNKNOWN);
                
                myRepository.initialize();
                
                con = myRepository.getConnection();
                
                try
                {
                    String content = FileUtils.readFileToString(file, "UTF-8");
                    
                    content = content.replace("KDM_Triple:1", "");
                    
                    if (!content.contains("<http://toif/"))
                    {
                        content = content.replaceAll("<", "<http://toif/");
                    }
                    File tempFile = File.createTempFile("toif", "kdm");
                    tempFile.deleteOnExit();
                    FileUtils.writeStringToFile(tempFile, content, "UTF-8");
                    con.add(tempFile, null, RDFFormat.NTRIPLES);
                }
                catch (IOException e)
                {
                    // Simple exception handling, replace with what's necessary
                    // for your use case!
                    throw new RuntimeException("Generating file failed", e);
                }
                
                
                
            }
            catch (RepositoryException e)
            {
                e.printStackTrace();
            }
            catch (RDFParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (con != null)
                    {
                        con.close();
                    }
                    myRepository.shutDown();
                }
                catch (RepositoryException e)
                {
                    e.printStackTrace();
                }
                
            }
            
            try
            {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window != null)
                {
                    IWorkbenchPage page = window.getActivePage();
                    if (page != null)
                    {
                        page.showView("com.kdmanalytics.toif.report.view");
                        
                    }
                    
                }
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
            
            return Status.OK_STATUS;
        }
        return Status.OK_STATUS;
    }
}
