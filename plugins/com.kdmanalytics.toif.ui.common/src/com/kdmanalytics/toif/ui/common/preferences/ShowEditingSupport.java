/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */
package com.kdmanalytics.toif.ui.common.preferences;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

/** Editing support for the show column
 * 
 * @author Ken Duck
 *
 */
public class ShowEditingSupport extends EditingSupport {
  
  
  public ShowEditingSupport(ColumnViewer viewer) {
    super(viewer);
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected boolean canEdit(Object element) {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override
  protected Object getValue(Object element) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected void setValue(Object element, Object value) {
    // TODO Auto-generated method stub
    
  }
  
}
