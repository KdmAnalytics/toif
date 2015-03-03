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

import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

@XmlRootElement(name = "fact")
public class CodeLocationReferencesFile extends Fact
{
    
    private CodeLocation location;
    
    private File file;
    
    public CodeLocationReferencesFile()
    {
        super();
        type = "toif:CodeLocationReferencesFile";
    }
    
    public CodeLocationReferencesFile(CodeLocation newLocation, File newFile)
    {
        super();
        type = "toif:CodeLocationReferencesFile";
        
        this.location = newLocation;
        this.file = newFile;
    }
    
    /**
     * @return the location
     */
    @XmlAttribute(name = "codeLocation")
    public String getLocation()
    {
        return location.getId();
    }
    
    /**
     * @param location
     *            the location to set
     */
    public void setLocation(CodeLocation location)
    {
        this.location = location;
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
    
}
