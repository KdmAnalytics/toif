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

import com.kdmanalytics.toif.framework.xmlElements.entities.ClusterIdentifier;
import com.kdmanalytics.toif.framework.xmlElements.entities.Finding;

@XmlRootElement(name = "fact")
public class FindingHasClusterIdentifier extends Fact
{
    
    private Finding finding;
    
    private ClusterIdentifier clusterId;
    
    public FindingHasClusterIdentifier()
    {
        super();
        type = "toif:FindingHasClusterIdentifier";
    }
    
    public FindingHasClusterIdentifier(Finding finding, ClusterIdentifier clusterId)
    {
        super();
        type = "toif:FindingHasClusterIdentifier";
        
        this.finding = finding;
        this.clusterId = clusterId;
    }
    
    /**
     * @return the finding
     */
    @XmlAttribute(name="finding")
    public String getFindingId()
    {
        return finding.getId();
    }
    
    /**
     * @param finding
     *            the finding to set
     */
    public void setFindingId(Finding finding)
    {
        this.finding = finding;
    }
    
    /**
     * @return the cweId
     */
    @XmlAttribute(name = "cluster")
    public String getClusterId()
    {
        return clusterId.getId();
    }
    
    /**
     * @param cweId
     *            the cweId to set
     */
    public void setClusterId(ClusterIdentifier clusterId)
    {
        this.clusterId = clusterId;
    }

}
