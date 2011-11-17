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

import static com.zotoh.core.xml.XmlUte.newSaxValidator;
import static javax.xml.transform.sax.SAXSource.sourceToInputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.zotoh.core.io.StreamUte;

/**
 * A XML reader that is preconfigured to do schema validation.
 * 
 * @author kenl
 *
 */
public class DTDValidator extends SaxHandler {
    
    private StreamSource _dtd;

    /**
     * @param doc
     * @param dtd
     * @return
     */
    public boolean scanForErrors(InputStream doc, URL dtd)    {
        return check(doc, dtd);
    }

    
    /**
     * 
     */
    public DTDValidator()
    {}

    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
     */
    @Override
    public InputSource resolveEntity (String publicId,  String systemId)
    throws SAXException, IOException     {
        return sourceToInputSource(_dtd);
    }
    
    /**
     * @param doc
     * @param dtd
     * @return
     */
    private boolean check(InputStream doc, URL dtd)     {
        
        InputStream inp= null;
        try         {
            inp= dtd.openStream();
        }
        catch (IOException e) {
            push(new SAXException(e));
        }

        if (! hasErrors())
        try     {
            XMLReader rdr= newSaxValidator().getXMLReader();
            _dtd= new StreamSource(inp);
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
        finally {
            _dtd=null;
            StreamUte.close(inp);
        }

        return hasErrors();
    }

}
