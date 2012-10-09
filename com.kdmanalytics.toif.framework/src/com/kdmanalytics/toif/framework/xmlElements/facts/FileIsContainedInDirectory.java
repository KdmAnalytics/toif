/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.kdmanalytics.toif.framework.xmlElements.entities.Directory;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

@XmlRootElement(name = "fact")
public class FileIsContainedInDirectory extends Fact
{
    
    private File file;
    
    private Directory directory;
    
    public FileIsContainedInDirectory()
    {
        super();
        type = "toif:FileIsContainedInDirectory";
    }
    
    public FileIsContainedInDirectory(File file, Directory directory)
    {
        super();
        type = "toif:FileIsContainedInDirectory";
        this.file = file;
        this.directory = directory;
    }
    
    /**
     * @return the file
     */
    @XmlAttribute(name = "file")
    public String getFile()
    {
        return file.getId();
    }
    
    /**
     * @param file
     *            the file to set
     */
    public void setFile(File file)
    {
        this.file = file;
    }
    
    /**
     * @return the directory
     */
    @XmlAttribute(name = "directory")
    public String getDirectory()
    {
        return directory.getId();
    }
    
    /**
     * @param directory
     *            the directory to set
     */
    public void setDirectory(Directory directory)
    {
        this.directory = directory;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directory == null) ? 0 : directory.hashCode());
        result = prime * result + ((file == null) ? 0 : file.hashCode());
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
        FileIsContainedInDirectory other = (FileIsContainedInDirectory) obj;
        if (directory == null)
        {
            if (other.directory != null)
                return false;
        }
        else if (!directory.equals(other.directory))
            return false;
        if (file == null)
        {
            if (other.file != null)
                return false;
        }
        else if (!file.equals(other.file))
            return false;
        return true;
    }
    
}
