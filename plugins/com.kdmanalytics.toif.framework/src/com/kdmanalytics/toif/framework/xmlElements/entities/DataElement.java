/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * class which represents the data Element entity.
 * 
 * @author Adam Nunn
 *         
 */
@XmlRootElement(name = "fact")
public class DataElement extends Entity
{
    
    // the name of the dataelement.
    private Name name;
    
    /**
     * construct a dataelement
     * 
     * @param name
     */
    public DataElement(String name)
    {
        super();
        
        // set the xml type
        type = "toif:DataElement";
        
        this.name = new Name(name);
    }
    
    /**
     * required blank constructor
     */
    public DataElement()
    {
        super();
        
        // set the xml type
        type = "toif:DataElement";
    }
    
    /**
     * get the name of the data element.
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the data element.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * Over ride the equals method
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof DataElement)
        {
            DataElement dataElement = (DataElement) obj;
            
            return name.getName().equals(dataElement.getName().getName());
        }
        return false;
        
    }
    
    /**
     * override the hash.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ "dataElement".hashCode();
    }
}
