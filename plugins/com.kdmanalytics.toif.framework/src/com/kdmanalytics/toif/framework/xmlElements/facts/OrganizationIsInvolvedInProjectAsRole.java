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

package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.kdmanalytics.toif.framework.xmlElements.entities.Organization;
import com.kdmanalytics.toif.framework.xmlElements.entities.Project;
import com.kdmanalytics.toif.framework.xmlElements.entities.Role;

@XmlType(propOrder = { "role", "project", "organization" })
@XmlRootElement(name = "fact")
public class OrganizationIsInvolvedInProjectAsRole extends Fact
{
    
    private Organization organization;
    
    private Project project;
    
    private Role role;
    
    public OrganizationIsInvolvedInProjectAsRole(Organization organization, Project project, Role role)
    {
        super();
        type = "toif:OrganizationIsInvolvedInProjectAsRole";
        this.organization = organization;
        this.project = project;
        this.role = role;
    }
    
    public OrganizationIsInvolvedInProjectAsRole()
    {
        super();
        type = "toif:OrganizationIsInvolvedInProjectAsRole";
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
     * @return the project
     */
    @XmlAttribute
    public String getProject()
    {
        return project.getId();
    }
    
    /**
     * @param project
     *            the project to set
     */
    public void setProject(Project project)
    {
        this.project = project;
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
