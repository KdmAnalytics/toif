/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.items;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import com.kdmanalytics.toif.report.internal.items.FileGroup;
import com.kdmanalytics.toif.report.internal.items.FindingEntry;
import com.kdmanalytics.toif.report.internal.items.ReportItem;

/**
 * Interface for the project. projects contain all the files (first level
 * children), locations, tools, and findings.
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * 
 */
public interface IToifProject extends IReportItem
{
    
    /**
     * get the project
     * 
     * @return
     */
    IProject getIProject();
    
    
    void setIProject(IProject iProject);
    
    /**
     * get the repository this project is in
     * 
     * @return
     */
    Repository getRepository();
    
    
    void setRepository(IFolder ifolder);
    
    /**
     * get the repository's connection
     * 
     * @return
     */
    RepositoryConnection getRepositoryConnection();
    
    /**
     * get the value factory for the repository
     * 
     * @return
     */
    ValueFactory getValueFactory();
    
    void dispose();
    
    /**
     * get the file groups in this project.
     * 
     * @return
     */
    List<FileGroup> getFileGroup();
    
    /**
     * add a file group to this project
     * 
     * @param file
     */
    void AddFileGroup(FileGroup file);
    
    /**
     * get all the finding entries that are not ok
     * 
     * @return
     */
    List<FindingEntry> getFindingEntriesNotOk();
    
    /**
     * get all the finding entries in this project
     * 
     * @return
     */
    List<FindingEntry> getFindingEntries();
    
    List<ReportItem> getChildren();
    
}
