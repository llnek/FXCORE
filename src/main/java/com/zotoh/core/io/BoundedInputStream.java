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

import static com.zotoh.core.util.CoreUte.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Counts an exact number of bytes from the stream.
 * 
 * @author kenl
 *
 */
public class BoundedInputStream extends java.io.FilterInputStream {
    
    private int _count;

    /**
     * @param inp
     * @param bytesToRead
     */
    public BoundedInputStream(InputStream inp, int bytesToRead)    {
        super(inp);
        _count = bytesToRead;
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException    {
        
        if (_count <= 0) {        return -1; }
        
        --_count;
        
        int c= super.read();
        if (c < 0)  { throw new IOException("No more data to read"); }
        
        return c;
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException    {
        return read(b, 0, b==null ? 0 : b.length);
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException    {
        
        tstObjArg("input-bytes", b) ;
        
        if (_count <= 0 || b==null) {        return -1; }

        int c;
        
        if (_count <  len)  { len = _count; }
        c=in.read(b, off, len);
        if (c < 0)  { throw new IOException("No more data to read"); }
        
        _count -=   c;
        
        return c;
    }

    
    /**
     * @return
     */
    public boolean hasMore()    {                return _count > 0;       }
    
    
    
    
}
