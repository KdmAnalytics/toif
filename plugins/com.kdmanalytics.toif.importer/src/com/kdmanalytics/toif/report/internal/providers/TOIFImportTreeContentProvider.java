/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.providers;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * the content provider for the import of toif data.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 *         
 */
public class TOIFImportTreeContentProvider implements ITreeContentProvider {
  
  @Override
  public Object[] getChildren(Object element) {
    // Project list
    if (element instanceof IWorkspace) {
      IProject[] allProjects = ((IWorkspace) element).getRoot().getProjects();
      ArrayList<IProject> accessibleProjects = new ArrayList<IProject>();
      for (int i = 0; i < allProjects.length; i++) {
        IProject project = allProjects[i];
        if (!project.isOpen()) continue;
        
        accessibleProjects.add(project);
      }
      return accessibleProjects.toArray();
    }
    
    return new Object[0];
  }
  
  @Override
  public Object getParent(Object element) {
    if (element instanceof IResource) {
      return ((IResource) element).getParent();
    }
    return null;
  }
  
  @Override
  public boolean hasChildren(Object element) {
    return getChildren(element).length > 0;
  }
  
  @Override
  public Object[] getElements(Object element) {
    return getChildren(element);
  }
  
  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // TODO Auto-generated method stub
    
  }
  
}
