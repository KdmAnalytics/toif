/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the checksum for the file entities.
 * 
 * @author adam
 * 
 */
@XmlRootElement(name = "checksum")
public class Checksum
{
    
    private String checksum;
    
    /**
     * constructs a new checksum
     * 
     * @param file
     *            The file entity this checksum is for.
     */
    public Checksum(String file)
    {
        super();
        
        this.checksum = checksum(file);
        
        if(checksum == null) {
            checksum = "none";
        }
    }
    
    /**
     * constructs a new checksum
     */
    public Checksum()
    {
        super();
    }
    
    /**
     * get the checksum
     * 
     * @return the checksum
     */
    public String getChecksum()
    {
        return checksum;
    }
    
    @XmlAttribute(name = "checksum")
    public void setChecksum(String file)
    {
    }
    
    /**
     * representation of MD5 digest
     * 
     * @param file
     *            path for the file to do the sum on.
     * @return The string representing the MD5 sum.
     */
    public String checksum(String file)
    {
        try
        {
            // The input stream for the file.
            InputStream inputStream = new FileInputStream(file);
            
            // get the MD5 implementation of the MessageDigest.
            java.security.MessageDigest md5er = MessageDigest.getInstance("MD5");
            
            byte[] buff = new byte[1024];
            int readBytes;
            
            // read into the buffer and update the digest.
            do
            {
                readBytes = inputStream.read(buff);
                if (readBytes > 0)
                    md5er.update(buff, 0, readBytes);
            }
            
            while (readBytes != -1);
            inputStream.close();
            
            // complete the digest.
            byte[] digest = md5er.digest();
            
            if (digest == null)
                return null;
            
            String strDigest = "0x";
            
            // compile the md5 string.
            for (int i = 0; i < digest.length; i++)
            {
                strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1).toUpperCase();
            }
            return strDigest;
        }
        
        catch (Exception e)
        {
            return null;
        }
    }
}
