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
 * representation of the directory
 * 
 * @author adam
 *         
 */
@XmlRootElement(name = "fact")
public class Directory extends Entity
{
    
    // the name of the directory
    private Name name;
    
    // the path to the directory
    private String path;
    
    // the actual directory
    private java.io.File directory;
    
    /**
     * Create the directory based on the path to the directory.
     * 
     * @param name
     */
    public Directory(String name)
    {
        super();
        type = "toif:Directory";
        try
        {
            this.directory = new java.io.File(name);
        }
        catch (Exception e)
        {
            System.err.println(name + " " + path);
        }
        this.path = name;
        
        // if empty then it is probably the root.
        if (directory.getName().isEmpty())
        {
            this.name = new Name(directory.getPath());
        }
        else
        {
            this.name = new Name(directory.getName());
        }
    }
    
    /**
     * required empty constructor
     */
    public Directory()
    {
        super();
        type = "toif:Directory";
    }
    
    /**
     * get the parent of the directory
     * 
     * @return the parent of the directory
     */
    public String getParent()
    {
        return directory.getParent();
    }
    
    /**
     * get the name of the directory
     * 
     * @return the name the name of the directory
     */
    @XmlElementRef
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the directory
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the path to the directory
     * 
     * @return the path as a string to the directory
     */
    public String getPath()
    {
        return path;
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
        Directory other = (Directory) obj;
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
    // * Over ride the equals method. This is so that we can make sure that only
    // * one of these elements is in the end toif output.
    // */
    // @Override
    // public boolean equals(Object obj)
    // {
    //
    // if (obj instanceof Directory)
    // {
    // Directory directory = (Directory) obj;
    //
    // return this.name.getName().equals(directory.getName().getName()) &&
    // this.path.equals(directory.getPath());
    // }
    // return false;
    //
    // }
    //
    // /**
    // * over ride the hash
    // */
    // @Override
    // public int hashCode()
    // {
    // return name.getName().hashCode() ^ path.hashCode() ^
    // "directory".hashCode();
    // }
    
}
