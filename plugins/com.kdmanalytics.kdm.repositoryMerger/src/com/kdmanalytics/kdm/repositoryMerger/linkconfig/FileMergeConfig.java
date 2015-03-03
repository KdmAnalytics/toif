package com.kdmanalytics.kdm.repositoryMerger.linkconfig;



/**
 * This class refers back to the LinkConfig class which handles both the Merge
 * and Link configuration files in one.
 * 
 */
public class FileMergeConfig extends MergeConfig
{
    
    private LinkConfig linkConfig;
    
    /**
     * Load configuration information from the file.
     * 
     * @param in
     */
    public FileMergeConfig(LinkConfig linkConfig)
    {
        this.linkConfig = linkConfig;
    }
    
    /**
     * Return the merge type based on the KDM type.
     * 
     */
    @Override
    public int getMergeType(String kdmType)
    {
        return linkConfig.getMergeType(kdmType);
    }

    /*
     * (non-Javadoc)
     * @see com.kdmanalytics.kdm.link.linker.MergeConfig#getNoIdMergeType()
     */
    @Override
    public int getNoIdMergeType()
    {
        return linkConfig.getNoIdMergeType();
    }
}
