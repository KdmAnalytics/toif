/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

import com.kdmanalytics.toif.report.internal.filters.TermFilter;
import com.kdmanalytics.toif.report.internal.listeners.ReportDoubleClickListener;
import com.kdmanalytics.toif.report.internal.providers.ReportContentProvider;
import com.kdmanalytics.toif.report.internal.providers.ReportStyledCellLabelProvider;
import com.kdmanalytics.toif.report.items.IToifProject;

/**
 * the report view.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public class ReportView extends ViewPart
{
    
    public static final String VIEW_ID = "com.kdmanalytics.toif.report.view";
    
    private enum Mode
    {
        Table, None,
    }
    
    private Composite parent;
    
    private PageBook pageBook;
    
    private PageBook viewerBook;
    
    private Map<Mode, ColumnViewer> viewers;
    
    /**
     * currently only table view.
     */
    private Mode currentViewerMode;
    
    private Composite reportPage;
    
    private Label numberOfDefects;
    
    private Label noReportPage;
    
    private Label emptyViewer;
    
    private IToifProject inputProject;
    
    private TableViewer tableViewer;
    
    protected IProject currentProject;
    
    /**
     * selection changed listener. looks for selection of project to reload toif
     * data.
     */
    ISelectionListener selectionListener = new ISelectionListener() {
        
        public void selectionChanged(IWorkbenchPart part, ISelection sel)
        {
            
            IProject newProject = null;
            
            if (sel instanceof IStructuredSelection)
            {
                
                for (Object object : ((IStructuredSelection) sel).toArray())
                {
                    if (object instanceof IProject)
                    {
                        newProject = (IProject) object;
                        
                        if (currentProject == newProject)
                        {
                            return;
                        } else {
                            currentProject = newProject;
                        }
                    }
                }
                
                if (newProject == null)
                {
                    return;
                }
                
                if (newProject != null)
                {
                    persistData();
                    File file = new File(newProject.getLocation() + "/.toifProject.ser");
                    
                    if (!file.exists())
                    {
                        pageBook.showPage(noReportPage);
                        return;
                    }
                    
                    try
                    {
                        FileInputStream fileIn = new FileInputStream(file);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        IToifProject project = (IToifProject) in.readObject();
                        in.close();
                        fileIn.close();
                        
                        if (project != null)
                        {
                            System.out.println("toif project deserialized");
                            // view.getTableViewer().setInput(project);
                            
                            IFolder repoFolder = ensureKdmRepoFolderExists(newProject);
                            project.setRepository(repoFolder);
                            project.setIProject(repoFolder.getProject());
                            // project.setIProject(iProject.getLocation());
                            
                            clearInput();
                            updateInput(project);
                            refresh();
                            return;
                        }
                    }
                    catch (IOException i)
                    {
                        System.out.println("problem with serialization file.");
                        i.printStackTrace();
                        // return;
                    }
                    catch (ClassNotFoundException c)
                    {
                        System.out.println("IToifProject class not found.");
                        c.printStackTrace();
                        // return;
                    }
                    catch (CoreException e)
                    {
                        System.out.println("repo folder could not be found.");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    
    /**
     * makes sure that the kdm repository folder does exist.
     * 
     * @param project
     *            the kdm project
     * @throws CoreException
     */
    private static IFolder ensureKdmRepoFolderExists(final IProject project) throws CoreException
    {
        IFolder kdmDataDir = project.getFolder(".KDM");
        if (!kdmDataDir.exists())
        {
            kdmDataDir.create(true, true, null);
        }
        
        IFolder kdmRepoDir = kdmDataDir.getFolder("repository");
        if (!kdmRepoDir.exists())
        {
            kdmRepoDir.create(true, true, null);
        }
        return kdmRepoDir;
    }
    
    public ReportView()
    {
        parent = null;
        pageBook = null;
        reportPage = null;
        viewers = null;
        currentViewerMode = Mode.None;
    }
    
    protected void persistData()
    {
        // ReportContentProvider cp = (ReportContentProvider)
        // tableViewer.getContentProvider();
        //
        // IToifProject project = cp.getProject();
        
        if (inputProject == null)
        {
            System.out.println("no project to save.");
            return;
        }
        
        if (inputProject.getIProject().getLocation() == null)
        {
            System.out.println("no project to save.");
            return;
        }
        
        File serFile = new File(inputProject.getIProject().getLocation() + "/.toifProject.ser");
        serFile.delete();
        
        try
        {
            serFile.createNewFile();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try
        {
            FileOutputStream fileOut = new FileOutputStream(serFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(inputProject);
            out.close();
            fileOut.close();
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
        
        return;
        
    }
    
    @Override
    public void createPartControl(Composite container)
    {
        getSite().getPage().addSelectionListener(selectionListener);
        parent = container;
        pageBook = new PageBook(parent, SWT.NONE);
        
        // Page 1 no data loaded label
        noReportPage = new Label(pageBook, SWT.TOP + SWT.LEFT + SWT.WRAP);
        noReportPage.setText("No TOIF data available");
        
        // Page 2 Report Composite
        reportPage = new Composite(pageBook, SWT.NONE);
        reportPage.setLayout(new GridLayout());
        reportPage.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        createSearchControl(reportPage);
        numberOfDefects = new Label(reportPage, SWT.NONE);
        numberOfDefects.setText("Number of Defects:");
        
        createReportViewerControl(reportPage);
        pageBook.showPage(noReportPage);
        
    }
    
    public void dispose()
    {
        persistData();
        System.err.println("DATA PERSISTED");
        getSite().getPage().removeSelectionListener(selectionListener);
    }
    
    /**
     * deal with the term filter. apply the term filter data to the current
     * view.
     * 
     * @param text
     */
    private void handleTermFilter(Text text)
    {
        String[] terms = text.getText().split("OR");
        if (text.getText().isEmpty())
        {
            List<ViewerFilter> filters = new ArrayList<ViewerFilter>(Arrays.asList(getCurrentViewer().getFilters()));
            for (Iterator<ViewerFilter> i = filters.iterator(); i.hasNext();)
            {
                ViewerFilter filter = (ViewerFilter) i.next();
                if (filter instanceof TermFilter)
                {
                    i.remove();
                }
            }
            getCurrentViewer().setFilters(filters.toArray(new ViewerFilter[] {}));
        }
        else
        {
            getCurrentViewer().addFilter(new TermFilter(terms));
        }
        text.setMessage("Search for Everything...");
        updateDefectCount();
    }

    
    /**
     * create the search bar control
     * 
     * @param parent
     * @return
     */
    private Control createSearchControl(Composite parent)
    {
        GridData ld = new GridData();
        ld.grabExcessHorizontalSpace = true;
        ld.horizontalAlignment = GridData.FILL;
        
        Composite search = new Composite(parent, SWT.NONE);
        GridLayout layout2 = new GridLayout(2, false);
        search.setLayout(layout2);
        search.setLayoutData(ld);
        
        // create a label to display information about the view in.
        final Text text = new Text(search, SWT.SINGLE | SWT.BORDER);
        text.setMessage("Search for Everything...");
        
        text.addListener(SWT.DefaultSelection, new Listener() {
            
            @Override
            public void handleEvent(Event event)
            {
                handleTermFilter(text);
                
            }
        });
        
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        text.setLayoutData(layoutData);
        
        Button button = new Button(search, SWT.BORDER);
        button.setText("Search");
        button.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                handleTermFilter(text);
            }
            
        });
        
        return search;
    }
    
    /**
     * create the control for the viewer.
     * 
     * @param parent
     * @return
     */
    private Control createReportViewerControl(Composite parent)
    {
        viewerBook = new PageBook(parent, SWT.NONE);
        viewerBook.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        TableViewer tableViewer = createReportTableViewer(viewerBook);
        tableViewer.addDoubleClickListener(new ReportDoubleClickListener());
        
        emptyViewer = new Label(viewerBook, SWT.TOP | SWT.LEFT | SWT.WRAP);
        viewers = new EnumMap<ReportView.Mode, ColumnViewer>(Mode.class);
        viewers.put(Mode.Table, tableViewer);
        setReportMode(Mode.Table);
        viewerBook.showPage(tableViewer.getControl());
        
        return viewerBook;
    }
    
    /**
     * create the report table.
     * 
     * @param parent
     * @return
     */
    private TableViewer createReportTableViewer(Composite parent)
    {
        tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.setContentProvider(new ReportContentProvider());
        tableViewer.setComparator(new ReportViewerComparator());
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        ColumnViewerToolTipSupport.enableFor(tableViewer);
        
        String[] titles = { "File", "Location", "Tool", "SFP", "CWE", "Trust", "Description" };
        int[] bounds = { 200, 100, 200, 70, 90, 50, 900 };
        
        // File Column
        TableViewerColumn col = createTableViewerColumn(tableViewer, titles[0], bounds[0], 0, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        // Location Column
        col = createTableViewerColumn(tableViewer, titles[1], bounds[1], 1, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        // Tool Column
        col = createTableViewerColumn(tableViewer, titles[2], bounds[2], 2, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        // SFP Column
        col = createTableViewerColumn(tableViewer, titles[3], bounds[3], 3, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        // CWE Column
        col = createTableViewerColumn(tableViewer, titles[4], bounds[4], 4, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        // Trust Column
        col = createTableViewerColumn(tableViewer, titles[5], bounds[5], 5, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        // Description Column
        col = createTableViewerColumn(tableViewer, titles[6], bounds[6], 6, true);
        col.setLabelProvider(new ReportStyledCellLabelProvider());
        
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(tableViewer.getTable());
        
        tableViewer.getTable().setMenu(menu);
        getSite().registerContextMenu(menuManager, tableViewer);
        
        return tableViewer;
    }
    
    /**
     * Create treeViewerColumns
     * 
     * @param title
     * @param bound
     * @param colNumber
     * @return
     */
    private TableViewerColumn createTableViewerColumn(TableViewer viewer, final String title, final int bound, final int colNumber,
            boolean enableSorting)
    {
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.LEFT);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        if (enableSorting == true)
        {
            column.addSelectionListener(getSelectionAdapter(viewer, column, colNumber));
        }
        return viewerColumn;
    }
    
    /**
     * Create a Selection Adapter for the given column and index
     * 
     * @param column
     * @param index
     * @return
     */
    private SelectionAdapter getSelectionAdapter(final TableViewer viewer, final TableColumn column, final int index)
    {
        SelectionAdapter selectionAdaptor = new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                ReportViewerComparator comparator = (ReportViewerComparator) viewer.getComparator();
                comparator.setColumn(index);
                int dir = comparator.getDirection();
                viewer.getTable().setSortDirection(dir);
                viewer.getTable().setSortColumn(column);
                viewer.refresh();
            }
        };
        return selectionAdaptor;
        
    }
    
    /**
     * @param table
     */
    private void setReportMode(Mode newMode)
    {
        currentViewerMode = newMode;
        getSite().setSelectionProvider(getCurrentViewer());
        getCurrentViewer().getControl().setFocus();
    }
    
    /**
     * get the mode that the view is in. eg table.
     * 
     * @return
     */
    public Mode getReportMode()
    {
        return currentViewerMode;
    }
    
    /**
     * @return
     */
    public ColumnViewer getCurrentViewer()
    {
        return viewers.get(currentViewerMode);
    }
    
    @Override
    public void setFocus()
    {
        pageBook.setFocus();
    }
    
    public IToifProject getReportInput()
    {
        return inputProject;
    }
    
    public void setReportInput(IToifProject project)
    {
        updateInput(project);
    }
    
    /**
     * sets the input. updates the defect counter
     * 
     * @param proj
     */
    public void updateInput(IToifProject proj)
    {
        IToifProject prevProj = inputProject;
        if (proj == null)
        {
            clearInput();
        }
        else
        {
            inputProject = proj;
            if (!inputProject.equals(prevProj))
            {
                updateReportViewer(true);
                for (ColumnViewer colViewer : viewers.values())
                {
                    colViewer.setInput(inputProject);
                    colViewer.getControl().pack();
                }
                updateDefectCount();
            }
            
            pageBook.showPage(reportPage);
        }
    }
    
    /**
     * clears input
     */
    public void clearInput()
    {
        inputProject = null;
        updateReportViewer(true);
    }
    
    /**
     * update the viewer. refreshes.
     */
    private void updateReportViewer(final boolean doExpand)
    {
        if (inputProject == null)
        {
            pageBook.showPage(noReportPage);
        }
        else
        {
            if (getCurrentViewer().getInput() != null)
            {
                Runnable runnable = new Runnable() {
                    
                    @Override
                    public void run()
                    {
                        getCurrentViewer().refresh();
                        updateDefectCount();
                    }
                };
                BusyIndicator.showWhile(getDisplay(), runnable);
                setViewerVisible(true);
            }
        }
    }
    
    /**
     * @return
     */
    private Display getDisplay()
    {
        if (pageBook != null && !pageBook.isDisposed())
        {
            return pageBook.getDisplay();
        }
        return null;
    }
    
    /**
     * @param b
     */
    private void setViewerVisible(boolean showReport)
    {
        if (showReport)
        {
            viewerBook.showPage(getCurrentViewer().getControl());
        }
        else
        {
            viewerBook.showPage(emptyViewer);
        }
    }
    
    public void refresh()
    {
        if (inputProject == null)
        {
            return;
        }
        setViewerVisible(true);
        updateReportViewer(false);
        pageBook.showPage(reportPage);
    }
    
    /**
     * update the defect counter. this calculates what to display in the status
     * text.
     */
    public void updateDefectCount()
    {
        
        final int totalEntries = inputProject.getFindingEntries().size();
        final int visibleEntries = ((TableViewer) getCurrentViewer()).getTable().getItemCount();
        
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            
            @Override
            public void run()
            {
                numberOfDefects.setText("Number of Defects: " + visibleEntries + " (" + (totalEntries - visibleEntries) + " filtered from view)");
                numberOfDefects.getParent().layout();
            }
        });
    }
    
    /**
     * @return
     */
    public ViewerFilter[] getFilters()
    {
        return getCurrentViewer().getFilters();
    }
    
    /**
     * @param array
     */
    public void setFilters(ViewerFilter[] array)
    {
        getCurrentViewer().setFilters(array);
    }
    
    public TableViewer getTableViewer()
    {
        return tableViewer;
    }
}
