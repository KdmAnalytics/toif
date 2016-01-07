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

import com.kdmanalytics.toif.framework.xmlElements.entities.DataElement;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;

@XmlType(propOrder = { "finding", "dataElement" })
@XmlRootElement(name = "fact")
public class DataElementIsInvolvedInFinding extends Fact
{
    
    private DataElement dataElement;
    
    private Finding finding;
    
    public DataElementIsInvolvedInFinding(DataElement dataElement, Finding finding)
    {
        super();
        type = "toif:DataElementIsInvolvedInFinding";
        this.dataElement = dataElement;
        this.finding = finding;
    }
    
    public DataElementIsInvolvedInFinding()
    {
        super();
        type = "toif:DataElementIsInvolvedInFinding";
    }
    
    /**
     * @return the dataElement
     */
    @XmlAttribute(name = "data")
    public String getDataElement()
    {
        return dataElement.getId();
    }
    
    /**
     * @param dataElement
     *            the dataElement to set
     */
    public void setDataElement(DataElement dataElement)
    {
        this.dataElement = dataElement;
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
    
}
