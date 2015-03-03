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
 * represents the name attribute
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "name")
public class Name
{
    
    // the name
    private String name;
    
    /**
     * construct a new name
     * 
     * @param name
     *            the name
     */
    public Name(String name)
    {
        super();
        this.name = name;
    }
    
    /**
     * required empty constructor.
     */
    public Name()
    {
        super();
    }
    
    /**
     * get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * set the name
     * 
     * @param name
     *            the name
     */
    @XmlAttribute(name = "name")
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Name other = (Name) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
