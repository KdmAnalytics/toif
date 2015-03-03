/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.ToifReportEntry;
import com.kdmanalytics.toif.report.internal.views.ReportView;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * handler for setting the trust of a finding.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class SetTrustHandler extends AbstractHandler
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
        final IStructuredSelection ss = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        final ReportView view = (ReportView) HandlerUtil.getActivePart(event);
        final Shell shell = HandlerUtil.getActiveShell(event);
        final Integer value = getNewTrustValue(shell);
        
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            
            public void run(IProgressMonitor monitor)
            {
                if (value != null)
                {
                    List<FindingEntry> entries = new ArrayList<FindingEntry>(ss.size());
                    for (Object o : ss.toList())
                    {
                        if (o instanceof ToifReportEntry)
                        {
                            IToifReportEntry toifReportEntry = (IToifReportEntry) o;
                            if (toifReportEntry.getFindingEntry() != null)
                            {
                                entries.add(toifReportEntry.getFindingEntry());
                            }
                        }
                    }
                    ModelUtil.setTrustValuesOnFindings(entries, view.getReportInput(), value, monitor);
                }
            }
        };
        
        try
        {
            new ProgressMonitorDialog(shell).run(true, false, runnable);
        }
        catch (InvocationTargetException e)
        {
            System.err.println("Unable to Set trust values." + e);
        }
        catch (InterruptedException e)
        {
            System.err.println("Unable to Set trust values." + e);
        }
        
        view.refresh();
        return null;
    }
    
    /**
     * Gets the new trust value.
     * 
     * @param shell
     * 
     * @return the new trust value
     */
    private Integer getNewTrustValue(Shell shell)
    {
        InputDialog dlg = new InputDialog(shell, "Trust Level", "Set the trust level:", "0", new IInputValidator() {
            
            @Override
            public String isValid(String newText)
            {
                try
                {
                    Integer.parseInt(newText);
                }
                catch (NumberFormatException e)
                {
                    return "This value should be an Integer!";
                }
                return null;
            }
        });
        dlg.open();
        Integer value = null;
        try
        {
            value = Integer.valueOf(dlg.getValue());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        return value;
    }
}
