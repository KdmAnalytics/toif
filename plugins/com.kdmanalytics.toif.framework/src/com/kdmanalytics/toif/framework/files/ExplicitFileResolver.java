/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.framework.files;

import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * A file resolver that is explicitly resolving to a specific file.
 * 
 * @author Ken Duck
 *        
 */
public class ExplicitFileResolver implements IFileResolver
{
    
    private File file;
    
    public ExplicitFileResolver(com.kdmanalytics.toif.framework.xmlElements.entities.File file)
    {
        this.file = file;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.files.IFileResolver#resolve(com.
     * kdmanalytics.toif.framework.xmlElements.entities.File)
     */
    @Override
    public File resolve(File file)
    {
        return this.file;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.kdmanalytics.toif.framework.files.IFileResolver#getDefaultFile()
     */
    @Override
    public com.kdmanalytics.toif.framework.xmlElements.entities.File getDefaultFile()
    {
        return file;
    }
    
}
