/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * Filter based on a set of acceptable resources
 * 
 * @author Ken Duck
 *        
 */
public class ResourceFilter extends ViewerFilter {
  
  private Set<IFile> accept;
  
  public ResourceFilter(Collection<IFile> resources) {
    accept = new HashSet<IFile>();
    accept.addAll(resources);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IFindingEntry) {
      IFile file = ((IFindingEntry) element).getFile();
      if (accept.contains(file)) {
        return true;
      }
    }
    return false;
  }
  
}
