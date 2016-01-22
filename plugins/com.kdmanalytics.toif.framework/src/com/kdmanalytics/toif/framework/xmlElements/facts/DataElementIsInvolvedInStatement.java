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
import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;

@XmlType(propOrder = { "statement", "dataElement" })
@XmlRootElement(name = "fact")
public class DataElementIsInvolvedInStatement extends Fact
{
    
    private DataElement dataElement;
    
    private Statement statement;
    
    public DataElementIsInvolvedInStatement(DataElement dataElement, Statement statement)
    {
        super();
        type = "toif:DataElementIsInvolvedInStatement";
        this.dataElement = dataElement;
        this.statement = statement;
    }
    
    public DataElementIsInvolvedInStatement()
    {
        super();
        type = "toif:DataElementIsInvolvedInStatement";
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
     * @return the statement
     */
    @XmlAttribute(name = "statement")
    public String getStatement()
    {
        return statement.getId();
    }
    
    /**
     * @param statement
     *            the statement to set
     */
    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }
    
}
