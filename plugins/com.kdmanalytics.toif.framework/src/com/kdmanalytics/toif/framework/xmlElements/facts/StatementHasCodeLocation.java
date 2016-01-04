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

import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.Statement;

@XmlType(propOrder = { "location", "statement" })
@XmlRootElement(name = "fact")
public class StatementHasCodeLocation extends Fact
{
    
    private Statement statement;
    
    private CodeLocation location;
    
    public StatementHasCodeLocation(Statement statement, CodeLocation codeLocation)
    {
        super();
        type = "toif:StatementHasCodeLocation";
        this.statement = statement;
        this.location = codeLocation;
    }
    
    public StatementHasCodeLocation()
    {
        super();
        type = "toif:StatementHasCodeLocation";
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
     * @return the codeLocation
     */
    @XmlAttribute
    public String getlocation()
    {
        return location.getId();
    }
    
    /**
     * @param codeLocation
     *            the codeLocation to set
     */
    public void setlocation(CodeLocation codeLocation)
    {
        this.location = codeLocation;
    }
    
}
