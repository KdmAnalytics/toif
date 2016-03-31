/**
 * KDM Analytics Inc (2014)
 * 
 * @Author Adam Nunn
 * @Date Mar 24, 2014
 */

package com.kdmanalytics.toif.assimilator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Records the maximum xmi:id encountered while parsing a KDM file
 * 
 * @author Kyle Girard <kyle@kdmanalytics.com>
 */
public class KdmXmiIdHandler extends DefaultHandler {
  
  /** The current maximum xmi:id. */
  private long maxId = 0;
  
  /**
   * If this element has an xmi:id, if it has an id check against the current maximum id and record
   * a new max if required.
   * 
   * @throws SAXException
   *           if the id is malformed
   */
  @Override
  public void startElement(final String namespaceURI, final String sName, final String qName, final Attributes attrs)
      throws SAXException {
    XMLNode node = new XMLNode(namespaceURI, sName, qName, attrs);
    String stringId = node.getAttribute("xmi:id");
    if (stringId != null) {
      try {
        long id = Long.parseLong(stringId);
        maxId = Math.max(id, maxId);
      } catch (NumberFormatException e) {
        throw new SAXException(e);
      }
    }
  }
  
  /**
   * Return the maximum xmi:id. Note this value only valid once parsing has completed
   * 
   * @return
   */
  public long getMaxId() {
    return maxId;
  }
}
