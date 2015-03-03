/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fact")
public class Vendor extends Organization
{
    
    public Vendor()
    {
        super();
        type = "toif:Vendor";
    }
    
    public Vendor(String name, String description, String address, String phone, String email)
    {
        super(name, description, address, phone, email);
        type = "toif:Vendor";
    }
}
