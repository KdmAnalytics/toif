/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kdmanalytics.toif.ccr.CoverageClaimGenerator;
import com.kdmanalytics.toif.report.internal.views.ReportView;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * executes the coverage claims generator.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class ccrHandler extends AbstractHandler implements IHandler
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
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        final IWorkbenchPage page = window.getActivePage();
        final ReportView view = (ReportView) page.findView(ReportView.VIEW_ID);
        // Display display = new Display();
        Shell shell = HandlerUtil.getActiveShell(event);
        shell.open();
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterNames(new String[] { "XML Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
        dialog.setFilterPath(System.getProperty("user.dir"));
        dialog.setFileName("toif_Coverage.xml");
        final String savePath = dialog.open();
        
        if (savePath == null)
        {
            return null;
        }
        
        try
        {
            final IToifProject proj = (IToifProject) view.getReportInput();
            
            class CcrJob extends Job
            {
                
                public CcrJob()
                {
                    super("Making Coverage Report...");
                }
                
                public IStatus run(IProgressMonitor monitor)
                {
                    SubMonitor progress = SubMonitor.convert(monitor);
                    progress.beginTask("Creating Coverage Report...", IProgressMonitor.UNKNOWN);
                    // make and run the coverage generator.
                    new CoverageClaimGenerator(proj.getRepository(), new File(savePath), false);
                    return Status.OK_STATUS;
                }
            }
            ;
            new CcrJob().schedule();
            
        }
        catch (NullPointerException e)
        {
            System.err.println("There was a null pointer exception.");
            e.printStackTrace();
        }
        
        return null;
    }
    
}
