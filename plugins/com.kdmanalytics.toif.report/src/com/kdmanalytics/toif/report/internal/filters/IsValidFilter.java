/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.filters;

import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * ViewerFilter that returns true for ToifReportEntry who's finding entries are
 * found to be valid.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class IsValidFilter extends AbstractValidFilter
{
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
     * .Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (element instanceof IToifReportEntry)
        {
            return valid(((IToifReportEntry) element));
        }
        return false;
    }
    
}
