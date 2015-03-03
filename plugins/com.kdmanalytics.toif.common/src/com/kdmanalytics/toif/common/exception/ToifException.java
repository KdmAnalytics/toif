package com.kdmanalytics.toif.common.exception;
/*******************************************************************************
 * Copyright (c) 2014 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

/*
 * This is exception is for general TOIF processing error. Do note that the facade may map other
 * exceptions to this type.
 
 */
public class ToifException extends Exception
	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3697967854289308401L;

	/**
     * Empty constructor.
     */
	public ToifException()
		{
		super();
		}

	public ToifException(final Exception e)
		{
		super(e);
		}

	public ToifException(final String text)
		{
		super(text);
		}
	 
	public ToifException(final String message, final Exception e)
		{
		super(message, e);
		}

	}
