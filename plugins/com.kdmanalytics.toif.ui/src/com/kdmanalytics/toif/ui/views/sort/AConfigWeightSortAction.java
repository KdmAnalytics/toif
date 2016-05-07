
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
public class AConfigWeightSortAction extends Action {
  
  /**
   * Viewer the filters will be applied to.
   */
  private TreeViewer viewer;
  
  public AConfigWeightSortAction(FindingView view, TreeViewer viewer2) {
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
    viewer.setComparator(new AdaptorConfigWeightComparator());
  }
}
