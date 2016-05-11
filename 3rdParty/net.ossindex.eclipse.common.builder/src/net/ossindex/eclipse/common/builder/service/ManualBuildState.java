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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/** Properties that can be used in expressions for "enabledWhen" and "visibleWhen"
 * 
 * @author Ken Duck
 *
 */
public class ManualBuildState extends AbstractSourceProvider implements ISelectionListener
{
	public final static String MY_STATE = "net.ossindex.eclipse.common.builder.buildAutomatically.active";
	public final static String NATURE_SELECTED = "net.ossindex.eclipse.common.builder.registeredNature.selected";
	public final static String ENABLED = "ENABLED";
	public final static String DISABLED = "DISABLED";
	private IProject currentProject;
	private String natureSelectedState = DISABLED;

	public ManualBuildState()
	{
		final ManualBuildState that = this;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(that);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#dispose()
	 */
	@Override
	public void dispose()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<?,?> getCurrentState()
	{
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceDescription desc= workspace.getDescription();
		boolean isAutoBuilding= desc.isAutoBuilding();
		Map<Object,Object> map = new HashMap<Object,Object>(1);
		String value = isAutoBuilding ? ENABLED : DISABLED;
		map.put(MY_STATE, value);
		map.put(NATURE_SELECTED, natureSelectedState);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames()
	{
		return new String[] { MY_STATE, NATURE_SELECTED };
	}

	/**
	 * Update the state of the variables.
	 */
	public void updateEnabled()
	{
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceDescription desc= workspace.getDescription();
		boolean isAutoBuilding= desc.isAutoBuilding();
		String value = isAutoBuilding ? ENABLED : DISABLED;
		fireSourceChanged(ISources.WORKBENCH, MY_STATE, value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if(selection instanceof ITreeSelection)
		{
			ITreeSelection s = (ITreeSelection)selection;
			IProject project = null;
			ICommonBuildService buildService = (ICommonBuildService) PlatformUI.getWorkbench().getService(ICommonBuildService.class);

			for(@SuppressWarnings("unchecked")Iterator<Object> it = s.iterator(); it.hasNext();)
			{
				Object obj = it.next();
				if(obj != null)
				{
					if (obj instanceof IAdaptable)
					{
						IAdaptable adapt=(IAdaptable)obj;
						obj = (IResource)adapt.getAdapter(IResource.class);
					}
				}
				if(obj != null)
				{
					if(obj instanceof IResource)
					{
						currentProject = ((IResource)obj).getProject();
						if(project == null)
						{
							project = currentProject;
						}
						else if(!project.equals(currentProject))
						{
							// If there are selected files from different
							// projects then reject them for now. This
							// complicates the build.
							natureSelectedState = DISABLED;
							fireSourceChanged(ISources.WORKBENCH, NATURE_SELECTED, natureSelectedState);
							return;
						}
					}
				}
			}

			natureSelectedState = buildService.supportsProject(currentProject) ? ENABLED : DISABLED;
			fireSourceChanged(ISources.WORKBENCH, NATURE_SELECTED, natureSelectedState);
		}
	}
}
