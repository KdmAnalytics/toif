/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */

package com.kdmanalytics.toif.ui.common.preferences;

import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.kdmanalytics.toif.ui.common.Activator;
import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;

/**
 * View and edit the adaptor configuration file.
 * 
 * @author Ken Duck
 *
 */
public class AConfigPreferences extends PreferencePage implements IWorkbenchPreferencePage {

  /**
   * 
   */
  private AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();

  /**
   * The table
   */
  private TableViewer viewer;

  /**
   * Table contents, wraps around the configuration file
   */
  private AConfigContentProvider contentProvider;
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createContents(Composite parent) {
    
    Composite composite = new Composite(parent, SWT.NONE);
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    composite.setLayout(layout);
    
    addTable(composite);
    
    return composite;
  }
  
  /** Add the configuration editing table to the layout
   * 
   * @param composite
   */
  private void addTable(Composite composite) {
    viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
    
        contentProvider = new AConfigContentProvider();
        viewer.setContentProvider(contentProvider);
    //    viewer.setComparator(new FindingViewerComparator());
    //
    //    // Listen to change events so we know what to run actions upon
    //    selection = new FindingSelectionChangedListener();
    //    viewer.addSelectionChangedListener(selection);
    //    
    //    // Enable the default filters
    //    FilterUtility filter = new FilterUtility(this, viewer);
    //    filter.applyFilters();
    //
    //
    Table table = viewer.getTable();
    GridData gridData = new GridData();
    gridData.heightHint = 400;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalSpan = 3;
    table.setLayoutData(gridData);
    
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    //    ColumnViewerToolTipSupport.enableFor(viewer);
    
    List<String> header = config.getHeaders();
    
    //    String[] titles = { "File", "Location", "Tool", "SFP", "CWE", "Trust", "Description" };
    //    int[] bounds = { 200, 100, 200, 70, 90, 50, 900 };
    
    for(int i = 0; i < header.size(); i++) {
      TableViewerColumn col = createTableViewerColumn(viewer, header.get(i), 50, 0, true);
      col.setLabelProvider(new AConfigStyledLabelProvider(config));
      if (config.getShowColumnIndex() == i) {
        col.setEditingSupport(new ShowEditingSupport(viewer, config));
      }
    }
    
    //    // File Column
    //    TableViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // Location Column
    //    col = createTableViewerColumn(viewer, titles[1], bounds[1], 1, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // Tool Column
    //    col = createTableViewerColumn(viewer, titles[2], bounds[2], 2, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // SFP Column
    //    col = createTableViewerColumn(viewer, titles[3], bounds[3], 3, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // CWE Column
    //    col = createTableViewerColumn(viewer, titles[4], bounds[4], 4, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // Trust Column
    //    col = createTableViewerColumn(viewer, titles[5], bounds[5], 5, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    //
    //    // Description Column
    //    col = createTableViewerColumn(viewer, titles[6], bounds[6], 6, true);
    //    col.setLabelProvider(new FindingStyledLabelProvider());
    
    viewer.setInput(config);
  }
  
  /** Create a column for the table.
   * 
   * @param viewer
   * @param title
   * @param bound
   * @param colNumber
   * @param enableSorting
   * @return
   */
  private TableViewerColumn createTableViewerColumn(TableViewer viewer, final String title, final int bound, final int colNumber, boolean enableSorting)
  {
      TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.LEFT);
      final TableColumn column = viewerColumn.getColumn();
      column.setText(title);
      column.setWidth(bound);
      column.setResizable(true);
      column.setMoveable(true);
      return viewerColumn;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.PreferencePage#performApply()
   */
  @Override
  protected void performApply() {
    doApply();
  }
  
  @Override
  protected void performDefaults() {
    config.resetToDefault();
    viewer.refresh();
  };
  
  @Override
  public boolean performCancel() {
    config.reset();
    return super.performCancel();
  }
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.PreferencePage#performOk()
   */
  @Override
  public boolean performOk() {
    if (!isValid()) return false;
    doApply();
    return true;
  }
  
  /**
   * Save the changes
   */
  private void doApply() {
    config.save();
  }
}
