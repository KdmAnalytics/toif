/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////
 * ////////////////////////////////////////////////////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.toolAdaptor;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * creates the commands from the command line arguments.
 * 
 * @author Adam Nunn
 * 
 */
public class AdaptorCommand
{
    
    protected String[] command = null;
    
    protected AdaptorOptions arguments = null;
    
    /**
     * 
     * @param args
     */
    public AdaptorCommand(String[] args)
    {
        setArguments(args);
    }
    
    /**
     * Load the required fields with the argument values.
     * 
     * @param args
     */
    protected void setArguments(String[] args)
    {
        // create the command line interface.
        final Cli<AdaptorOptions> CLI = CliFactory.createCli(AdaptorOptions.class);
        
        // Collect the arguments
        try
        {
            arguments = CLI.parseArguments(args);
        }
        catch (ArgumentValidationException e)
        {
            System.out.println(CLI.getHelpMessage());
            System.exit(1);
        }
        
    }
    
    /**
     * 
     * @param command
     */
    protected void setCommand(String[] command)
    {
        this.command = command;
    }
}
