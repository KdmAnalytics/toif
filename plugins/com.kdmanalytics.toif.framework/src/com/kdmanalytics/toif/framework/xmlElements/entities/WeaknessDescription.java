/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fact")
public class WeaknessDescription extends Entity
{
    
    private String weaknessDescription;
    
    public WeaknessDescription()
    {
        super();
        type = "toif:WeaknessDescription";
    }
    
    public WeaknessDescription(String weaknessDescription)
    {
        super();
        type = "toif:WeaknessDescription";
        this.weaknessDescription = weaknessDescription;
    }
    
    /**
     * @return the weaknessDescription
     */
    public String getWeaknessDescription()
    {
        return weaknessDescription;
    }
    
    /**
     * @param weaknessDescription
     *            the weaknessDescription to set
     */
    @XmlAttribute(name = "text")
    public void setWeaknessDescription(String weaknessDescription)
    {
        this.weaknessDescription = weaknessDescription;
    }
    
    /**
     * 
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof WeaknessDescription)
        {
            WeaknessDescription description = (WeaknessDescription) obj;
            
            return weaknessDescription.equals(description.getWeaknessDescription());
        }
        return false;
        
    }
    
    /**
     * 
     */
    @Override
    public int hashCode()
    {
        return weaknessDescription.hashCode();
    }
    
}
