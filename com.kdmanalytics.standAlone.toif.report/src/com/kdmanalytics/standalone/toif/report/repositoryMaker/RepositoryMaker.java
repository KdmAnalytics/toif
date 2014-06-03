/**
 * KDM Analytics Inc - (2012)
 *
 * @Author Adam Nunn <adam@kdmanalytics.com>
 * @Date Mar 27, 2012
 */
package com.kdmanalytics.standalone.toif.report.repositoryMaker;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import com.kdmanalytics.toif.report.util.IRepositoryMaker;

/**
 * @author adam
 *
 */
public class RepositoryMaker implements IRepositoryMaker
{
    
    private IFolder folder;
    
    private Repository repository;
    
    public RepositoryMaker()
    {
    }
    
    /**
     * @param folder
     */
    public RepositoryMaker(IFolder folder)
    {
        this.folder = folder;
        createRepository();
    }

    /**
     * 
     */
    @Override
    public void createRepository()
    {
        File file = new File(folder.getLocationURI());
        NativeStore nativeStore = new NativeStore(file);
        repository = new SailRepository(nativeStore);
    }
    
    /**
     * @return
     */
    @Override
    public Repository getRepository()
    {
        return repository;
    }
    
    @Override
    public void setFolder(IFolder folder) {
        this.folder = folder;
    }
    
}
