/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.assimilator;

import java.io.BufferedWriter;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.ui.internal.ReopenEditorMenu;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.kdm.repositoryMerger.RepositoryMerger;
import com.kdmanalytics.kdm.repositoryMerger.StatementWriter;

public class KdmXmlHandler extends DefaultHandler
{
    
    public static final String kdmNSHost = "org.omg.kdm";
    
    public static final String kdmNS = "http://" + kdmNSHost + "/";
    
    /**
     * @param repositoryConnection
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws RepositoryException
     */
    public static void addOrWrite(PrintWriter writer, RepositoryConnection repositoryConnection, Resource arg0, URI arg1, Value arg2,
            Resource... arg3) throws RepositoryException
    {
        if (writer == null)
        {
            repositoryConnection.add(arg0, arg1, arg2, arg3);
        }
        else
        {
            // String predicate = "";
            // if (arg2 instanceof Literal) {
            // predicate = "\""+arg2.stringValue()+"\"";
            // } else {
            // predicate = "<"+arg2.stringValue()+">";
            // }
            // //System.err.println("<"+arg0 + "> <" + arg1 + "> " + predicate);
            // writer.write("<"+arg0 + "> <" + arg1 + "> " + predicate+"  .\n");
            ValueFactory f = repositoryConnection.getValueFactory();
            
            if (arg2 instanceof Literal)
            {
                StatementWriter sw = new StatementWriter(writer, RepositoryMerger.NTRIPLES);
                sw.print(f.createURI(arg0.stringValue()), f.createURI(arg1.stringValue()), f.createLiteral(arg2.stringValue()));
                //System.err.println(arg0.stringValue()+" "+ f.createURI(arg1.stringValue())+" "+ f.createLiteral(arg2.stringValue()));
            }
            else
            {
                StatementWriter sw = new StatementWriter(writer, RepositoryMerger.NTRIPLES);
                sw.print(f.createURI(arg0.stringValue()), f.createURI(arg1.stringValue()), f.createURI(arg2.stringValue()));
                //System.err.println(arg0.stringValue()+" "+ f.createURI(arg1.stringValue())+" "+ f.createURI(arg2.stringValue()));
            }
            
        }
    }
    
    private Repository repository = null;
    
    private int commitCounter = 0;
    
    private RepositoryConnection con = null;
    
    private static final int commitInterval = 0;
    
    private XMLNode root = null;
    
    private String indent = "";
    
    private final Map<String, String> nodeNames = new HashMap<String, String>();
    
    private final List<DelayedRelation> postLoad = new ArrayList<DelayedRelation>();
    
    private final Stack<XMLNode> nodes = new Stack<XMLNode>();
    
    private final boolean storeHierarchy = false;
    
    private final boolean debug = false;
    
    private Long nextId = 0L;
    
    private long smallestBigNumber = Long.MAX_VALUE;
    
    private long count;
    
    private PrintWriter out;
    
    private StatementWriter sw;
    
    public KdmXmlHandler(Repository repository)
    {
        this.repository = repository;
    }
    
    /**
     * @param out
     */
    public KdmXmlHandler(PrintWriter out, Repository repository)
    {
        this.out = out;
        this.repository = repository;
        this.sw = new StatementWriter(out, RepositoryMerger.NTRIPLES);
    }
    
    /**
     * Add the specified child to the specified parent
     * 
     * @param parent
     * @param child
     * @throws RepositoryException
     */
    protected void addChild(XMLNode parent, XMLNode child) throws RepositoryException
    {
        // I need the nested nodes to remember as well, for finding XMI path
        // information.
        // Only do this if we need to.
        if (storeHierarchy)
        {
            parent.add(child);
        }
        
        // Do not output attributes as children. They are being output as
        // direct rdf tuplets.
        // Since the node has not already been commited, the attribute is
        // added here.
        
        if ("attribute".equals(child.getName()))
        {
            ValueFactory f = repository.getValueFactory();
            URI key = f.createURI(kdmNS, child.getAttribute("tag"));
            Literal value = f.createLiteral(child.getAttribute("value"));
            KdmXmlHandler.addOrWrite(out, con, f.createURI(parent.getURIString()), key, value);
            return;
        }
        
        // Standard contained node
        
        ValueFactory f = repository.getValueFactory();
        URI predicate = f.createURI(kdmNS, "contains");
        
        KdmXmlHandler.addOrWrite(out, con, f.createURI(parent.getURIString()), predicate, f.createURI(child.getURIString()));
        doCommit();
        
    }
    
    /**
     * Write the XMLNode into the RDF database
     * 
     * @param source
     * @throws RepositoryException
     */
    protected void commitNode(XMLNode source) throws RepositoryException
    {
        // Attributes are handled differently to conserve space
        if ("attribute".equals(source.getName()))
        {
            return;
        }
        
        // This is a standard node, not an attribute.
        setRDFAttribute(source, "kdmType", source.getKDMType());
        
        Map<String, String> attrs = source.getAttributes();
        if (attrs != null)
        {
            for (Iterator<String> it = attrs.keySet().iterator(); it.hasNext();)
            {
                String key = it.next();
                // Ignore the special xmi values
                if (key.contains(":"))
                {
                    continue;
                }
                String value = attrs.get(key);
                
                setRDFAttribute(source, key, value);
            }
        }
        
        // Add the references
        Set<String> referenceTypes = source.getReferenceTypes();
        if (referenceTypes != null)
        {
            for (Iterator<String> it = referenceTypes.iterator(); it.hasNext();)
            {
                String key = it.next();
                List<String> references = source.getReferences(key);
                for (Iterator<String> rit = references.iterator(); rit.hasNext();)
                {
                    String ref = rit.next();
                    ValueFactory f = repository.getValueFactory();
                    URI subject = f.createURI(source.getURIString());
                    URI predicate = f.createURI(kdmNS, key);
                    
                    postLoad.add(new DelayedRelation(subject, predicate, ref));
                }
            }
            // con.commit();
        }
    }
    
    /**
     * do the commit.
     * 
     * @throws RepositoryException
     */
    private void doCommit() throws RepositoryException
    {
        commitCounter++; // Statements since last commit
        if (commitCounter > commitInterval)
        {
            if (debug)
            {
                System.err.println("commiting...");
            }
            
            con.commit();
            commitCounter = 0;
        }
    }
    
    /**
     * Element ended. Pop the XMLNode off of the stack.
     * 
     */
    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException
    {
        
        indent = indent.substring(2);
        if (indent == null)
        {
            indent = "";
        }
        XMLNode node = nodes.pop();
        
        // Some nodes are represented by a special compressed format,
        // add these to the repository in a special way.
        // These nodes (and their children) are prevented from writing
        // in the conventional way in the "startElement" method.
        //
        // file;startline:startpos-endline:endpos;language;path|snippet|language
        if ("source/SourceRef".equals(node.getKDMType()))
        {
            List<XMLNode> regions = node.children;
            StringBuilder sb = new StringBuilder();
            for (XMLNode region : regions)
            {
                String type = region.getKDMType();
                if (!"source/SourceRegion".equals(type))
                {
                    continue;
                }
                List<String> file = region.getReferences("file");
                String startLine = region.getAttribute("startLine");
                String startPos = region.getAttribute("startPos");
                String endLine = region.getAttribute("endLine");
                String endPos = region.getAttribute("endPos");
                String language = region.getAttribute("language");
                String path = region.getAttribute("path");
                if (file != null && file.size() > 0)
                {
                    sb.append(file.get(0));
                }
                sb.append(";");
                if (startLine != null)
                {
                    sb.append(startLine);
                    if (startPos != null)
                    {
                        sb.append(":").append(startPos);
                    }
                    if (endLine != null)
                    {
                        sb.append("-").append(endLine);
                        if (endPos != null)
                        {
                            sb.append(":").append(endPos);
                        }
                    }
                }
                sb.append(";");
                if (language != null)
                {
                    sb.append(language);
                }
                sb.append(";");
                if (path != null)
                {
                    sb.append(path);
                }
            }
            String snippet = node.getAttribute("snippet");
            String language = node.getAttribute("language");
            sb.append("|");
            if (snippet != null)
            {
                sb.append(snippet.replaceAll(",", "&comma;").replaceAll("\\|", "&pipe;"));
            }
            sb.append("|");
            if (language != null)
            {
                sb.append(language);
            }
            
            if (!nodes.isEmpty())
            {
                XMLNode parent = nodes.peek();
                try
                {
                    setRDFAttribute(parent, "SourceRef", sb.toString());
                }
                catch (RepositoryException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * get the next id
     * 
     * @return the next id
     */
    public Long getNextId()
    {
        return nextId;
    }
    
    /**
     * get the smallest available number at the end of the long scale.
     * 
     * @return the smallest number available at the end of the long scale.
     */
    public long getSmallestBigNumber()
    {
        return smallestBigNumber;
    }
    
    /**
     * Commit postload data
     * 
     */
    public void postLoad() throws RepositoryException
    {
        try
        {
            con = repository.getConnection();
            
            // Commit postLoad data
            for (Iterator<DelayedRelation> it = postLoad.iterator(); it.hasNext();)
            {
                DelayedRelation rel = it.next();
                
                rel.commit(out, repository, con, root, nodeNames);
                doCommit();
            }
        }
        finally
        {
            con.commit();
            con.close();
        }
        
    }
    
    /**
     * set the next id
     * 
     * @param newId
     *            the id to set
     */
    private void setNextId(long newId)
    {
        
        if (newId > nextId)
        {
            nextId = newId;
        }
        
    }
    
    /**
     * Add the specified RDF tuple
     * 
     * @param key
     *            attribute key
     * @param value
     *            attribute value
     */
    public void setRDFAttribute(XMLNode source, String name, String value) throws RepositoryException
    {
        
        ValueFactory f = repository.getValueFactory();
        Literal literal = f.createLiteral(value);
        URI predicate = f.createURI(kdmNS, name);
        
        if (con == null)
        {
            con = repository.getConnection();
        }
        
        if (debug)
        {
            System.err.println("==========================================================");
            System.err.println("the connection is: " + con);
            System.err.println("the source is: " + source);
            System.err.println("the predicate is: " + predicate);
            System.err.println("the literal is: " + literal);
            System.err.println("==========================================================");
        }
        
        KdmXmlHandler.addOrWrite(out, con, f.createURI(source.getURIString()), predicate, literal);
        doCommit();
    }
    
    /**
     * Beginning of the document.
     * 
     */
    @Override
    public void startDocument() throws SAXException
    {
    }
    
    /**
     * New element found.
     * 
     */
    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
    {
        
        XMLNode node = new XMLNode(namespaceURI, sName, qName, attrs);
        String stringId = node.getAttribute("xmi:id");
        
        // System.err.println("DEALING WITH NODE: " + node.toString());
        
        count++;
//        if (count % 1000 == 0)
//        {
//            System.out.println("Processing Element Count: " + count);
//        }
        
        if (stringId != null)
        {
            try
            {
                long id = Long.parseLong(stringId);
                
                setNextId(id);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Bad ID: ");
                for (int i = 0; i < attrs.getLength(); i++)
                {
                    System.err.println(attrs.getQName(i) + " : " + attrs.getValue(i));
                }
                System.err.println(e);
            }
        }
        else
        {
            node.setId(getNextId() + 1);
        }
        
        if ("source/SourceRef".equals(node.getKDMType()) || "source/SourceRegion".equals(node.getKDMType()))
        {
            // These elements are represented in the repository in
            // a special "compressed" format and should not be output
            // using the conventional methods.
            if (!nodes.isEmpty())
            {
                XMLNode parent = nodes.lastElement();
                parent.add(node);
            }
            nodes.push(node);
            indent = indent + "  "; // Ensure indent is kept up to date
            return;
        }
        
        // KDM Type needs to be output before any more information about the
        // node.
        try
        {
            commitNode(node);
            
        }
        catch (RepositoryException ex)
        {
            throw new SAXException(ex);
        }
        
        // If we are remembering and no root is set, this is the root.
        if (storeHierarchy && root == null)
        {
            root = node;
        }
        
        // Process the node
        XMLNode parent = null;
        if (!nodes.isEmpty())
        {
            parent = nodes.lastElement();
        }
        nodes.push(node);
        
        indent = indent + "  ";
        try
        {
            if (parent != null)
            {
                addChild(parent, node);
            }
            
        }
        catch (RepositoryException ex)
        {
            throw new SAXException(ex);
        }
        
        // Remember IDs for postload and non xmi:id files (barf)
        String id = node.getAttribute("xmi:id");
        if (id != null)
        {
            String uri = node.getURIString();
            nodeNames.put(id, uri);
        }
    }
    
    /**
     * End of the document. Write all postLoad elements
     * 
     */
    public void stopDocument() throws SAXException
    {
    }
    
}
