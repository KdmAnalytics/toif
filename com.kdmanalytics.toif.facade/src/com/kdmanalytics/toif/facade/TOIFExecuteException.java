package com.kdmanalytics.toif.facade;


public class TOIFExecuteException extends Exception
{  

/**
	 * 
	 */
	private static final long serialVersionUID = 2251583928117753529L;

public TOIFExecuteException()
	{
	super();
	}

public TOIFExecuteException(final Exception e)
	{
	super(e);
	}

public TOIFExecuteException(final String text)
	{
	super(text);
	}
    
}
