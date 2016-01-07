/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.common;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.nature.ToifImportNature;

/**
 * Look through a delta for changed files for which the user should be informed of various events.
 * 
 * @author Ken Duck
 *        
 */
public class ResourceChangeDeltaPrinter implements IResourceDeltaVisitor {
  
  protected Set<IFile> citedResources;
  
  protected static boolean citingWarning = false;
  
  public ResourceChangeDeltaPrinter(Set<IFile> citedResource) {
    this.citedResources = citedResource;
  }
  
  /**
   * 
   */
  public boolean visit(IResourceDelta delta) throws CoreException {
    IResource res = delta.getResource();
    
    // Don't visit hidden and derived files
    if (res.isDerived()) return false;
    ResourceAttributes attrs = res.getResourceAttributes();
    if (attrs != null && attrs.isHidden()) {
      return false;
    }
    
    if (isSourceFile(res)) {
      final IProject project = res.getProject();
      if (project != null && project.exists() && project.isOpen()) {
        if (hasNature(project)) {
          visitResource(project, delta, res);
        }
      }
    }
    return true; // visit the children
  }
  
  /**
   * 
   * @param project
   * @return
   * @throws CoreException
   */
  protected boolean hasNature(IProject project) throws CoreException {
    return project.hasNature(ToifImportNature.NATURE_ID);
  }
  
  /**
   * 
   * @param project
   * @param delta
   * @param res
   * @throws CoreException
   */
  protected synchronized void visitResource(final IProject project, IResourceDelta delta, IResource res)
      throws CoreException {
    // Have we already warned on this project?
    if (project.getPersistentProperty(com.kdmanalytics.toif.ui.common.Activator.PROJECT_INCONSISTENT) == null) {
      // Set to true if the resource has PROPERLY changed
      boolean changed = false;
      
      switch (delta.getKind()) {
        case IResourceDelta.ADDED:
        case IResourceDelta.REMOVED: {
          // We cannot check the attributes for deleted items, so assume if we
          // get this far it is an interesting file.
          System.err.println("Changed 1");
          changed = true;
          break;
        }
        case IResourceDelta.CHANGED: {
          // All resources should be read only, if it is not then the contents
          // have actually changed.
          ResourceAttributes attrs = res.getResourceAttributes();
          if (!attrs.isReadOnly() && !attrs.isHidden() && !res.isDerived()) {
            System.err.println("Changed 2");
            changed = true;
          }
          break;
        }
      }
      
      if (changed) {
        try {
          project.setPersistentProperty(com.kdmanalytics.toif.ui.common.Activator.PROJECT_INCONSISTENT, "true");
        } catch (CoreException e) {
          e.printStackTrace();
        }
        Display.getDefault().asyncExec(new Runnable() {
          
          @Override
          public void run() {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            Shell shell = window.getShell();
            MessageDialog.openWarning(shell, "Inconsistent TOIF data",
                                      "The source has changed possibly rendering the imported TOIF data inconsistent. Reload the '*.kdm' file to ensure the data is consistent");
          }
        });
      }
    }
  }
  
  /**
   * We only care about certain files
   * 
   * @param res
   * @return
   */
  protected boolean isSourceFile(IResource res) {
    if (res instanceof IFile) {
      if (!isHidden(res)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Check is a resource or any of its children are hidden
   * 
   * @param res
   * @return
   */
  private boolean isHidden(IResource res) {
    if (res.isHidden()) return true;
    IContainer parent = res.getParent();
    
    if (parent instanceof IProject) {
      if (res.getName().startsWith(".")) return true;
      return false;
    } else {
      return isHidden(parent);
    }
  }
}
