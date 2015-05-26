/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.providers;

import java.net.URL;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.report.internal.Activator;
import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.internal.items.LocationGroup;
import com.kdmanalytics.toif.report.internal.items.ToolGroup;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * The Class ReportLabelProvider.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 */
public class ReportLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider, ITableColorProvider, IFontProvider
{
    
    /** The green. */
    private static final String GREEN = "green";
    
    private static final String BLUE = "blue";
    
    /** The red. */
    private static final String RED = "red";
    
    /** The foreground green. */
    private static final String FOREGROUND_GREEN = "foreground_green";
    
    private static final String FOREGROUND_BLACK = "foreground_black";
    
    /** The foreground red. */
    private static final String FOREGROUND_RED = "foreground_red";
    
    /** The Constant TICK_KEY. */
    private static final String TICK_KEY = "tick";
    
    /** The Constant CROSS_KEY. */
    private static final String CROSS_KEY = "cross";
    
    /** The Constant PAGE_WHITE_STACK_KEY. */
    private static final String PAGE_WHITE_STACK_KEY = "PAGE_WHITE_STACK_KEY";
    
    /** The Constant PAGE_KEY. */
    private static final String PAGE_KEY = "PAGE_KEY";
    
    /** The Constant WRENCH_KEY. */
    private static final String WRENCH_KEY = "WRENCH_KEY";
    
    private ColorRegistry colorReg;
    
    /**
     * Instantiates a new report label provider.
     */
    public ReportLabelProvider()
    {
        loadImagesIntoRegistry();
        loadColoursIntoRegistry();
    }
    
    private void loadColoursIntoRegistry()
    {
        colorReg = new ColorRegistry();
        if (colorReg.get(GREEN) == null)
        {
            colorReg.put(GREEN, new RGB(220, 255, 220));
        }
        if (colorReg.get(BLUE) == null)
        {
            colorReg.put(BLUE, new RGB(220, 220, 255));
        }
        if (colorReg.get(RED) == null)
        {
            colorReg.put(RED, new RGB(255, 220, 220));
        }
        if (colorReg.get(FOREGROUND_GREEN) == null)
        {
            colorReg.put(FOREGROUND_GREEN, new RGB(0, 80, 0));
        }
        if (colorReg.get(FOREGROUND_BLACK) == null)
        {
            colorReg.put(FOREGROUND_BLACK, new RGB(0, 0, 0));
        }
        if (colorReg.get(FOREGROUND_RED) == null)
        {
            colorReg.put(FOREGROUND_RED, new RGB(100, 0, 0));
        }
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
     */
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * Load images.
     */
    private void loadImagesIntoRegistry()
    {
        final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
        if (imgReg.get(TICK_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/tick.png");
            imgReg.put(TICK_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(CROSS_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/cross.png");
            imgReg.put(CROSS_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(PAGE_WHITE_STACK_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/page_white_stack.png");
            imgReg.put(PAGE_WHITE_STACK_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(PAGE_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/page.png");
            imgReg.put(PAGE_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(WRENCH_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/wrench.png");
            imgReg.put(WRENCH_KEY, ImageDescriptor.createFromURL(url));
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang
     * .Object, int)
     */
    @Override
    public Color getBackground(final Object element, final int columnIndex)
    {
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            
            if (Citing.TRUE == entry.isOk())
            {
                return colorReg.get(GREEN);
            }
            if (Citing.UNKNOWN == entry.isOk())
            {
                return colorReg.get(BLUE);
            }
            else
            {
                return colorReg.get(RED);
            }
        }
        
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
     * .Object, int)
     */
    @Override
    public Image getColumnImage(final Object element, final int columnIndex)
    {
        final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
        
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            
            switch (columnIndex)
            {
                case 0:
                    return Citing.TRUE == entry.isOk() ? imgReg.get(TICK_KEY) : imgReg.get(CROSS_KEY);
                default:
                    return null;
            }
            
        }
        
        if (element instanceof IFileGroup)
        {
            final URL url = null;
            switch (columnIndex)
            {
                case 0:
                    return imgReg.get(PAGE_WHITE_STACK_KEY);
                default:
                    break;
            }
            
            if (url == null)
            {
                return null;
            }
            
        }
        
        if (element instanceof LocationGroup)
        {
            final URL url = null;
            switch (columnIndex)
            {
                case 0:
                    return imgReg.get(PAGE_KEY);
                default:
                    break;
            }
            
            if (url == null)
            {
                return null;
            }
            
        }
        
        if (element instanceof ToolGroup)
        {
            final URL url = null;
            switch (columnIndex)
            {
                case 0:
                    return imgReg.get(WRENCH_KEY);
                default:
                    break;
            }
            
            if (url == null)
            {
                return null;
            }
            
        }
        // should never get here...
        System.err.println("no image found " + element);
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
     * .Object, int)
     */
    @Override
    public String getColumnText(final Object element, final int columnIndex)
    {
        String result = "";
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            switch (columnIndex)
            {
                case 0:
                    if (Citing.TRUE == entry.isOk())
                    {
                        result = "Not Weakness";
                    }
                    if (Citing.UNKNOWN == entry.isOk())
                    {
                        result = "Unknown";
                    }
                    else
                    {
                        result = "Is Weakness";
                    }
                    break;
                case 1:
                    result = entry.getSfp();
                    break;
                case 2:
                    result = entry.getCwe();
                    break;
                case 3:
                    result = entry.getTrust() + "";
                    break;
                case 4:
                    result = entry.getDescription();
                    break;
                
                default:
                    // should not reach here
                    result = "";
            }
        }
        else if (element instanceof IFileGroup)
        {
            final IFileGroup file = (IFileGroup) element;
            
            switch (columnIndex)
            {
                case 0:
                    result = file.getName() + " (" + file.getFindingEntriesNotOk().size() + ")";
                    break;
                default:
                    // should not reach here
                    result = "";
            }
            
        }
        else if (element instanceof ToolGroup)
        {
            final ToolGroup tool = (ToolGroup) element;
            
            switch (columnIndex)
            {
                case 0:
                    result = tool.toString();
                    break;
                default:
                    // should not reach here
                    result = "";
            }
            
        }
        else if (element instanceof LocationGroup)
        {
            final LocationGroup loc = (LocationGroup) element;
            
            switch (columnIndex)
            {
                case 0:
                    result = "Line Number: " + loc.getLineNumber();
                    break;
                default:
                    // should not reach here
                    result = "";
            }
            
        }
        
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
     */
    @Override
    public Font getFont(final Object element)
    {
        if (element instanceof IFileGroup)
        {
            return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang
     * .Object, int)
     */
    @Override
    public Color getForeground(final Object element, final int columnIndex)
    {
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            
            if (Citing.UNKNOWN == entry.isOk())
            {
                return colorReg.get(FOREGROUND_BLACK);
            }
            else
            {
                
                return Citing.TRUE == entry.isOk() ? colorReg.get(FOREGROUND_GREEN) : colorReg.get(FOREGROUND_RED);
            }
        }
        
        return null;
    }
    
    /**
     * Gets the image.
     * 
     * @param element
     *            the element
     * @return the image
     */
    public Image getImage(final Object element)
    {
        final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
        
        if (element instanceof IFileGroup)
        {
            return imgReg.get(PAGE_WHITE_STACK_KEY);
        }
        if (element instanceof LocationGroup)
        {
            return imgReg.get(PAGE_KEY);
        }
        if (element instanceof ToolGroup)
        {
            return imgReg.get(WRENCH_KEY);
        }
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            return Citing.TRUE == entry.isOk() ? imgReg.get(TICK_KEY) : imgReg.get(CROSS_KEY);
        }
        
        // should never get here
        System.err.println("no image found " + element);
        return null;
    }
    
    /**
     * Gets the text.
     * 
     * @param element
     *            the element
     * @return the text
     */
    public String getText(final Object element)
    {
        return "test: " + element.toString();
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.
     * Object)
     */
    @Override
    public String getToolTipText(final Object element)
    {
        if (element instanceof IToifReportEntry)
        {
            IFileGroup fileGroup = ((IToifReportEntry) element).getFileGroup();
            return fileGroup.getPath();
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.StyledCellLabelProvider#paint(org.eclipse.swt
     * .widgets.Event, java.lang.Object)
     */
    @Override
    protected void paint(final Event event, final Object element)
    {
        // method stub
        super.paint(event, element);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.
     * jface.viewers.ViewerCell)
     */
    @Override
    public void update(final ViewerCell cell)
    {
        Object element = cell.getElement();
        final StyledString styledString = new StyledString(getColumnText(element, cell.getColumnIndex()));
        
        if (element instanceof IFindingEntry)
        {
            final IFindingEntry entry = (IFindingEntry) element;
            
            if (Citing.UNKNOWN == entry.isOk())
            {
                cell.setForeground(colorReg.get(FOREGROUND_BLACK));
                cell.setBackground(colorReg.get(BLUE));
            }
            else
            {
                
                cell.setForeground(Citing.TRUE == entry.isOk() ? colorReg.get(FOREGROUND_GREEN) : colorReg.get(FOREGROUND_RED));
                cell.setBackground(Citing.TRUE == entry.isOk() ? colorReg.get(GREEN) : colorReg.get(RED));
            }
        }
        else if (element instanceof IFileGroup)
        {
            cell.setFont(getFont(element));
        }
        cell.setText(styledString.toString());
        cell.setStyleRanges(styledString.getStyleRanges());
        cell.setImage(cell.getColumnIndex() > 0 ? null : getImage(element));
    }
}
