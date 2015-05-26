/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This
 *  program and the // accompanying materials are made available under the terms
 *  of the Open Source // Initiative OSI - Open Software License v3.0 which
 *  accompanies this // distribution, and is available at
 *  http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.contributionItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.google.common.collect.Maps;
import com.kdmanalytics.toif.report.internal.items.Trace;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * The Class TraceContributionItem.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 */
public class TraceContributionItem extends CompoundContributionItem
{
    
    /** The Constant LINE_NUMBER_KEY. */
    private static final String LINE_NUMBER_KEY = "com.kdmanalytics.toif.report.lineNumber";
    
    /** An Empty array object to return when where are no ContributionItems */
    private static ContributionItem[] EMPTY = new ContributionItem[] {};
    
    /**
     * Id of the command these contribution items should represent.
     */
    private static final String COMMAND_ID = "com.kdmanalytics.toif.report.traceContribution";
    
    /**
     * The id of the dynamic menu contribution.
     */
    private static final String CONTRIBUTION_ID = "com.kdmanalytics.toif.report.traceContribution.options";
    
    /** The Constant ID. */
    public static final String ID = "com.kdmanalytics.toif.report.contributionItems";
    
    /**
     * Instantiates a new trace contribution item.
     */
    public TraceContributionItem()
    {
    }
    
    /**
     * generate contribution items for the selection.
     */
    @Override
    protected IContributionItem[] getContributionItems()
    {
        Object selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
        
        if (!(selection instanceof StructuredSelection))
        {
            return EMPTY;
        }
        
        StructuredSelection ss = (StructuredSelection) selection;
        
        if (ss.isEmpty())
        {
            return EMPTY;
        }
        
        Object element = ss.getFirstElement();
        
        if (!(element instanceof IToifReportEntry))
        {
            return EMPTY;
        }
        
        IToifReportEntry entry = (IToifReportEntry) element;
        
        final IFindingEntry fEntry = entry.getFindingEntry();
        
        List<Trace> traces = fEntry.getTraces();
        
        List<CommandContributionItem> list = new ArrayList<CommandContributionItem>();
        
        //for each of the traces for this finding entry. make contribution items for it.
        for (Trace trace : traces)
        {
            CommandContributionItemParameter param = new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                    CONTRIBUTION_ID, COMMAND_ID, CommandContributionItem.STYLE_PUSH);
            
            final String lineNumber = trace.getLineNumber();
            param.label = lineNumber;
            Map<Object, Object> parameters = Maps.newHashMap();
            parameters.put(LINE_NUMBER_KEY, lineNumber);
            
            param.parameters = parameters;
            
            list.add(new CommandContributionItem(param));
        }
        
        return list.toArray(new IContributionItem[] {});
    }
    
}
