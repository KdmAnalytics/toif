/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.kdmanalytics.toif.framework.xmlElements.entities.Directory;

@XmlType(propOrder = { "directory2", "directory1" })
@XmlRootElement(name = "fact")
public class DirectoryIsContainedInDirectory extends Fact
{
    
    private Directory directory1;
    
    private Directory directory2;
    
    public DirectoryIsContainedInDirectory()
    {
        super();
        type = "toif:DirectoryIsContainedInDirectory";
    }
    
    public DirectoryIsContainedInDirectory(Directory directory1, Directory directory2)
    {
        super();
        type = "toif:DirectoryIsContainedInDirectory";
        this.directory1 = directory1;
        this.directory2 = directory2;
    }
    
    /**
     * @return the directory1
     */
    @XmlAttribute(name = "directory1")
    public String getDirectory1()
    {
        return directory1.getId();
    }
    
    /**
     * @param directory1
     *            the directory1 to set
     */
    public void setDirectory1(Directory directory1)
    {
        this.directory1 = directory1;
    }
    
    /**
     * @return the directory1
     */
    public String getDirectory1Path()
    {
        return directory1.getPath();
    }
    
    /**
     * @return the directory1
     */
    public String getDirectory2Path()
    {
        return directory2.getPath();
    }
    
    /**
     * @return the directory2
     */
    @XmlAttribute(name = "directory2")
    public String getDirectory2()
    {
        return directory2.getId();
    }
    
    /**
     * @param directory2
     *            the directory2 to set
     */
    public void setDirectory2(Directory directory2)
    {
        this.directory2 = directory2;
    }
    
    /**
     * 
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof DirectoryIsContainedInDirectory)
        {
            DirectoryIsContainedInDirectory fact = (DirectoryIsContainedInDirectory) obj;
            
            return getDirectory1Path().equals(fact.getDirectory1Path()) && getDirectory2Path().equals(fact.getDirectory2Path());
        }
        return false;
        
    }
    
    /**
     * 
     */
    @Override
    public int hashCode() {
        return getDirectory1Path().hashCode() ^ getDirectory2Path().hashCode();
    }

    
}
