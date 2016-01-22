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
import javax.xml.bind.annotation.XmlType;

import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;
import com.kdmanalytics.toif.framework.xmlElements.entities.SFPIdentifier;

@XmlType(propOrder = { "sfpId", "finding" })
@XmlRootElement(name = "fact")
public class FindingHasSFPIdentifier extends Fact
{
    
    private Finding finding;
    
    private SFPIdentifier sfpId;
    
    public FindingHasSFPIdentifier()
    {
        super();
        type = "toif:FindingHasSFPIdentifier";
    }
    
    public FindingHasSFPIdentifier(Finding finding, SFPIdentifier sfpId)
    {
        super();
        type = "toif:FindingHasSFPIdentifier";
        
        this.finding = finding;
        this.sfpId = sfpId;
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
     * @param finding
     *            the finding to set
     */
    public void setFinding(Finding finding)
    {
        this.finding = finding;
    }
    
    /**
     * @return the cweId
     */
    @XmlAttribute(name = "sfp")
    public String getSfpId()
    {
        return sfpId.getId();
    }
    
    /**
     * @param cweId
     *            the cweId to set
     */
    public void setSfpId(SFPIdentifier sfpId)
    {
        this.sfpId = sfpId;
    }
    
}
