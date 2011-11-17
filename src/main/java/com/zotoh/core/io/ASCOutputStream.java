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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles a (ASCII) outputstream, adds a '\r' in front of a
 * '\n'. 
 *
 * @author kenl
 *
 */
public class ASCOutputStream extends FilterOutputStream {
    
    /**
     * @param os
     */
    public ASCOutputStream(OutputStream os)    {
        super(os);
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterOutputStream#write(int)
     */
    @Override
    public void write(int b)  throws IOException    {
        if (b == '\n') { out.write('\r'); }
        out.write(b);            
    }

    
    /* (non-Javadoc)
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte data[], int off, int len) throws IOException    {        
        if (data != null) for (int i = 0; i < len;  ++i) {        
            write( data[off + i]);
        }
    }
    
    
}
