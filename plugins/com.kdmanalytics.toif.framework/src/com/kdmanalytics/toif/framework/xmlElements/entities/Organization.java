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
import javax.xml.bind.annotation.XmlType;

/**
 * represents the organization entity.
 * 
 * @author adam
 *         
 */
@XmlType(propOrder = { "name", "description", "address", "phone", "email" })
@XmlRootElement(name = "fact")
public class Organization extends Entity
{
    
    // the name of the organization
    @XmlElementRef(name = "name")
    private Name name = null;
    
    // the description of the organization
    @XmlElementRef(name = "description")
    private Description description = null;
    
    // the address of the organization
    @XmlElementRef(name = "address")
    private Address address = null;
    
    // the phone number of the organization
    @XmlElementRef(name = "phone")
    private Phone phone = null;
    
    // the email address of the organization.
    @XmlElementRef(name = "email")
    private EmailAddress email = null;
    
    /**
     * construct an new organization entity.
     * 
     * @param name
     *            the name of the organization
     * @param description
     *            the description of the organization
     * @param address
     *            the address of the organization
     * @param phone
     *            the phone number of the of the organization
     * @param email
     *            the email address of the organization
     */
    public Organization(String name, String description, String address, String phone, String email)
    {
        super();
        type = "toif:Organization";
        
        if (name != null)
            this.name = new Name(name);
        if (description != null)
            this.description = new Description(description);
        if (address != null)
            this.address = new Address(address);
        if (phone != null)
            this.phone = new Phone(phone);
        if (email != null)
            this.email = new EmailAddress(email);
    }
    
    /**
     * required empty constructor
     */
    public Organization()
    {
        super();
        type = "toif:Organization";
    }
    
    /**
     * get the name of the organization
     * 
     * @return the name of the organization
     */
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the organization.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * the description of the organization
     * 
     * @return the description
     */
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * set the description of the organization.
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * get the address of the organization.
     * 
     * @return the address
     */
    public Address getAddress()
    {
        return address;
    }
    
    /**
     * set the address of the organization.
     * 
     * @param address
     *            the address to set
     */
    public void setAddress(String address)
    {
        this.address = new Address(address);
    }
    
    /**
     * get the organization's phone number
     * 
     * @return the phone
     */
    public Phone getPhone()
    {
        return phone;
    }
    
    /**
     * set the organization's phone number
     * 
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone)
    {
        this.phone = new Phone(phone);
    }
    
    /**
     * the organizations email address.
     * 
     * @return the email
     */
    public EmailAddress getEmail()
    {
        return email;
    }
    
    /**
     * set the organizations email address.
     * 
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = new EmailAddress(email);
    }
    
    /**
     * override the equals method. this makes sure that there are not duplicate
     * equivalent organizations in the toif output
     */
    @Override
    public boolean equals(Object obj)
    {
        
        if (obj instanceof Organization)
        {
            Organization org = (Organization) obj;
            
            return name.getName().equals(org.getName().getName()) && description.getText().equals(org.getDescription().getText())
                    && address.getAddress().equals(org.getAddress().getAddress()) && email.getEmail().equals(org.getEmail().getEmail())
                    && phone.getPhone().equals(org.getPhone().getPhone());
        }
        return false;
        
    }
    
    /**
     * override the hash
     */
    @Override
    public int hashCode()
    {
        return name.getName().hashCode() ^ description.getText().hashCode() ^ address.getAddress().hashCode() ^ email.getEmail().hashCode()
                ^ phone.getPhone().hashCode();
    }
    
}
