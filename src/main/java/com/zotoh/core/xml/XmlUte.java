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

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.TM;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static java.util.Collections.unmodifiableMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.zotoh.core.util.Logger;

/**
 * Common Util functions.
 *
 * @author kenl
 *
 */
public enum XmlUte implements XmlVars {
;

//    private static final String NSP_FEATURE= "http://xml.org/sax/features/namespace-prefixes";

    private static Logger _log= getLogger(XmlUte.class);  
    public static Logger tlog() {  return _log ; }    

    private static DocumentBuilderFactory _dfac;
    private static SAXParserFactory _sfac;

    /**
     * @return
     * @throws SAXException
     */
    public static SAXParser newSaxValidator() throws SAXException    {
        return newParser(true, true);
    }

    
    /**
     * @return
     * @throws SAXException
     */
    public static SAXParser newSaxParser() throws SAXException    {
        return newParser(true, false);
    }

    
    /**
     * @param s
     * @return
     */
    public static InputSource createInputSource(InputStream s)    {
        return  s==null ? null : new InputSource(new BufferedInputStream(s));
    }

    /**
     * @param atts
     * @return
     */
    public static Map<String,String> attrsToLNMap(Attributes atts)    {
        return attrsToMap(atts, false);
    }

    
    /**
     * @param atts
     * @return
     */
    public static Map<String,String> attrsToQNMap(Attributes atts)    {
        return attrsToMap(atts, true);
    }

    
    /**
     * @param atts
     * @return
     */
    public static String attributesToString(Map<String,String> atts)    {
        StringBuilder buf= new StringBuilder(1024);
        if (atts != null) for (String s : atts.keySet())         {
            buf.append(" ")
            .append(s)
            .append("=\"")
            .append(atts.get(s))
            .append("\"");
        }
        return buf.toString();
    }

    
    /**
     * @param out
     * @param s
     * @param enc
     * @throws IOException
     */
    public static void write(OutputStream out, String s, String enc) throws IOException    {
        if (!isEmpty(s)) {
            tstObjArg("output-stream", out) ;
            out.write(s.getBytes(enc));
            out.flush();
        }
    }

    
    /**
     * @param out
     * @param s
     * @throws IOException
     */
    public static void write(OutputStream out, String s) throws IOException    {
        write(out,s, null);
    }

    
    /**
     * @param inp
     * @return
     * @throws Exception
     */
    public static Document parseXML(InputStream inp)    throws Exception    {
        return inp==null ? null : newDOMer(true,false).parse(inp);
    }
    
    
    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static Document parseXML(File file)     throws Exception    {
        return file==null ? null : newDOMer(true,false).parse( file);
    }
    
    
    /**
     * @param xmlString
     * @return
     */
    public static String indexToProlog(String xmlString)    {
        if (!isEmpty(xmlString))        {
            int pos = xmlString.indexOf("<");
            if (pos > 0) {            
                xmlString = xmlString.substring(pos);
            }
        }
        return xmlString;
    }

    
    /**
     * @param em
     * @param a
     * @return
     */
    public static Iterator<Node> iterChildren(Element em, String a)    {
        return listChildren(em, a).iterator();
    }

    
    /**
     * @param em
     * @param a
     * @return
     */
    public static List<Node> listChildren(Element em, String a)    {
        tstObjArg("input-element", em) ;
        tstEStrArg("tag", a) ;
        
        NodeList lst= em.getElementsByTagName(a);
        int len= lst==null ? 0 : lst.getLength();
        
        List<Node> rc= new ArrayList<Node>();
        for (int i=0; i < len; ++i) {
            rc.add( lst.item(i));
        }

        return rc;
    }

    
    /**
     * @param em
     * @param a
     * @return
     */
    public static String getAttr(Element em, String a)    {
        return em==null || a==null ? null : em.getAttribute(a);
    }
    
    
    /**
     * @param em
     * @return
     */
    public static String getElementName(Element em)    {
        return em==null ? null : escape(getAttr(em, "name"));
    }

    
    /**
     * @param tag
     * @param obj
     * @return
     */
    public static String xmle(String tag, Object obj)    {
        return tag==null ? "" : starte(tag) + escape(nsb(obj)) + ende(tag);
    }

    
    /**
     * @param tag
     * @return
     */
    public static String starte(String tag)    {        return "<" + nsb(tag) + ">";    }

    
    /**
     * @param tag
     * @return
     */
    public static String ende(String tag)    {        return "</" + nsb(tag) + ">";    }
    
    
    /**
     * @param inStr
     * @return
     */
    public static String escape(String inStr)    {
        StringBuilder outBuf=   new StringBuilder(256);
        char c;
        int inLen= (inStr == null) ? 0 : inStr.length();

        for (int i = 0; i < inLen; ++i)        {
            switch ( c = inStr.charAt(i))            {
                case '\n':
                    outBuf.append("&#10;");
                    break;
                case '\r':
                    outBuf.append("&#13;");
                    break;
                case '<':
                    outBuf.append("&lt;");
                    break;
                case '>':
                    outBuf.append("&gt;");
                    break;
                case '&':
                    outBuf.append("&amp;");
                    break;
                case '\'':
                    outBuf.append("&apos;");
                    break;
                case '"':
                    outBuf.append("&quot;");
                    break;
                default:
                    outBuf.append(c);
            }
        }

        return outBuf.toString();
    }

    /**
     * Parse the input stream and convert it to a DOM document object.
     * 
     * @param inp the stream.
     * @return a DOM document.
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */    
    public static Document toDOM(InputStream inp) throws SAXException, IOException, 
    ParserConfigurationException    {
        return inp==null ? null : newDOMer(true, false).parse(inp);
    }
    
    private static SAXParser newParser(boolean nsAware, boolean validate)
                throws SAXException    {
        SAXParserFactory f= getSFac();
        try    {
            f.setNamespaceAware(nsAware);
            f.setValidating(validate);
            return f.newSAXParser();
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }

    private static DocumentBuilder newDOMer(boolean nsAware, boolean validate)
                throws SAXException, ParserConfigurationException    {
        DocumentBuilderFactory f= getDFac();
        try        {
            f.setNamespaceAware(nsAware);
            f.setValidating(validate);
            return f.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }

    private static DocumentBuilderFactory getDFac() {        return _dfac;    }

    private static SAXParserFactory getSFac() {        return _sfac;    }

    private static Map<String,String> attrsToMap(Attributes atts, boolean fullyQ)    {
        Map<String, String> ret = TM();
        int len = atts==null ? 0 : atts.getLength();
        for ( int i= 0; i < len; ++i) {
            ret.put( 
                    (fullyQ ? atts.getQName(i) : atts.getLocalName(i))
                    , nsb(atts.getValue(i)));
        }
        return unmodifiableMap(ret);
    }
    
    static {
        _dfac= DocumentBuilderFactory.newInstance();
        _sfac= SAXParserFactory.newInstance();
    }

    
    
}
