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

import com.kdmanalytics.toif.framework.xmlElements.entities.Adaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name="fact")
public class TOIFSegmentIsProcessedByAdaptor extends Fact
{   
    private Segment segment;
    private Adaptor adaptor;

    public TOIFSegmentIsProcessedByAdaptor() {
        super();
        type = "toif:TOIFSegmentIsProcessedByAdaptor";
    }
    
    public TOIFSegmentIsProcessedByAdaptor(Segment segment, Adaptor adaptor)
    {
        super();
        type = "toif:TOIFSegmentIsProcessedByAdaptor";
        this.segment = segment;
        this.adaptor = adaptor;
    }

    
    /**
     * @return the generator
     */
    @XmlAttribute
    public String getAdaptor()
    {
        return adaptor.getId();
    }

    
    /**
     * @param generator the generator to set
     */
    public void setAdaptor(Adaptor adaptor)
    {
        this.adaptor = adaptor;
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
