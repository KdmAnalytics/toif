/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.importWizard;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

import com.kdmanalytics.toif.report.internal.providers.TOIFImportTreeContentProvider;

/**
 * Import a TOIF file into the internal defect database.
 * 
 * @author Ken Duck
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ToifReportImportWizardPage extends WizardPage implements Listener, ISelectionChangedListener, ModifyListener
{
    
    private static final Logger LOG = Logger.getLogger(ToifReportImportWizardPage.class);
    
    private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;
    
    private static final int SIZING_SELECTION_PANE_WIDTH = 320;
    
    protected FileFieldEditor editor;
    
    boolean complete = false;
    
    /**
     * Target folder
     * 
     */
    private IFolder repositoryFolder = null;
    
    private IProject project;
    
    private TOIFImportTreeContentProvider contentprovider;
    
    /**
     * create the import page
     * 
     * @param pageName
     * @param selection
     */
    public ToifReportImportWizardPage(String pageName, IStructuredSelection selection)
    {
        super(pageName);
        setPageComplete(false);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    public void createControl(Composite parent)
    {
        LOG.debug("Creating import page");
        // The main page
        Composite page = new Composite(parent, SWT.None);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        page.setLayout(gl);
        
        createFileSelectionArea(page);
        
        createProjectTree(page);
        
        // Set the page's control
        setControl(page);
    }
    
    /**
     * create the project tree
     * 
     * @param page
     */
    private void createProjectTree(Composite page)
    {
        Label label = new Label(page, SWT.NONE);
        label.setText("Select target project:");
        
        DrillDownComposite drillDown = new DrillDownComposite(page, SWT.BORDER);
        GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
        spec.widthHint = SIZING_SELECTION_PANE_WIDTH;
        spec.heightHint = SIZING_CONTAINER_GROUP_HEIGHT;
        drillDown.setLayoutData(spec);
        TreeViewer tree = new TreeViewer(drillDown, SWT.NONE);
        drillDown.setChildTree(tree);
        contentprovider = new TOIFImportTreeContentProvider();
        tree.setContentProvider(contentprovider);
        tree.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        tree.setComparator(new ViewerComparator());
        tree.setUseHashlookup(true);
        tree.setInput(ResourcesPlugin.getWorkspace());
        tree.addSelectionChangedListener(this);
    }
    
    /**
     * create the file selection area
     * 
     * @param page
     */
    private void createFileSelectionArea(Composite page)
    {
        Composite fileSelectionArea = new Composite(page, SWT.NONE);
        GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        fileSelectionArea.setLayoutData(fileSelectionData);
        
        GridLayout fileSelectionLayout = new GridLayout();
        fileSelectionLayout.numColumns = 3;
        fileSelectionLayout.makeColumnsEqualWidth = false;
        fileSelectionLayout.marginWidth = 0;
        fileSelectionLayout.marginHeight = 0;
        fileSelectionArea.setLayout(fileSelectionLayout);
        
        editor = new FileFieldEditor("fileSelect", "Select TOIF Data: ", fileSelectionArea);
        
        editor.setStringValue("");
        editor.getTextControl(fileSelectionArea).addModifyListener(this);
        String[] extensions = new String[] { "*.kdm", "." };
        editor.setFileExtensions(extensions);
        // fileSelectionArea.moveAbove(null);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
     * Event)
     */
    @Override
    public void handleEvent(Event event)
    {
        setPageComplete();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
     * org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        // Unset target folder
        project = null;
        repositoryFolder = null;
        
        if (selection.isEmpty())
        {
            setErrorMessage("A target project must be selected");
            return;
        }
        Object first = selection.getFirstElement();
        if (first instanceof IProject)
        {
            project = (IProject) first;
            IFolder toifFolder = project.getFolder(".KDM/repository");
            repositoryFolder = toifFolder.getFolder("repository");
            setPageComplete();
            return;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events
     * .ModifyEvent)
     */
    @Override
    public void modifyText(ModifyEvent e)
    {
        setPageComplete();
    }
    
    /**
     * Called to determine if the data is consistent, and if it is then set the
     * page complete boolean.
     * 
     */
    private void setPageComplete()
    {
        // Default is page is not complete
        setPageComplete(false);
        setMessage(null);
        setErrorMessage(null);
        
        // Check input file
        String name = editor.getStringValue();
        if (name == null)
        {
            setErrorMessage("No input file specified");
            return;
        }
        
        if (name.isEmpty())
        {
            setErrorMessage("No input file specified");
            return;
        }
        
        if (project == null)
        {
            setErrorMessage("Select a target project");
            return;
        }
        
        // Check for file existence
        IPath location = new Path(name);
        IFileStore srcStore = EFS.getLocalFileSystem().getStore(location);
        if (!srcStore.fetchInfo().exists())
        {
            setErrorMessage("Source file " + srcStore + " does not exist");
            return;
        }
        
        // Check target folder
        if (repositoryFolder == null || !repositoryFolder.exists())
            this.setMessage("Click finish to import into empty repository");
        else this.setMessage("Click finish to integrate with existing repository");
        
        // If we get here the results are good.
        setPageComplete(true);
    }
    
    /**
     * Perform the actual load.
     * 
     * @return
     */
    public boolean finish()
    {
        // Check source file
        String name = editor.getStringValue();
        setErrorMessage("Importing " + name + " into " + project + "...");
        
        final ToifReportImportJob job = new ToifReportImportJob("Import SFP/CWE Data", project, name);
        job.setUser(true);
        job.setPriority(Job.BUILD);
        job.setRule(project);
        job.schedule();
        
        return true;
    }
    
}
