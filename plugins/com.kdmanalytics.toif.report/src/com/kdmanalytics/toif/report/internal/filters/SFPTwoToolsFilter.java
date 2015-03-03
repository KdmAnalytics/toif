/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ReportItem;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IReportItem;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * Filter that shows only findings with the same location and the same sfp
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class SFPTwoToolsFilter extends ViewerFilter
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
            ToolGroup tg = iToifReportEntry.getToolGroup();
            IFindingEntry fe = iToifReportEntry.getFindingEntry();
            return doesLocationContainTwoSameSFP(lg, tg.toString(), fe.getSfp());
        }
        return false;
    }
    
    /**
     * Returns true if the given location group contains the same two SFP's
     * 
     * @param location
     *            the location group
     * @param toolNameToExclude
     *            the tool name to exclude from the check
     * 
     * @return true if there is two SFP's at the same location
     */
    private boolean doesLocationContainTwoSameSFP(LocationGroup location, String toolNameToExclude, String sfpToExclude)
    {
        
        // if there are two tools contained under one location show it.
        final List<ReportItem> tools = location.getChildren();
        
        final int size = tools.size();
        
        // if the location group does not contain at least two tools, return
        // false.
        if (!(size >= 2))
        {
            return false;
        }
        
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        
        // for each of the tools in the location group, make a map of tool name
        // and the sfps in it.
        for (IReportItem tool : tools)
        {
            List<FindingEntry> entries = tool.getFindingEntries();
            List<String> sfps = new ArrayList<String>();
            
            for (IFindingEntry entry : entries)
            {
                sfps.add(entry.getSfp());
            }
            
            map.put(tool.toString(), sfps);
        }
        
        // for each of the tools in the map, we are going to compare its sfps to
        // the other sfp that the other tools contain. We'll do this by removing
        // a tool from a copy of the map, and comparing the results to all the
        // other tools in the map.
        for (String toolName : map.keySet())
        {
            
            // get the new copy of the map
            HashMap<String, List<String>> copyOfMap = new HashMap<String, List<String>>(map);
            
            List<String> sfpList = null;
            
            if (toolNameToExclude != null)
            {
                // get the sfps for this tool.
                sfpList = copyOfMap.remove(toolNameToExclude);
            }
            else
            {
                
                // get the sfps for this tool.
                sfpList = copyOfMap.remove(toolName);
            }
            // for each of the remaining tools, compare these sfps to the tool
            // that we removed. if there is a match, then anything at this
            // location or above isallowed to stay.
            for (String toolNameCopy : copyOfMap.keySet())
            {
                List<String> retained = null;
                
                if ((sfpToExclude != null))
                {
                    // the list of sfps from the original tool.
                    retained = new ArrayList<String>();
                    retained.add(sfpToExclude);
                }
                else
                {
                    // the list of sfps from the original tool.
                    retained = new ArrayList<String>(sfpList);
                }
                
                // retain only common sfps
                retained.retainAll(map.get(toolNameCopy));
                
                // if the retained list is not empty then there are common sfps
                // between tools, return true.
                if (!retained.isEmpty())
                {
                    return true;
                }
            }
        }
        
        return false;
        
    }
}
