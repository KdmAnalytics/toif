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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.kdmanalytics.toif.ui.common.FindingEntry;
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

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
class FindingContentProvider implements ITreeContentProvider
{
    /**
     * A map of entries to files. This makes it easier for us to update
     * entries on a per-file basis.
     */
    private Map<IFile,List<IFindingEntry>> findings = new HashMap<IFile,List<IFindingEntry>>();

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
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object parent)
    {
      return getChildren(parent);
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
    public IFindingEntry[] getEntries()
    {
        List<IFindingEntry> results = new LinkedList<IFindingEntry>();

        // Force update if the findings are currently empty
        if(findings.isEmpty() && currentProject != null)
        {
            updateFindings(currentProject);
        }

        for(List<IFindingEntry> list: findings.values())
        {
            for (IFindingEntry entry : list)
            {
                results.add(entry);
            }
        }

        return results.toArray(new IFindingEntry[results.size()]);
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
            findings.put(file, new LinkedList<IFindingEntry>());
        }
        List<IFindingEntry> list = findings.get(file);
        
		boolean grouped = false;
		for (Iterator<IFindingEntry> it = list.iterator(); it.hasNext();)
			{
			IFindingEntry fe = it.next();
			if (canGroup(fe, entry))
				{
				grouped = true;
				if (fe instanceof FindingGroup)
					{
					((FindingGroup) fe).add(entry);
					}
				else
					{
					it.remove();
					// BT-1158: GUI logic will replace this values as required since
					// text value is dynamic. For example CWE * or CWE-562
					
					FindingGroup group = new FindingGroup(fe.getFile(),
							fe.getLineNumber(), "SFP-000", "CWE-000");
					
					group.add((FindingEntry) fe);
					group.add(entry);
					list.add(0, group);
					}
				break;
				}
			}
		if (!grouped)
			{
			list.add(entry);
			}
    }

    /**
     * 
     * @param e1
     * @param e2
     * @return
     */
    private boolean canGroup(IFindingEntry e1, FindingEntry e2) {
      if (!e1.getFile().equals(e2.getFile())) return false;
      if (e1.getLineNumber() != e2.getLineNumber()) return false;
  // RJF fix    if (!e1.getCwe().equals(e2.getCwe())) return false;
      return true;
    }

    /** Update the information for the specified resource. Tell the view about
     * the changes.
     * 
     * @param resource
     */
    public void update(final TreeViewer viewer, final IFile file)
    {
        if(file == null) return;
        if(file.getProject() != this.currentProject) return;

        // Ensure we are running on the UI thread
        Display.getDefault().syncExec(new Runnable() {
            public void run()
            {
                // Get old findings
                List<IFindingEntry> oldFindings = findings.get(file);
                if(oldFindings == null) oldFindings = new LinkedList<IFindingEntry>();
                // Get new findings
                List<FindingEntry> newFindings = updateFindings(file);

                if(!equals(oldFindings, newFindings))
                {
                    findings.remove(file);
                    if(oldFindings != null)
                    {
                        for (IFindingEntry finding : oldFindings)
                        {
                            viewer.remove(finding);
                        }
                    }
                    viewer.refresh();
                    for (FindingEntry finding : newFindings)
                    {
                        addEntry(finding);
                    }

                }
            }

            /** Determine if lists are equal by sorting them and then comparing
             * them.
             * 
             * @param list1
             * @param newFindings
             * @return
             */
            private boolean equals(List<IFindingEntry> list1, List<FindingEntry> newFindings)
            {     
                if (list1 == null && newFindings == null) return true;
                if(list1 == null) return false;
                if(newFindings == null) return false;
                
                List<FindingEntry> c1 = new LinkedList<FindingEntry>();
                List<FindingEntry> c2 = new LinkedList<FindingEntry>();
                
                for (IFindingEntry entry: list1) {
                  if (entry instanceof FindingEntry) {
                    c1.add((FindingEntry)entry);
                  } else {
                    Collection<IFindingEntry> children = ((FindingGroup)entry).getFindingEntries();
                    for (IFindingEntry child : children) {
                      c1.add((FindingEntry)child);
                    }
                  }
                }

                for (IFindingEntry entry: newFindings) {
                  if (entry instanceof FindingEntry) {
                    c2.add((FindingEntry)entry);
                  } else {
                    Collection<IFindingEntry> children = ((FindingGroup)entry).getFindingEntries();
                    for (IFindingEntry child : children) {
                      c2.add((FindingEntry)child);
                    }
                  }
                }
                
                if(c1.size() != c2.size()) return false;

                Collections.sort(c1);
                Collections.sort(c2);      
                return c1.equals(c2);
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

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parent) {
      if (parent instanceof FindingGroup) {
        Object[] children = ((FindingGroup)parent).getFindingEntryArray();
        return children;
      }
      if (parent instanceof IProject) {
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
      return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
      return null;
    }

    @Override
    public boolean hasChildren(Object element) {
      if (element instanceof FindingGroup) {
        return true;
      }
      return false;
    }

    /** Get all of the entries themselves
     * 
     * @return
     */
    public FindingEntry[] getFindingEntries() {
      IFindingEntry[] entries = getEntries();
      List<FindingEntry> results = new LinkedList<FindingEntry>();
      for (IFindingEntry entry : entries) {
        if (entry instanceof FindingEntry) {
          results.add((FindingEntry)entry);
        } else {
          Collection<IFindingEntry> children = ((FindingGroup)entry).getFindingEntries();
          for (IFindingEntry child : children) {
            results.add((FindingEntry)child);
          }
        }
      }
      return results.toArray(new FindingEntry[results.size()]);
    }
}
