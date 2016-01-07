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
  
  private boolean acceptInvalidSfp = true;
  
  public void setAcceptInvalidSfp(boolean b) {
    acceptInvalidSfp = b;
  }
  
  /**
   * Get all findings for the file, possibly ignoring ones with invalid SFPs.
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
        if (acceptInvalidSfp || !"SFP--1".equals(entry.getSfp())) {
          results.add(entry);
        }
      }
    }
    return results;
  }
}
