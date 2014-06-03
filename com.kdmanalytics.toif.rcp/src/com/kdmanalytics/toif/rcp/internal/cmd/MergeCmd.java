package com.kdmanalytics.toif.rcp.internal.cmd;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.facade.ToifFacade;
import com.kdmanalytics.toif.rcp.internal.IToifCmd;
import com.kdmanalytics.toif.rcp.internal.ToifCli;

public class MergeCmd implements IToifCmd
	{

	@Override
	public void execute(ToifCli toifCli, String userArgs[])
		{
		
		ToifFacade toif = new ToifFacade();
		try
			{
			toif.merge( toifCli.getKdmfile(), toifCli.getInputfile(), true);
			}
		catch (ToifException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		}

	}
