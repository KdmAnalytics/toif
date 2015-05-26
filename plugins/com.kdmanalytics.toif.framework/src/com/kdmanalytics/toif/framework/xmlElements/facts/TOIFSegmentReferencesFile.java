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

import com.kdmanalytics.toif.framework.xmlElements.entities.File;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name = "fact")
public class TOIFSegmentReferencesFile extends Fact
{
    
    private File file = null;
    
    private Segment segment = null;
    
    /**
     * 
     * @param segment
     * @param file
     */
    public TOIFSegmentReferencesFile(Segment segment, File file)
    {
        super();
        type = "toif:TOIFSegmentReferencesFile";
        this.segment = segment;
        this.file = file;
    }
    
    /**
     * 
     */
    public TOIFSegmentReferencesFile() {
        super();
        type = "toif:TOIFSegmentReferencesFile";
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
     * @param file the file to set
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    
    /**
     * @return the segment
     */
    @XmlAttribute
    public String getSegment()
    {
        return segment.getName().getName();
    }

    
    /**
     * @param segment the segment to set
     */
    public void setSegment(Segment segment)
    {
        this.segment = segment;
    }
    
}
