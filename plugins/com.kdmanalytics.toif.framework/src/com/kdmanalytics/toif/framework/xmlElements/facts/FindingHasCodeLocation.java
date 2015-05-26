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

import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;

@XmlType(propOrder = { "location", "finding" })
@XmlRootElement(name="fact")
public class FindingHasCodeLocation extends Fact
{   
    
    private Finding finding;
    
    private CodeLocation location;
    
    public FindingHasCodeLocation(Finding finding, CodeLocation location)
    {
        super();
        type="toif:FindingHasCodeLocation";
        this.finding = finding;
        this.location = location;
    }
    
    public FindingHasCodeLocation() {
        super();
        type="toif:FindingHasCodeLocation";
    }

    
    /**
     * @return the finding
     */
    @XmlAttribute
    public String getFinding()
    {
        return finding.getId();
    }

    
    /**
     * @param finding the finding to set
     */
    public void setFinding(Finding finding)
    {
        this.finding = finding;
    }

    
    /**
     * @return the location
     */
    @XmlAttribute
    public String getLocation()
    {
        return location.getId();
    }

    
    /**
     * @param location the location to set
     */
    public void setLocation(CodeLocation location)
    {
        this.location = location;
    }
    
    
}
