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
 * tool group is the reporting tool that contains a finding.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ToolGroup extends ReportItem
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2194675981730565641L;

    String name;
    
    private List<FindingEntry> findings;
    
    /**
     * new tool
     * 
     * @param name
     *            the name of the tool
     */
    public ToolGroup(String name)
    {
        findings = new ArrayList<FindingEntry>();
        this.name = name;
    }
    
    /**
     * get the name of the tool
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    @Override
    public String getSearchableText()
    {
        return parent.getSearchableText() + " " + name;
    }
    
    /**
     * add a finding to the tool
     * 
     * @param entry
     * @return
     */
    public boolean addFinding(FindingEntry entry)
    {
        
        findings.add(entry);
        return true;
    }
    
    /**
     * get the finding entries in the tool
     */
    public List<FindingEntry> getFindingEntries()
    {
        return findings;
    }
    
    /**
     * get the finding entries that are not ok.
     * 
     * @return
     */
    public List<FindingEntry> getFindingEntriesNotOk()
    {
        List<FindingEntry> results = new ArrayList<FindingEntry>();
        for (FindingEntry entry : findings)
        {
            if (!entry.isOk())
            {
                results.add(entry);
            }
        }
        return results;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        // result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ToolGroup other = (ToolGroup) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public List<ReportItem> getChildren()
    {
        List<ReportItem> result = new ArrayList<ReportItem>();
        result.addAll(findings);
        return result;
    }
    
}
