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
import javax.xml.bind.annotation.XmlType;

/**
 * represents the role entity.
 * 
 * @author adam
 * 
 */
@XmlType(propOrder = { "name", "description" })
@XmlRootElement(name = "fact")
public class Role extends Entity
{
    
    // the name of the role
    private Name name;
    
    // the description of the role
    private Description description;
    
    /**
     * construct a new role
     * 
     * @param name
     *            the name of the role
     * @param description
     *            the description of the role
     */
    public Role(String name, String description)
    {
        super();
        type = "toif:Role";
        this.name = new Name(name);
        this.description = new Description(description);
    }
    
    /**
     * required empty constructor
     */
    public Role()
    {
        super();
        type = "toif:Role";
    }
    
    /**
     * get the name of the role
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name for the role
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the description for the role
     * 
     * @return the description
     */
    @XmlElementRef(name = "description")
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * set the description for the role
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * override the equals method. This is to be sure that there is only one of
     * these roles in the toif output.
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Role)
        {
            Role role = (Role) obj;
            
            return name.getName().equals(role.getName().getName()) && description.getText().equals(role.getDescription().getText());
        }
        return false;
        
    }
    
    /**
     * override the hashcode.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ description.getText().hashCode();
    }
    
}
