/*******************************************************************************
 * Copyright (c) 2018 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * ViewerFilter that determines if there are ToifReportEntry's with two
 * SFP/CWE's combination at the same location
 * 
 * @author Robert <robert@kdmanalytics.com>
 * 
 * 
 */
public class SFPCWETwoToolsFilter extends AbstractTwoToolsFilter
	{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
		{
		if (element instanceof IFindingEntry)
			{
			IFindingEntry entry = (IFindingEntry) element;
			return doesLocationContainTwoSameSFPCWE(entry);
			}
		return false;
		}

	/**
	 * Returns true if the given location has at least two CWE's
	 * 
	 * @param targetEntry
	 * @return
	 */
	private boolean doesLocationContainTwoSameSFPCWE(IFindingEntry targetEntry)
		{
		IFile file = targetEntry.getFile();
		int targetLine = targetEntry.getLineNumber();
		String toolNameToExclude = targetEntry.getTool();
		String targetPattern =  targetEntry.getCwe();

		// List of all tools represented at this file location
		Set<String> tools = new HashSet<String>();

		// Map of tool name to SFP/CWEs
		HashMap<String, List<String>> sfpcweMap = new HashMap<String, List<String>>();

		try
			{
			List<IFindingEntry> findings = getFindings(file);

			for (IFindingEntry entry : findings)
				{
				int line = entry.getLineNumber();
				if (line == targetLine)
					{
					String tool = entry.getTool();
					tools.add(tool);

					if (!sfpcweMap.containsKey(tool))
						sfpcweMap.put(tool, new LinkedList<String>());
					
					List<String> list = sfpcweMap.get(tool);
					list.add(entry.getSfp() + entry.getCwe());
					}
				}
			}
		catch (CoreException e)
			{
			e.printStackTrace();
			}

		final int size = tools.size();

		// if the location group does not contain at least two tools, return
		// false.
		if (!(size >= 2))
			{
			return false;
			}
		
		Map<String,Set<String>> hack = new HashMap<String,Set<String>>();
		for (String toolName : sfpcweMap.keySet())
			{
			for( String pattern: sfpcweMap.get(toolName))
				{
				Set<String> set = hack.getOrDefault(pattern, new HashSet<String>());
				set.add( toolName);
				if (set.size() > 1 && pattern.matches(targetPattern))
					return true;
				
				hack.put( pattern, set);	
				}
			}
		
		if (targetPattern.matches("CWE000"))
			{
			for ( Set<String> set : hack.values())
				{
				if (set.size() > 1)
					return true;
				}
			}
		else
			{
			Set<String> set = hack.get( targetEntry.getSfp() + targetPattern);
			if (set != null && set.size() > 1)
				return true;
			}

		return false;
		
		// for each of the tools in the map, we are going to compare its cwes to
		// the other cwe that the other tools contain. We'll do this by removing
		// a tool from a copy of the map, and comparing the results to all the
		// other tools in the map.
	/*
		for (String toolName : sfpcweMap.keySet())
			{

			// get the new copy of the map
			HashMap<String, List<String>> copyOfMap = new HashMap<String, List<String>>(
					sfpcweMap);

			List<String> cweList = null;

			if (toolNameToExclude != null)
				{
				// get the sfps for this tool.
				cweList = copyOfMap.remove(toolNameToExclude);
				}
			else
				{

				// get the sfps for this tool.
				cweList = copyOfMap.remove(toolName);
				}
			// for each of the remaining tools, compare these sfps to the tool
			// that we removed. if there is a match, then anything at this
			// location or above isallowed to stay.
			for (String toolNameCopy : copyOfMap.keySet())
				{
				List<String> retained = null;

				if ((cweToExclude != null))
					{
					// the list of sfps from the original tool.
					retained = new ArrayList<String>();
					retained.add(cweToExclude);
					}
				else
					{
					// the list of sfps from the original tool.
					retained = new ArrayList<String>(cweList);
					}

				// retain only common sfps
				retained.retainAll(sfpcweMap.get(toolNameCopy));

				// if the retained list is not empty then there are common sfps/cwe
				// between tools, return true.
				if (!retained.isEmpty())
					{
					return true;
					}
				}
			}

		return false;
*/
		}
	}
