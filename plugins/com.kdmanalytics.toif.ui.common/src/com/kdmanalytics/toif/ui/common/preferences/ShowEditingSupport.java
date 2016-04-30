/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */

package com.kdmanalytics.toif.ui.common.preferences;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.ShowField;

/**
 * Editing support for the show column.
 * 
 * Initial concept from here:
 *   o http://www.vogella.com/tutorials/EclipseJFaceTableAdvanced/article.html
 * 
 * @author Ken Duck
 *
 */
public class ShowEditingSupport extends EditingSupport {
  
  private final TableViewer viewer;
  private final CellEditor editor;
  private AdaptorConfiguration config;
  
  public ShowEditingSupport(TableViewer viewer, AdaptorConfiguration config) {
    super(viewer);
    this.viewer = viewer;
    this.config = config;
    this.editor = new CheckboxCellEditor(viewer.getTable());
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
   */
  @Override
  protected CellEditor getCellEditor(Object element) {
    return editor;
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
   */
  @Override
  protected boolean canEdit(Object element) {
    @SuppressWarnings("unchecked")
    List<Object> row = (List<Object>)element;
    ShowField state = (ShowField) row.get(config.getShowColumnIndex());
    switch(state) {
      case UNSET: return false;
      default: return true;
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
   */
  @Override
  protected Object getValue(Object element) {
    @SuppressWarnings("unchecked")
    List<Object> row = (List<Object>)element;
    ShowField state = (ShowField) row.get(config.getShowColumnIndex());
    switch(state) {
      case YES: return true;
      case NO: return false;
      default: throw new IllegalArgumentException("Unsupported editor state: " + state);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void setValue(Object element, Object value) {
    @SuppressWarnings("unchecked")
    List<Object> row = (List<Object>)element;
    String cwe = (String) row.get(config.getCweColumnIndex());
    Boolean b = (Boolean)value;
    if(b) {
      row.set(config.getShowColumnIndex(), ShowField.YES);
    } else {
      row.set(config.getShowColumnIndex(), ShowField.NO);
    }
    config.update(row);
    viewer.update(element, null);
  }
  
}
