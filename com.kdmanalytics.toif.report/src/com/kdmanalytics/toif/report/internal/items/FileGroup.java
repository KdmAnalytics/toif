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

import com.kdmanalytics.toif.report.items.IFileGroup;

/**
 * class representing a file group. file groups represent a file in toif. files
 * contain findings and tools.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class FileGroup extends ReportItem implements IFileGroup
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -1315272657743409326L;

    public static int locationCount = 0;
    
    private final List<LocationGroup> locations;
    
    private String path;
    
    public FileGroup()
    {
        locations = new ArrayList<LocationGroup>();
        
    }
    
    public FileGroup(String stringValue)
    {
        path = stringValue;
        locations = new ArrayList<LocationGroup>();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFileGroup#AddLocation(com
     * .kdmanalytics.toif.report.internal.items.LocationGroup)
     */
    @Override
    public void AddLocation(LocationGroup location)
    {
        if (!locations.contains(location))
        {
            locations.add(location);
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {

        if (!(obj instanceof FileGroup))
        {
            return false;
        }

        FileGroup other = (FileGroup) obj;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFileGroup#getChildren()
     */
    @Override
    public List<ReportItem> getChildren()
    {
        List<ReportItem> result = new ArrayList<ReportItem>();
        for (ReportItem reportItem : locations)
        {
            if (!reportItem.getChildren().isEmpty())
            {
                result.add(reportItem);
            }
        }
        // result.addAll(locations);
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFileGroup#getFindingEntries
     * ()
     */
    @Override
    public List<FindingEntry> getFindingEntries()
    {
        List<FindingEntry> entries = new ArrayList<FindingEntry>();
        
        for (LocationGroup location : locations)
        {
            entries.addAll(location.getFindingEntries());
            locationCount++;
        }
        
        return entries;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFileGroup#getFindingEntriesNotOk
     * ()
     */
    @Override
    public List<FindingEntry> getFindingEntriesNotOk()
    {
        List<FindingEntry> entries = new ArrayList<FindingEntry>();
        
        for (LocationGroup location : locations)
        {
            entries.addAll(location.getFindingEntriesNotOk());
        }
        
        return entries;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFileGroup#
     * getFindingEntriesNotOkTrustSum()
     */
    @Override
    public int getFindingEntriesNotOkTrustSum()
    {
        
        return 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFileGroup#getLocationGroup()
     */
    @Override
    public List<LocationGroup> getLocationGroup()
    {
        return locations;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFileGroup#getName()
     */
    @Override
    public String getName()
    {
        int index = path.lastIndexOf("/");
        
        path = path.replaceFirst(".class", ".java");
        
        return path.substring(index + 1);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFileGroup#getPath()
     */
    @Override
    public String getPath()
    {
        return path;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFileGroup#getSearchableText
     * ()
     */
    @Override
    public String getSearchableText()
    {
        return parent.getSearchableText() + " " + path;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }
    
    @Override
    public String toString()
    {
        return "File path: " + path;
    }
    
}
