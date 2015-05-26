/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * represents an email address element.
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "email")
public class EmailAddress
{
    
    // the email address
    private String email;
    
    /**
     * constructs a new email address
     * 
     * @param email
     */
    public EmailAddress(String email)
    {
        super();
        this.email = email;
    }
    
    /**
     * required empty constructor
     */
    public EmailAddress()
    {
        super();
    }
    
    /**
     * get the email address
     * 
     * @return the email address
     */
    public String getEmail()
    {
        return email;
    }
    
    /**
     * set the email address.
     * 
     * @param email
     */
    @XmlAttribute(name = "email")
    public void setEmail(String email)
    {
        this.email = email;
    }
    
}
