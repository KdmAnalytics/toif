/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views.sort;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.kdmanalytics.toif.ui.views.FindingView;

/**
 * Pop up the filter dialog and apply appropriate filters.
 * 
 * A bunch of code from the FiltersHandler
 * 
 * @author Ken Duck
 *        
 */
public class FindingGroupSortAction extends Action {
  
  /**
   * Viewer the filters will be applied to.
   */
  private TreeViewer viewer;
  
  public FindingGroupSortAction(FindingView view, TreeViewer viewer2) {
    this.viewer = viewer2;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.action.Action#run()
   */
  @Override
  public void run() {
    viewer.getTree().setSortDirection(SWT.NONE);
    viewer.setComparator(new FindingGroupComparator());
  }
}
