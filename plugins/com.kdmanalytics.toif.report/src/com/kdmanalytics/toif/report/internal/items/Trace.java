/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.io.Serializable;

/**
 * a trace is additional line numbers for the location of a finding.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class Trace implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1373786791887348407L;
    private String lineNumber;
    
    /**
     * get the line number
     * 
     * @return
     */
    public String getLineNumber()
    {
        return lineNumber;
    }
    
    /**
     * set the line number
     * 
     * @param lineNumber
     */
    public void setLineNumber(String lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
    /**
     * create a new trace.
     * 
     * @param lineNumber
     */
    public Trace(String lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
}
