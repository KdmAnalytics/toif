/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */

package com.kdmanalytics.toif.ui.common.preferences;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.TrustField;

/**
 * Editing support for the Adaptor "trust" columns.
 * 
 * Editor help here:
 *   o http://www.vogella.com/tutorials/EclipseJFaceTableAdvanced/article.html
 * 
 * @author Ken Duck
 *
 */
public class TrustEditingSupport extends EditingSupport {
  
  private final TableViewer viewer;
  private final CellEditor editor;
  private AdaptorConfiguration config;
  
  /**
   * Column this editor is being configured for
   */
  private int index;
  
  public TrustEditingSupport(TableViewer viewer, AdaptorConfiguration config, int index) {
    super(viewer);
    this.viewer = viewer;
    this.config = config;
    this.editor = new TextCellEditor(viewer.getTable());
    this.index = index;
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
    TrustField state = (TrustField) row.get(index);
    return state.isValid();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
   */
  @Override
  protected Object getValue(Object element) {
    @SuppressWarnings("unchecked")
    List<Object> row = (List<Object>)element;
    TrustField state = (TrustField) row.get(index);
    return state.toString();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void setValue(Object element, Object value) {
    @SuppressWarnings("unchecked")
    List<Object> row = (List<Object>)element;
    String text = (String)value;
      row.set(index, TrustField.fromString(text));
    config.update(row);
    viewer.update(element, null);
  }
  
}
