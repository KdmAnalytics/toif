/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.facts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.kdmanalytics.toif.framework.xmlElements.entities.Project;
import com.kdmanalytics.toif.framework.xmlElements.entities.Segment;

@XmlRootElement(name = "fact")
public class TOIFSegmentIsRelatedToProject extends Fact
{   
    private Segment segment;
    private Project project;
    
    public TOIFSegmentIsRelatedToProject() {
        super();
        type = "toif:TOIFSegmentIsRelatedToProject";
    }
    
    public TOIFSegmentIsRelatedToProject(Segment segment, Project project)
    {
        super();
        type = "toif:TOIFSegmentIsRelatedToProject";
        this.segment = segment;
        this.project = project;
    }
    
    /**
     * @return the segment
     */
    @XmlAttribute
    public String getSegment()
    {
        return segment.getName().getName();
    }
    
    /**
     * @param segment the segment to set
     */
    public void setSegment(Segment segment)
    {
        this.segment = segment;
    }
    
    /**
     * @return the project
     */
    @XmlAttribute
    public String getProject()
    {
        return project.getId();
    }
    
    /**
     * @param project the project to set
     */
    public void setProject(Project project)
    {
        this.project = project;
    }
}
