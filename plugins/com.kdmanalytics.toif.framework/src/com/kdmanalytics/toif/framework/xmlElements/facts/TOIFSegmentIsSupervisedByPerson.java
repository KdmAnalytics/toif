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

package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.kdmanalytics.toif.framework.xmlElements.entities.Person;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name = "fact")
public class TOIFSegmentIsSupervisedByPerson extends Fact
{
    
    private Segment segment;
    
    private Person person;
    
    public TOIFSegmentIsSupervisedByPerson()
    {
        super();
        type = "toif:TOIFSegmentIsSupervisedByPerson";
    }
    
    public TOIFSegmentIsSupervisedByPerson(Segment segment, Person person)
    {
        super();
        type = "toif:TOIFSegmentIsSupervisedByPerson";
        this.segment = segment;
        this.person = person;
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
     * @return the person
     */
    @XmlAttribute
    public String getPerson()
    {
        return person.getId();
    }
    
    /**
     * @param person
     *            the person to set
     */
    public void setPerson(Person person)
    {
        this.person = person;
    }
}
