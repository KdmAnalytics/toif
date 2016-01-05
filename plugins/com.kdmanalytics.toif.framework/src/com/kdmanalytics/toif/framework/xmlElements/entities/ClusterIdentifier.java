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
 * class which represents the cluster indentity entity
 * 
 * @author Adam Nunn
 *         
 */
@XmlRootElement(name = "fact")
public class ClusterIdentifier extends Entity
{
    
    // the name of the cluster
    private Name name;
    
    /**
     * required empty constructor
     */
    public ClusterIdentifier()
    {
        super();
        type = "toif:ClusterIdentifier";
    }
    
    /**
     * create a clusterIdentifier entity.
     * 
     * @param property
     */
    public ClusterIdentifier(String property)
    {
        super();
        type = "toif:ClusterIdentifier";
        
        this.name = new Name(property);
        
    }
    
    /**
     * get the name of the cluster
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
        
    }
    
    /**
     * set the name of the constructor.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * Override the equals to find equivalent clusterIdents
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof ClusterIdentifier)
        {
            ClusterIdentifier cluster = (ClusterIdentifier) obj;
            
            return name.getName().equals(cluster.getName().getName());
        }
        return false;
        
    }
    
    /**
     * override the hash.
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode();
    }
    
}
