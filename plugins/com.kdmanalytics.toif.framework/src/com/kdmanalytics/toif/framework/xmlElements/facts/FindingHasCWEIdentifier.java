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

import com.kdmanalytics.toif.framework.xmlElements.entities.CWEIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;

@XmlRootElement(name = "fact")
public class FindingHasCWEIdentifier extends Fact
{
    
    private Finding finding;
    
    private CWEIdentifier cweId;
    
    public FindingHasCWEIdentifier()
    {
        super();
        type = "toif:FindingHasCWEIdentifier";
    }
    
    public FindingHasCWEIdentifier(Finding finding, CWEIdentifier cweId)
    {
        super();
        type = "toif:FindingHasCWEIdentifier";
        
        this.finding = finding;
        this.cweId = cweId;
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
    @XmlAttribute(name = "cwe")
    public String getCweId()
    {
        return cweId.getId();
    }
    
    /**
     * @param cweId
     *            the cweId to set
     */
    public void setCweId(CWEIdentifier cweId)
    {
        this.cweId = cweId;
    }

}
