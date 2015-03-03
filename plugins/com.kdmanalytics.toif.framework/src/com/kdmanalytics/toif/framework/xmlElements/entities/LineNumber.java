/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * represents the linenumber entity.
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "lineNumber")
public class LineNumber
{
    
    // the linenumber
    private Integer lineNumber;
    
    /**
     * costruct a new line number entity.
     * 
     * @param lineNumber
     */
    public LineNumber(Integer lineNumber)
    {
        super();
        this.lineNumber = lineNumber;
    }
    
    /**
     * required empty constructor
     */
    public LineNumber()
    {
        super();
    }
    
    /**
     * get the line number.
     * 
     * @return the line number.
     */
    public Integer getLineNumber()
    {
        return lineNumber;
    }
    
    /**
     * set the line number
     * 
     * @param lineNumber
     *            the line number where the finding is found.
     */
    @XmlAttribute(name = "lineNumber")
    public void setLineNumber(Integer lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
}
