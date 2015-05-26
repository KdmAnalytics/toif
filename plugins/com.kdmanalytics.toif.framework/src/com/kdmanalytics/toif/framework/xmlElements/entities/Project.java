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
 * represents the project entity
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "fact")
public class Project extends Entity
{
    
    // the name of the project
    private Name name;
    
    // the description of the project
    private Description description;
    
    /**
     * create a new project entity
     * 
     * @param name
     *            the name of the project
     * @param description
     *            the description of the project
     */
    public Project(String name, String description)
    {
        super();
        type = "toif:Project";
        this.name = new Name(name);
        this.description = new Description(description);
    }
    
    /**
     * required empty constructor
     */
    public Project()
    {
        super();
        type = "toif:Project";
    }
    
    /**
     * get the name of the project
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the project
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the description of the project
     * 
     * @return the description
     */
    @XmlElementRef(name = "description")
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * get the description of the project
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * override the equals method so that we can make sure that there is only
     * one of these elements in the toif output.
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Project)
        {
            Project proj = (Project) obj;
            
            return name.getName().equals(proj.getName().getName()) && description.getText().equals(proj.getDescription().getText());
        }
        return false;
        
    }
    
    /**
     * override the hascode.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ description.getText().hashCode();
    }
    
}
