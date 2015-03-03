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

import com.kdmanalytics.toif.framework.xmlElements.entities.Organization;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name="fact")
public class TOIFSegmentIsOwnedByOrganization extends Fact
{   
    private Segment segment;
    private Organization organization;
    
    public TOIFSegmentIsOwnedByOrganization() {
        super();
        type = "toif:TOIFSegmentIsOwnedByOrganization";
    }

    public TOIFSegmentIsOwnedByOrganization(Segment segment, Organization organization)
    {
        super();
        type = "toif:TOIFSegmentIsOwnedByOrganization";
        this.segment = segment;
        this.organization = organization;
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

    
    /**
     * @return the organization
     */
    @XmlAttribute
    public String getOrganization()
    {
        return organization.getId();
    }

    
    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }
}
