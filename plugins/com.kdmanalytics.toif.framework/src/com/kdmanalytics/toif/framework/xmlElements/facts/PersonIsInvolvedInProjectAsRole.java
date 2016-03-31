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

import com.kdmanalytics.toif.framework.xmlElements.entities.Person;
import com.kdmanalytics.toif.framework.xmlElements.entities.Project;
import com.kdmanalytics.toif.framework.xmlElements.entities.Role;

@XmlRootElement(name = "fact")
public class PersonIsInvolvedInProjectAsRole extends Fact
{
    
    private Person person;
    
    private Project project;
    
    private Role role;
    
    public PersonIsInvolvedInProjectAsRole(Person person, Project project, Role role)
    {
        super();
        type = "toif:PersonIsInvolvedInProjectAsRole";
        this.person = person;
        this.project = project;
        this.role = role;
    }
    
    public PersonIsInvolvedInProjectAsRole()
    {
        super();
        type = "toif:PersonIsInvolvedInProjectAsRole";
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
