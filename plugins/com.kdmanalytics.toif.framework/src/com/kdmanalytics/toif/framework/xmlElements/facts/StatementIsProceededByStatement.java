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

import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;

@XmlRootElement(name = "fact")
public class StatementIsProceededByStatement extends Fact
{
    
    private Statement statement1;
    
    private Statement statement2;
    
    public StatementIsProceededByStatement()
    {
        super();
        type = "toif:StatementIsProceededByStatement";
    }
    
    public StatementIsProceededByStatement(Statement statement1, Statement statement2)
    {
        super();
        type = "toif:StatementIsProceededByStatement";
        this.statement1 = statement1;
        this.statement2 = statement2;
    }
    
    /**
     * @return the statement
     */
    @XmlAttribute
    public String getStatement1()
    {
        return statement1.getId();
    }
    
    /**
     * @param statement
     *            the statement to set
     */
    public void setStatement1(Statement statement)
    {
        this.statement1 = statement;
    }
    
    /**
     * @return the finding
     */
    @XmlAttribute
    public String getStatement2()
    {
        return statement2.getId();
    }
    
    /**
     * @param finding
     *            the finding to set
     */
    public void setStatement2(Statement statement)
    {
        this.statement2 = statement;
    }
}
