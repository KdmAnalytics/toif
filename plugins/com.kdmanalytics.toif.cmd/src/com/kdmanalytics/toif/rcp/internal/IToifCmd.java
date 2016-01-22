
package com.kdmanalytics.toif.rcp.internal;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Open Source Initiative OSI - Open Software
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
public interface IToifCmd {
  
  void execute(ToifCli toifCli, String userArgs[]);
}
