/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * Filter ToifReportEntry based on string matches from the FindingEntry
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class TermFilter extends ViewerFilter
{
    
    /** The terms. */
    private final String[] terms;
    
    /**
     * Instantiates a new term filter.
     * 
     * @param terms
     *            the terms
     */
    public TermFilter(String[] terms)
    {
        this.terms = terms;
    }
    
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
            IFindingEntry findingEntry = iToifReportEntry.getFindingEntry();
            String text = findingEntry.getSearchableText().toLowerCase();
            for (String term : terms)
            {
                if (text.contains(term.toLowerCase().trim()))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
}
