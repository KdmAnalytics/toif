/**
 * 
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

import org.openrdf.model.URI;

/**
 * representing the rdf statements
 * 
 * @author adam
 * 
 */
public class ToifStatement
{
    
    private ToifRdfResource subject;
    
    private URI predicate;
    
    private ToifRdfResource object;
    
    /**
     * create a new statement
     * 
     * @param subjectResource
     *            the subject
     * @param predicateURI
     *            the predicate
     * @param objectResource
     *            the object
     */
    public ToifStatement(ToifRdfResource subjectResource, URI predicateURI, ToifRdfResource objectResource)
    {
        this.subject = subjectResource;
        this.predicate = predicateURI;
        this.object = objectResource;
    }
    
    /**
     * get the subject
     * 
     * @return the subject
     */
    public ToifRdfResource getSubject()
    {
        return subject;
    }
    
    /**
     * set the subject
     * 
     * @param subject
     *            the subject to set
     */
    public void setSubject(ToifRdfResource subject)
    {
        this.subject = subject;
    }
    
    /**
     * get the predicate
     * 
     * @return the predicate
     */
    public URI getPredicate()
    {
        return predicate;
    }
    
    /**
     * set the predicate
     * 
     * @param predicate
     *            the predicate to set
     */
    public void setPredicate(URI predicate)
    {
        this.predicate = predicate;
    }
    
    /**
     * get the object
     * 
     * @return the object
     */
    public ToifRdfResource getObject()
    {
        return object;
    }
    
    /**
     * set the object
     * 
     * @param object
     *            the object to set
     */
    public void setObject(ToifRdfResource object)
    {
        this.object = object;
    }
    
    /**
     * return this statement as a string.
     */
    public String toString()
    {
        return subject.getElement().getAttribute("id") + " " + predicate.stringValue() + " " + object.getElement().getAttribute("id");
    }
    
}
