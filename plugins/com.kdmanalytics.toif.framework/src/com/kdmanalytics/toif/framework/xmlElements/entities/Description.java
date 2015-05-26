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
 * representation of the description entity.
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "description")
public class Description
{
    
    // the description
    private String text;
    
    /**
     * construct a description
     * 
     * @param text
     *            the description.
     */
    public Description(String text)
    {
        super();
        this.text = text;
    }
    
    /**
     * required empty constructor
     */
    public Description()
    {
        super();
    }
    
    /**
     * get the text
     * 
     * @return
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * set the text for the description
     * 
     * @param text
     *            the description
     */
    @XmlAttribute(name = "text")
    public void setText(String text)
    {
        this.text = text;
    }
}
