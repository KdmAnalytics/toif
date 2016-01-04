
package com.kdmanalytics.toif.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.internal.filters.AbstractTwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.AndFilter;
import com.kdmanalytics.toif.ui.internal.filters.CWETwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.FiltersDialog;
import com.kdmanalytics.toif.ui.internal.filters.InvalidSfpFilter;
import com.kdmanalytics.toif.ui.internal.filters.IsValidFilter;
import com.kdmanalytics.toif.ui.internal.filters.NotValidFilter;
import com.kdmanalytics.toif.ui.internal.filters.SFPTwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.TermFilter;
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
      
      AndFilter andFilter = new AndFilter();
      
      // Hack to ensure that the term filter is applied in addition to the
      // filters that are handled by the dialog
      for (ViewerFilter filter : viewer.getFilters()) {
        if (filter instanceof TermFilter) {
          andFilter.add(filter);
        }
      }
      
      handleTrustFilter(dialog, andFilter);
      
      handleTwoToolFilter(dialog, andFilter);
      handleCWETwoToolFilter(dialog, andFilter);
      handleSFPTwoToolFilter(dialog, andFilter);
      
      handleIsValidFilter(dialog, andFilter);
      handleNotValidFilter(dialog, andFilter);
      handleInvalidSfpFilter(dialog, andFilter);
      
      // InvalidSFP filter affects the operations of some filters, so
      // we need to know if it is enabled
      boolean acceptInvalidSfp = true;
      for (ViewerFilter filter : andFilter.getFilters()) {
        if (filter instanceof InvalidSfpFilter) {
          acceptInvalidSfp = false;
          break;
        }
      }
      // Should we ignore invalid SFPs?
      for (ViewerFilter filter : andFilter.getFilters()) {
        if (filter instanceof AbstractTwoToolsFilter) {
          ((AbstractTwoToolsFilter) filter).setAcceptInvalidSfp(acceptInvalidSfp);
        }
      }
      
      List<ViewerFilter> filterList = new ArrayList<ViewerFilter>();
      if (!andFilter.isEmpty()) filterList.add(andFilter);
      viewer.setFilters(filterList.toArray(new ViewerFilter[filterList.size()]));
      viewer.refresh();
      view.updateDefectCount();
    }
  }
  
  /**
   * handle the not valid filter. by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleNotValidFilter(FiltersDialog dialog, AndFilter filterList) {
    NotValidFilter notValidFilter = dialog.getNotValidFilter();
    if (notValidFilter != null) {
      filterList.add(notValidFilter);
    }
    
  }
  
  /**
   * handle the invalid sfp filter. by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleInvalidSfpFilter(FiltersDialog dialog, AndFilter filterList) {
    InvalidSfpFilter invalidSfpFilter = dialog.getInvalidSfpFilter();
    if (invalidSfpFilter != null) {
      filterList.add(invalidSfpFilter);
    }
    
  }
  
  /**
   * handle the same cwe filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleCWETwoToolFilter(FiltersDialog dialog, AndFilter filterList) {
    CWETwoToolsFilter cweTwoToolsFilter = dialog.getCWETwoToolsFilter();
    if (cweTwoToolsFilter != null) {
      filterList.add(cweTwoToolsFilter);
    }
    
  }
  
  /**
   * handle the same sfp filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleSFPTwoToolFilter(FiltersDialog dialog, AndFilter filterList) {
    SFPTwoToolsFilter sfpTwoToolsFilter = dialog.getSFPTwoToolsFilter();
    if (sfpTwoToolsFilter != null) {
      filterList.add(sfpTwoToolsFilter);
    }
    
  }
  
  /**
   * handle the trust filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleTrustFilter(FiltersDialog dialog, AndFilter filterList) {
    TrustFilter trustFilter = dialog.getTrustFilter();
    if (trustFilter != null) {
      trustFilter.setAmount(dialog.getTrustAmount());
      filterList.add(trustFilter);
    }
  }
  
  /**
   * handle the two tools filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleTwoToolFilter(FiltersDialog dialog, AndFilter filterList) {
    TwoToolsFilter twoToolsFilter = dialog.getTwoToolsFilter();
    if (twoToolsFilter != null) {
      filterList.add(twoToolsFilter);
    }
  }
  
  /**
   * handle the is valid filter by adding it to the filterList.
   * 
   * @param dialog
   * @param filterList
   */
  private void handleIsValidFilter(FiltersDialog dialog, AndFilter filterList) {
    IsValidFilter isValidFilter = dialog.getIsValidFilter();
    if (isValidFilter != null) {
      filterList.add(isValidFilter);
    }
  }
}
