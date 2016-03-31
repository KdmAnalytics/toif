
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
public class FilterAction extends Action {
  
  /**
   * Viewer the filters will be applied to.
   */
  private TableViewer viewer;
  
  /**
   * Used to update the count label
   */
  private FindingView view;
  
  public FilterAction(FindingView view, TableViewer viewer) {
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
    FiltersDialog dialog = new FiltersDialog(viewer.getControl().getShell(), viewer.getFilters());
    int code = dialog.open();
    if (code == FiltersDialog.OK) {
      dialog.getFilters();
      
      FilterUtility filterUtils = new FilterUtility(view, viewer);
      
      handleTrustFilter(dialog, filterUtils);
      
      handleTwoToolFilter(dialog, filterUtils);
      handleCWETwoToolFilter(dialog, filterUtils);
      handleSFPTwoToolFilter(dialog, filterUtils);
      
      handleIsValidFilter(dialog, filterUtils);
      handleNotValidFilter(dialog, filterUtils);
      handleInvalidSfpFilter(dialog, filterUtils);
      filterUtils.applyFilters();
    }
  }
  
  /**
   * handle the not valid filter. by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleNotValidFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    NotValidFilter notValidFilter = dialog.getNotValidFilter();
    if (notValidFilter != null) {
      filterUtils.add(notValidFilter);
    }
    else {
      filterUtils.remove(NotValidFilter.class);
    }

  }
  
  /**
   * handle the invalid sfp filter. by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleInvalidSfpFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    InvalidSfpFilter invalidSfpFilter = dialog.getInvalidSfpFilter();
    if (invalidSfpFilter != null) {
      filterUtils.add(invalidSfpFilter);
    }
    else {
      filterUtils.remove(InvalidSfpFilter.class);
    }

  }
  
  /**
   * handle the same cwe filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleCWETwoToolFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    CWETwoToolsFilter cweTwoToolsFilter = dialog.getCWETwoToolsFilter();
    if (cweTwoToolsFilter != null) {
      filterUtils.add(cweTwoToolsFilter);
    }
    else {
      filterUtils.remove(CWETwoToolsFilter.class);
    }

  }
  
  /**
   * handle the same sfp filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleSFPTwoToolFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    SFPTwoToolsFilter sfpTwoToolsFilter = dialog.getSFPTwoToolsFilter();
    if (sfpTwoToolsFilter != null) {
      filterUtils.add(sfpTwoToolsFilter);
    }
    else {
      filterUtils.remove(SFPTwoToolsFilter.class);
    }

  }
  
  /**
   * handle the trust filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleTrustFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    TrustFilter trustFilter = dialog.getTrustFilter();
    if (trustFilter != null) {
      trustFilter.setAmount(dialog.getTrustAmount());
      filterUtils.add(trustFilter);
    }
    else {
      filterUtils.remove(TrustFilter.class);
    }
  }
  
  /**
   * handle the two tools filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleTwoToolFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    TwoToolsFilter twoToolsFilter = dialog.getTwoToolsFilter();
    if (twoToolsFilter != null) {
      filterUtils.add(twoToolsFilter);
    }
    else {
      filterUtils.remove(TwoToolsFilter.class);
    }
  }
  
  /**
   * handle the is valid filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterUtils
   */
  private void handleIsValidFilter(FiltersDialog dialog, FilterUtility filterUtils) {
    IsValidFilter isValidFilter = dialog.getIsValidFilter();
    if (isValidFilter != null) {
      filterUtils.add(isValidFilter);
    }
    else {
      filterUtils.remove(IsValidFilter.class);
    }
  }
}
