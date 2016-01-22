/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.items;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.items.IFileGroup;
import com.kdmanalytics.toif.report.items.IToifProject;
import com.kdmanalytics.toif.report.util.IRepositoryMaker;

/**
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 *         
 */
public class Project extends ReportItem implements IToifProject {
  
  /**
   * 
   */
  private static final long serialVersionUID = -1786648025858789753L;
  
  private transient IProject iProject;
  
  /**
   * the internal repository for the kdm data.
   */
  private transient Repository repository;
  
  /**
   * the connection to the internal repository
   */
  private transient RepositoryConnection con;
  
  private List<FileGroup> files;
  
  private boolean inWorkbench;
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getIProject()
   */
  @Override
  public IProject getIProject() {
    return iProject;
  }
  
  public Project(final IFolder kdmFolder, boolean workbenchPresent) {
    files = new ArrayList<FileGroup>();
    IFolder kdmRepoFolder = ensureExists(kdmFolder);
    setRepository(kdmRepoFolder);
    iProject = kdmRepoFolder.getProject();
    inWorkbench = workbenchPresent;
  }
  
  public Project(final IFolder kdmFolder, Repository myRepository, boolean workbenchPresent) {
    files = new ArrayList<FileGroup>();
    IFolder kdmRepoFolder = ensureExists(kdmFolder);
    repository = myRepository;
    try {
      con = repository.getConnection();
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    iProject = kdmRepoFolder.getProject();
    inWorkbench = workbenchPresent;
  }
  
  public void setIProject(IProject iProject) {
    this.iProject = iProject;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getRepository()
   */
  @Override
  public Repository getRepository() {
    return repository;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject# getRepositoryConnection()
   */
  @Override
  public RepositoryConnection getRepositoryConnection() {
    return con;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getValueFactory ()
   */
  @Override
  public ValueFactory getValueFactory() {
    
    ValueFactory factory = repository.getValueFactory();
    return factory;
  }
  
  /**
   * 
   * @param folder
   */
  public void setRepository(IFolder folder) {
    if (folder == null) {
      return;
    }
    
    IRepositoryMaker repMaker = getRepositoryMaker(folder);
    
    // if (repMaker == null)
    // {
    // repository = new SailRepository(new NativeStore(new
    // File(folder.getRawLocationURI())));
    // }
    // else
    // {
    
    repository = repMaker.getRepository();
    // }
    
    if (repository == null) {
      return;
    }
    try {
      repository.initialize();
      con = repository.getConnection();
      
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * @param folder
   * @return
   */
  private IRepositoryMaker getRepositoryMaker(IFolder folder) {
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                                                                                                 "com.kdmanalytics.toif.report.repositoryMaker.extensionpoint");
    try {
      for (IConfigurationElement e : config) {
        System.out.println("Evaluating extension");
        
        final Object o = e.createExecutableExtension("class");
        
        if (o instanceof IRepositoryMaker) {
          IRepositoryMaker repositoryMaker = (IRepositoryMaker) o;
          repositoryMaker.setFolder(folder);
          repositoryMaker.createRepository();
          return repositoryMaker;
        }
      }
    } catch (CoreException e) {
      System.err.println("There was a core excption... " + e);
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#dispose()
   */
  @Override
  public void dispose() {
    try {
      con.close();
      if (!inWorkbench) {
        repository.shutDown();
      }
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
    
    }
    
  }
  
  /**
   * @param kdmFolder
   * @return
   */
  private IFolder ensureExists(final IFolder kdmFolder) {
    if (!kdmFolder.exists()) {
      try {
        kdmFolder.create(true, true, null);
      } catch (CoreException e1) {
        System.err.println("Folder was not created!!!");
        e1.printStackTrace();
      }
    }
    return kdmFolder;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getFileGroup()
   */
  @Override
  public List<FileGroup> getFileGroup() {
    return files;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#AddFileGroup
   * (com.kdmanalytics.toif.report.internal.items.FileGroup)
   */
  @Override
  public void AddFileGroup(FileGroup file) {
    if (!files.contains(file)) {
      files.add(file);
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject# getFindingEntriesNotOk()
   */
  @Override
  public List<FindingEntry> getFindingEntriesNotOk() {
    List<FindingEntry> results = new ArrayList<FindingEntry>();
    
    for (IFileGroup file : files) {
      for (FindingEntry entry : file.getFindingEntries()) {
        if (Citing.FALSE == entry.isOk()) {
          results.add(entry);
        }
      }
      
    }
    return results;
  }
  
  @Override
  public String toString() {
    return "The Project";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getFindingEntries ()
   */
  @Override
  public List<FindingEntry> getFindingEntries() {
    List<FindingEntry> entries = new ArrayList<FindingEntry>();
    FileGroup.locationCount = 0;
    for (IFileGroup file : files) {
      List<FindingEntry> list = file.getFindingEntries();
      entries.addAll(list);
    }
    return entries;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kdmanalytics.toif.report.internal.items.IToifProject#getChildren()
   */
  @Override
  public List<ReportItem> getChildren() {
    List<ReportItem> result = new ArrayList<ReportItem>();
    
    for (ReportItem reportItem : files) {
      if (!reportItem.getChildren().isEmpty()) {
        result.add(reportItem);
      }
    }
    // result.addAll(files);
    return result;
  }
  
  /**
   * prints the contents of the repository. mainly for debug purposes.
   * 
   * @param repository
   *          the repository to print.
   */
  void printDB(Repository repository) {
    // RepositoryConnection con;
    
    try {
      // con = repository.getConnection();
      // get all statements.
      final RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
      
      // for all the statements.
      while (statements.hasNext()) {
        final Statement st = statements.next();
        // print statements.
        System.out.println(st.toString());
        
      }
      
      statements.close();
    } catch (final RepositoryException e) {
      // LOG.error("There was a repository error while printing the database. "
      // + e);
    }
    
  }
  
}
