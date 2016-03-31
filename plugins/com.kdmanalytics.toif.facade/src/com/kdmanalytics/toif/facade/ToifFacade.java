
package com.kdmanalytics.toif.facade;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.kdmanalytics.toif.assimilator.Assimilator;
import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.framework.toolAdaptor.AbstractAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.ToolAdaptorUtil;

public class ToifFacade implements IToifFacade {
  
  /**
   * Empty constructor.
   */
  public ToifFacade() {
  
  }
  
  @Override
  public Set<AbstractAdaptor> availableAdapters() {
    return ToolAdaptorUtil.getAdaptors();
  }
  
  @Override
  public Set<AbstractAdaptor> availableAdapters(Language lang) {
    Set<AbstractAdaptor> aset = new HashSet<>();
    
    for (AbstractAdaptor adaptor : ToolAdaptorUtil.getAdaptors()) {
      if (adaptor.getLanguage() == lang) aset.add(adaptor);
    }
    return aset;
  }
  
  @Override
  public Set<AbstractAdaptor> runnableAdapterReportingTool() {
    Set<AbstractAdaptor> result = new HashSet<>();
    
    for (AbstractAdaptor instance : ToolAdaptorUtil.getAdaptors()) {
      String runtimeName = instance.getRuntoolName();
      
      ProcessBuilder process = null;
      
      // TODO: push the check into the adaptor
      if (runtimeName.equals("findbugs")) {
        // if the system is linux, findbugs can be executed on its
        // own.
        if ("Linux".equals(System.getProperty("os.name"))) {
          process = new ProcessBuilder();
          ArrayList<String> commands = new ArrayList<>();
          commands.add(runtimeName);
          commands.add("-textui");
          process.command(commands);
        } else {
          process = new ProcessBuilder();
          String[] commands = {
                                "cmd.exe", "/C", "findbugs.bat", "-textui"
          };
          process.command(commands);
        }
      } else {
        process = new ProcessBuilder(runtimeName);
      }
      
      try {
        process.start();
        
        result.add(instance);
      } catch (IOException e) {
        System.err.println("The process for " + instance.getAdaptorName() + " could not be started.");
        continue;
      }
    }
    
    return result;
  }
  
  @Override
  public boolean isAdapterReportingToolRunnable(AbstractAdaptor adaptor) {
    try {
      
      String runtimeName = adaptor.getRuntoolName();
      ProcessBuilder process = null;
      
      process = new ProcessBuilder(runtimeName);
      
      process.start();
      
      return true;
      
    } catch (IOException e) {
      System.err.println("The process for " + adaptor.getAdaptorName() + " could not be started.");
      return false;
    }
    
  }
  
  /**
   * executes the adaptor.
   */
  @Override
  public boolean execute(AbstractAdaptor adaptor, File inputFile, File houseKeepingFile, File outputDirectory,
                         File workingDirectory, String[] additionalArgs, String rename) throws IllegalArgumentException,
                             ToifException {
                             
    // Provide exception guard for internal TOIF Exceptions
    try {
      return execute(adaptor, inputFile, houseKeepingFile, outputDirectory, workingDirectory, additionalArgs, null,
                     rename);
    }
    
    // do not map IllegalArgumentException
    catch (IllegalArgumentException iex) {
      throw iex;
    }
    // All other exceptions map to ToifException
    catch (Exception e) {
      throw new ToifException(e);
    }
  }
  
  @Override
  public boolean execute(AbstractAdaptor adaptor, File inputFile, File houseKeepingFile, File OutputDirectory,
                         File workingDirectory, String additionalArgs[], boolean[] validLines, String rename)
                             throws IllegalArgumentException, ToifException {
    checkNotNull(adaptor, "Adaptor must be specified");
    checkNotNull(inputFile, "input File must be specified");
    checkNotNull(houseKeepingFile, "Housekeeping file must be specified");
    checkNotNull(OutputDirectory, "output directory must be specified");
    
    List<String> args = Lists.newArrayList("-i", inputFile.getAbsolutePath(), "-h", houseKeepingFile.getAbsolutePath(),
                                           "-o", OutputDirectory.getAbsolutePath(), "--unknownCWE");
                                           
    if (rename != null) {
      args.add("-n");
      args.add(rename);
    }
    
    if (additionalArgs != null) {
      for (String arg : additionalArgs)
        args.add(arg);
    }
    
    // Provide exception guard for internal TOIF Exceptions
    try {
      ToolAdaptor toolAdaptor = new ToolAdaptor();
      
      return toolAdaptor.runToolAdaptor(adaptor, args, workingDirectory, validLines);
    }
    // do not map IllegalArgumentException
    catch (IllegalArgumentException iex) {
      throw iex;
    }
    
    catch (ToifException te) {
      throw te;
    }
    
    // All other exceptions map to ToifException
    catch (Exception e) {
      throw new ToifException(e);
    }
  }
  
  @Override
  public boolean merge(File kdmOutputFile, Set<File> toifFiles, boolean createZip) throws ToifException {
    // Provide exception guard for internal TOIF Exceptions
    try {
      Assimilator ass = new Assimilator(createZip);
      List<String> args = Lists.newArrayList("-k", kdmOutputFile.getAbsolutePath());
      
      for (File file : toifFiles) {
        args.add(file.getAbsolutePath());
      }
      
      ass.assimilate(args.toArray(new String[args.size()]));
      return true;
    }
    
    catch (Exception e) {
      throw new ToifException(e);
    }
  }
  
  @Override
  public boolean merge(File kdmOutputFile, File kdmFile, Set<File> toifFiles, boolean createZip) throws ToifException {
    // Provide exception guard for internal TOIF Exceptions
    try {
      Assimilator ass = new Assimilator(createZip);
      List<String> args = Lists.newArrayList("-m", kdmOutputFile.getAbsolutePath());
      List<String> kdm = Lists.newArrayList(kdmFile.getAbsolutePath());
      args.addAll(kdm);
      
      for (File file : toifFiles) {
        args.add(file.getAbsolutePath());
      }
      
      ass.assimilate(args.toArray(new String[args.size()]));
      return true;
    } catch (Exception e) {
      throw new ToifException(e.getMessage(), e);
    }
  }
}
