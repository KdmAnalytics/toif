/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.util.ArrayList;
import java.util.List;

/**
 * location groups are the codelocations that have findings attached to them.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class LocationGroup extends ReportItem
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 242998160312564325L;

    private final List<ToolGroup> toolGroups;
    
    private String toifLineNumber;
    
    private String path;
    
    private String realLineNumber = null;
    
    public LocationGroup()
    {
        toolGroups = new ArrayList<ToolGroup>();
    }
    
    public LocationGroup(String path, String lineNumber)
    {
        toolGroups = new ArrayList<ToolGroup>();
        this.toifLineNumber = lineNumber;
        this.path = path;
    }
    
    public ToolGroup AddToolGroup(ToolGroup entry)
    {
        if (!toolGroups.contains(entry))
        {
            toolGroups.add(entry);
            return entry;
        }
        else
        {
            return toolGroups.get(toolGroups.indexOf(entry));
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        LocationGroup other = (LocationGroup) obj;
        if (getLineNumber() == null)
        {
            if (other.getLineNumber() != null)
            {
                return false;
            }
        }
        else if (!getLineNumber().equals(other.getLineNumber()))
        {
            return false;
        }
        else if (!getToifLineNumber().equals(other.getToifLineNumber()))
        {
            return false;
        }
        
        if (path == null)
        {
            if (other.path != null)
            {
                return false;
            }
        }
        else if (!path.equals(other.path))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public List<ReportItem> getChildren()
    {
        List<ReportItem> result = new ArrayList<ReportItem>();
        result.addAll(toolGroups);
        return result;
    }
    
    @Override
    public List<FindingEntry> getFindingEntries()
    {
        List<FindingEntry> results = new ArrayList<FindingEntry>();
        
        for (ToolGroup tool : toolGroups)
        {
            for (FindingEntry entry : tool.getFindingEntries())
            {
                results.add(entry);
                
            }
            
        }
        return results;
    }
    
    /**
     * return the findings in this location group that are not ok.
     * 
     * @return
     */
    public List<FindingEntry> getFindingEntriesNotOk()
    {
        List<FindingEntry> results = new ArrayList<FindingEntry>();
        
        for (ToolGroup tool : toolGroups)
        {
            for (FindingEntry entry : tool.getFindingEntries())
            {
                if (!entry.isOk())
                {
                    results.add(entry);
                }
            }
            
        }
        return results;
    }
    
    public String getToifLineNumber()
    {
        return toifLineNumber;
    }
    
    public String getLineNumber()
    {
        if (realLineNumber != null)
        {
            return realLineNumber;
        }
        else
        {
            return toifLineNumber;
        }
    }
    
    public String getPath()
    {
        return path;
    }
    
    /**
     * @return the realLineNumber
     */
    public String getRealLineNumber()
    {
        return realLineNumber;
    }
    
    @Override
    public String getSearchableText()
    {
        return parent.getSearchableText() + " | " + toString();
    }
    
    public List<ToolGroup> getToolGroups()
    {
        return toolGroups;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getLineNumber() == null) ? 0 : getLineNumber().hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }
    
    /**
     * @param string
     */
    public void setRealLineNumber(String string)
    {
        
        this.realLineNumber = string;
        
    }
    
    @Override
    public String toString()
    {
        return "Line number: " + getToifLineNumber();
    }
    
}
