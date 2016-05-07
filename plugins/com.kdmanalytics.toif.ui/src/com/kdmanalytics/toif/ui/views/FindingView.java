/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.kdmanalytics.etoif.ccr.CoverageClaimGenerator;
import com.kdmanalytics.toif.ui.Activator;
import com.kdmanalytics.toif.ui.common.FindingEntry;
import com.kdmanalytics.toif.ui.common.IFindingEntry;
import com.kdmanalytics.toif.ui.internal.filters.ResourceFilter;
import com.kdmanalytics.toif.ui.internal.filters.TermFilter;
import com.kdmanalytics.toif.ui.views.sort.AdaptorConfigWeightComparator;
import com.kdmanalytics.toif.ui.views.sort.FindingViewColumnComparator;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained
 * from the model. The sample creates a dummy model on the fly, but a real implementation would
 * connect to the model available either in this or another plug-in (e.g. the workspace). The view
 * is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each
 * view can present the same model objects using different labels and icons, if needed.
 * Alternatively, a single label provider can be shared between views in order to ensure that
 * objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class FindingView extends ViewPart
{

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.kdmanalytics.toif.views.FindingView";

    /**
     * Time format used to make file names.
     * 
     * Not actually ISO format, since that doesn't work on some file systems
     */
    private DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

    /**
     * Track citing time so we can back up citing information on a daily
     * (or new usage) basis. This is used with a session property.
     */
    public static final QualifiedName PROJECT_CITE_DATE = new QualifiedName(Activator.PLUGIN_ID, "citeDate");

    /** The Constant DESCRIPTION_KEY. */
    private static final String DESCRIPTION_KEY = "description";

    /** The Constant EXPORT_KEY. */
    private static final String EXPORT_KEY = "export";

    /** The Constant COVERAGE_KEY. */
    private static final String COVERAGE_KEY = "coverage";

    /** The Constant FILTER_KEY. */
    private static final String FILTER_KEY = "filter";
    
    /** The Constant SORT_KEY. */
    private static final String SORT_KEY = "sort";

    /** The Constant FILTER_KEY. */
    private static final String NOT_WEAKNESS_KEY = "not_weakness";

    /** The Constant FILTER_KEY. */
    private static final String IS_WEAKNESS_KEY = "is_weakness";

    /** The Constant FILTER_KEY. */
    private static final String UNCITE_WEAKNESS_KEY = "uncite_weakness";

    /** The Constant FILTER_KEY. */
    private static final String SET_TRUST_KEY = "set_trust";

    /** The Constant FILTER_KEY. */
    private static final String TRACE_KEY = "trace";

    /** The Constant FILTER_KEY. */
    private static final String MORE_INFO_KEY = "more_info";

    /**
     * Project currently being displayed
     */
    private IProject currentProject;

    private FindingSelectionChangedListener selection = null;

    /**
     * Conent for table
     */
    private FindingContentProvider contentProvider;

    private TreeViewer viewer;
    private Action defaultSortActionButton;
    private Action descriptionAction;
    private Action exportAction;
    private Action coverageAction;
    private Action filterAction;
    private Action defaultSortAction;
    private Action doubleClickAction;
    private Action notAWeaknessAction;
    private Action isAWeaknessAction;
    private Action unciteWeaknessAction;
    private Action setTrustLevelAction;
    private Action traceAction;
    private Action moreInformationAction;

    /**
     * Label that indicates number of findings.
     */
    private Label label;
    private Label projectLabel;
    private Label filterLabel;

    /**
     * File to save a citing history within
     */
    private IFile saveFile;

    /**
     * selection changed listener. looks for selection of project to reload toif
     * data.
     */
    ISelectionListener selectionListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart part, ISelection sel)
        {
            if (sel instanceof IStructuredSelection)
            {
                for (Object object : ((IStructuredSelection) sel).toArray())
                {
                    if (object instanceof IAdaptable)
                    {
                        IAdaptable adapt=(IAdaptable)object;
                        object = (IResource)adapt.getAdapter(IResource.class);
                    }

                    // Find out the project for the resource
                    if(object instanceof IResource)
                    {
                        IProject newProject = ((IResource)object).getProject();

                        if (currentProject != newProject)
                        {
                            currentProject = newProject;
                            viewer.setInput(newProject);
                            updateDefectCount();
                        }

                        // Bail after the first project is found.
                        return;
                    }
                }
            }
        }
    };

    /**
     * The constructor.
     */
    public FindingView()
    {
        // Load images
        loadImagesIntoRegistry();

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        //		// Listen for resource change events
        //		IResourceChangeListener preListener = new IResourceChangeListener()
        //		{
        //			public void resourceChanged(IResourceChangeEvent event)
        //			{
        //				System.err.println("PRE-BUILD:");
        //				IResourceDelta delta = event.getDelta();
        //				update(delta);
        //			}
        //		};
        //		workspace.addResourceChangeListener(preListener, IResourceChangeEvent.PRE_BUILD);

        // Listen for resource change events, specifically post-build events.
        // This will allow us to refresh only once the markers have been created.
        IResourceChangeListener postListener = new IResourceChangeListener()
        {
            public void resourceChanged(IResourceChangeEvent event)
            {
                IResourceDelta delta = event.getDelta();
                update(delta);
            }
        };
        workspace.addResourceChangeListener(postListener, IResourceChangeEvent.POST_BUILD);

        // Listen for pertinent project change events that should cause the view contents to change
        IResourceChangeListener projectListener = new IResourceChangeListener() {

            @Override
            public void resourceChanged(IResourceChangeEvent event)
            {
                if (event == null || event.getDelta() == null) return;
                try
                {
                    event.getDelta().accept(new IResourceDeltaVisitor()
                    {
                        public boolean visit(IResourceDelta delta) throws CoreException
                        {
                            if (delta.getKind() == IResourceDelta.CHANGED)
                            {
                                final IResource resource = delta.getResource();
                                if (!(resource instanceof IProject)) return true;
                                IResourceDelta[] children = delta.getAffectedChildren();
                                // If the project file was changed then refresh things
                                if(children.length == 1 && children[0].getResource().getName().equals(".project"))
                                {
                                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            clear();
                                        }

                                    });
                                }
                                return false;
                            }
                            return true;
                        }
                    });
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
            };
        };

        workspace.addResourceChangeListener(projectListener);
    }

    /**
     * A clear is called when something dramatic has changed in the project.
     */
    private void clear()
    {
        Control control = viewer.getControl();
        if(!control.isDisposed())
        {
            viewer.getControl().setRedraw(false);
            contentProvider.clear();
            viewer.refresh();
            viewer.getControl().setRedraw(true);
        }
    }

    /** Update data for the specified file(s)
     * 
     * @param delta
     */
    protected void update(final IResourceDelta delta)
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run()
            {
                if(delta != null)
                {
                    viewer.getControl().setRedraw(false);
                    IResourceDeltaVisitor visitor = new IResourceDeltaVisitor()
                    {
                        /*
                         * (non-Javadoc)
                         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
                         */
                        public boolean visit(IResourceDelta delta) throws CoreException
                        {
                            IResource resource = delta.getResource();
                            if(resource instanceof IFile)
                            {
                                FindingContentProvider content = (FindingContentProvider) viewer.getContentProvider();
                                content.update(viewer, (IFile)resource);
                            }
                            return true;
                        }
                    };

                    try
                    {
                        delta.accept(visitor);
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                    viewer.getControl().setRedraw(true);
                }
                updateDefectCount();
            }
        });

    }

    /**
     * Load images.
     */
    private void loadImagesIntoRegistry()
    {
        final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
        if (imgReg.get(DESCRIPTION_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/flag_purple.png");
            imgReg.put(DESCRIPTION_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(EXPORT_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/table_go.png");
            imgReg.put(EXPORT_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(COVERAGE_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/page_code_go.png");
            imgReg.put(COVERAGE_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(FILTER_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/filter.gif");
            imgReg.put(FILTER_KEY, ImageDescriptor.createFromURL(url));
        }
        
        if(imgReg.get(SORT_KEY) == null)
        {
          final URL url = this.getClass().getResource("/icons/table_sort.png");
          imgReg.put(SORT_KEY, ImageDescriptor.createFromURL(url));
        }

        if (imgReg.get(NOT_WEAKNESS_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/tick.png");
            imgReg.put(NOT_WEAKNESS_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(IS_WEAKNESS_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/cross.png");
            imgReg.put(IS_WEAKNESS_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(UNCITE_WEAKNESS_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/bullet_white.png");
            imgReg.put(UNCITE_WEAKNESS_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(SET_TRUST_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/priority.gif");
            imgReg.put(SET_TRUST_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(TRACE_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/timeline_marker.png");
            imgReg.put(TRACE_KEY, ImageDescriptor.createFromURL(url));
        }
        if (imgReg.get(MORE_INFO_KEY) == null)
        {
            final URL url = this.getClass().getResource("/icons/information.png");
            imgReg.put(MORE_INFO_KEY, ImageDescriptor.createFromURL(url));
        }
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite composite)
    {
        getSite().getPage().addSelectionListener(selectionListener);


        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);

        createSearchControl(composite);

        viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        contentProvider = new FindingContentProvider();
        viewer.setContentProvider(contentProvider);
        
        // Set the default comparator. Selecting columns will change the comparator.
        viewer.setComparator(new AdaptorConfigWeightComparator());

        // Listen to change events so we know what to run actions upon
        selection = new FindingSelectionChangedListener();
        viewer.addSelectionChangedListener(selection);
        
        // Enable the default filters
        FilterUtility filter = new FilterUtility(this, viewer);
        filter.applyFilters();


        Tree tree = viewer.getTree();
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        tree.setLayoutData(gridData);

        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        ColumnViewerToolTipSupport.enableFor(viewer);

        String[] titles = { "File", "Location", "Tool", "SFP", "CWE", "Trust", "Description" };
        int[] bounds = { 200, 100, 200, 70, 90, 50, 900 };

        // File Column
        TreeViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // Location Column
        col = createTableViewerColumn(viewer, titles[1], bounds[1], 1, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // Tool Column
        col = createTableViewerColumn(viewer, titles[2], bounds[2], 2, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // SFP Column
        col = createTableViewerColumn(viewer, titles[3], bounds[3], 3, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // CWE Column
        col = createTableViewerColumn(viewer, titles[4], bounds[4], 4, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // Trust Column
        col = createTableViewerColumn(viewer, titles[5], bounds[5], 5, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        // Description Column
        col = createTableViewerColumn(viewer, titles[6], bounds[6], 6, true);
        col.setLabelProvider(new FindingStyledLabelProvider());

        //        MenuManager menuManager = new MenuManager();
        //        Menu menu = menuManager.createContextMenu(viewer.getTable());
        //        
        //        viewer.getTable().setMenu(menu);
        //        getSite().registerContextMenu(menuManager, viewer);


//        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        getSite().setSelectionProvider(viewer);

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.kdmanalytics.etoif2.viewer");
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose()
    {
        getSite().getPage().removeSelectionListener(selectionListener);
    }

    /**
     * create the search bar control
     * 
     * @param parent
     * @return
     */
    private void createSearchControl(Composite parent)
    {
        // create a label to display information about the view in.
        final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        text.setLayoutData(gridData);


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

        // Search button
        Button button = new Button(parent, SWT.BORDER);
        gridData = new GridData();
        button.setLayoutData(gridData);

        button.setText("Search");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                handleTermFilter(text);
            }

        });

        // Clear button
        button = new Button(parent, SWT.BORDER);
        gridData = new GridData();
        button.setLayoutData(gridData);

        button.setText("Clear");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                handleTermFilterClear(text);
            }

        });


        Composite labelRow = new Composite(parent, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 3;
        labelRow.setLayoutData(gridData);
        labelRow.setLayout(new RowLayout());

        // ProjectLabel indicates the currently selected project
        projectLabel = new Label(labelRow, SWT.NONE);
        if(this.currentProject != null) projectLabel.setText("[" + currentProject.getName() + "]");
        else projectLabel.setText("[No project selected]");

        // Filter label indicates whether a filter is active or not
        filterLabel = new Label(labelRow, SWT.NONE);

        // Label indicates the search results (number of defects)
        label = new Label(labelRow, SWT.NONE);
        label.setText("Number of Defects:");
    }

    /**
     * 
     * @param viewer
     * @param title
     * @param bound
     * @param colNumber
     * @param enableSorting
     * @return
     */
    private TreeViewerColumn createTableViewerColumn(TreeViewer viewer, final String title, final int bound, final int colNumber, boolean enableSorting)
    {
        TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
        final TreeColumn column = viewerColumn.getColumn();
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
    private SelectionAdapter getSelectionAdapter(final TreeViewer viewer2, final TreeColumn column, final int index)
    {
        SelectionAdapter selectionAdaptor = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
              ViewerComparator comparator = viewer2.getComparator();
              if (!(comparator instanceof FindingViewColumnComparator)) {
                comparator = new FindingViewColumnComparator();
                viewer2.setComparator(comparator);
              }
              ((FindingViewColumnComparator)comparator).setColumn(index);
              int dir = ((FindingViewColumnComparator)comparator).getDirection();
              viewer2.getTree().setSortDirection(dir);
              viewer2.getTree().setSortColumn(column);
              viewer2.refresh();
            }
        };
        return selectionAdaptor;

    }

    /**
     * 
     */
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                FindingView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        //		getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * 
     */
    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * 
     * @param manager
     */
    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(filterAction);
        manager.add(defaultSortAction);
    }

    /**
     * 
     * @param manager
     */
    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(notAWeaknessAction);
        manager.add(isAWeaknessAction);
        manager.add(unciteWeaknessAction);
        // Remove the setTrustLevel since it should be set by the Adaptor Configuration preferences
        //manager.add(setTrustLevelAction);
        manager.add(traceAction);
        manager.add(moreInformationAction);
    }

    /**
     * 
     * @param manager
     */
    private void fillLocalToolBar(IToolBarManager manager)
    {
        manager.add(defaultSortActionButton);
        manager.add(descriptionAction);
        manager.add(exportAction);
        manager.add(coverageAction);
    }

    /**
     * 
     */
    private void makeActions()
    {
        final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();

        // Export Action
        defaultSortActionButton = new DefaultSortAction(this, viewer);
        defaultSortActionButton.setText("Default sort");
        defaultSortActionButton.setToolTipText("Default sort");
        defaultSortActionButton.setImageDescriptor(imgReg.getDescriptor(SORT_KEY));

        // Export Action
        descriptionAction = new Action()
        {
            public void run()
            {
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        IWorkbenchPage page = window.getActivePage();
                        try
                        {
                          page.showView(DefectDescriptionView.ID);
                        }
                        catch (PartInitException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };
        descriptionAction.setText("Defect Description");
        descriptionAction.setToolTipText("Defect Description");
        descriptionAction.setImageDescriptor(imgReg.getDescriptor(DESCRIPTION_KEY));

        // Export Action
        exportAction = new Action()
        {
            public void run()
            {
                FileDialog dialog = new FileDialog(viewer.getControl().getShell(), SWT.SAVE);
                dialog.setFilterExtensions(new String [] {"*.tsv"});
                String result = dialog.open();
                File file = new File(result);
                try
                {
                    if(selection.isEmpty())
                    {
                        MessageDialog.openInformation(viewer.getControl().getShell(), "No selection for export", "Findings must be selected within the view for export.");
                    }
                    else
                    {
                        selection.exportTsv(file);
                    }
                }
                catch (IOException e)
                {
                    // build the error message and include the current stack trace
                    MultiStatus status = createMultiStatus(e.getLocalizedMessage(), e);
                    // show error dialog
                    ErrorDialog.openError(viewer.getControl().getShell(), "Error", "This is an error", status);
                }
            }
        };
        exportAction.setText("Export Selection");
        exportAction.setToolTipText("Export Selection");
        exportAction.setImageDescriptor(imgReg.getDescriptor(EXPORT_KEY));

        // Coverage Action
        coverageAction = new Action()
        {
            public void run()
            {
                FileDialog dialog = new FileDialog(viewer.getControl().getShell(), SWT.SAVE);
                dialog.setFilterNames(new String[] { "XML Files", "All Files (*.*)" });
                dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
                dialog.setFilterPath(System.getProperty("user.dir"));
                dialog.setFileName("toif_Coverage.xml");
                final String savePath = dialog.open();

                if (savePath != null)
                {
                    try
                    {
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
                                // Make and run the coverage generator on the workspace for now. We probably want to
                                // restrict to the active project?
                                FindingContentProvider contents = (FindingContentProvider)viewer.getContentProvider();
                                FindingEntry[] findings = contents.getFindingEntries();
                                new CoverageClaimGenerator(findings, new File(savePath), false);
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
                }
            }
        };
        coverageAction.setText("Export Coverage");
        coverageAction.setToolTipText("Export Coverage");
        coverageAction.setImageDescriptor(imgReg.getDescriptor(COVERAGE_KEY));

        // Filter Action
        filterAction = new FilterAction(this, viewer);
        filterAction.setText("Filters...");
        filterAction.setImageDescriptor(imgReg.getDescriptor(FILTER_KEY));

        defaultSortAction = new DefaultSortAction(this, viewer);
        defaultSortAction.setText("Default sort");
        defaultSortAction.setImageDescriptor(imgReg.getDescriptor(SORT_KEY));

        // Not a Weakness
        notAWeaknessAction = new Action()
        {
            public void run()
            {
                selection.cite(false);
                backupCitings();
            }
        };
        notAWeaknessAction.setText("Not a Weakness");
        notAWeaknessAction.setImageDescriptor(imgReg.getDescriptor(NOT_WEAKNESS_KEY));


        // Not a Weakness
        isAWeaknessAction = new Action()
        {
            public void run()
            {
                selection.cite(true);
                backupCitings();
            }
        };
        isAWeaknessAction.setText("Is a Weakness");
        isAWeaknessAction.setImageDescriptor(imgReg.getDescriptor(IS_WEAKNESS_KEY));


        // Uncite Weakness
        unciteWeaknessAction = new Action()
        {
            public void run()
            {
                selection.cite(null);
                backupCitings();
            }
        };
        unciteWeaknessAction.setText("Uncite Weakness");
        unciteWeaknessAction.setImageDescriptor(imgReg.getDescriptor(UNCITE_WEAKNESS_KEY));


        // Set trust level
        setTrustLevelAction = new Action()
        {
            public void run()
            {
                InputDialog dialog = new InputDialog(
                        viewer.getControl().getShell(),
                        "Trust Level",
                        "Set the trust level",
                        Integer.toString(selection.getTrust()),
                        new IntegerInputValidator());

                int code = dialog.open();
                if(code == InputDialog.OK)
                {
                    String value = dialog.getValue();

                    // Don't need try/catch. If we get here the value MUST
                    // be an integer.
                    int val = Integer.parseInt(value);
                    selection.setTrust(val);
                }
                backupCitings();
            }
        };
        setTrustLevelAction.setText("Set Trust Level");
        setTrustLevelAction.setImageDescriptor(imgReg.getDescriptor(SET_TRUST_KEY));


        // Set trust level
        traceAction = new Action()
        {
            public void run()
            {
                showMessage("Trace executed");
            }
        };
        traceAction.setText("Trace");
        traceAction.setImageDescriptor(imgReg.getDescriptor(TRACE_KEY));
        // Disable trace since no tools support it yet.
        traceAction.setEnabled(false);


        // Set trust level
        moreInformationAction = new Action()
        {
            public void run()
            {
                selection.moreInfo();
            }
        };
        moreInformationAction.setText("More Information");
        moreInformationAction.setImageDescriptor(imgReg.getDescriptor(MORE_INFO_KEY));




        // Double click action
        doubleClickAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                IFindingEntry finding = (IFindingEntry)((IStructuredSelection)selection).getFirstElement();
                IFile file = finding.getFile();

                if(!file.exists() || file.getProjectRelativePath().toString().startsWith(".KDM/TOIF"))
                {
                    MessageDialog.openError(viewer.getControl().getShell(), "Cannot open file", "The file '" + file.getName() + "' is not in the project.");
                }
                else
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    try
                    {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put(IMarker.LINE_NUMBER, finding.getLineNumber());
                        IMarker marker = file.createMarker(IMarker.TEXT);
                        marker.setAttributes(map);
                        IDE.openEditor(page, marker); //3.0 API
                        marker.delete();
                    }
                    catch(CoreException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /** If this is the first citing on a project. If it is then save a backup
     * of the current settings.
     *
     * Also saves if the citing is on a different day.
     */
    protected void backupCitings()
    {
        if(currentProject == null) return;

        try
        {
            Date date = (Date) currentProject.getSessionProperty(PROJECT_CITE_DATE);
            Date now = new Date();
            if(saveFile == null || date == null || !isSameDay(date, now))
            {
                IFolder kdm = currentProject.getFolder(".KDM");
                IFolder toif = kdm.getFolder("TOIF");
                IFolder history = toif.getFolder("history");
                if(!history.exists()) history.create(true, true, null);
                String name = currentProject.getName() + "." + isoDateFormat.format(now) + ".tsv";
                saveFile = history.getFile(name);
            }

            // Save the findings
            FindingContentProvider contents = (FindingContentProvider)viewer.getContentProvider();
            FindingEntry[] findings = contents.getFindingEntries();
            FindingSelection selection = new FindingSelection(findings, true);

            selection.exportTsv(saveFile.getLocation().toFile());
            currentProject.setSessionProperty(PROJECT_CITE_DATE, now);
        }
        catch (CoreException | IOException e)
        {
            e.printStackTrace();
        }

    }

    /** Return true if these dates are on the same day
     * 
     * @param date1
     * @param date2
     * @return
     */
    private boolean isSameDay(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    private void hookDoubleClickAction()
    {
        viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message)
    {
        MessageDialog.openInformation(
                viewer.getControl().getShell(),
                "Finding View",
                message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }

    /**
     * deal with the term filter. apply the term filter data to the current
     * view.
     * 
     * From TOIF ReportView
     * 
     * @param text
     */
    private void handleTermFilter(Text text)
    {
        String[] terms = text.getText().split("OR");
        if (text.getText().isEmpty())
        {
            handleTermFilterClear(text);
        }
        else
        {
            FilterUtility filter = new FilterUtility(this, viewer);
            filter.add(new TermFilter(terms));
            filter.applyFilters();
            updateDefectCount();
            text.setMessage("Search for Everything...");
        }
    }

    /** Clear the term filter
     * 
     * @param text
     */
    private void handleTermFilterClear(Text text)
    {
        FilterUtility filter = new FilterUtility(this, viewer);
        filter.clear();
        filter.applyFilters();

        updateDefectCount();
        text.setMessage("Search for Everything...");
        text.setText("");
    }

    /** From http://www.vogella.com/tutorials/EclipseDialogs/article.html#dialogs_jface_errordialog
     * 
     * @param msg
     * @param t
     * @return
     */
    private static MultiStatus createMultiStatus(String msg, Throwable t) {

        List<Status> childStatuses = new ArrayList<>();	
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTrace: stackTraces)
        {
            Status status = new Status(IStatus.ERROR, "com.kdmanalytics.toif", stackTrace.toString());
            childStatuses.add(status);
        }

        MultiStatus ms = new MultiStatus("com.kdmanalytics.toif",
                IStatus.ERROR, childStatuses.toArray(new Status[] {}),
                t.toString(), t);
        return ms;
    }

    /** Set the text for the label.
     * 
     * @param text
     */
    public void setLabel(final String text)
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run()
            {
                // Setup the project label with the project name
                if(currentProject != null && currentProject.isOpen())
                {
                    projectLabel.setText("[" + currentProject.getName() + "]");
                }
                else
                {
                    projectLabel.setText("[No project selected]");
                }

                // Indicate if there are any filters active
                if(viewer.getFilters() == null || viewer.getFilters().length == 0)
                {
                    filterLabel.setText("");
                }
                else
                {
                    filterLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
                    filterLabel.setText("(Filter(s) active)");
                }

                // Write the count label
                String useText = text;
                if(currentProject != null && currentProject.isOpen())
                {
                    try
                    {
                        if(currentProject.getPersistentProperty(com.kdmanalytics.toif.ui.common.Activator.PROJECT_INCONSISTENT) != null)
                        {
                            useText = "[Inconsistent] " + text;
                            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
                        }
                        else
                        {
                            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                        }
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }

                label.setText(useText);

                // Layout the labels cleanly
                label.getParent().layout();
            }
        });
    }

    /**
     * update the defect counter. this calculates what to display in the status
     * text.
     */
    public void updateDefectCount()
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run()
            {
                final int totalEntries = contentProvider.getEntries().length;
                final int visibleEntries = viewer.getTree().getItemCount();

                setLabel("Number of Defects: " + visibleEntries + " (" + (totalEntries - visibleEntries) + " filtered from view)");
            }
        });
    }

    /**
     * Citing data or other information may have changed, refresh the view
     */
    public void refresh()
    {
        this.viewer.refresh();
    }

    /** Add a new filter to the FindingView
     * 
     * @param resources
     */
    public void setResourceFilter(List<IResource> resources)
    {
        ViewerFilter[] filters = viewer.getFilters();
        List<ViewerFilter> keep = new LinkedList<ViewerFilter>();
        for (ViewerFilter filter : filters)
        {
            if(!(filter instanceof ResourceFilter))
            {
                keep.add(filter);
            }
        }
        viewer.setFilters(keep.toArray(new ViewerFilter[keep.size()]));

        if(resources == null) return;
        if(resources.isEmpty()) return;
        Set<IFile> selectedFiles = new HashSet<IFile>();
        for(IResource resource: resources)
        {
            if(resource instanceof IProject) return;
            addFiles(selectedFiles, resource);
        }
        
        FilterUtility filter = new FilterUtility(this, viewer);
        filter.add(new ResourceFilter(selectedFiles));
        filter.applyFilters();

        updateDefectCount();
    }

    /** Add all files recursively to the provided filter set
     * 
     * @param selectedFiles
     * @param resource
     */
    private void addFiles(Set<IFile> selectedFiles, IResource resource)
    {
        if(resource != null)
        {
            if(resource instanceof IFile)
            {
                selectedFiles.add((IFile)resource);
            }
            else if(resource instanceof IContainer)
            {
                try
                {
                    IResource[] children = ((IContainer)resource).members();
                    if(children != null)
                    {
                        for (IResource child : children)
                        {
                            addFiles(selectedFiles, child);
                        }
                    }
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
