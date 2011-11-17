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
 

package com.zotoh.crypto;

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataSource;

import com.zotoh.core.io.SmartFileInputStream;

/**
 * @author kenl
 *
 */
public class SmDataSource implements DataSource {
    
    private String _ctype;
    private byte[] _bits;
    private File _fn;

    /**
     * @param content
     * @param contentType
     */
    public SmDataSource(File content, String contentType)    {
        tstObjArg("file", content) ;       
        _ctype= nsb(contentType);
        _fn= content;
    }

    
    /**
     * @param content
     * @param contentType
     */
    public SmDataSource(byte[] content, String contentType)    {
        tstObjArg("bits", content) ;
        _ctype= nsb(contentType);
        _bits= content;
    }

    
    /**
     * @param content
     */
    public SmDataSource(File content)    {
    	this(content, "");
    }

    
    /**
     * @param content
     */
    public SmDataSource(byte[] content)    {
    	this(content, "");
    }

    
    /* (non-Javadoc)
     * @see javax.activation.DataSource#getContentType()
     */
    public String getContentType()        {        
        return _ctype;    
    }

    
    /* (non-Javadoc)
     * @see javax.activation.DataSource#getInputStream()
     */
    public InputStream getInputStream() throws IOException     {
        return _fn==null ? asStream(_bits) : new SmartFileInputStream(_fn);
    }

    
    /* (non-Javadoc)
     * @see javax.activation.DataSource#getName()
     */
    public String getName()        {        
        return "Unknown";    
    }

    
    /* (non-Javadoc)
     * @see javax.activation.DataSource#getOutputStream()
     */
    public java.io.OutputStream getOutputStream() throws IOException     {
        throw new IOException("Not implemented");
    }

}
