/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * Provides content to the view. Beyond working like a simple content provider, this class provides
 * methods for updating a subset of the data, reducing the amount of work required to do a refresh.
 * 
 * The findings are mapped by IFile, which makes it easy to find and update findings on a file by
 * file basis.
 * 
 * @author Ken Duck
 *        
 */
class FindingContentProvider implements IStructuredContentProvider
{
    /**
     * A map of entries to files. This makes it easier for us to update
     * entries on a per-file basis.
     */
    private Map<IFile,List<FindingEntry>> findings = new HashMap<IFile,List<FindingEntry>>();

    /**
     * Project we are currently showing issues for
     */
    private IProject currentProject;

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
        // If a new project is selected, clear the old findings to force a reload.
        if(newInput instanceof IProject)
        {
            if(newInput != currentProject)
            {
                currentProject = (IProject) newInput;
                findings.clear();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object parent)
    {
        // Make sure the findings are only fully instantiated *once*
        if(findings.isEmpty())
        {
            if(currentProject != null && currentProject.isOpen())
            {
                Collection<FindingEntry> findings = updateFindings(currentProject);
                if(findings != null)
                {
                    for (FindingEntry finding : findings)
                    {
                        addEntry(finding);
                    }
                }
            }
        }
        return getEntries();
    }

    /** Update the findings in the findings map for all IFiles found
     * within the specified resource.
     * 
     * Also returns a list of new findings.
     * 
     * @param workspaceRoot
     * @return A list of new findings
     */
    private List<FindingEntry> updateFindings(IResource resource)
    {
        List<FindingEntry> results = new LinkedList<FindingEntry>();
        if(resource.exists())
        {
            if(resource.getProject().isOpen())
            {
                try
                {
                    IMarker[] problems = resource.findMarkers(IMarker.TEXT, true, IResource.DEPTH_INFINITE);

                    for (IMarker marker : problems)
                    {
                        String type = marker.getType();
                        if(type != null && type.startsWith("com.kdmanalytics.toif"))
                        {
                            FindingEntry entry = new FindingEntry(marker);
                            results.add(entry);
                        }
                    }
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    /** Return an array of all of the entries.
     * 
     * @return
     */
    public FindingEntry[] getEntries()
    {
        List<FindingEntry> results = new LinkedList<FindingEntry>();

        // Force update if the findings are currently empty
        if(findings.isEmpty() && currentProject != null)
        {
            updateFindings(currentProject);
        }

        for(List<FindingEntry> list: findings.values())
        {
            for (FindingEntry entry : list)
            {
                results.add(entry);
            }
        }

        return results.toArray(new FindingEntry[results.size()]);
    }

    /** Add an entry to the map
     * 
     * @param entry
     */
    private void addEntry(FindingEntry entry)
    {
        IFile file = entry.getFile();
        if(!findings.containsKey(file))
        {
            findings.put(file, new LinkedList<FindingEntry>());
        }
        List<FindingEntry> list = findings.get(file);
        list.add(entry);
    }

    /** Update the information for the specified resource. Tell the view about
     * the changes.
     * 
     * @param resource
     */
    public void update(final TableViewer viewer, final IFile file)
    {
        if(file == null) return;
        if(file.getProject() != this.currentProject) return;

        // Ensure we are running on the UI thread
        Display.getDefault().syncExec(new Runnable() {
            public void run()
            {
                // Get old findings
                List<FindingEntry> oldFindings = findings.get(file);
                if(oldFindings == null) oldFindings = new LinkedList<FindingEntry>();
                // Get new findings
                List<FindingEntry> newFindings = updateFindings(file);

                if(!equals(oldFindings, newFindings))
                {
                    findings.remove(file);
                    if(oldFindings != null)
                    {
                        for (FindingEntry finding : oldFindings)
                        {
                            viewer.remove(finding);
                        }
                    }
                    for (FindingEntry finding : newFindings)
                    {
                        viewer.add(finding);
                        addEntry(finding);
                    }

                }
            }

            /** Determine if lists are equal by sorting them and then comparing
             * them.
             * 
             * @param list1
             * @param list2
             * @return
             */
            private boolean equals(List<FindingEntry> list1, List<FindingEntry> list2)
            {     
                if (list1 == null && list2 == null) return true;
                if(list1 == null) return false;
                if(list2 == null) return false;
                if(list1.size() != list2.size()) return false;

                Collections.sort(list1);
                Collections.sort(list2);      
                return list1.equals(list2);
            }
        });
    }

    /**
     * Clear the data so we know to reload it.
     */
    public void clear()
    {
        findings.clear();
    }
}
