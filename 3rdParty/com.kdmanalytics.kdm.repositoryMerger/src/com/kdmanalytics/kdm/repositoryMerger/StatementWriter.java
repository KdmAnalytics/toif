
package com.kdmanalytics.kdm.repositoryMerger;

import java.io.PrintWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ntriples.NTriplesUtil;

/**
 * This is a utility class used to provide output assistance for various KDM
 * output formats.
 * 
 * Supported formats includes:
 * 
 * o NTRIPLES o MODIFIED_NTRIPLES o Direct to a repository
 * 
 */
public class StatementWriter
{
    
    private static final Logger LOG = Logger.getLogger(StatementWriter.class);
    
    /**
     * In the repository output solution, repository connection is stored here
     */
    private RepositoryConnection con;
    
    /**
     * In the file output solution, output stream is here
     */
    private PrintWriter output;
    
    /**
     * The format we are storing in
     */
    private int format;
    
    /**
     * Number of statements written.
     */
    long count = 0;
    
    /**
     * 
     * @param out
     * @param format
     */
    public StatementWriter(PrintWriter out, int format)
    {
        this.output = out;
        this.format = format;
    }
    
    /**
     * Make a statement writer that writes directly to a repository.
     * 
     * @param repository
     */
    public StatementWriter(Repository repository)
    {
        try
        {
            con = repository.getConnection();
        }
        catch (RepositoryException e)
        {
            LOG.error("Exception writing repository", e);
        }
    }
    
    /**
     * Call to dispose of any resources that were created for the writer
     * purposes. Does not close or shutdown underlying streams or repositories.
     */
    public void dispose()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (RepositoryException e)
            {
                LOG.error("Exception disposing StatementWriter", e);
            }
        }
        
        con = null;
        output = null;
    }
    
    /**
     * 
     * @param subject
     * @param predicate
     * @param object
     */
    public void print(URI subject, URI predicate, Value object)
    {
        ++count;
        // DIRECT to repository
        if (con != null)
        {
            try
            {
                con.add(subject, predicate, object);
            }
            catch (RepositoryException e)
            {
                LOG.error("Exception writing repository", e);
            }
        }
        else
        {
            if (output == null)
            {
                LOG.error("Attempting to write to disposed StatementWriter");
                return;
            }
            // NTRIPLES format
            if ((format & RepositoryMerger.NTRIPLES) > 0)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<");
                sb.append(subject);
                sb.append("> ");
                sb.append("<");
                sb.append(predicate);
                sb.append("> ");
                if (object instanceof URI)
                {
                    sb.append("<");
                    sb.append(object);
                    sb.append("> .");
                }
                else
                {
                    sb.append("\"");
                    String str = object.stringValue();
                    
                    // change all windows backslashes to forward slashes.
                    if ("path".equals(predicate.getLocalName()))
                    {
                        str = str.replace("\\", "/");
                    }
                    
                    // Truncate to 100 characters, cause more then that is
                    // excessive and unlikely to impact the user
                    if (str.length() > 97)
                    {
                        str = str.substring(0, 97) + "...";
                    }
                    
                    str = NTriplesUtil.escapeString(str);
                    
                    sb.append(str);
                    sb.append("\" .");
                }
                output.println(sb.toString());
            }
            // COMPRESSED NTRIPLES format
            else
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<");
                sb.append(subject.getLocalName());
                sb.append("> ");
                sb.append("<");
                sb.append(predicate.getLocalName());
                sb.append("> ");
                if (object instanceof URI)
                {
                    sb.append("<");
                    sb.append(((URI) object).getLocalName());
                    sb.append("> .");
                }
                else
                {
                    sb.append("\"");
                    String str = object.stringValue();
                    
                    // change all windows backslashes to forward slashes.
                    if ("path".equals(predicate.getLocalName()))
                    {
                        str = str.replace("\\", "/");
                    }
                    
                    // Truncate to 100 characters, cause more then that is
                    // excessive and unlikely to impact the user
                    if (str.length() > 97)
                    {
                        str = str.substring(0, 97) + "...";
                    }
                    str = NTriplesUtil.escapeString(str);
                    
                    sb.append(str);
                    sb.append("\" .");
                }
                output.println(sb.toString());
            }
        }
    }
    
    /**
     * Return the number of statements output at this point.
     * 
     * @return
     */
    public long getCount()
    {
        return count;
    }
    
    public void close()
    {
        output.flush();
        output.close();
    }
    
}
