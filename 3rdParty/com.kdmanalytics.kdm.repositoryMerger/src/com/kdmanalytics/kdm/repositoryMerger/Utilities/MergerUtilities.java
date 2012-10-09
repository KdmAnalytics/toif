
package com.kdmanalytics.kdm.repositoryMerger.Utilities;

import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class MergerUtilities
{
    
    private Repository repository;
    
    /**
     * 
     * @param repository
     */
    public MergerUtilities(Repository repository)
    {
        this.repository = repository;
        
    }
    
    /**
     * 
     * @param <T>
     * @param <E>
     * @param iter
     */
    private <T, E extends Exception> void closeQuietly(CloseableIteration<T, E> iter)
    {
        try
        {
            if (iter != null)
            {
                iter.close();
            }
        }
        catch (final Exception e)
        {
            // TODO: Log
        }
        
    }
    
    /**
     * 
     * @param con
     */
    private void closeQuietly(RepositoryConnection con)
    {
        try
        {
            if (con != null)
            {
                con.close();
            }
        }
        catch (final RepositoryException e)
        {
            // TODO:Log
        }
    }
    
    /**
     * 
     * @param <T>
     * @param <E>
     * @param con
     * @param statements
     */
    private <T, E extends Exception> void closeQuietly(RepositoryConnection con, CloseableIteration<Statement, RepositoryException> statements)
    {
        closeQuietly(statements);
        closeQuietly(con);
    }
    
    /**
     * Return the URI for the owner of the specified element.
     * 
     * @param uri
     * @return First found parent URI, or null if no parent found. An error is
     *         written to stderr if there is more then one parent found.
     * @throws RepositoryException
     */
    public URI getOwner(URI uri) throws RepositoryException
    {
        final ValueFactory f = repository.getValueFactory();
        final URI predicate = f.createURI(MergerURI.KdmNS + "contains");
        
        CloseableIteration<Statement, RepositoryException> statements = null;
        RepositoryConnection con = null;
        
        try
        {
            con = repository.getConnection();
            statements = con.getStatements(null, predicate, uri, false);
            
            URI result = null;
            while (statements.hasNext())
            {
                final Statement st = statements.next();
                if (result == null)
                {
                    result = (URI) st.getSubject();
                }
                else
                {
                    // TODO:LOG.error("Element has more then one parent (" + uri
                    // + ", " + result + "," + st.getSubject() + ")");
                }
                return (URI) st.getSubject();
                
            }
            return result;
        }
        finally
        {
            closeQuietly(con, statements);
        }
    }
    
    /**
     * Simple function that creates a predicate based on the provided string
     * predicate name.
     * 
     * @param pred
     * @return
     */
    protected URI getPredicate(String pred)
    {
        final ValueFactory f = repository.getValueFactory();
        return f.createURI(MergerURI.KdmNS, pred);
    }
    
    /**
     * Returns the requested RDF Attribute as a string value.
     * 
     * @param id
     * @param name
     * @return
     * @throws RepositoryException
     */
    public String getRDFAttribute(URI id, String name) throws RepositoryException
    {
        return getRDFAttribute(id, name, null);
    }
    
    /**
     * Returns the requested RDF Attribute as a string value. This version is
     * used for the rare cases when a context is used.
     * 
     * @param id
     * @param name
     * @param context
     * @return
     * @throws RepositoryException
     */
    public String getRDFAttribute(URI id, String name, Value context) throws RepositoryException
    {
        final Value value = getRDFAttributeValue(id, name);
        if (value == null)
        {
            return null;
        }
        return value.stringValue();
    }
    
    /**
     * Same as getRDFAttribute, but returns the result as a "Value" as opposed
     * to a "String".
     * 
     * @param name
     * @param name
     * @return
     * @throws RepositoryException
     */
    public Value getRDFAttributeValue(URI source, String name) throws RepositoryException
    {
        if (source == null)
        {
            // TODO: log
            return null;
        }
        final ValueFactory f = repository.getValueFactory();
        final URI predicate = f.createURI(MergerURI.KdmNS, name);
        RepositoryConnection con = null;
        CloseableIteration<Statement, RepositoryException> statements = null;
        
        try
        {
            con = repository.getConnection();
            statements = con.getStatements(source, predicate, null, false);
            
            if (statements.hasNext())
            {
                final Statement st = statements.next();
                return st.getObject();
            }
        }
        finally
        {
            closeQuietly(con, statements);
        }
        return null;
    }
    
    /**
     * Return the entities that are related in any way
     * 
     * @param id
     * @param predicate
     * @return
     */
    public CloseableIteration<Statement, RepositoryException> getRelated(Value id) throws RepositoryException
    {
        
        RepositoryConnection con = null;
        try
        {
            con = repository.getConnection();
            return con.getStatements((Resource) id, null, null, false);
        }
        finally
        {
            closeQuietly(con);
        }
    }
    
    /**
     * Return the entities that are related in the specified manner
     * 
     * @param id
     * @param predicate
     * @return
     */
    public List<URI> getRelated(Value id, String predicate) throws RepositoryException
    {
        final ArrayList<URI> results = new ArrayList<URI>();
        CloseableIteration<Statement, RepositoryException> statements = null;
        
        RepositoryConnection con = null;
        try
        {
            con = repository.getConnection();
            
            ValueFactory f = repository.getValueFactory();
            URI predicate2 = getPredicate(predicate);
            statements = con.getStatements((Resource) id, predicate2, null, true, (Resource) null);
            while (statements.hasNext())
            {
                final Statement st = statements.next();
                final URI result = (URI) st.getObject();
                results.add(result);
            }
        }
        finally
        {
            closeQuietly(con, statements);
        }
        
        return results;
    }
    
    /**
     * Return the entities that are related in the specified manner
     * 
     * @param id
     * @param predicate
     * @return
     */
    public URI getRelatedValue(Value id, String predicate) throws RepositoryException
    {
        CloseableIteration<Statement, RepositoryException> statements = null;
        RepositoryConnection con = null;
        try
        {
            con = repository.getConnection();
            statements = con.getStatements((Resource) id, getPredicate(predicate), null, false);
            if (statements.hasNext())
            {
                final Statement st = statements.next();
                final Value val = st.getObject();
                if (val instanceof URI)
                {
                    return (URI) st.getObject();
                }
                // TODO: Log
            }
        }
        finally
        {
            closeQuietly(con, statements);
        }
        return null;
    }
    
    /**
     * Get the root element for the specified context
     * 
     * @param context
     * @return
     * @throws RepositoryException
     */
    public Value getRootId() throws RepositoryException
    {
        ValueFactory f = repository.getValueFactory();
        Value segmentURI = f.createURI("http://kdmanalytics.com/", Long.toString(0));
        return segmentURI;
    }
    
}
