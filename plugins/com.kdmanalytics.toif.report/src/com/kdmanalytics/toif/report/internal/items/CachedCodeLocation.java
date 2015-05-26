/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the // accompanying materials are made available under the terms of the Open
 * Source // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Value;

/**
 * code locations that are stored for future use.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class CachedCodeLocation
{
    
    private Value id;
    
    private String path;
    
    private String line;
    
    private LinkedList<Value> findings;
    
    /**
     * new cached location
     * 
     * @param codeLocation
     *            toif code location
     * @param path
     *            code location path
     * @param lineno
     *            code location linenumber
     */
    public CachedCodeLocation(Value codeLocation, String path, String lineno)
    {
        this.id = codeLocation;
        this.path = path;
        this.line = lineno;
        this.findings = new LinkedList<Value>();
    }
    
    /**
     * does the given path and line match this codelocation
     * 
     */
    public boolean matches(String path, String line)
    {
        if (!this.path.equals(path))
            return false;
        if (!this.line.equals(line))
            return false;
        return true;
    }
    
    /**
     * get the code location id
     * 
     * @return
     */
    public Value getCodeLocation()
    {
        return id;
    }
    
    /**
     * get the codelocation path
     * 
     * @return
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * get the codelocation linenumber
     * 
     * @return
     */
    public String getLineNumber()
    {
        return line;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CachedCodeLocation))
            return false;
        CachedCodeLocation loc = (CachedCodeLocation) o;
        return id.equals(loc.id);
    }
    
    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
    
    /**
     * add an associated finding
     * 
     * @param finding
     */
    public void addFinding(Value finding)
    {
        findings.add(finding);
    }
    
    /**
     * get findings associated with this code location
     * 
     * @return
     */
    public List<Value> getFindings()
    {
        return findings;
    }
}
