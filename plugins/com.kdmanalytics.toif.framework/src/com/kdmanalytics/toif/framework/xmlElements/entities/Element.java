/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
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
 * Type that all other entities and facts extend.
 * 
 * @author adam
 *         
 */
@XmlRootElement
public class Element
{
    
    // the type of the element.
    protected String type = "";
    
    // the id of the element.
    private long id;
    
    /**
     * empty constructor.
     */
    public Element()
    {
    }
    
    /**
     * get the id of the element.
     * 
     * @return the id
     */
    @XmlAttribute
    public String getId()
    {
        return id + "";
    }
    
    /**
     * set the id of the element
     * 
     * @param id
     *            the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }
    
    /**
     * get the type of the element.
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * set the type of the element.
     * 
     * @param type
     *            the type to set
     */
    @XmlAttribute(name = "xsi:type")
    public void setType(String type)
    {
        this.type = type;
    }
    
}
