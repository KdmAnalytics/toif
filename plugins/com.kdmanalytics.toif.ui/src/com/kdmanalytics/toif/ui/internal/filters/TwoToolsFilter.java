/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * ViewerFilter that filters entries where location group for a toifreport entry has more than 1
 * finding entry
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * @author Ken Duck
 *         
 */
public class TwoToolsFilter extends AbstractTwoToolsFilter {
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers .Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IFindingEntry) {
      IFindingEntry targetEntry = (IFindingEntry) element;
      
      IFile file = targetEntry.getFile();
      int targetLine = targetEntry.getLineNumber();
      
      // List of all tools represented at this file location
      Set<String> tools = new HashSet<String>();
      
      try {
        List<IFindingEntry> findings = getFindings(file);
        
        for (IFindingEntry entry : findings) {
          int line = entry.getLineNumber();
          if (line == targetLine) {
            String tool = entry.getTool();
            tools.add(tool);
          }
        }
      } catch (CoreException e) {
        e.printStackTrace();
      }
      
      return tools.size() >= 2;
    }
    return false;
  }
  
}
