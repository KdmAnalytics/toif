/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This class monitors for changes in resources to provide appropriate warnings when in "Import"
 * mode.
 * 
 * @author Ken Duck
 *        
 */
public class ResourceChangeMonitor implements IStartup, IResourceChangeListener, ISelectionListener {
  
  /**
   * Track all cited resources that are currently selected
   */
  private Set<IFile> citedResource = new HashSet<IFile>();
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IStartup#earlyStartup()
   */
  @Override
  public void earlyStartup() {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
    
    final ResourceChangeMonitor monitor = this;
    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
      
      public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ISelectionService service = window.getSelectionService();
        service.addSelectionListener(monitor);
      }
    });
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.
   * IResourceChangeEvent)
   */
  @Override
  public void resourceChanged(IResourceChangeEvent event) {
    switch (event.getType()) {
      case IResourceChangeEvent.POST_CHANGE: {
        try {
          event.getDelta().accept(getDeltaPrinter(citedResource));
        } catch (CoreException e) {
          e.printStackTrace();
        }
        break;
      }
    }
  }
  
  /**
   * 
   * @param citedResource
   * @return
   */
  protected IResourceDeltaVisitor getDeltaPrinter(Set<IFile> citedResource) {
    return new ResourceChangeDeltaPrinter(citedResource);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
   * org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection selection) {
    if (selection instanceof IStructuredSelection) {
      IStructuredSelection sel = (IStructuredSelection) selection;
      for (Iterator<?> it = sel.iterator(); it.hasNext();) {
        Object o = it.next();
        if (o instanceof IResource) {
          setSelectedCitedResources((IResource) o);
        }
      }
    }
  }
  
  /**
   * 
   * @param resource
   */
  private void setSelectedCitedResources(IResource resource) {
    citedResource.clear();
    
    // Only works with open projects
    if (resource instanceof IProject) {
      if (!((IProject) resource).isOpen()) {
        return;
      }
    }
    try {
      if (resource instanceof IFile) {
        Map<QualifiedName, String> props = resource.getPersistentProperties();
        for (Map.Entry<QualifiedName, String> entry : props.entrySet()) {
          String key = entry.getKey().toString();
          if (key.startsWith(Activator.PLUGIN_ID) && key.endsWith(":citing")) {
            citedResource.add((IFile) resource);
          }
        }
      }
      if (resource instanceof IContainer) {
        IResource[] children = ((IContainer) resource).members();
        if (children != null) {
          for (IResource child : children) {
            setSelectedCitedResources(child);
          }
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }
  
}
