/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Builds the defect model.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class BuildDefectModelHandler extends AbstractHandler implements IHandler
{
    
    /**
     * initiate the building of the defect model.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection s = HandlerUtil.getCurrentSelection(event);
        if (s != null)
        {
            ModelUtil.buildModel(s);
        }
        return null;
    }
    
}
