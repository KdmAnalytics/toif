/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.nature;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import com.kdmanalytics.toif.ui.common.ToifUtilities;

/**
 * Common utilities for managing natures and builders.
 * 
 * @author Ken Duck
 *        
 */
public class ToifImportNatureUtil {
  
  /**
   * Builders register with the nature to allow themselves to be added when the nature is enabled.
   */
  private static Set<String> availableBuilders = new HashSet<String>();
  
  /**
   * Register a builder with the nature
   * 
   * @param id
   */
  public static void registerBuilder(String id) {
    availableBuilders.add(id);
  }
  
  /**
   * Get builders that are valid for this project
   * 
   * @param project
   * @return
   */
  public static Set<String> getAvailableBuilders(IProject project) {
    return availableBuilders;
  }
  
  /**
   * Get all possible builders, even those that are not valid for this project
   * 
   * @return
   */
  public static Set<String> getAllAvailableBuilders() {
    return availableBuilders;
  }
  
  /**
   * Toggles TOIF nature on a project
   *
   * @param project
   *          to have sample nature added or removed
   * @throws CoreException
   */
  public static void toggleImportNature(IProject project) throws CoreException {
    IProjectDescription description = project.getDescription();
    String[] natures = description.getNatureIds();
    
    for (int i = 0; i < natures.length; ++i) {
      if (ToifImportNature.NATURE_ID.equals(natures[i])) {
        // KLUDGE: Clear markers. We want to do this now to ensure the view gets properly notified.
        ToifUtilities.clearEToifMarkers(project);
        
        // Remove the nature
        String[] newNatures = new String[natures.length - 1];
        System.arraycopy(natures, 0, newNatures, 0, i);
        System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
        description.setNatureIds(newNatures);
        project.setDescription(description, null);
        
        // The project can no longer be inconsistent since it is not an import nature anymore.
        project.setPersistentProperty(com.kdmanalytics.toif.ui.common.Activator.PROJECT_INCONSISTENT, null);
        return;
      }
    }
    
    // Add the nature
    String[] newNatures = new String[natures.length + 1];
    System.arraycopy(natures, 0, newNatures, 0, natures.length);
    newNatures[natures.length] = ToifImportNature.NATURE_ID;
    description.setNatureIds(newNatures);
    project.setDescription(description, null);
  }
  
  /**
   * Enable the import nature
   * 
   * @param project
   * @throws CoreException
   */
  public static void enableImportNature(IProject project) throws CoreException {
    if (!project.hasNature(ToifImportNature.NATURE_ID)) {
      toggleImportNature(project);
    }
  }
  
  /**
   * Disable the import nature
   * 
   * @param project
   * @throws CoreException
   */
  public static void disableImportNature(IProject project) throws CoreException {
    if (project.hasNature(ToifImportNature.NATURE_ID)) {
      toggleImportNature(project);
    }
  }
  
}
