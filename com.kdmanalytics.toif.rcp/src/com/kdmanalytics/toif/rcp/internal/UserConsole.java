package com.kdmanalytics.toif.rcp.internal;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.File;

import com.kdmanalytics.toif.rcp.internal.cmd.AdaptorCmd;
import com.kdmanalytics.toif.rcp.internal.cmd.MergeCmd;
import com.kdmanalytics.toif.rcp.internal.cmd.VersionCmd;
import com.lexicalscope.jewel.cli.CliFactory;

public class UserConsole
	{
	String toifArgs[]    = null;
	String AdaptorArgs[] = null;
	
	public UserConsole()
		{	
		}
	
	public UserConsole(String toifArgs[], String adaptorArgs[])
		{
		this.toifArgs    = toifArgs;
		this.AdaptorArgs = adaptorArgs;
		}
	
    public void execute()
    	{
    	// Check out CLI options
    	ToifCli toifCli = null;
    	try
    		{
    	    toifCli = CliFactory.parseArguments(ToifCli.class, toifArgs);
    		}
    	catch( Exception ex)
    		{
    		System.err.println( "Invalid Arguments: " + ex.getMessage());
    		return;
    		}
    	    
    	
    	// Check if supplied options are valid
    	if ( argsValid( toifCli) == false)
    		{
    		System.err.println( "Invalid arguments");
    		return;
    		}
    	
    	// Are we doing Version?
    	if (toifCli.isVersion())     doVersion( toifCli );
    	if (toifCli.isAdaptor())     doAdaptor( toifCli );
    	if (toifCli.isMerge())       doMerge( toifCli);
    		
    	
    	}
    
    private void doVersion( ToifCli toifCli)
    	{
    	VersionCmd cmd = new VersionCmd();
    	cmd.execute( toifCli, this.AdaptorArgs);   	
    	}
    
    private void doAdaptor( ToifCli toifCli)
    	{
    	// Ensure that output directory is specifed
    	if (!toifCli.isOutputdirectory())
    		{
    		System.err.println( "Output directory needs to be specified");
    		return;
    		}
    	
    	// Ensure that specified directory exists
    	if (!toifCli.getOutputdirectory().exists())
    		toifCli.getOutputdirectory().mkdirs();
    		
        AdaptorCmd cmd = new AdaptorCmd();
        cmd.execute(toifCli, this.AdaptorArgs);
    	
    	}
    
    private void doMerge( ToifCli toifCli)
    	{
    	MergeCmd cmd = new MergeCmd();
    	cmd.execute( toifCli, this.AdaptorArgs);
    	}
    
    /***************************************************
     * Just perform some sanity checking on the arguments
     * 
     **************************************************/
    private boolean argsValid( ToifCli cli)
    	{
    	// Check that we are only doing a single command
    	if (cli.isMerge() && cli.isAdaptor())
    		{
    		System.err.println( "Can only do adaptor or merge");
    		return false;
    		}
    	
    	// Check inputfile
    	if (cli.isInputfile())
    		{
    		for (File file: cli.getInputfile())
    			{
    			if (!file.exists())
    				{
    				System.err.println( "Specified inputfile does not exist: " + file.getAbsolutePath());
    				return false;
    				}
   			
    			if (!file.isFile() && !file.isDirectory())
   				{
    				System.err.println( "Specified inputfile not valid: " + file.getAbsolutePath());
				    return false;
    				}

 
    			}

    		}
    	
    	// Check house keeping
    	if (cli.isHousekeeping())
    		{
    		if (!cli.getHousekeeping().isFile())
    			{
    			System.err.println( "Specified housekeeping file not valid: " + cli.getHousekeeping());
    			return false;
    			}
    		}
    	
    	return true;
    	}
	}
