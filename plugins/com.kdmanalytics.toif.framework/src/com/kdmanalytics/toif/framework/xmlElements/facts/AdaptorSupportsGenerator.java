/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
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
import com.kdmanalytics.toif.framework.xmlElements.entities.Generator;

@XmlType(propOrder = { "generator", "adaptor" })
@XmlRootElement(name = "fact")
public class AdaptorSupportsGenerator extends Fact
{
    
    private Adaptor adaptor;
    
    private Generator generator;
    
    public AdaptorSupportsGenerator()
    {
        super();
        type = "toif:AdaptorSupportsGenerator";
    }
    
    /**
     * 
     * @param adaptor
     * @param generator
     */
    public AdaptorSupportsGenerator(Adaptor adaptor, Generator generator)
    {
        super();
        type = "toif:AdaptorSupportsGenerator";
        this.adaptor = adaptor;
        this.generator = generator;
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
     * @param adaptor
     *            the adaptor to set
     */
    public void setAdaptor(Adaptor adaptor)
    {
        this.adaptor = adaptor;
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
     * @param generator
     *            the generator to set
     */
    public void setGenerator(Generator generator)
    {
        this.generator = generator;
    }
}
