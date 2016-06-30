/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.IFindingEntry;
import com.kdmanalytics.toif.ui.common.ShowField;

/** Filter that rejects findings whose CWEs are marked as "No" for "Show" in the
 * adaptor configuration.
 * 
 * @author Ken Duck
 *
 */
public class ConfiguredVisibilityFilter extends AbstractValidFilter {
  
  /**
   * The adaptor configuration file.
   */
  private AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers .Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IFindingEntry) {
      String cwe = ((IFindingEntry) element).getCwe();
      if (cwe != null) {
        return config.getVisibility(cwe);
      }
      return true;
    }
    return false;
  }
  
  /** Return the number of CWEs set to invisible
   * 
   * @return
   */
  public int size() {
    int size = config.getSize();
    int count = 0;
    for (int i = 0; i < size; i++) {
      ShowField show = (ShowField) config.getCell(i, config.getShowColumnIndex());
      if (show == null || !show.toBoolean()) {
        count++;
      }
    }
    return count;
  }
  
}
