/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/** Common utility functions for working with EToif projects
 * 
 * @author Ken Duck
 *
 */
public class ToifUtilities
{
	public static void clearEToifMarkers(IProject project) throws CoreException
	{
		// Clear markers
		project.deleteMarkers("com.kdmanalytics.toif.markers.CppcheckMarker", true, IResource.DEPTH_INFINITE);
		project.deleteMarkers("com.kdmanalytics.toif.markers.FindbugsMarker", true, IResource.DEPTH_INFINITE);
		project.deleteMarkers("com.kdmanalytics.toif.markers.JlintMarker", true, IResource.DEPTH_INFINITE);
		project.deleteMarkers("com.kdmanalytics.toif.markers.RatsMarker", true, IResource.DEPTH_INFINITE);
		project.deleteMarkers("com.kdmanalytics.toif.markers.SplintMarker", true, IResource.DEPTH_INFINITE);
	}
}
