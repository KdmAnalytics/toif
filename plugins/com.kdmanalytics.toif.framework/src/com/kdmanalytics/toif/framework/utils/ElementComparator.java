/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.utils;

import java.util.Comparator;

import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.Entity;

/**
 * Custom comparator to compare and hense sort the elements.
 * 
 * @author Adam Nunn
 *         
 */
public class ElementComparator implements Comparator<Element>
{
    
    /**
     * the compare method should put entities ahead of the facts. Facts should
     * always be after the entities which they reference. The easiest way to do
     * this is to just put all the facts at the end of the file.
     */
    @Override
    public int compare(Element o1, Element o2)
    {
        if (o1 instanceof Entity && o2 instanceof Entity)
        {
            return 0;
        }
        else if (o1 instanceof Entity)
        {
            return -1;
        }
        else if (o2 instanceof Entity)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    
}
