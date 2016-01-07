/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.nature;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddRemoveToifImportNatureHandler extends AbstractHandler {
  
  public Object execute(ExecutionEvent event) throws ExecutionException {
    // TODO Auto-generated method stub
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    //
    if (selection instanceof IStructuredSelection) {
      for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
        Object element = it.next();
        IProject project = null;
        if (element instanceof IProject) {
          project = (IProject) element;
        } else if (element instanceof IAdaptable) {
          project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
        }
        if (project != null) {
          try {
            ToifImportNatureUtil.toggleImportNature(project);
          } catch (CoreException e) {
            // TODO log something
            throw new ExecutionException("Failed to toggle nature", e);
          }
        }
      }
    }
    
    return null;
  }
  
}
