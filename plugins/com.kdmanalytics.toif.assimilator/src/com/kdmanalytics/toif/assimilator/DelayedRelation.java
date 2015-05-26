/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Open Source
 * Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.assimilator;

import java.io.PrintWriter;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

class DelayedRelation
{
    
    private static final Logger LOG = Logger.getLogger(DelayedRelation.class);
    
    URI subject = null;
    
    URI predicate = null;
    
    URI object = null;
    
    String src = null;
    
    String snk = null;
    
    private boolean debug = false;
    
    public DelayedRelation(URI subject, URI predicate, String snk)
    {
        this.subject = subject;
        this.predicate = predicate;
        this.snk = snk;
    }
    
    /**
     * 
     * @param repository
     * @param con
     * @param nodeNames
     * @throws RepositoryException
     */
    public void commit(PrintWriter out, Repository repository, RepositoryConnection con, XMLNode root, Map<String, String> nodeNames) throws RepositoryException
    {
        if (debug)
        {
            System.err.println("============================================");
            System.err.println("delayed relation commit...");
            System.err.println("connection: " + con);
            System.err.println("subject: " + subject);
            System.err.println("object: " + object);
            System.err.println("src: " + src);
            System.err.println("snk: " + snk);
            System.err.println("============================================");
        }
        
        if (subject == null && src == null)
        {
            LOG.error("Cannot commit delayed relation " + this);
            return;
        }
        if (object == null && snk == null)
        {
            LOG.error("Cannot commit delayed relation " + this);
            return;
        }
        if (subject == null)
        {
            LOG.error("Cannot commit delayed relation (null source) " + this);
            return;
        }
        if (object == null)
        {
            for (StringTokenizer st = new StringTokenizer(snk); st.hasMoreTokens();)
            {
                String token = st.nextToken();
                String target = nodeNames.get(token);
                if (target == null)
                {
                    // Is this using the gross node identification method? If so
                    // then
                    // try to find the target node.
                    if (root != null)
                        target = root.getURIByPath(token);
                    if (target == null)
                    {
                        LOG.error("Cannot commit delayed relation " + this);
                        continue;
                    }
                }
                ValueFactory f = repository.getValueFactory();
                object = f.createURI(target);
                
                if (debug)
                {
                    System.err.println("adding statement... subject:" + subject + " predicate: " + predicate + " object: " + object);
                    System.err.println("repository: " + con.getRepository());
                }
                
                TripleStatementWriter.addOrWrite(out, con, subject, predicate, object);
            }
        }
    }
    
    public String toString()
    {
        return "DelayedRelation: " + subject + "(" + src + ")," + predicate + "," + object + "(" + snk + ")";
    }
}
