/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////
 * ////////////////////////////////////////////////////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Stream gobbler example to keep the output of the tool flowing.
 * 
 * @author Adam Nunn
 */
public class StreamGobbler implements Runnable
{
    
    private BufferedReader is;
    
    private OutputStream os;
    
    /**
     * stream gobbler constructor takes an input stream
     * 
     * @param is
     */
    StreamGobbler(InputStream is)
    {
        this(is, null);
    }
    
    /**
     * takes input and output stream.
     * 
     * @param is
     * @param redirect
     */
    public StreamGobbler(InputStream is, OutputStream redirect)
    {
        this.is = new BufferedReader(new InputStreamReader(is));
        this.os = redirect;
    }
    
    /**
     * run the gobbler.
     */
    public void run()
    {
        try
        {
            PrintWriter pw = null;
            if (os != null)
            {
                pw = new PrintWriter(os);
            }
            
            String line = is.readLine();
            while (line != null)
            {
                if (pw != null)
                {
                    pw.println(line);
                    
                }
                line = is.readLine();

            }
            if (pw != null)
            {
                pw.flush();
            }
            
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }
}
