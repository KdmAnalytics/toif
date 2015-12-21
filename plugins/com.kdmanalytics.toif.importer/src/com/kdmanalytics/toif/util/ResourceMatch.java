/**
 * KDM Analytics Inc - (2012)
 * 
 * @Author Adam Nunn <adam@kdmanalytics.com>
 * @Date Mar 16, 2012
 */

package com.kdmanalytics.toif.util;

import org.eclipse.core.resources.IResource;

/**
 * A mapping of a resource and how well it matches to a path.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 */
public class ResourceMatch
{
    
    /**
     * the resource which has been matched.
     */
    private IResource iResource = null;
    
    /**
     * how well the resource matches the directory structure.
     */
    private int score = 0;
    
    /**
     * 
     */
    public ResourceMatch()
    {
    }
    
    /**
     * @param iResource
     *            the resource which has been matched.
     * @param score
     *            How many directories are a match to this resource.
     */
    public ResourceMatch(IResource iResource, int score)
    {
        this.iResource = iResource;
        this.score = score;
    }
    
    public IResource getIResource()
    {
        return iResource;
    }
    
    /**
     * The score is how well the resource matches the directory structure of
     * another resource.
     * 
     * @return the score
     */
    public int getScore()
    {
        return score;
    }
    
}
