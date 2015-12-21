/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.framework.files;

import java.io.File;

/** A file resolver where a directory root is provided and all files
 * resolve as a child of this root. This is not particularly clever
 * at ensuring matches are correct.
 * 
 * @author Ken Duck
 *
 */
public class RootFileResolver implements IFileResolver
{
    private File root;

    public RootFileResolver(String path)
    {
        root = new File(path);
    }

    /*
     * (non-Javadoc)
     * @see com.kdmanalytics.toif.framework.files.IFileResolver#resolve(com.kdmanalytics.toif.framework.xmlElements.entities.File)
     */
    @Override
    public com.kdmanalytics.toif.framework.xmlElements.entities.File resolve(com.kdmanalytics.toif.framework.xmlElements.entities.File file)
    {
        File afile = new File(root, file.getPath());
        return new com.kdmanalytics.toif.framework.xmlElements.entities.File(afile.getAbsolutePath());
    }
    
    /*
     * (non-Javadoc)
     * @see com.kdmanalytics.toif.framework.files.IFileResolver#getDefaultPath()
     */
    @Override
    public com.kdmanalytics.toif.framework.xmlElements.entities.File getDefaultFile()
    {
        throw new UnsupportedOperationException();
    }
    
}
