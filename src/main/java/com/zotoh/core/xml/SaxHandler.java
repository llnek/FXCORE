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

import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.xml.XmlUte.attrsToQNMap;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.util.Map;
import java.util.Stack;

import com.zotoh.core.util.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.zotoh.core.io.StreamData;

/**
 * @author kenl
 *
 */
public class SaxHandler extends DefaultHandler implements ErrorHandler  {
    
    private Logger ilog() {       return _log=getLogger(StreamData.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    protected Stack<SAXException> _errors, _warns;
    protected StringBuilder _elemData;
    protected SaxHandler _prevHdlr;

    protected boolean _trimCDATA= true;
    protected String _docType, _sysID;

    protected transient Locator _locator = null;
    protected XMLReader _rdr;

    /**
     * Constructor.
     * 
     * Create a new handler that is chained to the previous one.  This is a way we handle
     * a nested element which has it's own sax handling code.
     * 
     * @param previousHandler
     */
    public SaxHandler(SaxHandler previousHandler)    {
        iniz();
        attachHandler(previousHandler);
    }


    /**
     * 
     */
    public SaxHandler()    {
        this(null);
    }

    
    /**
     * @param trim
     */
    public void setTrimCDATA(boolean trim)    { 
    	_trimCDATA= trim; 
    }

    
    /**
     * @return
     */
    public boolean isCDATATrimmed()    {    	        return _trimCDATA;       }

    
    /**
     * @return
     */
    public String getDOCTYPE()    {                return _docType;       }

    
    /**
     * @return
     */
    public String getSYSTEMID()    {                return _sysID;       }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String lname, String qname, Attributes atts) throws SAXException    {       
        onStartElement(uri, lname, qname, attrsToQNMap(atts));
    }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String lname, String qname) throws SAXException    {
        onEndElement(uri,lname,qname, getLastElementCDATA());
    }

    /**
     * Pop current handler, revert back to parent handler.
     * 
     * @throws SAXException error while xml processing.
     */    
    public void resetDocumentHandler() throws SAXException    {
        if ( _rdr != null &&  _prevHdlr != null) { 
                 _rdr.setContentHandler( _prevHdlr);
        }
    }

    
    /**
     * @return
     */
    public String getWarningsAsString()    {
        return  getExceptionsAsString(_warns);
    }

    
    /**
     * @return
     */
    public String getErrorsAsString()    {
        return         getExceptionsAsString( _errors);
    }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] chars, int start, int n) throws SAXException    {
        _elemData.append(chars, start, n);
    }

    
    /**
     * @return
     */
    protected String getLastElementCDATA()    {
        String v=  _elemData.toString();
        try         {
        	return isEmpty(v) ? null : ( _trimCDATA ? v.trim() : v);
        }
        finally {
            _elemData.setLength(0);        	
        }
    }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    @Override
    public void warning(SAXParseException e) throws SAXException   {
        _warns.push(e);
    }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    @Override
    public void error(SAXParseException e) throws SAXException    {
        _errors.push(e);
    }

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException    {
        _errors.push(e);
    }

    /**
     * @param e
     */
    protected void push(SAXException e)   {
        _errors.push(e);
    }
    
    /**
     * @param uri
     * @param lname
     * @param qname
     * @param atts
     * @throws SAXException
     */
    protected void onStartElement(String uri, String lname, String qname, Map<String,String> atts)
    throws SAXException    {
        // subclass do the work
    }

    
    /**
     * @param uri
     * @param lname
     * @param qname
     * @param cdata
     * @throws SAXException
     */
    protected void onEndElement(String uri, String lname, String qname, String cdata)  
    throws SAXException    {
        // subclass do the work        
    }

    
    /**
     * @param curr
     */
    protected void attachHandler(SaxHandler curr)    {
        if (curr != null)         {
            _elemData= curr._elemData;
            _prevHdlr= curr;
            _sysID= curr._sysID;
            _docType= curr._docType;
            _locator= curr._locator;
            _errors= curr._errors;
            _warns= curr._warns;
            _rdr= curr._rdr;
        }
    }

    
    /**
     * @return
     */
    protected boolean hasErrors()    {
    	return _errors.size() > 0;
    }

    
    /**
     * 
     */
    protected void iniz()    {
        _errors= new Stack<SAXException>();
        _warns= new Stack<SAXException>();
        _locator= null;
        _sysID= null;
        _docType= null;
        _elemData = new StringBuilder(256);
    }

    private String getExceptionsAsString(Stack<SAXException> stk)    {
        StringBuilder ret=new StringBuilder(1024);
        for (int i=0; i < stk.size(); ++i)        {
            addAndDelim(ret, "\n", eToString(stk.get(i)));
        }
        return ret.toString();
    }

    private String eToString(SAXException e)    {
        Exception e_= e.getException();
        String err= e.getMessage();
        int line= -1;

        if (e instanceof SAXParseException) {
            line = ( (SAXParseException) e).getLineNumber();
        }

        return "SAX error near line " + line + " : " +
                (e_== null ? err : err + ": " + e_.getMessage());
    }

}

