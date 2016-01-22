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

import com.kdmanalytics.toif.framework.xmlElements.entities.Date;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name = "fact")
public class TOIFSegmentIsCreatedAtDate extends Fact
{
    
    private Segment segment;
    
    private Date date;
    
    public TOIFSegmentIsCreatedAtDate()
    {
        super();
        type = "toif:TOIFSegmentIsCreatedAtDate";
    }
    
    public TOIFSegmentIsCreatedAtDate(Segment segment, Date date)
    {
        super();
        type = "toif:TOIFSegmentIsCreatedAtDate";
        this.segment = segment;
        this.date = date;
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
    
    /**
     * @return the date
     */
    @XmlAttribute
    public String getDate()
    {
        return date.getId();
    }
    
    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date)
    {
        this.date = date;
    }
    
}
