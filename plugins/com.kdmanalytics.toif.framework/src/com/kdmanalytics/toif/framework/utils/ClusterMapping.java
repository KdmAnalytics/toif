/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.utils;

/**
 * Mapping clusters to sfps.
 * 
 * @author adam
 *         
 */
public class ClusterMapping
{
    
    /**
     * Given a SFP, return the cluster it belongs to.
     * 
     * @param sfp
     * @return
     */
    static String getCluster(String sfp)
    {
        if (sfp == null)
        {
            return "No Cluster Found!";
        }
        
        if (!sfp.startsWith("SFP-"))
        {
            return "No Cluster Found!";
        }
        
        // get the int from the SFP. Java doesn't like strings for switches.
        int sfpId = 0;
        try
        {
            sfpId = Integer.parseInt(sfp.substring(4));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            System.err.println("No SFP found. Config file may be wrong");
        }
        
        switch (sfpId)
        {
            case -1:
            {
                return "Observation";
            }
            case 1:
            {
                return "Risky Values";
            }
            case 2:
            {
                return "Entity Points";
            }
            case 3:
            {
                return "API";
            }
            case 4:
            case 5:
            case 6:
            {
                return "Exception Management";
            }
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            {
                return "Memory Access";
            }
            case 12:
            {
                return "Memory Management";
            }
            case 13:
            case 14:
            case 15:
            {
                return "Resource Management";
            }
            case 16:
            case 17:
            case 18:
            {
                return "Path Resolution";
            }
            case 19:
            case 20:
            case 21:
            case 22:
            {
                return "Synchronization";
            }
            case 23:
            {
                return "Information Leak";
            }
            case 24:
            case 25:
            case 26:
            case 27:
            {
                return "Tainted Input";
            }
            case 28:
            {
                return "Entry Points";
            }
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            {
                return "Authentication ";
            }
            case 35:
            {
                return "Access Control";
            }
            case 36:
            {
                return "Privilege";
            }
            
            default:
                return "No Cluster Found!";
        }
        
    }
}
