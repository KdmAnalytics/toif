/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.handlers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.ui.views.FindingView;

/** Set a filter in the TOIF View based on the current selection
 * 
 * @author Ken Duck
 *
 */
public class FilterToifHandler extends AbstractHandler implements IHandler
{
	
	public FilterToifHandler()
	{
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				ISelectionService service = window.getSelectionService();
				ISelection selection = service.getSelection();
				
				if(selection instanceof IStructuredSelection)
				{
					IStructuredSelection sel = (IStructuredSelection)selection;
					List<IResource> resources = new LinkedList<IResource>();
					for(Iterator<?> it = sel.iterator(); it.hasNext();)
					{
						Object o = it.next();
						if(o instanceof IAdaptable)
						{
							o = ((IAdaptable) o).getAdapter(IResource.class);
						}
						if(o instanceof IResource)
						{
							resources.add((IResource) o);
						}
					}
					
					IWorkbenchPage page = window.getActivePage();
					try
					{
						FindingView view = (FindingView)page.showView("com.kdmanalytics.toif.views.FindingView");
						view.setResourceFilter(resources);
						view.refresh();
					}
					catch (PartInitException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		return null;
	}

}
