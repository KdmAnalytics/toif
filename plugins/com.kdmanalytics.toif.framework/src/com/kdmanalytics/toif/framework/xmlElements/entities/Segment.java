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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * represents the segment entity in the toif output.
 * 
 * @author adam
 *         
 */
@XmlRootElement(name = "toif:TOIFSegment")
public class Segment extends Element
{
    
    // the name of the segment
    @XmlElementRef
    private Name name;
    
    // the description of the segment
    private Description description;
    
    /*
     * doctype stuff.
     */
    @XmlAttribute(name = "xmlns:toif")
    private String toif = "http://kdmanalytics.com/toif.xsd";
    
    @XmlAttribute(name = "xmlns:xsi")
    private String xsi = "http://www.w3.org/2001/XMLSchema-instance";
    
    @XmlAttribute(name = "xmlns:xmi")
    private String xmi = "http://www.omg.org/XMI";
    
    @XmlAttribute(name = "xsi:schemaLocation")
    private String schemaLocation = "http://kdmanalytics.com/TOIFSchema.xsd TOIFSchema.xsd";
    
    // the list of element in the segment.
    private ArrayList<Element> elements = new ArrayList<Element>();
    
    /**
     * construct a new segment.
     */
    public Segment()
    {
        super();
        type = "toif:TOIFSegment";
    }
    
    /**
     * get the name of the segment.
     * 
     * @return the name of the segment.
     */
    public Name getName()
    {
        return name;
    }
    
    /**
     * set the name of the segment.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = new Name(name);
    }
    
    /**
     * get the description of the segment.
     * 
     * @return the description
     */
    @XmlElementRef(name = "description")
    public Description getDescription()
    {
        return description;
    }
    
    /**
     * set the description of the segment
     * 
     * @param description
     *            the description to set
     */
    @XmlAttribute(name = "description")
    public void setDescription(String description)
    {
        this.description = new Description(description);
    }
    
    /**
     * @return the elements
     */
    @XmlElementRef()
    public ArrayList<Element> getElements()
    {
        return elements;
    }
    
    public void clearSegment()
    {
        elements.clear();
    }
    
    /**
     * @param elements
     *            the elements to set
     */
    public void setElements(ArrayList<Element> elements)
    {
        this.elements = elements;
    }
    
    /**
     * 
     * @param finding
     */
    public void addElement(Element element)
    {
        elements.add(element);
        
    }
    
    public void setId(int id)
    {
        this.name = new Name(id + "");
    }
    
    public String getId()
    {
        return null;
    }
    
}
