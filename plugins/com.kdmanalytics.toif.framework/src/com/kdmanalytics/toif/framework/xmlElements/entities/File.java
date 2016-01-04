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

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fact")
public class File extends Entity
{
    
    // the name of the file.
    private Name name;
    
    // the version of the file
    private Version version;
    
    // the checksum of the file.
    private Checksum checksum;
    
    // the path to the file.
    private String path;
    
    /**
     * required empty constructor
     */
    public File()
    {
        super();
        type = "toif:File";
    }
    
    /**
     * cronstruct a new file entity
     * 
     * @param name
     *            the name of the file.
     */
    public File(String name)
    {
        super();
        type = "toif:File";
        this.path = name;
        
        java.io.File file = new java.io.File(name);
        
        this.name = new Name(file.getName());
        
        checksum = new Checksum(file.getPath());
        
        if (checksum == null)
        {
            checksum = new Checksum("none");
        }
        
    }
    
    /**
     * get the name of the file.
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the file
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the version of the file.
     * 
     * @return the version
     */
    @XmlElementRef(name = "version")
    public Version getVersion()
    {
        return version;
    }
    
    /**
     * set the version of the file.
     * 
     * @param version
     *            the version to set
     */
    public void setVersion(String version)
    {
        this.version = new Version(version);
    }
    
    /**
     * get the checksum.
     * 
     * @return the checksum
     */
    @XmlElementRef(name = "checksum")
    public Checksum getChecksum()
    {
        return checksum;
    }
    
    /**
     * set the checksum
     * 
     * @param checksum
     *            the checksum to set
     */
    public void setChecksum(String checksum)
    {
        this.checksum = new Checksum(checksum);
    }
    
    public String getPath()
    {
        return path;
    }
    
    /**
     * get the parent of the file.
     * 
     * @return
     */
    public String getParent()
    {
        java.io.File file = new java.io.File(path);
        if (file.getParent() != null)
        {
            return file.getParent();
        }
        return null;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
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
        File other = (File) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (path == null)
        {
            if (other.path != null)
                return false;
        }
        else if (!path.equals(other.path))
            return false;
        return true;
    }
    
    // /**
    // * override the equals method so that only one of these files is present
    // in
    // * the elements.
    // */
    // @Override
    // public boolean equals(Object obj)
    // {
    //
    // if (obj instanceof File)
    // {
    // File file = (File) obj;
    //
    // return getName().getName().equals(file.getName().getName()) &&
    // getPath().equals(file.getPath());
    // }
    // return false;
    //
    // }
    //
    // /**
    // * override the hashcode.
    // */
    // @Override
    // public int hashCode()
    // {
    // return getName().getName().hashCode() ^ getPath().hashCode() ^
    // "file".hashCode();
    // }
}
