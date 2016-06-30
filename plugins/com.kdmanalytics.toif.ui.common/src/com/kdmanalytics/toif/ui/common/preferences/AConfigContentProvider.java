/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */

package com.kdmanalytics.toif.ui.common.preferences;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;

/**
 * Supply the contents of the configuration page as a content provider suitable for display in a
 * table.
 * 
 * @author Ken Duck
 *
 */
public class AConfigContentProvider implements IStructuredContentProvider {
  
  /**
   * The configuration should be provided
   */
  private AdaptorConfiguration config;
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    if(newInput instanceof AdaptorConfiguration) {
      config = (AdaptorConfiguration)newInput;
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(Object inputElement) {
    if(config != null) return config.getDataArray();
    return new Object[0];
  }
}
