/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.listeners;

import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.util.MemberUtil;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * Listens for double clicks on the report. this will take you to the source
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ReportDoubleClickListener implements IDoubleClickListener
{
    
    public ReportDoubleClickListener()
    {
    }
    
    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        
        StructuredSelection selection = (StructuredSelection) event.getSelection();
        Object element = selection.getFirstElement();
        
        if (element instanceof IToifReportEntry)
        {
            
            IToifReportEntry iToifReportEntry = (IToifReportEntry) element;
            IProject activeProject = iToifReportEntry.getProject().getIProject();
            
            try
            {
                IToifReportEntry item = (IToifReportEntry) element;
                FindingEntry entry = item.getFindingEntry();
                
                LocationGroup locationGroup = (LocationGroup) entry.getParent().getParent();
                
                IFileGroup file = (IFileGroup) entry.getParent().getParent().getParent();
                IResource member = MemberUtil.findMembers(activeProject, file);
                
                System.err.println("Member type " + member);
                if (member == null)
                {
                    MessageDialog.openWarning(null, "No Source File Found!", "A source file could not be found for this location.");
                    return;
                }
                
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                HashMap<String, Object> map = new HashMap<String, Object>();
                
                if (element instanceof IFileGroup)
                {
                    map.put(IMarker.LINE_NUMBER, 1);
                }
                else
                {
                    map.put(IMarker.LINE_NUMBER, Integer.parseInt(locationGroup.getLineNumber()));
                }

                IMarker marker = member.createMarker(IMarker.TEXT);
                marker.setAttributes(map);
                IDE.openEditor(page, marker);
                marker.delete();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            
        }
        
    }
    
}
