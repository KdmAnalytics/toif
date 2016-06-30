
package com.kdmanalytics.toif.convert;

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
import com.kdmanalytics.toif.convert.internal.UserConsole;

/**
 * This class controls all aspects of the application's execution
 */
public class ConvertApplication implements IApplication {
  private static final Logger LOG = LoggerFactory.getLogger(ConvertApplication.class);
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app. IApplicationContext)
   */
  public Object start(IApplicationContext context) throws Exception {
    List<String> argList = Lists.newArrayList();
    
    // Strip some arguments passed by eclipse that we are
    // not interested it and build argument list
    String[] appArgs = Platform.getApplicationArgs();
    
    for (String s : appArgs) {
      if (s.equals("-product")) continue;
      argList.add(s);
    }
    
    String[] args = argList.toArray(new String[argList.size()]);
    
    if (LOG.isDebugEnabled()){
      LOG.debug("Starting headless TOIF..." );
    }
    UserConsole uc = new UserConsole(args);
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
