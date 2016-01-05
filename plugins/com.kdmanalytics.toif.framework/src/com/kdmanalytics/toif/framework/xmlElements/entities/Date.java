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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent the date entity
 * 
 * @author Adam Nunn
 *         
 */
@XmlRootElement(name = "fact")
public class Date extends Entity
{
    
    // how the date should be represented.
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    
    // the date
    private String date;
    
    /**
     * create the date using the current time and date.
     */
    public Date()
    {
        super();
        
        // set the xml type to "toif:Date"
        type = "toif:Date";
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        date = sdf.format(cal.getTime());
    }
    
    /**
     * get the date
     * 
     * @return the date
     */
    public String getDate()
    {
        return date;
    }
    
    /**
     * set the date
     * 
     * @param date
     *            the date to set
     */
    @XmlAttribute(name = "date")
    public void setDate(String date)
    {
        this.date = date;
    }
    
}
