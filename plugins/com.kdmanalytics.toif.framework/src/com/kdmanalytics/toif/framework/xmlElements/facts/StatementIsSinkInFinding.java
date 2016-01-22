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

import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;
import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;

@XmlRootElement(name = "fact")
public class StatementIsSinkInFinding extends Fact
{
    
    private Statement statement;
    
    private Finding finding;
    
    public StatementIsSinkInFinding()
    {
        super();
        type = "toif:StatementIsSinkInFinding";
    }
    
    public StatementIsSinkInFinding(Statement statement, Finding finding)
    {
        super();
        type = "toif:StatementIsSinkInFinding";
        this.statement = statement;
        this.finding = finding;
    }
    
    /**
     * @return the statement
     */
    @XmlAttribute
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
    
}
