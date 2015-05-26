/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.kdmanalytics.toif.framework.xmlElements.entities.Adaptor;
import com.kdmanalytics.toif.framework.xmlElements.entities.Vendor;

@XmlType(propOrder = { "vendor", "adaptor" })
@XmlRootElement(name="fact")
public class AdaptorIsSuppliedByVendor extends Fact
{
    
    private Adaptor adaptor;
    
    private Vendor vendor;
    
    public AdaptorIsSuppliedByVendor(Adaptor adaptor, Vendor vendor)
    {
        super();
        type = "toif:AdaptorIsSuppliedByVendor";
        this.adaptor = adaptor;
        this.vendor = vendor;
    }
    

    public AdaptorIsSuppliedByVendor()
    {
        super();
        type = "toif:AdaptorIsSuppliedByVendor";
    }


    
    /**
     * @return the adaptor
     */
    @XmlAttribute
    public String getAdaptor()
    {
        return adaptor.getId();
    }


    
    /**
     * @param adaptor the adaptor to set
     */
    public void setAdaptor(Adaptor adaptor)
    {
        this.adaptor = adaptor;
    }


    
    /**
     * @return the vendor
     */
    @XmlAttribute
    public String getVendor()
    {
        return vendor.getId();
    }


    
    /**
     * @param vendor the vendor to set
     */
    public void setVendor(Vendor vendor)
    {
        this.vendor = vendor;
    }
}
