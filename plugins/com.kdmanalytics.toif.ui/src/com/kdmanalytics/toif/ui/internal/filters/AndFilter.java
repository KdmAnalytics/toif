/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
 *  program and the // accompanying materials are made available under the terms
 *  of the Open Source // Initiative OSI - Open Software License v3.0 which
 *  accompanies this // distribution, and is available at
 *  http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Used to make filters ADDITIVE (AND them together). ALL filters must return valid for something to
 * be valid.
 * 
 * @author Ken Duck
 *         
 */
public class AndFilter extends ViewerFilter {
  
  List<ViewerFilter> filterList = new ArrayList<ViewerFilter>();
  
  /**
   * 
   * @param filter
   */
  public void add(ViewerFilter filter) {
    filterList.add(filter);
  }
  
  /**
   * Returns true if there are no filters set
   * 
   * @return
   */
  public boolean isEmpty() {
    return filterList.isEmpty();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers .Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    // No filters -- nothing is valid
    if (filterList.isEmpty()) return false;
    
    for (ViewerFilter filter : filterList) {
      // Return false is ANY filter rejects a value
      if (!filter.select(viewer, parentElement, element)) return false;
    }
    return true;
  }
  
  /**
   * Get an array of installed filters.
   * 
   * @return
   */
  public ViewerFilter[] getFilters() {
    return filterList.toArray(new ViewerFilter[filterList.size()]);
  }

  public void clear() {
    filterList.clear();
  }

  /** Remove all filters of the specified class.
   * 
   * @param cls
   */
  public void remove(Class<? extends ViewerFilter> cls) {
    for (Iterator<ViewerFilter> it = filterList.iterator(); it.hasNext();) {
      ViewerFilter filter = it.next();
      if(cls.isAssignableFrom(filter.getClass())) {
        it.remove();
      }
    }
  }
  
}
