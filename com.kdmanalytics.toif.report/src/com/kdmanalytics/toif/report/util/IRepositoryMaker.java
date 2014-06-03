/**
 * KDM Analytics Inc - (2012)
 * 
 * @Author Adam Nunn <adam@kdmanalytics.com>
 * @Date Apr 25, 2012
 */

package com.kdmanalytics.toif.report.util;

import org.eclipse.core.resources.IFolder;
import org.openrdf.repository.Repository;

/**
 * maker interface for making the repository. repositorys are created
 * differently under different circumstances.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public interface IRepositoryMaker
{
    
    /**
     * create the repository
     */
    public void createRepository();
    
    /**
     * get the repository
     * 
     * @return
     */
    public Repository getRepository();
    
    /**
     * set the folder for the repository
     * 
     * @param folder
     */
    public void setFolder(IFolder folder);
    
}
