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
 
package com.zotoh.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.zotoh.core.util.CoreUte.bytesToChars;
import static com.zotoh.core.util.CoreUte.niceFPath;

import static com.zotoh.core.util.CoreUte.*;

/**
 * Wrapper on top of a File input stream such that it can 
 * delete itself from the file system when necessary. 
 *
 * @author kenl
 *
 */
public class SmartFileInputStream extends InputStream {
    
    protected boolean _deleteFile= false,
    _closed = true;
    private transient InputStream _inp;
    private File _fn;
    private long pos = 0L;

    /**
     * @param file
     * @param deleteFile
     */
    public SmartFileInputStream(File file, boolean deleteFile)    {
        tstObjArg("file", file) ;
        _deleteFile= deleteFile;
    	_fn= file;
    }

    
    /**
     * @param file
     */
    public SmartFileInputStream(File file)    {
    	this(file,false);
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException    {
        pre();
        return _inp.available();
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException    {
        pre();       
        int r = _inp.read();
        ++pos;
        return r;
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException    {
        return b==null ? -1 : read(b, 0, b.length);
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int offset, int len) throws IOException    {
        if (b==null) { return -1; }
        pre();
        int r = _inp.read(b, offset, len);
        if (r== -1 ) { 
            pos= -1;
        }   else {
            pos = pos + r;
        }
        return r;
    }

    /**
     * @param ch
     * @return
     * @throws IOException
     */
    public int read(char[] ch) throws IOException    {
        return ch==null ? -1 : read(ch, 0, ch.length);
    }

    
    /**
     * @param ch
     * @param offset
     * @param len
     * @return
     * @throws IOException
     */
    public int read(char[] ch, int offset, int len) throws IOException    {
        if (ch==null || len <= 0 || offset < 0) { return -1; }
        byte[] b = new byte[len];
        int r = read(b, offset, len);
        if (r > 0) { bytesToChars(b, ch, r); }
        return r;
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException    {
        if (n < 0L) { return -1L; }
        pre();
        long r= _inp.skip(n);
        if (r > 0) { pos +=  r; }
        return r;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException    {
    	_inp= StreamUte.close(_inp);
        _closed= true;
    }

    
    /* (non-Javadoc)
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public void mark(int readLimit)    {        
        if (_inp != null) {
            _inp.mark(readLimit);
        }
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    @Override
    public void reset() throws IOException    {
        
    	close();

    	_inp= new FileInputStream(_fn);
    	_closed=false;
    	pos=0;
    }


    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported()    {        return true;    }

    
    /**
     * @param dfile
     */
    public void setDelete(boolean dfile)    {        _deleteFile = dfile;    }

    
    /**
     * @throws Exception
     */
    public void delete() throws Exception    {
        close();
        if (_deleteFile && _fn != null) {        
        	_fn.delete();
        }
    }

    
    /**
     * @return
     * @throws IOException
     */
    public String getFilename() throws IOException    {        
        return niceFPath(_fn);
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()        {    	
        try { return getFilename(); } catch (Exception e) { return ""; }    
    }

    
    /**
     * @return
     */
    public long getPosition()        {                return pos;       }

    /**/
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable    {
        delete();
    }
    
    private void pre() throws IOException       {        
        if (_closed) { ready(); }
    }

    private void ready() throws IOException    {
        reset();
    }

    
    
    
}
