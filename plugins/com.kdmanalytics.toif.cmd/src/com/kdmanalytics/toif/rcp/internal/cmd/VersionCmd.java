
package com.kdmanalytics.toif.rcp.internal.cmd;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptorUtil;
import com.kdmanalytics.toif.rcp.internal.IToifCmd;
import com.kdmanalytics.toif.rcp.internal.ToifCli;
import com.kdmanalytics.toif.rcp.internal.util.BuildInformation;
import com.kdmanalytics.toif.rcp.internal.util.CommonUtil;

public class VersionCmd implements IToifCmd {
  
  @Override
  public void execute(ToifCli toifCli, String userArgs[]) {
    BuildInformation bi = new BuildInformation(this);
    System.out.println("Version=" + bi.getVersion());
    System.out.println("");
    
    if (toifCli.isVerbose()) {
      // List detected plugins
      for (AbstractAdaptor adaptor : ToolAdaptorUtil.getAdaptors()) {
        StringBuilder sb = new StringBuilder();
        sb.append("Adaptor Name=");
        String name = CommonUtil.padRight(adaptor.getAdaptorName(), 15);
        sb.append(name);
        
        sb.append(" tool=");
        String tool = CommonUtil.padRight(adaptor.getRuntoolName(), 15);
        sb.append(tool);
        
        sb.append(" version=");
        bi = new BuildInformation(this);
        sb.append(bi.getVersion());
        
        System.out.println(sb);
      }
    }
    
  }
  
}
