/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kdmanalytics.toif.report.internal.items.ToifReportEntry;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * The Class MoreInformationHandler. for displaying more information about the
 * cwe.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * @author Adam Nunn <adam@kdmanalytics.com>
 */
public class MoreInformationHandler extends AbstractHandler implements IHandler
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
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        
        for (Object o : ((IStructuredSelection) selection).toArray())
        {
            if (o instanceof ToifReportEntry)
            {
                IFindingEntry entry = ((IToifReportEntry) o).getFindingEntry();
                String cwe = entry.getCwe();
                
                IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
                IWebBrowser browser;
                try
                {
                    browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR, null, cwe, cwe);
                    URL url = new URL("http://cwe.mitre.org/data/definitions/" + cwe.replace("CWE-", "") + ".html");
                    browser.openURL(url);
                }
                catch (PartInitException e)
                {
                    e.printStackTrace();
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                
            }
        }
        return null;
    }
    
    /**
     * 
     */
    @Override
    public boolean isEnabled()
    {
        return true;
    }
    
}
