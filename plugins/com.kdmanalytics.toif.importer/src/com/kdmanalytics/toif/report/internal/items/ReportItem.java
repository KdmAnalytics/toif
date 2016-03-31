/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.io.Serializable;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;

import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IReportItem;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * class representing the report items.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 *         
 */
public abstract class ReportItem extends PlatformObject implements IReportItem, Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -8611788270468710322L;
  
  protected IReportItem parent = null;
  
  protected String searchableText = "";
  
  public ReportItem() {
  
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#setParent(com
   * .kdmanalytics.toif.report.internal.items.IReportItem)
   */
  @Override
  public void setParent(IReportItem parent) {
    this.parent = parent;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getSearchableText ()
   */
  @Override
  public String getSearchableText() {
    return searchableText;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getParent()
   */
  @Override
  public IReportItem getParent() {
    return parent;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ReportItem other = (ReportItem) obj;
    if (parent == null) {
      if (other.parent != null) return false;
    } else if (!parent.equals(other.parent)) return false;
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getChildren()
   */
  @Override
  public abstract List<ReportItem> getChildren();
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getFindingEntries ()
   */
  @Override
  public abstract List<FindingEntry> getFindingEntries();
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getProject()
   */
  @Override
  public IToifProject getProject() {
    IReportItem item = this;
    
    while (item != null) {
      if (item instanceof IToifProject) {
        return (IToifProject) item;
      } else {
        item = item.getParent();
      }
    }
    
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IReportItem#getTrustSum()
   */
  @Override
  public int getTrustSum() {
    
    int result = 0;
    
    for (IFindingEntry entry : getFindingEntries()) {
      result += entry.getTrust();
    }
    return result;
  }
  
}
