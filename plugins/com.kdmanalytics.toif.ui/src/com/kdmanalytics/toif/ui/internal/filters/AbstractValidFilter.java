/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This
 *  program and the // accompanying materials are made available under the terms
 *  of the Open Source // Initiative OSI - Open Software License v3.0 which
 *  accompanies this // distribution, and is available at
 *  http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * Base class for the family of ValidFilters
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * @author Ken Duck
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
     * @return true if the entry has been cited as "true"
     */
    protected boolean valid(FindingEntry entry)
    {
    	Boolean b = entry.getCiting();
        if(b != null) return b;
        return false;
    }
    
}
