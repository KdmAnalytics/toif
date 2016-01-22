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

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * class which represents the cweindentity entity
 * 
 * @author Adam Nunn
 *         
 */
@XmlRootElement(name = "fact")
public class CWEIdentifier extends Entity
{
    
    // the name of the cwe
    private Name name;
    
    /**
     * required blank constructor
     */
    public CWEIdentifier()
    {
        super();
        type = "toif:CWEIdentifier";
    }
    
    /**
     * create a cweIdentifier entity.
     * 
     * @param property
     */
    public CWEIdentifier(String property)
    {
        super();
        type = "toif:CWEIdentifier";
        
        this.name = new Name(property);
        
    }
    
    /**
     * get the name of the cwe
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
        
    }
    
    /**
     * set teh name of the cwe
     * 
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
        
        if (obj instanceof CWEIdentifier)
        {
            CWEIdentifier cwe = (CWEIdentifier) obj;
            
            return name.getName().equals(cwe.getName().getName());
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
