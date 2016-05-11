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
package net.ossindex.eclipse.common.impl;

import java.util.LinkedList;
import java.util.List;

import net.ossindex.eclipse.common.ICUtils;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IIncludeEntry;
import org.eclipse.cdt.core.model.IMacroEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * @author Ken Duck
 *
 */
public class CUtils extends CommonUtils implements ICUtils
{
	/** Return true if this is a C/C++ compilation unit
	 * 
	 * @param resource
	 * @return
	 */
	public boolean isCppCompilationUnit(IResource resource)
	{
		if(resource instanceof IFile)
		{
			IProject project = resource.getProject();
			String name = resource.getName();
			return CoreModel.isValidCXXSourceUnitName(project, name) ||
					CoreModel.isValidCSourceUnitName(project, name);
		}
		return false;
	}
	

	/**
	 * Get the settings for the provided resource
	 */
	public String[] getIncludePaths(IResource resource)
	{
		IPath path = resource.getLocation();
		List<String> results = new LinkedList<String>();
		
		try
		{
			IIncludeEntry[] includes = CoreModel.getIncludeEntries(path);
			if(includes != null)
			{
				for (IIncludeEntry include : includes)
				{
					results.add(include.getFullIncludePath().toString());
				}
			}
		}
		catch (CModelException e)
		{
			e.printStackTrace();
		}
		
		return results.toArray(new String[results.size()]);
	}

	/**
	 * Get the settings for the provided resource
	 */
	public String[] getMacros(IResource resource)
	{
		IPath path = resource.getLocation();
		List<String> results = new LinkedList<String>();
		
		try
		{
			IMacroEntry[] macros = CoreModel.getMacroEntries(path);
			if(macros != null)
			{
				for (IMacroEntry macro : macros)
				{
					String name = macro.getMacroName();
					String value = macro.getMacroValue();
					if(value != null && !value.isEmpty())
					{
						results.add(name + "=" + value);
					}
					else
					{
						results.add(name);
					}
				}
			}
		}
		catch (CModelException e)
		{
			e.printStackTrace();
		}
		return results.toArray(new String[results.size()]);
	}

	/** Get the project level include settings.
	 * 
	 * This code is not currently used and does not actually do anything besides
	 * print some data to stderr.
	 * 
	 * It also contains some roughed in code for setting project wide includes.
	 * 
	 * @param project
	 */
	protected void getProjectSettings(IProject project)
	{
		System.err.println("SETTINGS FOR " + project);
		ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(project, true);
		ICConfigurationDescription configDecriptions[] = projectDescription.getConfigurations();
		
		for (ICConfigurationDescription configDescription : configDecriptions)
		{
			ICFolderDescription projectRoot = configDescription.getRootFolderDescription();
			ICLanguageSetting[] settings = projectRoot.getLanguageSettings();
			for (ICLanguageSetting setting : settings)
			{
				List<ICLanguageSettingEntry> includes = setting.getSettingEntriesList(ICSettingEntry.INCLUDE_PATH);
				for (ICLanguageSettingEntry include : includes)
				{
					System.err.println("  -I " + include);
				}
				
				List<ICLanguageSettingEntry> macros = setting.getSettingEntriesList(ICSettingEntry.MACRO);
				for (ICLanguageSettingEntry macro : macros)
				{
					System.err.println("  -D " + macro);
				}
				
//				includes.addAll(setting.getSettingEntriesList(ICSettingEntry.INCLUDE_PATH));
//				includes.add(new CIncludePathEntry("/my/local/include/path", ICSettingEntry.LOCAL));
//				setting.setSettingEntries(ICSettingEntry.INCLUDE_PATH, includes);
			}
		}
//		CoreModel.getDefault().setProjectDescription(project, projectDescription);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.ICUtils#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.ICUtils#getResource(java.lang.Object)
	 */
	@Override
	public Object getResource(Object object) {
    	// If this is a C element then convert it to an IResource
    	if(object instanceof ICElement)
    	{
    		object = ((ICElement)object).getResource();
    	}
		return object;
	}
}
