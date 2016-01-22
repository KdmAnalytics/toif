/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.items;

import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IAdaptable;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;

/**
 * report entry interface. the general report entry
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 *         
 */
public interface IToifReportEntry extends IAdaptable {
  
  void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
  
  void removePropertyChangeListener(PropertyChangeListener listener);
  
  /**
   * @return the project
   */
  IToifProject getProject();
  
  /**
   * @param project
   *          the project to set
   */
  void setProject(IToifProject project);
  
  /**
   * @return the fileGroup
   */
  IFileGroup getFileGroup();
  
  /**
   * @param fileGroup
   *          the fileGroup to set
   */
  void setFileGroup(IFileGroup fileGroup);
  
  /**
   * @return the locationGroup
   */
  LocationGroup getLocationGroup();
  
  /**
   * @param locationGroup
   *          the locationGroup to set
   */
  void setLocationGroup(LocationGroup locationGroup);
  
  /**
   * @return the toolGroup
   */
  ToolGroup getToolGroup();
  
  /**
   * @param toolGroup
   *          the toolGroup to set
   */
  void setToolGroup(ToolGroup toolGroup);
  
  /**
   * @return the findingEntry
   */
  FindingEntry getFindingEntry();
  
  /**
   * @param findingEntry
   *          the findingEntry to set
   */
  void setFindingEntry(FindingEntry findingEntry);
  
}
