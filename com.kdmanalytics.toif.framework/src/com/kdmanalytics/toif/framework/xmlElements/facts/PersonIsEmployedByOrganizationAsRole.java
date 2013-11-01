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

import com.kdmanalytics.toif.framework.xmlElements.entities.Organization;
import com.kdmanalytics.toif.framework.xmlElements.entities.Person;
import com.kdmanalytics.toif.framework.xmlElements.entities.Role;

@XmlType(propOrder = { "role", "organization","person" })
@XmlRootElement(name = "fact")
public class PersonIsEmployedByOrganizationAsRole extends Fact
{
    
    private Person person;
    
    private Organization organization;
    
    private Role role;
    
    public PersonIsEmployedByOrganizationAsRole(Person person, Organization organization, Role role)
    {
        super();
        type = "toif:PersonIsEmployedByOrganizationAsRole";
        this.person = person;
        this.organization = organization;
        this.role = role;
    }
    
    public PersonIsEmployedByOrganizationAsRole()
    {
        super();
        type = "toif:PersonIsEmployedByOrganizationAsRole";
    }
    
    /**
     * @return the person
     */
    @XmlAttribute
    public String getPerson()
    {
        return person.getId();
    }
    
    /**
     * @param person
     *            the person to set
     */
    public void setPerson(Person person)
    {
        this.person = person;
    }
    
    /**
     * @return the organization
     */
    @XmlAttribute
    public String getOrganization()
    {
        return organization.getId();
    }
    
    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(Organization organization)
    {
        this.organization = organization;
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
