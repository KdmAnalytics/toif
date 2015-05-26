
package com.kdmanalytics.toif.findbugs;

/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * The parser for the findbugs output.
 * 
 * @author adam
 * 
 */
public class FindBugsParser extends DefaultHandler
{
    
    private FindingCreator findingCreator;
    
    private String id;
    
    private Integer line;
    
    private Integer offset;
    
    private File file;
    
    private Properties props;
    
    private boolean first;
    
    private String description;
    
    private ArrayList<Element> traces = new ArrayList<>();
    
    private Stack<String> stack = new Stack<>();
    
    /**
     * construct a findbugs parser.
     */
    public FindBugsParser(Properties props, File file, String name, boolean unknownCWE)
    {
        findingCreator = new FindingCreator(props, name, unknownCWE);
        this.file = file;
        this.props = props;
    }
    
    /**
     * start the parse. The details are handed to the findingCreator.
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    {
 
        if ("BugInstance".equals(qName))
        {
            id = attrs.getValue("type");
            first = true;
            traces.clear();
        }
        
        if (!"SourceLine".equals(qName))
        {
            stack.push(qName);
            first = true;
        }
        
//        if ("SourceLine".equals(qName) && (attrs.getLength() >= 5))
//        {
        
        if ("SourceLine".equals(qName) && "BugInstance".equals(stack.peek())) {
            
            String startByte = attrs.getValue("startBytecode");
            if (first)
            {
                String start = attrs.getValue("start");
                if (start!= null) {
                    line = Integer.parseInt(start);
                } else {
                    line = 1;
                }
                if (startByte != null)
                {
                    offset = Integer.parseInt(startByte);
                }
                description = props.getProperty(id + "Msg");
                first = false;
            }
            else
            {
                String start = attrs.getValue("start");
                traces.add(new CodeLocation(Integer.parseInt(start), null, Integer.parseInt(startByte)));
            }
            
        } else if ("SourceLine".equals(qName)) {
            String startByte = attrs.getValue("startBytecode");
            String start = attrs.getValue("start");
            if (start != null) {
                line = Integer.parseInt(start);
            } else {
                line = 1;
            }
            if (startByte != null)
            {
                offset = Integer.parseInt(startByte);
            }
            description = props.getProperty(id + "Msg");
            first = false;
        }
    }
    
    /**
     * called on the end element.
     */
    public void endElement(String uri, String localName, String qName)
    {
        
        if (!"SourceLine".equals(qName))
        {
            stack.pop();
            first = true;
        }
        if ("BugInstance".equals(qName))
            findingCreator.create(description, id, line, offset, null, file, null, null, traces.toArray(new CodeLocation[traces.size()]));
    }
    
    /**
     * reuturn the gathered elements.
     * 
     * @return
     */
    public ArrayList<Element> getElements()
    {
        return findingCreator.getElements();
    }
}
