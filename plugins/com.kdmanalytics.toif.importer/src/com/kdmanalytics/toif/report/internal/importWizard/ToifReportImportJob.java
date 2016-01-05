/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.importWizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

import com.kdmanalytics.toif.nature.ToifImportNatureUtil;
import com.kdmanalytics.toif.report.internal.items.FileGroup;
import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.ProjectFactory;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.internal.items.Trace;
import com.kdmanalytics.toif.report.items.IToifProject;
import com.kdmanalytics.toif.ui.common.IToifMarker;
import com.kdmanalytics.toif.ui.common.ToifUtilities;
import com.kdmanalytics.toif.ui.views.FindingView;
import com.kdmanalytics.toif.util.MemberUtil;

/**
 * job that actually imports the toif data.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 *         
 */
public class ToifReportImportJob extends Job {
  
  private final String name;
  
  private final IProject project;
  
  /**
   * new job
   * 
   * @param name
   * @param contentprovider
   */
  public ToifReportImportJob(String name, IProject project, String fname) {
    super(name);
    this.name = fname;
    this.project = project;
  }
  
  /**
   * copy the toif data.
   * 
   * @param sourceFile
   *          source
   * @param destinationFile
   *          destination file
   * @throws IOException
   */
  public void copy(File sourceFile, File destinationFile) throws IOException {
    
    FileUtils.copyDirectory(sourceFile, destinationFile);
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime. IProgressMonitor)
   */
  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      // Reset the PROJECT_CITE_DATE to force a new history file to be created
      project.setSessionProperty(FindingView.PROJECT_CITE_DATE, null);
    } catch (CoreException e) {
      e.printStackTrace();
    }
    File file = new File(name);
    File dataDir = new File(project.getFolder(".KDM/repository").getLocationURI());
    
    SubMonitor progress = SubMonitor.convert(monitor, 100);
    progress.beginTask("Running SFP/CWE Importer", 100);
    
    if (file.isDirectory()) {
      
      try {
        copy(file, dataDir);
      } catch (IOException e) {
        e.printStackTrace();
      }
      
    } else {
      try {
        FileUtils.deleteDirectory(dataDir);
        System.err.println("deleted");
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      
      Repository myRepository = new SailRepository(new NativeStore(dataDir));
      RepositoryConnection con = null;
      try {
        myRepository.initialize();
        
        con = myRepository.getConnection();
        
        try {
          String content = FileUtils.readFileToString(file, "UTF-8");
          
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
    }
    
    progress.worked(20);
    
    // Create markers for the findings imported by the repository.
    createMarkers(progress.newChild(80));
    
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      
      public void run() {
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
      }
    });
    
    // Unmark the project as "changed". This means that if the sources
    // within the project are changed after this point a warning dialog
    // will be presented to the user.
    try {
      project.setPersistentProperty(com.kdmanalytics.toif.ui.common.Activator.PROJECT_INCONSISTENT, null);
    } catch (CoreException e1) {
      e1.printStackTrace();
    }
    
    Job job = new Job("Enable Import Nature") {
      
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        try {
          ToifImportNatureUtil.enableImportNature(project);
        } catch (CoreException e) {
          e.printStackTrace();
        }
        return Status.OK_STATUS;
      }
    };
    
    // Start the Job
    job.setUser(true);
    job.setPriority(Job.BUILD);
    job.schedule();
    
    return Status.OK_STATUS;
  }
  
  /**
   * Create markers for the imported findings.
   */
  private void createMarkers(IProgressMonitor monitor) {
    File dataDir = new File(project.getFolder(".KDM/repository").getLocationURI());
    IFolder kdmIFolder = project.getFolder(".KDM");
    IFolder toifIFolder = project.getFolder(".KDM/TOIF");
    
    // Ensure appropriate directories exist
    try {
      if (!kdmIFolder.exists()) kdmIFolder.create(true, true, null);
      // Delete old TOIF data
      deltree(toifIFolder);
      if (!toifIFolder.exists()) toifIFolder.create(true, true, null);
    } catch (CoreException e) {
      e.printStackTrace();
    }
    
    final Repository myRepository = new SailRepository(new NativeStore(dataDir));
    try {
      // Clear markers
      ToifUtilities.clearEToifMarkers(project);
      
      // Prepare the repository
      myRepository.initialize();
      
      // Project aProject = new Project(myRepository, project, true);
      IToifProject tProject = ProjectFactory.createProjectModel(project.getFolder(".KDM/repository"), myRepository,
                                                                true, null);
      List<FileGroup> fileGroup = tProject.getFileGroup();
      SubMonitor progress = SubMonitor.convert(monitor, fileGroup.size());
      
      for (FileGroup fileG : fileGroup) {
        progress.setTaskName("Processing " + fileG.getPath());
        try {
          IResource resource = MemberUtil.findMembers(project, fileG);
          // If the resource does not exist, create one that does.
          if (resource == null) {
            IFile ifile = toifIFolder.getFile(fileG.getPath());
            if (!ifile.exists()) {
              IFolder parent = (IFolder) ifile.getParent();
              if (!parent.exists()) mkdirs(parent);
            }
            ifile.create(new ByteArrayInputStream("".getBytes()), true, null);
            resource = ifile;
          }
          
          List<FindingEntry> findings = fileG.getFindingEntries();
          if (findings != null) {
            for (FindingEntry finding : findings) {
              ToolGroup tool = finding.getTool();
              String markerId = getMarkerId(tool.getName());
              if (markerId != null) {
                String description = finding.getDescription();
                String cwe = finding.getCwe();
                String sfp = finding.getSfp();
                int line = 0;
                List<Trace> trace = finding.getTraces();
                if (trace != null) {
                  if (!trace.isEmpty()) {
                    Trace t = trace.get(0);
                    line = Integer.parseInt(t.getLineNumber());
                  }
                }
                try {
                  if (resource != null) {
                    IMarker m = resource.createMarker(markerId);
                    m.setAttribute(IMarker.LINE_NUMBER, line);
                    m.setAttribute(IMarker.MESSAGE, description);
                    m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
                    m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                    
                    if (cwe != null) {
                      m.setAttribute(IToifMarker.CWE, cwe);
                    }
                    
                    if (sfp != null) {
                      m.setAttribute(IToifMarker.SFP, sfp);
                    }
                  }
                } catch (CoreException e) {
                  System.err.println("Exception creating marker: " + e.getMessage());
                }
              }
            }
          }
        } catch (CoreException e) {
          e.printStackTrace();
        }
        
        progress.worked(1);
      }
      
      tProject.dispose();
      readOnly(project);
    } catch (RepositoryException | CoreException e1) {
      e1.printStackTrace();
    } finally {
      if (myRepository != null) {
        try {
          myRepository.shutDown();
        } catch (RepositoryException e) {
          e.printStackTrace();
        }
      }
      ;
    }
  }
  
  /**
   * Set the contents to be read only
   * 
   * @param project2
   * @throws CoreException
   */
  private void readOnly(IResource resource) throws CoreException {
    ResourceAttributes attrs = resource.getResourceAttributes();
    
    if (attrs.isHidden()) return;
    if (resource.getName().startsWith(".")) return;
    
    // Read only the resource
    if (resource instanceof IFile) {
      attrs.setReadOnly(true);
      ((IFile) resource).setResourceAttributes(attrs);
    }
    if (resource instanceof IFolder) {
      attrs.setReadOnly(true);
      ((IFolder) resource).setResourceAttributes(attrs);
    }
    
    // Read only the children
    if (resource instanceof IContainer) {
      IResource[] children = ((IContainer) resource).members();
      if (children != null) {
        for (IResource child : children) {
          readOnly(child);
        }
      }
    }
  }
  
  /**
   * Delete the file tree
   * 
   * @param folder
   * @throws CoreException
   */
  private void deltree(IFolder folder) throws CoreException {
    if (!folder.exists()) return;
    IResource[] members = folder.members();
    if (members != null) {
      for (IResource member : members) {
        if (member instanceof IFile) {
          member.delete(true, null);
        } else {
          deltree((IFolder) member);
        }
      }
    }
    folder.delete(true, null);
  }
  
  /**
   * Create a folder and its parents
   * 
   * @param parent
   * @throws CoreException
   */
  private void mkdirs(IContainer parent) throws CoreException {
    if (parent.exists()) {
      return;
    }
    mkdirs(parent.getParent());
    
    ((IFolder) parent).create(true, true, null);
  }
  
  /**
   * 
   * @param name2
   * @return
   */
  private String getMarkerId(String name) {
    if (name.startsWith("Findbugs")) return "com.kdmanalytics.toif.markers.FindbugsMarker";
    if (name.startsWith("JLint")) return "com.kdmanalytics.toif.markers.JlintMarker";
    if (name.startsWith("Rough")) return "com.kdmanalytics.toif.markers.RatsMarker";
    if (name.startsWith("Cppcheck")) return "com.kdmanalytics.toif.markers.CppcheckMarker";
    if (name.startsWith("Splint")) return "com.kdmanalytics.toif.markers.SplintMarker";
    return null;
  }
}
