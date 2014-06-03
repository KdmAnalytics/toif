/*******************************************************************************
 * Copyright (c) 2014 KDM Analytics, Inc. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Open Source Initiative OSI - Open Software
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/


package com.kdmanalytics.toif.assimilator;

import java.io.PrintWriter;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.kdm.repositoryMerger.RepositoryMerger;
import com.kdmanalytics.kdm.repositoryMerger.StatementWriter;


/**
 * Triple statement writer utility
 * 
 * @author Kyle Girard <kyle@kdmanaltyics.com>
 * @Date Mar 20, 2014
 *
 */
public final class TripleStatementWriter {

  /**
   * @param repositoryConnection
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   * @throws RepositoryException
   */
  public static void addOrWrite(PrintWriter writer, RepositoryConnection repositoryConnection,
      Resource arg0, URI arg1, Value arg2, Resource... arg3) throws RepositoryException {
    if (writer == null) {
      repositoryConnection.add(arg0, arg1, arg2, arg3);
    } else {
      ValueFactory f = repositoryConnection.getValueFactory();
      
      if (arg2 instanceof Literal) {
        StatementWriter sw = new StatementWriter(writer, RepositoryMerger.NTRIPLES);
        sw.print(f.createURI(arg0.stringValue()), f.createURI(arg1.stringValue()),
            f.createLiteral(arg2.stringValue()));
      } else {
        StatementWriter sw = new StatementWriter(writer, RepositoryMerger.NTRIPLES);
        sw.print(f.createURI(arg0.stringValue()), f.createURI(arg1.stringValue()),
            f.createURI(arg2.stringValue()));
      }
      
    }
  }
  
}
