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

import static javax.xml.transform.sax.SAXSource.sourceToInputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.zotoh.core.io.StreamUte;

/**
 * Implementation of a simple XML scanner, which basically scans for xml syntax errors, nothing more.
 *
 * @author kenl
 *
 */
public class XmlScanner extends SaxHandler  {
    
    /**
     * @param doc
     * @return
     */
    public boolean scan(URL doc)     {
        
        InputStream inp = null;
        boolean ok= false;
        if (doc != null)
        try         {
            ok=scan( inp = doc.openStream() );            
        }
        catch (IOException e) {
            push(new SAXException(e));            
        }
        finally {
            StreamUte.close(inp);
        }
        return ok;
    }
    
    
    /**
     * @param doc
     * @return
     */
    public boolean scan(InputStream doc)    {
        
        if (doc != null)
        try        {
            XMLReader rdr= XmlUte.newSaxParser().getXMLReader();
            rdr.setContentHandler(this);
            rdr.setEntityResolver(this);
            rdr.setErrorHandler(this);
            rdr.parse(sourceToInputSource(new StreamSource(doc)));
        }
        catch (IOException e) {
            push(new SAXException(e));
        }
        catch (SAXException e) {
            push(e);
        }

        return ! hasErrors();
    }

	
	/**
	 * 
	 */
	public XmlScanner()
	{}

}
