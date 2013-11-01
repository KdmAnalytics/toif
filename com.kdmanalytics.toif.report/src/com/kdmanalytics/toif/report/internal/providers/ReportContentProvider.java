/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.providers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToifReportEntry;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * content provider for the report view.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class ReportContentProvider implements IStructuredContentProvider
{
    
    private static final Object[] NO_CHILDREN = new Object[] {};
    
    private IToifProject project;
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose()
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
     * .lang.Object)
     */
    @Override
    public Object[] getElements(Object inputElement)
    {
        
        if (inputElement instanceof ToifReportEntry[]) {
            return (Object[]) inputElement;
        }
        
        ToifReportEntry[] list = getElementList(inputElement);
        
        if (list != null)
        {
            return list;
        }
        else
        {
            return NO_CHILDREN;
        }
    }
    
    public ToifReportEntry[] getElementList(Object inputElement)
    {
        if (inputElement instanceof IToifProject)
        {
            project = (IToifProject) inputElement;
            
            List<ToifReportEntry> elements = new LinkedList<ToifReportEntry>();
            
            for (IFileGroup fileGroup : project.getFileGroup())
            {
                for (LocationGroup locationGroup : fileGroup.getLocationGroup())
                {
                    for (ToolGroup toolGroup : locationGroup.getToolGroups())
                    {
                        for (FindingEntry findingEntry : toolGroup.getFindingEntries())
                        {
                            ToifReportEntry entry = new ToifReportEntry(project, fileGroup, locationGroup, toolGroup, findingEntry);
                            elements.add(entry);
                        }
                    }
                }
            }
            return elements.toArray(new ToifReportEntry[elements.size()]);
        }
        
        return null;
    }
    
    public IToifProject getProject()
    {
        return project;
    }
    
}
