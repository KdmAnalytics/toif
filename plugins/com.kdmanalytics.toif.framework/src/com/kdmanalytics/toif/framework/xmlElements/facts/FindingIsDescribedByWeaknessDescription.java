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
import javax.xml.bind.annotation.XmlType;

import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;
import com.kdmanalytics.toif.framework.xmlElements.entities.WeaknessDescription;

@XmlType(propOrder = { "weaknessDescription", "finding" })
@XmlRootElement(name = "fact")
public class FindingIsDescribedByWeaknessDescription extends Fact
{
    
    private Finding finding;
    
    private WeaknessDescription weaknessDescription;
    
    public FindingIsDescribedByWeaknessDescription()
    {
        super();
        type = "toif:FindingIsDescribedByWeaknessDescription";
    }
    
    public FindingIsDescribedByWeaknessDescription(Finding finding, WeaknessDescription weaknessDescription)
    {
        super();
        type = "toif:FindingIsDescribedByWeaknessDescription";
        
        this.finding = finding;
        this.weaknessDescription = weaknessDescription;
    }
    
    /**
     * @return the finding
     */
    @XmlAttribute(name = "finding")
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
     * @return the weaknessDescription
     */
    @XmlAttribute(name = "description")
    public String getWeaknessDescription()
    {
        return weaknessDescription.getId();
    }
    
    /**
     * @param weaknessDescription
     *            the weaknessDescription to set
     */
    public void setWeaknessDescription(WeaknessDescription weaknessDescription)
    {
        this.weaknessDescription = weaknessDescription;
    }
    
}
