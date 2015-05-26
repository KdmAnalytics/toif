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
 * Represents the Address entity
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "address")
public class Address
{
    
    // a string representing the address.
    private String address;
    
    /**
     * construct the entity
     * 
     * @param address
     */
    public Address(String address)
    {
        super();
        this.address = address;
    }
    
    /**
     * empty constructor.
     */
    public Address()
    {
        super();
    }
    
    /**
     * get the address
     * 
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }
    
    /**
     * set the address
     * 
     * @param address
     *            The address.
     */
    @XmlAttribute(name = "address")
    public void setAddress(String address)
    {
        this.address = address;
    }
    
}
