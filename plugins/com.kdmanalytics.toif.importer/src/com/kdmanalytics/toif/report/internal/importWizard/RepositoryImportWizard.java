/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.importWizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Import a TOIF file into the internal defect database.
 * 
 * @author Ken Duck
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class RepositoryImportWizard extends Wizard implements IImportWizard
{
    
    RepositoryImportWizardPage mainPage;
    
    public RepositoryImportWizard()
    {
        super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        return mainPage.finish();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        setWindowTitle("File Import Wizard"); // NON-NLS-1
        setNeedsProgressMonitor(true);
        mainPage = new RepositoryImportWizardPage("Import TOIF Data", selection); // NON-NLS-1
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages()
    {
        super.addPages();
        addPage(mainPage);
    }
    
}
