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
 * representation of the phone number attribute
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "phone")
public class Phone
{
    
    // the phone number
    private String phone;
    
    /**
     * constuct a new phone number attribute
     * 
     * @param phone
     *            the phone number
     */
    public Phone(String phone)
    {
        super();
        this.phone = phone;
    }
    
    /**
     * required empty constructor
     */
    public Phone()
    {
        super();
    }
    
    /**
     * get the phone number
     * 
     * @return the phone number
     */
    public String getPhone()
    {
        return phone;
    }
    
    /**
     * set the phone number
     * 
     * @param phone
     *            the phone number
     */
    @XmlAttribute(name = "phone")
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
