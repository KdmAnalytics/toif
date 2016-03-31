/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * Base filter allows special handling of SFP--1
 * 
 * @author Ken Duck
 *        
 */
public abstract class AbstractTwoToolsFilter extends ViewerFilter {
  
  /**
   * Filters that must hold true for all findings in order for this
   * filter to be satisfied.
   */
  private List<ViewerFilter> subFilters = new LinkedList<ViewerFilter>();
  
  /**
   * Clear all of the subfilters
   */
  public void clearFilters()
  {
    subFilters.clear();
  }
  
  /** Add a new filter that must hold true for all findings in order for this
   * filter to be satisfied.
   * 
   * @param filter
   */
  public void add(ViewerFilter filter)
  {
    subFilters.add(filter);
  }
  
  /** Set a new list of sub-filters
   * 
   * @param subFilters2
   */
  public void setSubFilters(List<ViewerFilter> subFilters) {
    this.subFilters.addAll(subFilters);
  }
  
  /**
   * Get all findings for the file, ignoring ones not accepted by the
   * sub-filters.
   * 
   * @param file
   * @return
   * @throws CoreException
   */
  protected List<FindingEntry> getFindings(IFile file) throws CoreException {
    List<FindingEntry> results = new LinkedList<FindingEntry>();
    IMarker[] problems = file.findMarkers(IMarker.TEXT, true, IResource.DEPTH_INFINITE);
    
    for (IMarker marker : problems) {
      String type = marker.getType();
      if (type != null && type.startsWith("com.kdmanalytics.toif")) {
        FindingEntry entry = new FindingEntry(marker);
        boolean accept = true;
        for(ViewerFilter filter: subFilters) {
          if(!filter.select(null, null, entry)) {
            accept = false;
            break;
          }
        }
        if(accept) {
          results.add(entry);
        }
      }
    }
    return results;
  }
}
