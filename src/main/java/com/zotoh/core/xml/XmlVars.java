/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
 *
 * THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 * MODIFY IT UNDER THE TERMS OF THE APACHE LICENSE, 
 * VERSION 2.0 (THE "LICENSE").
 *
 * THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 * BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *   
 * SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING PERMISSIONS 
 * AND LIMITATIONS UNDER THE LICENSE.
 *
 * You should have received a copy of the Apache License
 * along with this distribution; if not, you may obtain a copy of the 
 * License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 ??*/
 
package com.zotoh.core.xml;

/**
 * Constants. 
 *
 * @author kenl
 *
 */
public interface XmlVars {
    
    public static final String XSD_NSP = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_PFX = "xsd";
    public static final String XML_NSP = "http://www.w3.org/XML/1998/namespace";
    public static final String XML_PFX = "xml";
    public static final String XML_LANG= "xml:lang";

    public static final String XMLHDLINE= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String XERCES= "org.apache.xerces.parsers.SAXParser";
    public static final String SLASH= "/";
    public static final String COMMA= ",";
    public static final String DOT= ".";
    public static final String HASHHASH= "##";
    public static final String HASH= "#";
    public static final String LPAREN= "(";
    public static final String RPAREN= ")";
    public static final String DOLLAR= "$";
    public static final String CSEP= "?";
    
    public static final String DV_NONE= "None";
    public static final String DV_XSD= "XSD";
    public static final String DV_DTD= "DTD";
}
