/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * represents the person entity.
 * 
 * @author adam
 * 
 */
@XmlType(propOrder = { "name", "phone", "email" })
@XmlRootElement(name = "fact")
public class Person extends Entity
{
    
    // the person's name
    private Name name;
    
    // the person's phone number
    private Phone phone;
    
    // the person's email address.
    private EmailAddress email;
    
    /**
     * the required empty constructor.
     */
    public Person()
    {
        super();
        type = "toif:Person";
    }
    
    /**
     * construct a new person entity.
     * 
     * @param name
     *            the person's name
     * @param email
     *            the person's email address.
     * @param phone
     *            the person's phone number
     */
    public Person(String name, String email, String phone)
    {
        super();
        type = "toif:Person";
        
        this.name = new Name(name);
        this.email = new EmailAddress(email);
        this.phone = new Phone(phone);
        
    }
    
    /**
     * get the person's name
     * 
     * @return the name
     */
    @XmlElementRef(name = "name")
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the person's name
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the persons phone number
     * 
     * @return the phone
     */
    @XmlElementRef(name = "phone")
    public Phone getPhone()
    {
        return phone;
    }
    
    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone)
    {
        this.phone = new Phone(phone);
    }
    
    /**
     * @return the email
     */
    @XmlElementRef(name = "email")
    public EmailAddress getEmail()
    {
        return email;
    }
    
    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = new EmailAddress(email);
    }
    
    /**
     * override the equals method so that we can ensure the toif output only has
     * one equivalent person.
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Person)
        {
            Person person = (Person) obj;
            
            return name.getName().equals(person.getName().getName()) && phone.getPhone().equals(person.getPhone().getPhone())
                    && email.getEmail().equals(person.getEmail().getEmail());
        }
        return false;
        
    }
    
    /**
     * override the hashcode
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ phone.getPhone().hashCode() ^ email.getEmail().hashCode();
    }
}
