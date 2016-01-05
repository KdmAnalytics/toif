/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.items;

import java.util.List;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ReportItem;

/**
 * Interface for the file group. files contain locations.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 *         
 */
public interface IFileGroup extends IReportItem {
  
  /**
   * add a location fo the file
   * 
   * @param location
   */
  void AddLocation(LocationGroup location);
  
  /**
   * get the children in this file
   */
  List<ReportItem> getChildren();
  
  /**
   * get the findings in this file
   */
  List<FindingEntry> getFindingEntries();
  
  /**
   * get the findings that are not ok in this file
   * 
   * @return
   */
  List<FindingEntry> getFindingEntriesNotOk();
  
  /**
   * get the trust sum of the findings that are not ok.
   * 
   * @return
   */
  int getFindingEntriesNotOkTrustSum();
  
  /**
   * get the locations within this file
   * 
   * @return
   */
  List<LocationGroup> getLocationGroup();
  
  /**
   * get the name of the file
   * 
   * @return
   */
  String getName();
  
  /**
   * get the path of the file.
   * 
   * @return
   */
  String getPath();
  
  String getSearchableText();
  
}
