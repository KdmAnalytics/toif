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

import com.kdmanalytics.toif.framework.xmlElements.entities.Organization;
import com.kdmanalytics.toif.framework.xmlElements.entities.Role;

@XmlType(propOrder = { "role", "organization2","organization1" })
@XmlRootElement(name="fact")
public class OrganizationIsPartOfOrganizationAsRole extends Fact
{
    
    private Organization organization1;
    
    private Organization organization2;
    
    private Role role;
    
    public OrganizationIsPartOfOrganizationAsRole(Organization organization1, Organization organization2, Role role)
    {
        super();
        type = "toif:OrganizationIsPartOfOrganizationAsRole";
        this.organization1 = organization1;
        this.organization2 = organization2;
        this.role = role;
    }
    
    public OrganizationIsPartOfOrganizationAsRole()
    {
        super();
        type = "toif:OrganizationIsPartOfOrganizationAsRole";
    }
    
    /**
     * @return the organization1
     */
    @XmlAttribute
    public String getOrganization1()
    {
        return organization1.getId();
    }
    
    /**
     * @param organization1
     *            the organization1 to set
     */
    public void setOrganization1(Organization organization1)
    {
        this.organization1 = organization1;
    }
    
    /**
     * @return the organization2
     */
    @XmlAttribute
    public String getOrganization2()
    {
        return organization2.getId();
    }
    
    /**
     * @param organization2
     *            the organization2 to set
     */
    public void setOrganization2(Organization organization2)
    {
        this.organization2 = organization2;
    }
    
    /**
     * @return the role
     */
    @XmlAttribute
    public String getRole()
    {
        return role.getId();
    }
    
    /**
     * @param role
     *            the role to set
     */
    public void setRole(Role role)
    {
        this.role = role;
    }
}
