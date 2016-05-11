/**
 *	Copyright (c) 2015 Vör Security Inc.
 *	All rights reserved.
 *	
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *	    * Redistributions of source code must retain the above copyright
 *	      notice, this list of conditions and the following disclaimer.
 *	    * Redistributions in binary form must reproduce the above copyright
 *	      notice, this list of conditions and the following disclaimer in the
 *	      documentation and/or other materials provided with the distribution.
 *	    * Neither the name of the <organization> nor the
 *	      names of its contributors may be used to endorse or promote products
 *	      derived from this software without specific prior written permission.
 *	
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ossindex.eclipse.common.builder.service;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/** Implementation of the ICommonBuildService
 * 
 * @author Ken Duck
 *
 */
public class CommonBuildServiceImpl implements ICommonBuildService
{
	private static CommonBuildServiceImpl mInstance;

	/**
	 * Registered natures
	 */
	private Set<String> natures = new HashSet<String>();

	/**
	 * Registered builders
	 */
	private Set<String> builders = new HashSet<String>();

	/**
	 * Text for the menu item and button hover
	 */
	private String menuText = "Manual Build";

	private ImageDescriptor icon;

	private CommonBuildServiceImpl()
	{

	}

	/** Get the singleton instance
	 * 
	 * @return
	 */
	public synchronized static CommonBuildServiceImpl getInstance()
	{
		if(mInstance == null) mInstance = new CommonBuildServiceImpl();
		return mInstance;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#registerNature(java.lang.String)
	 */
	@Override
	public void registerNature(String natureId)
	{
		natures.add(natureId);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#registerBuilder(java.lang.String)
	 */
	@Override
	public void registerBuilder(String builderId)
	{
		builders.add(builderId);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#containsBuilder(java.lang.String)
	 */
	@Override
	public boolean containsBuilder(String builderId)
	{
		return builders.contains(builderId);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#supportsProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public boolean supportsProject(IProject project)
	{
		if(project != null)
		{
			// Check all registered natures against the project's natures
			for(String nature: natures)
			{
				try
				{
					if(project.isOpen() && project.hasNature(nature)) return true;
				}
				catch (CoreException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#brandManualBuild(java.lang.String, org.eclipse.jface.resource.ImageDescriptor)
	 */
	@Override
	public void brandManualBuild(String text, ImageDescriptor icon)
	{
		menuText  = text;
		this.icon = icon;

		refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#getMenuText()
	 */
	@Override
	public String getMenuText()
	{
		return menuText;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.service.ICommonBuildService#getIcon()
	 */
	@Override
	public ImageDescriptor getIcon()
	{
		return icon;
	}
	
	/**
	 * Refresh the UI elements
	 */
	private void refresh()
	{
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
				if (commandService != null)
				{
					commandService.refreshElements("net.ossindex.eclipse.common.builder.commands.OssIndexBuildCommand", null);
				}
			}
		});
	}
}
