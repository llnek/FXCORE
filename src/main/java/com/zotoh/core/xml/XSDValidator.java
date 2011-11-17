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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * A XML reader that is preconfigured to do schema validation.
 * 
 * @author kenl
 *
 */
public class XSDValidator extends SaxHandler  {
    
    private static SchemaFactory s_xsdFac;
    static    {
        s_xsdFac= SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    /**
     * @param doc
     * @param sd
     * @return
     * @throws MalformedURLException
     */
    public boolean scanForErrors(InputStream doc, File sd) throws MalformedURLException    {
        return sd==null ? false : check(doc, sd.toURI().toURL() );
    }

    
    /**
     * @param doc
     * @param sd
     * @return
     */
    public boolean scanForErrors(InputStream doc, URL sd)    {
        return doc==null || sd==null ? false : check(doc, sd);
    }

    
    /**
     * 
     */
    public XSDValidator()
    {}

    private boolean check(InputStream doc, URL xsd)    {
        try        {
            getSV(xsd, this).
            validate( new SAXSource( sourceToInputSource( new StreamSource( doc))));
        }
        catch (IOException e) {
            push( new SAXException(e));
        }
        catch (SAXException e) {
            push(e);
        }

        return ! hasErrors();
    }

    private static synchronized Validator getSV(URL src, ErrorHandler err) throws SAXException    {
        Schema xsd= s_xsdFac.newSchema( src);
        Validator v= xsd.newValidator();
        v.setErrorHandler(err);
        return v;
    }

}
