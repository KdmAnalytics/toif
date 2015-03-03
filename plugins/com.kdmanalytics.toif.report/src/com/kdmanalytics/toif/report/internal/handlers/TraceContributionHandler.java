/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.kdmanalytics.toif.report.internal.items.FileGroup;
import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.util.MemberUtil;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * handles the displaying of the source from the trace contributions.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class TraceContributionHandler extends AbstractHandler
{
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
     * .ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final IStructuredSelection s = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        
        final String lineNumber = event.getParameter("com.kdmanalytics.toif.report.lineNumber");
        
        if (s.isEmpty())
        {
            return null;
        }
        
        Object element = s.getFirstElement();
        
        if (element instanceof IToifReportEntry)
        {
            IToifReportEntry iToifReportEntry = (IToifReportEntry) element;
            
            FindingEntry fEntry = iToifReportEntry.getFindingEntry();
            
            IProject activeProject = iToifReportEntry.getProject().getIProject();
            
            try
            {
                FileGroup file = (FileGroup) fEntry.getParent().getParent().getParent();
                IResource member = MemberUtil.findMembers(activeProject, file);
                
                if (member == null)
                {
                    MessageDialog.openWarning(null, "No Source File Found!", "A source file could not be found for this location.");
                    return null;
                }
                
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                HashMap<String, Object> map = new HashMap<String, Object>();
                
                map.put(IMarker.LINE_NUMBER, Integer.parseInt(lineNumber));

                IMarker marker = member.createMarker(IMarker.TEXT);
                marker.setAttributes(map);
                IDE.openEditor(page, marker);
                marker.delete();
            }
            catch (CoreException exception)
            {
                exception.printStackTrace();
            }
        }
        return null;
    }
    
}
