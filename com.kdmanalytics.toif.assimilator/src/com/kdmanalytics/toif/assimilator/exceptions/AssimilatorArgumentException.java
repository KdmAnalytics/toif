/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.assimilator.exceptions;

/**
 * an exception for when the arguments given to the running process are bad.
 * 
 * @author adam
 * 
 */
public class AssimilatorArgumentException extends Exception
{
    
    private static final long serialVersionUID = 1L;
    
    String message = null;
    
    /**
     * constructor with a message.
     * 
     * @param message
     *            the exception message.
     */
    public AssimilatorArgumentException(String message)
    {
        super(message);
        this.message = message;
    }
    
    /**
     * constructor no arguments. makes an unknown exception.
     */
    public AssimilatorArgumentException()
    {
        this.message = "unknown";
    }
    
    /**
     * return the message for this exception.
     */
    public String getMessage()
    {
        return message;
    }
    
}
