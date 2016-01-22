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

package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.kdmanalytics.toif.framework.xmlElements.entities.Generator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name = "fact")
public class TOIFSegmentIsGeneratedByGenerator extends Fact
{
    
    private Segment segment;
    
    private Generator generator;
    
    public TOIFSegmentIsGeneratedByGenerator()
    {
        super();
        type = "toif:TOIFSegmentIsGeneratedByGenerator";
    }
    
    public TOIFSegmentIsGeneratedByGenerator(Segment segment, Generator generator)
    {
        super();
        type = "toif:TOIFSegmentIsGeneratedByGenerator";
        this.segment = segment;
        this.generator = generator;
    }
    
    /**
     * @return the generator
     */
    @XmlAttribute
    public String getGenerator()
    {
        return generator.getId();
    }
    
    /**
     * @param generator
     *            the generator to set
     */
    public void setGenerator(Generator generator)
    {
        this.generator = generator;
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
     * @param segment
     *            the segment to set
     */
    public void setSegment(Segment segment)
    {
        this.segment = segment;
    }
    
}
