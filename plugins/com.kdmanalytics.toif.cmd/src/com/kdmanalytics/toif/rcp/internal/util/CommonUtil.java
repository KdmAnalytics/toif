package com.kdmanalytics.toif.rcp.internal.util;


/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public final class CommonUtil
	{
    //-----------------------------------------------------------------------------------
	// Extract version information of jar Manifest
	//-----------------------------------------------------------------------------------  
	public static  final String getVersion( Object object)
		{			
		try
			{			
			URL manifestUrl = object.getClass().getProtectionDomain().getCodeSource().getLocation();

			JarFile jar = new JarFile(manifestUrl.getFile());
			Manifest mf = jar.getManifest();
			String version =  mf.getMainAttributes().getValue("Bundle-Version");		
	        jar.close();
			
            return version;
			} 
		catch (MalformedURLException e)
			{
		    // Just skip
			} 
		catch (IOException e)
			{
		    // Just skip
			}
		
		return null;
		}
	
  
  public static String padRight(String s, int n) {
  return String.format("%1$-" + n + "s", s);  
  }

public static String padLeft(String s, int n) {
 return String.format("%1$" + n + "s", s);  
}

  
 }


	
