/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing the adaptor entity
 * 
 * @author Adam Nunn
 * 
 */
@XmlRootElement(name = "fact")
public class Adaptor extends Entity
{
    
    // the name of the adaptor
    private Name name;
    
    // the description of the adaptor
    private Description description;
    
    // the version of the adaptor
    private Version version;
    
    /**
     * Creates an adaptor entity from the name, description, and version.
     * 
     * @param name
     *            the name of the adaptor
     * @param description
     *            the description of the adaptor
     * @param version
     *            the version of the adaptor
     */
    public Adaptor(String name, String description, String version)
    {
        super();
        
        // set the xml type of this entity to "toif:Adaptor"
        type = "toif:Adaptor";
        
        this.name = new Name(name);
        this.description = new Description(description);
        this.version = new Version(version);
    }
    
    /**
     * required blank constructor
     */
    public Adaptor()
    {
        super();
        type = "toif:Adaptor";
    }
    
    /**
     * Get the name of the adaptor
     * 
     * @return the name
     */
    @XmlElementRef(name="name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * Set the name of the adaptor.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * Get the description of the adaptor.
     * 
     * @return the description
     */
    @XmlElementRef(name="name")
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * set the description of the adaptor
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * get the version of the adaptor
     * 
     * @return the version
     */
    @XmlElementRef(name="version")
    public Version getVersion()
    {
        return version;
    }
    
    /**
     * set the version of the adaptor
     * 
     * @param version
     *            the version to set
     */
    public void setVersion(String version)
    {
        this.version = new Version(version);
    }
    
    /**
     * Override the equals method. Any Adaptor which has the same name,
     * description, and version is equivalent.
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Adaptor)
        {
            Adaptor adaptor = (Adaptor) obj;
            
            return name.getName().equals(adaptor.getName().getName()) && description.getText().equals(adaptor.getDescription().getText()) && version.getVersion().equals(adaptor.getVersion().getVersion());
        }
        return false;
        
    }
    
    /**
     * create the hash for the object.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ description.getText().hashCode() ^ version.getVersion().hashCode();
    }
}
