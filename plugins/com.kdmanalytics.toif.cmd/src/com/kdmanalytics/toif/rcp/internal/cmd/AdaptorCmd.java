
package com.kdmanalytics.toif.rcp.internal.cmd;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.facade.ToifFacade;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptorUtil;
import com.kdmanalytics.toif.rcp.internal.IToifCmd;
import com.kdmanalytics.toif.rcp.internal.ToifCli;

public class AdaptorCmd implements IToifCmd {
  
  private static Logger LOG = LoggerFactory.getLogger(AdaptorCmd.class);
  
  @Override
  public void execute(ToifCli toifCli, String userArgs[]) {
    // Expand each adaptor as required
    for (String toolName : toifCli.getAdaptor()) {
      LOG.debug("tool name={}",toolName);
      
      for (AbstractAdaptor adaptor : ToolAdaptorUtil.getAdaptors()) {
        if (toolName.equalsIgnoreCase(adaptor.getRuntoolName())) {
          ToifFacade toif = new ToifFacade();
          
          try {
            for (File inputFile : toifCli.getInputfile()) {
              toif.execute(adaptor, inputFile, toifCli.getHousekeeping(), toifCli.getOutputdirectory(), new File("."),
                           userArgs, null);
            }
          } catch (IllegalArgumentException | ToifException e) {
            System.err.println("Illegal Argument:" + e.getMessage());
            return;
          }
        }
      }
    }
  }
  
}
