package com.kdmanalytics.toif.ui.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.internal.filters.AbstractTwoToolsFilter;
import com.kdmanalytics.toif.ui.internal.filters.AndFilter;
import com.kdmanalytics.toif.ui.internal.filters.ConfiguredVisibilityFilter;
import com.kdmanalytics.toif.ui.internal.filters.InvalidSfpFilter;
import com.kdmanalytics.toif.ui.internal.filters.TermFilter;

/** Filters interact in interesting ways. This ensures that everyone works
 * with filters in the same way.
 * 
 * @author Ken Duck
 *
 */
public class FilterUtility {
  
  private FindingView view;
  private TreeViewer viewer;
  private AndFilter andFilter;

  /** Instantiate the utilities. Find important filters.
   * 
   * @param viewer2
   */
  public FilterUtility(FindingView view, TreeViewer viewer2) {
    this.viewer = viewer2;
    this.view = view;
    
    andFilter = new AndFilter();
    // The visibility filter is ALWAYS enabled
    andFilter.add(new ConfiguredVisibilityFilter());
    
    ViewerFilter[] filters = viewer2.getFilters();
    if(filters != null) {
      for (ViewerFilter filter : filters) {
        if(filter instanceof AndFilter) {
          andFilter = (AndFilter) filter;
        }
      }
    }
  }

  /** Add a new filter to the andFilter
   * 
   * @param filter
   */
  public void add(ViewerFilter filter) {
    // If we are not adding a TermFilter, then bail if the filter already exists
    if(!(filter instanceof TermFilter)) {
      ViewerFilter[] filters = andFilter.getFilters();
      for (ViewerFilter subFilter : filters) {
        if(subFilter.getClass().isAssignableFrom(filter.getClass())) {
          return;
        }
      }
    }
    
    andFilter.add(filter);
  }

  /** Ensure all filters are properly set up then apply them.
   * 
   * Make sure that 2+ filters have appropriate sub filters.
   * 
   */
  public void applyFilters() {
    // InvalidSFP filter affects the operations of some filters, so
    // we need to know if it is enabled
    List<ViewerFilter> subFilters = new LinkedList<ViewerFilter>();
    for (ViewerFilter filter : andFilter.getFilters()) {
      if (filter instanceof InvalidSfpFilter) {
        subFilters.add(filter);
      }
      if (filter instanceof TermFilter) {
        subFilters.add(filter);
      }
    }
    // Should we ignore invalid SFPs?
    for (ViewerFilter filter : andFilter.getFilters()) {
      if (filter instanceof AbstractTwoToolsFilter) {
        ((AbstractTwoToolsFilter) filter).setSubFilters(subFilters);
      }
    }
    
    List<ViewerFilter> filterList = new ArrayList<ViewerFilter>();
    if (!andFilter.isEmpty()) filterList.add(andFilter);
    viewer.setFilters(filterList.toArray(new ViewerFilter[filterList.size()]));
    viewer.refresh();
    view.updateDefectCount();
  }

  /**
   * Clear all filters.
   */
  public void clear() {
    andFilter.clear();
    // The visibility filter is ALWAYS enabled
    andFilter.add(new ConfiguredVisibilityFilter());
  }

  /** Remove all filters of the specified class.
   * 
   * @param cls
   */
  public void remove(Class<? extends ViewerFilter> cls) {
    andFilter.remove(cls);
  }
}
