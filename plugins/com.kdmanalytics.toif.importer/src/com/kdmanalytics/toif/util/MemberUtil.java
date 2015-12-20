/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.kdmanalytics.toif.report.items.IFileGroup;

/**
 * member utilities.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class MemberUtil
{
    
    private MemberUtil()
    {
        
    }
    
    /**
     * find suitable resources
     * 
     * @param member
     * @param file
     * @return
     * @throws CoreException
     */
    public static IResource findMembers(IResource member, IFileGroup file) throws CoreException
    {
        String name = file.getName();
        
        String path = file.getPath();
        path = path.replace("\\", "/");
        
        String[] pathArray = path.split("/");
        ArrayUtils.reverse(pathArray);
        
        List<IResource> result = new ArrayList<IResource>();
        List<IResource> toDo = new ArrayList<IResource>();
        
        toDo.add(member);
        
        while (!toDo.isEmpty())
        {
            IResource resource = toDo.remove(0);
            
            if (resource instanceof IContainer)
            {
                for (IResource iResource : ((IContainer) resource).members())
                {
                    toDo.add(iResource);
                }
            }
            
            result.add(resource);
            
        }
        
        ResourceMatch match = new ResourceMatch();
        
        for (IResource iResource : result)
        {
            if (iResource == null)
            {
                continue;
            }
            if (iResource.getName().equals(name))
            {
                IPath ipath = iResource.getLocation();
                
                String stringPath = ipath.toString();
                stringPath = stringPath.replace("\\", "/");
                
                String[] resourcePathArray = stringPath.split("/");
                ArrayUtils.reverse(resourcePathArray);
                
                int smallestSize = (pathArray.length < resourcePathArray.length) ? pathArray.length : resourcePathArray.length;
                for (int i = 0; i < smallestSize; i++)
                {
                    if (pathArray[i].equals(resourcePathArray[i]))
                    {
                        if (i >= match.getScore())
                        {
                            match = new ResourceMatch(iResource, i);
                        }
                    }
                    else
                    {
                        break;
                    }
                    
                }
            }
        }
        
        return match.getIResource();
    }
    
}
