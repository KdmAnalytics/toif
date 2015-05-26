/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

/**
 * @Author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class TrustSelectionListener implements SelectionListener
{
    
    private Text trustAmount;
    
    public TrustSelectionListener(Text trustAmount)
    {
        this.trustAmount = trustAmount;
        
    }
    
    protected final String getTrustText()
    {
        return trustAmount.getText();
    }
    
    /**
     * Sets the trustAmount
     * 
     * @param trustAmount
     *            The Text Widget for the trust.
     */
    public void setTrustAmount(Text trustAmount)
    {
        this.trustAmount = trustAmount;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
     * .swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
     * .events.SelectionEvent)
     */
    @Override
    public void widgetSelected(SelectionEvent e)
    {
        
    }
    
}
