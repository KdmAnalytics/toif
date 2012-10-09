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

import org.openrdf.model.Value;
import org.openrdf.repository.Repository;

import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IReportItem;

/**
 * finding entrys are the individual findings.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class FindingEntry extends ReportItem implements IFindingEntry
{
    
    String finding = "Test";
    
    private boolean isOk;
    
    private String weaknessDescription = "Test";
    
    private String sfp = "-";
    
    private String cwe;
    
    private int trust = 0;
    
    private Repository repository;
    
    Value value = null;
    
    private List<Trace> traces = new ArrayList<Trace>();
    
    public FindingEntry()
    {
        isOk = true;
    }
    
    public FindingEntry(Value value, Repository repository)
    {
        isOk = true;
        this.value = value;
        this.repository = repository;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#getValue()
     */
    @Override
    public Value getValue()
    {
        return value;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#equals(java
     * .lang.Object)
     */
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
        FindingEntry other = (FindingEntry) obj;
        if (cwe == null)
        {
            if (other.cwe != null)
            {
                return false;
            }
        }
        else if (!cwe.equals(other.cwe))
        {
            return false;
        }
        
        if (parent == null)
        {
            if (other.parent != null)
            {
                return false;
            }
        }
        else if (!parent.equals(other.parent))
        {
            return false;
        }
        if (sfp == null)
        {
            if (other.sfp != null)
            {
                return false;
            }
        }
        else if (!sfp.equals(other.sfp))
        {
            return false;
        }
        if (weaknessDescription == null)
        {
            if (other.weaknessDescription != null)
            {
                return false;
            }
        }
        else if (!weaknessDescription.equals(other.weaknessDescription))
        {
            return false;
        }
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getChildren()
     */
    @Override
    public List<ReportItem> getChildren()
    {
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#getCwe()
     */
    @Override
    public String getCwe()
    {
        return cwe;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getDescription
     * ()
     */
    @Override
    public String getDescription()
    {
        return weaknessDescription;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getFindingEntries
     * ()
     */
    @Override
    public List<FindingEntry> getFindingEntries()
    {
        List<FindingEntry> findings = new ArrayList<FindingEntry>();
        findings.add(this);
        return findings;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getFindingId()
     */
    @Override
    public String getFindingId()
    {
        return value.stringValue();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getRepository()
     */
    @Override
    public Repository getRepository()
    {
        return repository;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getSearchableText
     * ()
     */
    @Override
    public String getSearchableText()
    {
        return parent.getSearchableText() + " " + toString();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#getSfp()
     */
    @Override
    public String getSfp()
    {
        return sfp;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#getTool()
     */
    @Override
    public ToolGroup getTool()
    {
        
        IReportItem item = this;
        
        while (item != null)
        {
            if (item instanceof ToolGroup)
            {
                return (ToolGroup) item;
            }
            else
            {
                item = item.getParent();
            }
        }
        
        return null;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#getTrust()
     */
    @Override
    public int getTrust()
    {
        return trust;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((cwe == null) ? 0 : cwe.hashCode());
        // result = prime * result + ((finding == null) ? 0 :
        // finding.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((sfp == null) ? 0 : sfp.hashCode());
        result = prime * result + ((weaknessDescription == null) ? 0 : weaknessDescription.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#isOk()
     */
    @Override
    public boolean isOk()
    {
        return isOk;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setCwe(java
     * .lang.String)
     */
    @Override
    public void setCwe(String cwe)
    {
        this.cwe = cwe;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setDescription
     * (java.lang.String)
     */
    @Override
    public void setDescription(String stringValue)
    {
        weaknessDescription = stringValue;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setIsOk(boolean
     * )
     */
    @Override
    public void setIsOk(boolean isOk)
    {
        this.isOk = isOk;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setSfp(java
     * .lang.String)
     */
    @Override
    public void setSfp(String sfp)
    {
        this.sfp = sfp;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setTrust(int)
     */
    @Override
    public void setTrust(int value)
    {
        this.trust = value;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.report.internal.items.IFindingEntry#toString()
     */
    @Override
    public String toString()
    {
        return sfp + " : " + cwe + " : trust = " + trust + " : " + weaknessDescription;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#getTraces()
     */
    @Override
    public List<Trace> getTraces()
    {
        return traces;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.kdmanalytics.toif.report.internal.items.IFindingEntry#setTraces(java
     * .util.List)
     */
    @Override
    public void setTraces(List<Trace> tracesList)
    {
        this.traces = tracesList;
        
    }
    
}
