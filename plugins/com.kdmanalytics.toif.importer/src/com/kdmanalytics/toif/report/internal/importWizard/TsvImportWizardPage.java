/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.importWizard;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

import com.kdmanalytics.toif.report.internal.items.FileGroup;
import com.kdmanalytics.toif.report.internal.providers.TOIFImportTreeContentProvider;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.ui.common.FindingData;
import com.kdmanalytics.toif.ui.views.FindingView;
import com.kdmanalytics.toif.util.MemberUtil;

/**
 * Import *.tsv Citing file and apply the data to the existing defect database
 * 
 * @author Ken Duck
 *         
 */
public class TsvImportWizardPage extends WizardPage implements Listener, ISelectionChangedListener, ModifyListener {
  
  private static final Logger LOG = Logger.getLogger(TsvImportWizardPage.class);
  
  private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;
  
  private static final int SIZING_SELECTION_PANE_WIDTH = 320;
  
  protected FileFieldEditor editor;
  
  boolean complete = false;
  
  private IProject project;
  
  private TOIFImportTreeContentProvider contentprovider;
  
  /**
   * The digest allows us to generate simple and short unique IDs for each finding.
   */
  private static MessageDigest digest;
  
  static {
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 
   * @param pageName
   * @param selection
   */
  public TsvImportWizardPage(String pageName, IStructuredSelection selection) {
    super(pageName);
    setPageComplete(false);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets .Composite)
   */
  @Override
  public void createControl(Composite parent) {
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
  private void createProjectTree(Composite page) {
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
  private void createFileSelectionArea(Composite page) {
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
    String[] extensions = new String[] {
                                         "*.tsv", "."
    };
    editor.setFileExtensions(extensions);
    // fileSelectionArea.moveAbove(null);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets. Event)
   */
  @Override
  public void handleEvent(Event event) {
    setPageComplete();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
   * org.eclipse.jface.viewers.SelectionChangedEvent)
   */
  @Override
  public void selectionChanged(SelectionChangedEvent event) {
    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
    // Unset target folder
    project = null;
    
    if (selection.isEmpty()) {
      setErrorMessage("A target project must be selected");
      return;
    }
    Object first = selection.getFirstElement();
    if (first instanceof IProject) {
      project = (IProject) first;
      setPageComplete();
      return;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events .ModifyEvent)
   */
  @Override
  public void modifyText(ModifyEvent e) {
    setPageComplete();
  }
  
  /**
   * Called to determine if the data is consistent, and if it is then set the page complete boolean.
   * 
   */
  private void setPageComplete() {
    // Default is page is not complete
    setPageComplete(false);
    setMessage(null);
    setErrorMessage(null);
    
    // Check input file
    String name = editor.getStringValue();
    if (name == null) {
      setErrorMessage("No input file specified");
      return;
    }
    
    if (name.isEmpty()) {
      setErrorMessage("No input file specified");
      return;
    }
    
    if (project == null) {
      setErrorMessage("Select a target project");
      return;
    }
    
    // Check for file existence
    IPath location = new Path(name);
    IFileStore srcStore = EFS.getLocalFileSystem().getStore(location);
    if (!srcStore.fetchInfo().exists()) {
      setErrorMessage("Source file " + srcStore + " does not exist");
      return;
    }
    
    // Check target folder
    else this.setMessage("Click finish to import into existing repository");
    
    // If we get here the results are good.
    setPageComplete(true);
  }
  
  /**
   * Perform the actual load.
   * 
   * @return
   */
  public boolean finish() {
    // Check source file
    final String name = editor.getStringValue();
    setErrorMessage("Importing " + name + " into " + project + "...");
    IPath location = new Path(name);
    File file = location.toFile();
    
    Reader in = null;
    CSVParser parser = null;
    try {
      in = new FileReader(file);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter('\t').withIgnoreEmptyLines();
      
      parser = new CSVParser(in, format);
      
      System.err.println("FILE: " + name);
      
      Map<Integer, String> lookup = new HashMap<Integer, String>();
      boolean header = true;
      
      for (CSVRecord record : parser) {
        int size = record.size();
        
        IFile ifile = null;
        String tool = null;
        String description = null;
        int line = 0;
        int offset = 0;
        int trust = 0;
        Boolean status = null;
        int kdmLine = 0;
        String cwe = null;
        String sfp = null;
        
        // Read the header first
        if (header) {
          System.err.print("  ");
          for (int i = 0; i < size; i++) {
            if (i > 0) System.err.print(",");
            String cell = record.get(i);
            lookup.put(i, cell);
            System.err.print(cell);
          }
          header = false;
          System.err.println();
          System.err.println("  ------------------------------------------");
        }
        
        // Otherwise this is a data row
        else {
          for (int i = 0; i < size; i++) {
            String cell = record.get(i);
            String colName = lookup.get(i);
            if ("Resource".equals(colName)) {
              IFileGroup group = new FileGroup(cell);
              try {
                IResource resource = MemberUtil.findMembers(project, group);
                if (resource != null) {
                  ifile = (IFile) resource;
                }
              } catch (CoreException e) {
                e.printStackTrace();
              }
            } else if ("SFP".equals(colName)) {
              sfp = cell;
            } else if ("CWE".equals(colName)) {
              cwe = cell;
            }
            // Valid is *old* name for "Citing Status"
            else if ("Valid".equals(colName)) {
              if (cell != null && !cell.trim().isEmpty()) {
                status = Boolean.parseBoolean(cell);
              }
            } else if ("Citing Status".equals(colName)) {
              if (cell != null && !cell.trim().isEmpty()) {
                status = Boolean.parseBoolean(cell);
              }
            } else if ("Trust".equals(colName)) {
              if (cell != null && !cell.trim().isEmpty()) {
                try {
                  trust = Integer.parseInt(cell);
                } catch (NumberFormatException e) {}
              }
            } else if ("Line Number".equals(colName)) {
              if (cell != null && !cell.trim().isEmpty()) {
                try {
                  line = Integer.parseInt(cell);
                } catch (NumberFormatException e) {}
              }
            } else if ("KDM Line Number".equals(colName)) {
              if (cell != null && !cell.trim().isEmpty()) {
                try {
                  kdmLine = Integer.parseInt(cell);
                } catch (NumberFormatException e) {}
              }
            }
            // "Generator Tool" is *old* name for "SCA Tool"
            else if ("Generator Tool".equals(colName)) {
              tool = cell;
            } else if ("SCA tool".equalsIgnoreCase(colName)) {
              tool = cell;
            } else if ("Weakness Description".equals(colName)) {
              description = cell;
            } else {
              System.err.println("WARNING: Unknown column name '" + colName + "'");
            }
          }
          
          System.err.print("  ");
          System.err.print(sfp);
          System.err.print(",");
          System.err.print(cwe);
          System.err.print(",");
          System.err.print(status);
          System.err.print(",");
          System.err.print(trust);
          System.err.print(",");
          System.err.print(ifile);
          System.err.print(",");
          System.err.print(line);
          System.err.print(",");
          System.err.print(kdmLine);
          System.err.print(",");
          System.err.print(tool);
          System.err.print(",");
          System.err.print(description);
          System.err.println();
          
          if (ifile != null) {
            // Create an associated finding. This will allow us to
            // set the trust and citing status for the finding. If the
            // finding does not actually exist in the database this information
            // is still stored in case the finding exists in the future.
            FindingData finding = new FindingData(ifile, tool, description, line, offset, cwe, sfp);
            finding.setTrust(trust);
            if (status != null) {
              finding.cite(status);
            }
          }
        }
      }
      
      try {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
          IWorkbenchPage page = window.getActivePage();
          if (page != null) {
            FindingView view = (FindingView) page.showView("com.kdmanalytics.toif.views.FindingView");
            view.refresh();
          }
          
        }
      } catch (PartInitException e) {
        e.printStackTrace();
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (parser != null) {
        try {
          parser.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    // PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
    // {
    // public void run()
    // {
    // final ToifReportImportJob job = new ToifReportImportJob("Import SFP/CWE Data", project,
    // name);
    // job.setUser(true);
    // job.setPriority(Job.BUILD);
    // job.setRule(project);
    // job.schedule();
    // }
    // });
    
    return true;
  }
  
  /**
   * Returns a unique ID for the finding. Uses an MD5 checksum for uniqueness while keeping the
   * value reasonably short.
   * 
   * @return
   */
  public String getUniqueId(String tool, int line, int offset, String cwe, String description) {
    if (digest != null) {
      String id = tool + ":" + line + ":" + offset + ":" + cwe + ":" + description;
      return getHex(digest.digest(id.getBytes()));
    } else {
      // Fall back that should never ever be required. Not necessarily unique.
      String id = tool + ":" + line + ":" + offset + ":" + cwe;
      return id;
    }
  }
  
  /**
   * 
   * @param hash
   * @return
   */
  private String getHex(byte[] hash) {
    StringBuffer hexString = new StringBuffer();
    
    for (int i = 0; i < hash.length; i++) {
      if ((0xff & hash[i]) < 0x10) {
        hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
      } else {
        hexString.append(Integer.toHexString(0xFF & hash[i]));
      }
    }
    
    return hexString.toString();
  }
  
}
