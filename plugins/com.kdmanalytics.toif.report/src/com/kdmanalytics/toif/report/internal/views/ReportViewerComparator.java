/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * The Class ReportViewerComparator.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 */
public class ReportViewerComparator extends ViewerComparator
{
    
    /** The column index. */
    private int columnIndex;
    
    /** The Constant DESCENDING. */
    private static final int DESCENDING = 1;
    
    /** The direction. */
    private int direction = DESCENDING;
    
    /**
     * Instantiates a new report viewer comparator.
     */
    public ReportViewerComparator()
    {
        this.columnIndex = -1;
        direction = DESCENDING;
    }
    
    /**
     * Gets the direction.
     * 
     * @return the direction
     */
    public int getDirection()
    {
        return direction == 1 ? SWT.DOWN : SWT.UP;
    }
    
    /**
     * Sets the column.
     * 
     * @param column
     *            the new column
     */
    public void setColumn(int column)
    {
        if (column == this.columnIndex)
        {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        }
        else
        {
            // New column; do an ascending sort
            this.columnIndex = column;
            direction = DESCENDING;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.
     * viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        int result = 0;
        IToifReportEntry entry1 = (IToifReportEntry) e1;
        IToifReportEntry entry2 = (IToifReportEntry) e2;
        
        switch (columnIndex)
        {
            case 0:
            {
                IFileGroup file1 = entry1.getFileGroup();
                IFileGroup file2 = entry2.getFileGroup();
                result = file1.getName().split(" ")[0].compareTo(file2.getName().split(" ")[0]);
                break;
            }
            case 1:
            {
                LocationGroup group1 = entry1.getLocationGroup();
                LocationGroup group2 = entry2.getLocationGroup();
                result = compareLocationGroups(group1, group2);
                break;
            }
            case 2:
            {
                ToolGroup toolGroup1 = entry1.getToolGroup();
                ToolGroup toolGropu2 = entry2.getToolGroup();
                result = super.compare(viewer, toolGroup1, toolGropu2);
                break;
            }
            case 3:
            {
                String sfp1 = entry1.getFindingEntry().getSfp().replace("SFP-", "");
                String sfp2 = entry2.getFindingEntry().getSfp().replace("SFP-", "");
                int sfp1Int = 0;
                int sfp2Int = 0;
                
                try
                {
                    sfp1Int = Integer.parseInt(sfp1);
                }
                catch (NumberFormatException nfe)
                {
                    sfp1Int = -1;
                }
                
                try
                {
                    sfp2Int = Integer.parseInt(sfp2);
                }
                catch (NumberFormatException nfe)
                {
                    sfp2Int = -1;
                }
                
                result = sfp1Int - sfp2Int;
                break;
            }
            case 4:
            {
                String cwe1 = entry1.getFindingEntry().getCwe().replace("CWE-", "");
                String cwe2 = entry2.getFindingEntry().getCwe().replace("CWE-", "");
                
                int cwe1Int = 0;
                int cwe2Int = 0;
                try
                {
                    cwe1Int = Integer.parseInt(cwe1);
                }
                catch (NumberFormatException nfe)
                {
                    cwe1Int = -1;
                }
                
                try
                {
                    cwe2Int = Integer.parseInt(cwe2);
                }
                catch (NumberFormatException nfe)
                {
                    cwe2Int = -1;
                }
                result = cwe1Int - cwe2Int;
                
                break;
            }
            case 5:
            {
                int cwe1Int = entry1.getFindingEntry().getTrust();
                int cwe2Int = entry2.getFindingEntry().getTrust();
                result = cwe1Int - cwe2Int;
                break;
            }
            case 6:
            {
                String desc1 = entry1.getFindingEntry().getDescription();
                String desc2 = entry2.getFindingEntry().getDescription();
                result = desc1.compareTo(desc2);
                break;
            }
        }
        
        // If descending order, flip the direction
        if (direction == DESCENDING)
        {
            result = -result;
        }
        
        return result;
    }
    
    /**
     * Compare two int strings.
     * 
     * @param str1
     *            the str1
     * @param str2
     *            the str2
     * @return the int
     */
    private int compareTwoIntStrings(String str1, String str2)
    {
        int res1 = 0;
        int res2 = 0;
        int result = 0;
        
        try
        {
            res1 = Integer.parseInt(str1);
        }
        catch (NumberFormatException nfe)
        {
            res1 = -10;
        }
        
        try
        {
            res2 = Integer.parseInt(str2);
        }
        catch (NumberFormatException nfe)
        {
            res2 = -10;
        }
        
        if (res1 > res2)
        {
            result = 1;
        }
        else if (res1 < res2)
        {
            result = -1;
        }
        return result;
    }
    
    /**
     * Compare location groups.
     * 
     * @param loc1
     *            the loc1
     * @param loc2
     *            the loc2
     * @return the int
     */
    private int compareLocationGroups(LocationGroup loc1, LocationGroup loc2)
    {
        return compareTwoIntStrings(loc1.getLineNumber(), loc2.getLineNumber());
    }
    
}
