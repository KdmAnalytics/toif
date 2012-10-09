/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * 
 * Exports the selection to a csv file.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class ExportSelectionHandler extends AbstractHandler implements IHandler
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
        ISelection s = HandlerUtil.getCurrentSelection(event);
        if (!(s instanceof IStructuredSelection))
        {
            return null;
        }
        
        // Display display = new Display();
        Shell shell = HandlerUtil.getActiveShell(event);
        shell.open();
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterNames(new String[] { "TSV Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.tsv", "*.*" });
        dialog.setFilterPath(System.getProperty("user.dir"));
        dialog.setFileName("toif.tsv");
        String savePath = dialog.open();
        
        if (savePath == null)
        {
            return null;
        }
        
        try
        {
            FileWriter writer = new FileWriter(savePath);
            
            writeHeader(writer);
            writeFindings((IStructuredSelection) s, writer);
            
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            System.err.println("There was a problem reading or writing to the file.");
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            System.err.println("There was a null pointer exception.");
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean isEnabled()
    {
        return true;
    }
    
    /**
     * write the findings to the csv file.
     * 
     * @param selection
     *            the selected element from the report
     * @param writer
     *            the file writer to use to output
     * @throws IOException
     */
    private void writeFindings(IStructuredSelection selection, FileWriter writer) throws IOException
    {
        Set<FindingEntry> entries = new HashSet<FindingEntry>();
        for (Object object : selection.toList())
        {
            if (object instanceof IToifReportEntry)
            {
                IToifReportEntry reportItem = (IToifReportEntry) object;
                entries.add(reportItem.getFindingEntry());
            }
        }
        
        for (FindingEntry entry : entries)
        {
            ToolGroup tool = (ToolGroup) entry.getParent();
            LocationGroup location = (LocationGroup) tool.getParent();
            
            // sfp
            writer.append(entry.getSfp());
            writer.append('\t');
            
            // cwe
            writer.append(entry.getCwe());
            writer.append('\t');
            
            
            // valid
            String valid = "" + !entry.isOk();
            writer.append(valid.toUpperCase());
            writer.append('\t');
            
            // trust
            writer.append(entry.getTrust() + "");
            writer.append('\t');
            
            // resource
            writer.append(location.getPath());
            writer.append('\t');
            
            // linenumber
            writer.append(location.getToifLineNumber());
            writer.append('\t');
            
            // kdmLineNumber
            if (location.getRealLineNumber() != null)
            {
                writer.append(location.getRealLineNumber());
            }
            else
            {
                writer.append("");
            }
            writer.append('\t');
            
            // tool
            writer.append(tool.toString());
            writer.append('\t');
            
            // description
            writer.append(entry.getDescription());
            writer.append('\n');
        }
        
    }
    
    /**
     * @param writer
     * @throws IOException
     */
    private void writeHeader(FileWriter writer) throws IOException
    {
        writer.append("SFP");
        writer.append('\t');
        writer.append("CWE");
        writer.append('\t');
        // writer.append("Cluster");
        // writer.append('\t');
        writer.append("Valid");
        writer.append('\t');
        writer.append("Trust");
        writer.append('\t');
        
        writer.append("Resource");
        writer.append('\t');
        writer.append("Line Number");
        writer.append('\t');
        writer.append("KDM Line Number");
        writer.append('\t');
        writer.append("Generator Tool");
        writer.append('\t');
        writer.append("Weakness Description");
        writer.append('\n');
    }
    
}
