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
import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.internal.items.ReportItem;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.internal.items.Trace;

/**
 * interface for the finding entries. finding-entries are the actual findings.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 *         
 */
public interface IFindingEntry extends IReportItem {
  
  String getValue();
  
  boolean equals(Object obj);
  
  /**
   * get the children of this report item
   */
  List<ReportItem> getChildren();
  
  /**
   * get the finding's CWE
   * 
   * @return
   */
  String getCwe();
  
  /**
   * get the findings description
   * 
   * @return
   */
  String getDescription();
  
  /**
   * get the finding entries inside this report entry
   */
  List<FindingEntry> getFindingEntries();
  
  /**
   * @return the findingId
   */
  String getFindingId();
  
  /**
   * get the searchable text of this entry.
   */
  String getSearchableText();
  
  String getSfp();
  
  /**
   * 
   * @return
   */
  ToolGroup getTool();
  
  /**
   * get the trust of this finding
   * 
   * @return
   */
  int getTrust();
  
  /**
   * is this finding ok
   * 
   * @return
   */
  Citing isOk();
  
  /**
   * set the cwe name for this finding
   * 
   * @param cwe
   */
  void setCwe(String cwe);
  
  /**
   * set the description for this finding
   * 
   * @param stringValue
   */
  void setDescription(String stringValue);
  
  /**
   * set if this finding is ok
   * 
   * @param isOk
   */
  void setIsOk(Citing isOk);
  
  /**
   * set the sfp value for this finding
   * 
   * @param sfp
   */
  void setSfp(String sfp);
  
  /**
   * set the trust value for this finding
   * 
   * @param value
   */
  void setTrust(int value);
  
  /**
   * get the string value for this entry
   * 
   * @return
   */
  String toString();
  
  /**
   * get the traces belonging to this finding.
   * 
   * @return
   */
  List<Trace> getTraces();
  
  void setTraces(List<Trace> tracesList);
  
}
