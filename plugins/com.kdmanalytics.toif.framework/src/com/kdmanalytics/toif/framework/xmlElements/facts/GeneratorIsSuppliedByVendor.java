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

import com.kdmanalytics.toif.framework.xmlElements.entities.Generator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Vendor;

@XmlType(propOrder = { "vendor", "generator" })
@XmlRootElement(name="fact")
public class GeneratorIsSuppliedByVendor extends Fact
{   
    
    private Generator generator;
    
    private Vendor vendor;
    
    public GeneratorIsSuppliedByVendor(Generator generator, Vendor vendor)
    {
        super();
        type="toif:GeneratorIsSuppliedByVendor";
        this.generator = generator;
        this.vendor = vendor;
    }
    
    public GeneratorIsSuppliedByVendor() {
        super();
        type="toif:GeneratorIsSuppliedByVendor";
    }

    
    /**
     * @return the generator
     */
    @XmlAttribute
    public String getGenerator()
    {
        return generator.getId();
    }

    
    /**
     * @param generator the generator to set
     */
    public void setGenerator(Generator generator)
    {
        this.generator = generator;
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
