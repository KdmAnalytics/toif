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
 * represents a generator entity.
 * 
 * @author adam
 * 
 */
@XmlType(propOrder = { "name", "description", "version" })
@XmlRootElement(name = "fact")
public class Generator extends Entity
{
    
    // the name of the generator
    private Name name;
    
    // the description of the generator
    private Description description;
    
    // the version of the generator
    private Version version;
    
    /**
     * construct a new generator entity.
     * 
     * @param name
     *            the name of the generator
     * @param description
     *            the generator's description
     * @param version
     *            the generator's version.
     */
    public Generator(String name, String description, String version)
    {
        super();
        type = "toif:Generator";
        if (name != null)
            this.name = new Name(name);
        if (description != null)
            this.description = new Description(description);
        if (version != null)
            this.version = new Version(version);
    }
    
    /**
     * required empty constructor
     */
    public Generator()
    {
        super();
        type = "toif:Generator";
    }
    
    /**
     * get the name of the generator
     * 
     * @return the name the generator's name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the generator.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the description of the generator
     * 
     * @return the description the generators description.
     */
    @XmlElementRef(name = "description")
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * set the description of the generatpor
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * get the version of the generator.
     * 
     * @return the version the version of the generator.
     */
    @XmlElementRef(name = "version")
    public Version getVersion()
    {
        return version;
    }
    
    /**
     * set the version of the generator.
     * 
     * @param version
     *            the version to set
     */
    public void setVersion(String version)
    {
        this.version = new Version(version);
    }
    
    /**
     * override the equals method. this is so that we can make sure that there
     * is only one of these elements in toif output.
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Generator)
        {
            Generator gen = (Generator) obj;
            
            return name.getName().equals(gen.getName().getName()) && description.getText().equals(gen.getDescription().getText())
                    && version.getVersion().equals(gen.getVersion().getVersion());
        }
        return false;
        
    }
    
    /**
     * override the hashcode
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ description.getText().hashCode() ^ version.getVersion().hashCode();
    }
    
}
