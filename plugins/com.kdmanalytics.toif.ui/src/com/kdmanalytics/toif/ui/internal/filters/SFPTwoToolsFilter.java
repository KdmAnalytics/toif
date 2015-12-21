/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * Filter that shows only findings with the same location and the same sfp
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * @author Ken Duck
 * 
 */
public class SFPTwoToolsFilter extends AbstractTwoToolsFilter
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
		if (element instanceof FindingEntry)
		{
			return doesLocationContainTwoSameSFP((FindingEntry)element);
		}
		return false;
	}

	/**
	 * Returns true if the given location group contains the same two SFP's
	 * 
	 * @param location
	 *            the location group
	 * @param toolNameToExclude
	 *            the tool name to exclude from the check
	 * 
	 * @return true if there is two SFP's at the same location
	 */
	private boolean doesLocationContainTwoSameSFP(FindingEntry targetEntry)
	{
		IFile file = targetEntry.getFile();
		int targetLine = targetEntry.getLineNumber();
		String toolNameToExclude = targetEntry.getTool();
		String sfpToExclude = targetEntry.getSfp();

		// List of all tools represented at this file location
		Set<String> tools = new HashSet<String>();

		// Map of tool name to SFPs
		HashMap<String, List<String>> sfpMap = new HashMap<String, List<String>>();

		try
		{
			List<FindingEntry> findings = getFindings(file);

			for (FindingEntry entry : findings)
			{
				int line = entry.getLineNumber();
				if(line == targetLine)
				{
					String tool = entry.getTool();
					tools.add(tool);

					if(!sfpMap.containsKey(tool)) sfpMap.put(tool, new LinkedList<String>());
					List<String> list = sfpMap.get(tool);
					list.add(entry.getSfp());
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


		// for each of the tools in the map, we are going to compare its cwes to
		// the other cwe that the other tools contain. We'll do this by removing
		// a tool from a copy of the map, and comparing the results to all the
		// other tools in the map.
		for (String toolName : sfpMap.keySet())
		{

			// get the new copy of the map
			HashMap<String, List<String>> copyOfMap = new HashMap<String, List<String>>(sfpMap);

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

				if ((sfpToExclude != null))
				{
					// the list of sfps from the original tool.
					retained = new ArrayList<String>();
					retained.add(sfpToExclude);
				}
				else
				{
					// the list of sfps from the original tool.
					retained = new ArrayList<String>(cweList);
				}

				// retain only common sfps
				retained.retainAll(sfpMap.get(toolNameCopy));

				// if the retained list is not empty then there are common sfps
				// between tools, return true.
				if (!retained.isEmpty())
				{
					return true;
				}
			}
		}

		return false;

	}
}
