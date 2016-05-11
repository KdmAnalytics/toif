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
package net.ossindex.eclipse.common.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import net.ossindex.eclipse.common.builder.service.ManualBuildState;

/** Register for selection events on projects. This allows us to ensure the
 * OssIndexBuildState is properly set.
 * 
 * @author Ken Duck
 *
 */
public class Setup implements IStartup {

	@Override
	public void earlyStartup()
	{
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				ISelectionService selectionService = win.getSelectionService();

				ISelectionListener listener = new ISelectionListener()
				{

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
							Object obj = s.getFirstElement();
							if(obj instanceof IProject)
							{
								// Get the source provider service
								ISourceProviderService sourceProviderService = (ISourceProviderService) win.getService(ISourceProviderService.class);
								// now get my service
								ManualBuildState stateService = (ManualBuildState) sourceProviderService.getSourceProvider(ManualBuildState.MY_STATE);
								stateService.updateEnabled();
							}
						}
					}
				};

				selectionService.addSelectionListener(listener);

			}
		});
	}
}
