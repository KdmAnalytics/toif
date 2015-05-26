/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.items;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.ReportItem;

/**
 * interface for all report items.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public interface IReportItem extends IAdaptable
{
    
    /**
     * set the parent.
     * 
     * @param parent
     */
    void setParent(IReportItem parent);
    
    /**
     * get the searchable text for this item
     * 
     * @return
     */
    String getSearchableText();
    
    /**
     * get the parent.
     * 
     * @return
     */
    IReportItem getParent();
    
    /**
     * get the children contianed in this item
     * 
     * @return
     */
    List<ReportItem> getChildren();
    
    /**
     * get all the finding entries in this item
     * 
     * @return
     */
    List<FindingEntry> getFindingEntries();
    
    /**
     * get eh project this item belongs to .
     * 
     * @return
     */
    IToifProject getProject();
    
    /**
     * get the sum of the trust of all the findings within this item.
     * 
     * @return
     */
    int getTrustSum();
    
}
