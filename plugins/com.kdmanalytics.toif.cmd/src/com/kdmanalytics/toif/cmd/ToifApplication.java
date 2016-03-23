
package com.kdmanalytics.toif.cmd;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.kdmanalytics.toif.rcp.internal.UserConsole;

/**
 * This class controls all aspects of the application's execution
 */
public class ToifApplication implements IApplication {
  private static final Logger LOG = LoggerFactory.getLogger(ToifApplication.class);
 
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app. IApplicationContext)
   */
  public Object start(IApplicationContext context) throws Exception {
    boolean markerFound = false;
    List<String> toifArgList = Lists.newArrayList();
    List<String> adaptorArgList = Lists.newArrayList();
    
    // Strip some arguments passed by eclipse that we are
    // not interested it and build argument list
    String[] appArgs = Platform.getApplicationArgs();
    
    for (String s : appArgs) {
      if (s.equals("-product")) continue;
      
      if (markerFound) adaptorArgList.add(s);
      else {
        if (s.equals("--")) {
          markerFound = true;
          continue;
        } else toifArgList.add(s);
      }
    }
    
    String[] toifArgs = toifArgList.toArray(new String[toifArgList.size()]);
    String[] adaptorArgs = adaptorArgList.toArray(new String[adaptorArgList.size()]);
    
    UserConsole uc = new UserConsole(toifArgs, adaptorArgs);
    uc.execute();
    
    return IApplication.EXIT_OK;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  public void stop() {
    // nothing to do
  }
}
