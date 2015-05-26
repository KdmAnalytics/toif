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

@XmlRootElement(name="version")
public class Version
{   
    private String version;

    public Version(String version)
    {
        super();
        this.version = version;
    }
    
    public Version() {
        super();
    }

    
    public String getVersion()
    {
        return version;
    }

    @XmlAttribute(name="version")
    public void setVersion(String version)
    {
        this.version = version;
    }
}
