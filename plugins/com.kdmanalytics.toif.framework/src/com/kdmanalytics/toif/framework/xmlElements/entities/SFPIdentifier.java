/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * class which represents the sfp-indentity entity
 * 
 * @author Adam Nunn
 * 
 */
@XmlRootElement(name = "fact")
public class SFPIdentifier extends Entity
{
    
    // the name of the cwe
    private Name name;
    
    /**
     * required blank constructor
     */
    public SFPIdentifier()
    {
        super();
        type = "toif:SFPIdentifier";
    }
    
    /**
     * create a cweIdentifier entity.
     * 
     * @param property
     */
    public SFPIdentifier(String property)
    {
        super();
        type = "toif:SFPIdentifier";
        
        this.name = new Name(property);
        
    }
    
    /**
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
        
    }
    
    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * Override the equals to find equivalent cweIdents
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof SFPIdentifier)
        {
            SFPIdentifier sfp = (SFPIdentifier) obj;
            
            return name.getName().equals(sfp.getName().getName());
        }
        return false;
        
    }
    
    /**
     * override the hash.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode();
    }
    
}
