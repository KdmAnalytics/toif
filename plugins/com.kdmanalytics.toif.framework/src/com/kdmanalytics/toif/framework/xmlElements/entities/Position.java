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
 * represents the position attribute.
 * 
 * @author adam
 *         
 */
@XmlRootElement(name = "position")
public class Position
{
    
    // the position
    private Integer position;
    
    /**
     * construct a new position attribute.
     * 
     * @param position
     *            the position of the finding
     */
    public Position(Integer position)
    {
        super();
        this.position = position;
    }
    
    /**
     * required empty constructor.
     */
    public Position()
    {
        super();
    }
    
    /**
     * get the postion.
     * 
     * @return the position
     */
    public Integer getPosition()
    {
        return position;
    }
    
    /**
     * set the postion
     * 
     * @param position
     *            the position.
     */
    @XmlAttribute(name = "position")
    public void setPosition(Integer position)
    {
        this.position = position;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (position != null)
            return position.toString();
        return "0";
    }
}
