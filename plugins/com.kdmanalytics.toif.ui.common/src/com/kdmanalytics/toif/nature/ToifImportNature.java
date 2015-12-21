/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.nature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.kdmanalytics.toif.ui.common.ToifUtilities;

public class ToifImportNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "com.kdmanalytics.toif.ui.common.ToifImportNature";
	
	/**
	 * Project this is a nature for
	 */
	private IProject project;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException
	{
		// Create the TOIF work directory
		IFolder kdm = project.getFolder(".KDM");
		if(!kdm.exists())
		{
			kdm.create(true, true, null);
			kdm.setDerived(true, null);
			kdm.setHidden(true);
		}
		IFolder folder = kdm.getFolder("TOIF");
		try
		{
			if(!folder.exists())
			{
				folder.create(true, true, null);
			}
			folder.setDerived(true, null);
			
			IFile housekeeping = folder.getFile("housekeeping");
			if(!housekeeping.exists())
			{
				InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
				housekeeping.create(stream, true, null);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException
	{
		// Clear markers
		ToifUtilities.clearEToifMarkers(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
