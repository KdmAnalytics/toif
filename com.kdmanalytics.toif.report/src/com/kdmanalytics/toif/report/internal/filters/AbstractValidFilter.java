/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This
 *  program and the // accompanying materials are made available under the terms
 *  of the Open Source // Initiative OSI - Open Software License v3.0 which
 *  accompanies this // distribution, and is available at
 *  http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.filters;

import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * Base class for the family of ValidFilters
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public abstract class AbstractValidFilter extends ViewerFilter
{
    
    /**
     * Returns true if the finding entry of the given report entry is not OK
     * 
     * @param element
     *            a toif report entry
     * 
     * @return true if the finding entry contained within the given report entry
     *         isOK() returns false;
     */
    protected boolean valid(IToifReportEntry element)
    {
        IFindingEntry entry = element.getFindingEntry();
        return (Citing.TRUE == entry.isOk());
    }
    
}
