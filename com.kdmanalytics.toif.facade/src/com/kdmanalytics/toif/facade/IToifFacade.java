
package com.kdmanalytics.toif.facade;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.File;
import java.util.Set;

import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;

public interface IToifFacade
{
    
    /**
     * get all the available adaptors that can be run.
     * 
     * @param lang
     *            the enumerated value of the language that you need the adaptor
     *            to be for.
     * @return the list of adaptors.
     */
    Set<AbstractAdaptor> availableAdapters();
    
    /**
     * get all the available adaptors that can be run.
     * 
     * @param lang
     *            the enumerated value of the language that you need the adaptor
     *            to be for.
     * @return the list of adaptors.
     */
    Set<AbstractAdaptor> availableAdapters(Language lang);

    
    /**
     * Return list of runnable reporting tools.
     * 
     * @return return a list of runnable reporting tools.
     */
    Set<AbstractAdaptor> runnableAdapterReportingTool();
    
    /**
     * returns if the reporting tool for the given adaptor is runnable.
     * 
     * @param adaptor
     *            the adaptor that we want to check has a working reporting
     *            tool.
     * @return true if the reporting tool is runnable.
     */
    boolean isAdapterReportingToolRunnable(AbstractAdaptor adaptor);
    
   
    /**
     * execute the adaptors. these create the toif files.
     * 
     * @param adapter
     *            the adaptor to be run.
     * @param inputFile
     *            the source file on which to run the adaptor.
     * @param houseKeepingFile
     *            the housekeeping file for the adaptor.
     * @param OutputDirectory
     *            the output directory to store the toif file.
     * @param additionalArgs
     *            and additional arguments for the adaptors.
     * 
     * @return true if run.
     * @throws IllegalArgumentException
     * @throws ToifInternalException
     */
    boolean execute(AbstractAdaptor adapter, File inputFile, File houseKeepingFile, File OutputDirectory, File workingDirectory,
            String[] additionalArgs, String rename) 
            		throws IllegalArgumentException, ToifException;
    
    /**
     * execute the adaptors. these create the toif files.
     * 
     * @param adapter
     *            the adaptor to be run.
     * @param inputFile
     *            the source file on which to run the adaptor.
     * @param houseKeepingFile
     *            the housekeeping file for the adaptor.
     * @param OutputDirectory
     *            the output directory to store the toif file.
     * @param additionalArgs
     *            and additional arguments for the adaptors.
     * @param validLines
     *            array for booleans. each index is a different line of the
     *            file. valid or not.
     * 
     * @return true if run.
     * @throws IllegalArgumentException
     * @throws ToifInternalException
     */
    boolean execute(AbstractAdaptor adapter, File inputFile, File houseKeepingFile, File OutputDirectory, File workingDirectory,
            String[] additionalArgs, boolean[] validLines,String rename) throws IllegalArgumentException, ToifException;
    
    
    /**
     * merge the toif and kdm files.
     * 
     * @param kdmOutputFile
     *            the file that we are going to output. Its a zipped kdm file
     *            eg: test.kdm
     * @param kdmFile
     *            the input file. This zipped kdm file has come from the
     *            extractor linker.
     * @param toifFiles
     *            the files that were created by the adaptors.
     * @return true if run.
     * @throws ToifInternalException
     */
    boolean merge(File kdmOutputFile, File kdmFile, Set<File> toifFiles, boolean createZip) throws ToifException;
    
    /**
     * merge the toif and kdm files.
     * 
     * @param kdmOutputFile
     *            the file that we are going to output. Its a zipped kdm file
     *            eg: test.kdm
     * @param kdmFile
     *            the input file. This zipped kdm file has come from the
     *            extractor linker.
     * @param toifFiles
     *            the files that were created by the adaptors.
     * @return true if run.
     * @throws ToifInternalException
     */
    boolean merge(File kdmOutputFile, Set<File> toifFiles, boolean createZip) throws ToifException;
    
    
}
