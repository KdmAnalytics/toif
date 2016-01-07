/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.framework.files;

import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * Given an input path, returns the absolute path to the file. This is used to
 * resolve the file paths provided by the various tools. For example, FindBugs
 * returns the file path starting from the top of the sourcePath (package +
 * filename).
 * 
 * @author Ken Duck
 *        
 */
public interface IFileResolver
{
    
    /**
     * Return the resolved absolute path to a file, if possible
     * 
     * @param file
     * @return
     */
    public File resolve(File file);
    
    /**
     * Return a default path if one is available for this resolver. Only really
     * applicable to the ExplicitFileResolver and is being used until all
     * adaptors have been updated to get file names from the tool output.
     * 
     * @return
     */
    public File getDefaultFile();
}
