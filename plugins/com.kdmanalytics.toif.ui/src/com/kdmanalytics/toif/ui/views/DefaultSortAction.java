
package com.kdmanalytics.toif.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

import com.kdmanalytics.toif.ui.internal.filters.CWETwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.FiltersDialog;
import com.kdmanalytics.toif.ui.internal.filters.InvalidSfpFilter;
import com.kdmanalytics.toif.ui.internal.filters.IsValidFilter;
import com.kdmanalytics.toif.ui.internal.filters.NotValidFilter;
import com.kdmanalytics.toif.ui.internal.filters.SFPTwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.TrustFilter;
import com.kdmanalytics.toif.ui.internal.filters.TwoToolsFilter;

/**
 * Pop up the filter dialog and apply appropriate filters.
 * 
 * A bunch of code from the FiltersHandler
 * 
 * @author Ken Duck
 *        
 */
public class DefaultSortAction extends Action {
  
  /**
   * Viewer the filters will be applied to.
   */
  private TableViewer viewer;
  
  /**
   * Used to update the count label
   */
  private FindingView view;
  
  public DefaultSortAction(FindingView view, TableViewer viewer) {
    this.viewer = viewer;
    this.view = view;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.action.Action#run()
   */
  @Override
  public void run() {
    //    FiltersDialog dialog = new FiltersDialog(viewer.getControl().getShell(), viewer.getFilters());
    //    int code = dialog.open();
    //    if (code == FiltersDialog.OK) {
    //      dialog.getFilters();
    //      
    //      FilterUtility filterUtils = new FilterUtility(view, viewer);
    //      
    //      handleTrustFilter(dialog, filterUtils);
    //      
    //      handleTwoToolFilter(dialog, filterUtils);
    //      handleCWETwoToolFilter(dialog, filterUtils);
    //      handleSFPTwoToolFilter(dialog, filterUtils);
    //      
    //      handleIsValidFilter(dialog, filterUtils);
    //      handleNotValidFilter(dialog, filterUtils);
    //      handleInvalidSfpFilter(dialog, filterUtils);
    //      filterUtils.applyFilters();
    //    }
    System.err.println("DEFAULT SORT");
  }
}
