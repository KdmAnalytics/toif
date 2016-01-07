/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.PlatformObject;

import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IToifProject;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * represents the report entries.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 *         
 */
public class ToifReportEntry extends PlatformObject implements IToifReportEntry {
  
  private IToifProject project;
  
  private IFileGroup fileGroup;
  
  private LocationGroup locationGroup;
  
  private ToolGroup toolGroup;
  
  private FindingEntry findingEntry;
  
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  
  public ToifReportEntry() {
  
  }
  
  /**
   * create a new report entry.
   * 
   * @param project
   *          the toif project
   * @param fileGroup
   *          the file
   * @param locationGroup
   *          the code location
   * @param toolGroup
   *          the reporting tool
   * @param findingEntry
   *          the finding entry
   */
  public ToifReportEntry(IToifProject project, IFileGroup fileGroup, LocationGroup locationGroup, ToolGroup toolGroup,
                         FindingEntry findingEntry) {
    this.project = project;
    this.fileGroup = fileGroup;
    this.locationGroup = locationGroup;
    this.toolGroup = toolGroup;
    this.findingEntry = findingEntry;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#
   * addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  @Override
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#
   * removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#getProject()
   */
  @Override
  public IToifProject getProject() {
    return project;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#setProject
   * (com.kdmanalytics.toif.report.internal.items.Project)
   */
  @Override
  public void setProject(IToifProject project) {
    propertyChangeSupport.firePropertyChange("project", this.project, this.project = project);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#getFileGroup ()
   */
  @Override
  public IFileGroup getFileGroup() {
    return fileGroup;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#setFileGroup
   * (com.kdmanalytics.toif.report.internal.items.FileGroup)
   */
  @Override
  public void setFileGroup(IFileGroup fileGroup) {
    propertyChangeSupport.firePropertyChange("fileGroup", this.fileGroup, this.fileGroup = fileGroup);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#getLocationGroup ()
   */
  @Override
  public LocationGroup getLocationGroup() {
    return locationGroup;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#setLocationGroup
   * (com.kdmanalytics.toif.report.internal.items.LocationGroup)
   */
  @Override
  public void setLocationGroup(LocationGroup locationGroup) {
    propertyChangeSupport.firePropertyChange("locationGroup", this.locationGroup, this.locationGroup = locationGroup);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#getToolGroup ()
   */
  @Override
  public ToolGroup getToolGroup() {
    return toolGroup;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#setToolGroup
   * (com.kdmanalytics.toif.report.internal.items.ToolGroup)
   */
  @Override
  public void setToolGroup(ToolGroup toolGroup) {
    propertyChangeSupport.firePropertyChange("toolGroup", this.toolGroup, this.toolGroup = toolGroup);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#getFindingEntry ()
   */
  @Override
  public FindingEntry getFindingEntry() {
    return findingEntry;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifReportEntry#setFindingEntry
   * (com.kdmanalytics.toif.report.internal.items.FindingEntry)
   */
  @Override
  public void setFindingEntry(FindingEntry findingEntry) {
    propertyChangeSupport.firePropertyChange("findingEntry", this.findingEntry, this.findingEntry = findingEntry);
  }
  
}
