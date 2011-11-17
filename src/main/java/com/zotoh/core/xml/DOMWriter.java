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

import static com.zotoh.core.util.CoreUte.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Document;

import com.zotoh.core.util.Logger;

import com.zotoh.core.util.CoreVars;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static com.zotoh.core.util.LoggerFactory.getLogger;

/**
 * Simple class to output a DOM object to string or file. 
 *
 * @author kenl
 *
 */
public class DOMWriter implements XmlVars, CoreVars {
    
    private transient Logger _log= getLogger(DOMWriter.class);  
    public Logger tlog() {   return _log;    }    
    private transient Writer _wtr;
	
	/**
	 * Convert a DOM object to string, allowing for nice indentation if required.
	 * 
	 * @param doc the DOM.
	 * @param indent true if nice indentation is needed.
	 * @return the string.
	 * @throws IOException 
	 */	
    public static String writeOneDoc(Document doc) throws IOException    {        
        return doc==null ? null : new DOMWriter().write(doc).getWriter().toString();
    }


    /**
     * @param writer
     */
    public DOMWriter(Writer writer)    {
        setWriter(writer);
    }

    
    /**
     * 
     */
    public DOMWriter()    {
        this(new StringWriter());
    }

    
    /**
     * @param writer
     * @return
     */
    public DOMWriter setWriter(Writer writer)    {
        tstObjArg("writer", writer) ;
        _wtr= writer; 
        return this;
    }

    
    /**
     * @return
     */
    public Writer getWriter()        {            	return _wtr;       }

    
    /**
     * @param doc
     * @return
     * @throws IOException
     */
    public DOMWriter write(Document doc) throws IOException    {
        
        if (doc != null)
        try  {
            Transformer t= TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(_wtr));
        }
        catch (TransformerException e) {
            throw new IOException(e);
        }

        return this;
    }

}
