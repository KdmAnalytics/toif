/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */
package com.kdmanalytics.toif.ui.common.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.kdmanalytics.toif.ui.common.Activator;

/** Root preference page
 * 
 * @author Ken Duck
 *
 */
public class ToifPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("TOIF Preferences");
  }

  @Override
  protected void createFieldEditors() {
    
  }
  
  
}
