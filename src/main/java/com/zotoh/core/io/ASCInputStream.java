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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles an (ASCII) inputstream - skips over all '\r'
 * characters.
 *   
 * @author kenl
 *
 */
public class ASCInputStream extends FilterInputStream {
    
    /**
     * @param s
     */
    public ASCInputStream(InputStream s)    {
        super(s);
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException    {
        int c= in.read();
        
        if (c == '\r') {            
            c = in.read();
        }
        
        return c;
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte data[], int off, int len) throws IOException    {
        
        tstObjArg("input-data", data) ;
        
        if (len <= 0)        { return 0; }

        int i, c= read();
        
        if ( c == -1)  {        return c; }
        
        data[off] = (byte)c;
        i = 1;
        
        for (; i < len; ++i)  {                
            if ((c=read()) == -1)  {     break; }            
            if (c == '\r') {
                if ((c = in.read()) == -1) {                break; }
            }
            data[off + i] = (byte)c;
        }

        return i;
    }
    
    
    
    
}

