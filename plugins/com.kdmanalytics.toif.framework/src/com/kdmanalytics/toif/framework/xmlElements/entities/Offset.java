/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * represents the offset attribute
 * 
 * @author adam
 *         
 */
@XmlRootElement(name = "offset")
public class Offset
{
    
    // the offset
    private Integer offset;
    
    /**
     * construct a new offset entity
     * 
     * @param offset
     */
    public Offset(Integer offset)
    {
        super();
        this.offset = offset;
    }
    
    /**
     * required empty constructor
     */
    public Offset()
    {
        super();
    }
    
    /**
     * get the offset
     * 
     * @return the offset
     */
    public Integer getOffset()
    {
        return offset;
    }
    
    /**
     * set the offset
     * 
     * @param offset
     *            the offset.
     */
    @XmlAttribute(name = "offset")
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (offset != null)
            return offset.toString();
        return "0";
    }
}
