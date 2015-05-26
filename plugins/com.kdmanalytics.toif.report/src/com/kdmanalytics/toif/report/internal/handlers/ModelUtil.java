/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.ProjectFactory;
import com.kdmanalytics.toif.report.internal.views.ReportView;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * Utilities for the toif model.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ModelUtil
{
    
    private ModelUtil()
    {
        // private constructor to prevent construction
    }
    
    /**
     * Sets the trust values on findings.
     * 
     * @param ss
     *            the ss
     * @param treeViewer
     *            the tree viewer
     * @param value
     *            the value
     */
    public static void setTrustValuesOnFindings(final Collection<FindingEntry> findingEntries, final IToifProject project, final Integer value,
            final IProgressMonitor monitor)
    {
        SubMonitor progress = SubMonitor.convert(monitor, 100);
        SubMonitor loopProgress = progress.newChild(100).setWorkRemaining(findingEntries.size());
        
        final Repository rep = project.getRepository();
        final List<FindingEntry> entries = new LinkedList<FindingEntry>();
        for (FindingEntry entry : findingEntries)
        {
            // get entry tool name
            String toolName = entry.getTool().toString();
            
            // get entry weakness term.
            String descriptor = entry.getDescription().split(":")[0];
            
            List<FindingEntry> projEntries = entry.getProject().getFindingEntries();
            SubMonitor innerLoopProgress = loopProgress.newChild(100).setWorkRemaining(projEntries.size());
            
            // for each finding, match the weakness term and tool
            // name.
            // if a match, set its trust.
            for (FindingEntry finding : projEntries)
            {
                // get entry tool name
                String findingToolName = finding.getTool().toString();
                innerLoopProgress.setTaskName(findingToolName);
                
                // get entry weakness term.
                String findingDescriptior = finding.getDescription();
                
                if (findingToolName.equals(toolName) && findingDescriptior.startsWith(descriptor))
                {
                    finding.setTrust(value);
                    //
                    // treeViewer.update(finding, null);
                    entries.add(finding);
                    setTrustInRepository(finding, rep, value);
                }
                
                innerLoopProgress.worked(1);
            }
            
        }
        
    }
    
    /**
     * Sets the trust in repository.
     * 
     * @param finding
     *            the finding
     * @param rep
     *            the rep
     * @param value
     *            the value
     */
    private static void setTrustInRepository(IFindingEntry finding, Repository rep, int value)
    {
        try
        {
            ValueFactory factory = rep.getValueFactory();
            RepositoryConnection con = rep.getConnection();
            
            URI trustURI = factory.createURI("http://toif/trust");
            URI findingURI = factory.createURI(finding.getFindingId());
            
            con.remove(findingURI, trustURI, null);
            
            con.add(findingURI, trustURI, factory.createLiteral(value));
        }
        catch (RepositoryException e)
        {
            System.err.println("Could not add or remove the trust statements in the repository: " + e);
        }
        
    }
    
    /**
     * Create the project model
     * 
     * @param s
     */
    public static void buildModel(ISelection s)
    {
        final ReportView view = (ReportView) openView();
        // final TreeViewer treeViewer = view.getViewer();
        
        boolean inWorkbench = false;
        IPerspectiveDescriptor[] desc = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives();
        
        for (IPerspectiveDescriptor iPerspectiveDescriptor : desc)
        {
            if (iPerspectiveDescriptor.getId().equals("com.kdmanalytics.kdmwb.kdm.ui.KdmPerspective"))
            {
                inWorkbench = true;
                break;
            }
        }
        
        if (s instanceof IStructuredSelection)
        {
            IProject iProject = null;
            
            for (Object object : ((IStructuredSelection) s).toArray())
            {
                if (object instanceof IProject)
                {
                    iProject = (IProject) object;
                }
            }
            
            if (iProject != null)
            {
                
                File file = new File(iProject.getLocation() + "/.toifProject.ser");
                
                if (file.exists())
                {
                    
                    try
                    {
                        FileInputStream fileIn = new FileInputStream(file);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        IToifProject project = (IToifProject) in.readObject();
                        in.close();
                        fileIn.close();
                        
                        if (project != null)
                        {
                            System.out.println("toif project deserialized");
                            // view.getTableViewer().setInput(project);
                            
                            IFolder repoFolder = ensureKdmRepoFolderExists(iProject);
                            project.setRepository(repoFolder);
                            project.setIProject(repoFolder.getProject());
                            // project.setIProject(iProject.getLocation());
                            
                            view.clearInput();
                            view.updateInput(project);
                            view.refresh();
                            return;
                        }
                    }
                    catch (IOException i)
                    {
                        i.printStackTrace();
                        // return;
                    }
                    catch (ClassNotFoundException c)
                    {
                        System.out.println("IToifProject class not found");
                        c.printStackTrace();
                        // return;
                    }
                    catch (CoreException e)
                    {
                        System.out.println("repo folder could not be found");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            
            final boolean wbValue = inWorkbench;
            final IFolder folder = getFolderFromSelection((IStructuredSelection) s);
            if (folder != null)
            {
                ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
                try
                {
                    IRunnableWithProgress runnable = new IRunnableWithProgress() {
                        
                        @Override
                        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
                        {
                            monitor.setTaskName("Populating TOIF View.");
                            final IToifProject project = ProjectFactory.createProjectModel(folder, wbValue, monitor);
                            
                            // have to set the input on the UI thread
                            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                                
                                @Override
                                public void run()
                                {
                                    view.setReportInput(project);
                                }
                            });
                            
                        }
                    };
                    
                    dialog.run(true, false, runnable);
                }
                catch (InvocationTargetException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        }
    }
    
    /**
     * open the report view.
     * 
     * @return
     */
    private static IViewPart openView()
    {
        try
        {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null)
            {
                IWorkbenchPage page = window.getActivePage();
                if (page != null)
                {
                    return page.showView(ReportView.VIEW_ID);
                    
                }
                
            }
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * get the project folder from the selection
     * 
     * @param selection
     * @return
     */
    private static IFolder getFolderFromSelection(IStructuredSelection selection)
    {
        IFolder folder = null;
        for (Object object : selection.toArray())
        {
            try
            {
                if (object instanceof IProject)
                {
                    IProject project = (IProject) object;
                    folder = ensureKdmRepoFolderExists(project);
                }
                else if (object instanceof IJavaProject)
                {
                    IJavaProject project = (IJavaProject) object;
                    folder = ensureKdmRepoFolderExists(project.getProject());
                }
            }
            catch (CoreException ce)
            {
                ce.printStackTrace();
                return null;
            }
        }
        return folder;
    }
    
    /**
     * makes sure that the kdm repository folder does exist.
     * 
     * @param project
     *            the kdm project
     * @throws CoreException
     */
    private static IFolder ensureKdmRepoFolderExists(final IProject project) throws CoreException
    {
        IFolder kdmDataDir = project.getFolder(".KDM");
        if (!kdmDataDir.exists())
        {
            kdmDataDir.create(true, true, null);
        }
        
        IFolder kdmRepoDir = kdmDataDir.getFolder("repository");
        if (!kdmRepoDir.exists())
        {
            kdmRepoDir.create(true, true, null);
        }
        return kdmRepoDir;
    }
    
    /**
     * gets the project folder ready.
     * 
     * @param folder
     * @throws CoreException
     */
    public void prepareFolder(IFolder folder) throws CoreException
    {
        if (!folder.exists())
        {
            folder.create(true, true, null);
        }
        
        IContainer parent = folder.getParent();
        if (parent instanceof IFolder)
        {
            prepareFolder((IFolder) parent);
        }
    }
    
}
