/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * ViewerFilter that filters entries where location group for a toifreport entry
 * has more than 1 finding entry
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class TwoToolsFilter extends ViewerFilter
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
            IToifReportEntry iToifReportEntry = (IToifReportEntry) element;
            LocationGroup lg = iToifReportEntry.getLocationGroup();
            if (lg != null)
            {
                return lg.getToolGroups().size() >= 2;
                //return lg.getFindingEntries().size() >= 2;
            }
        }
        return false;
    }
    
}
