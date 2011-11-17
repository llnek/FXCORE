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

import java.io.IOException;

/**
 * Simple extension on top of <i>ByteArrayInputStream</i> to provide
 * users the abilities to access a range of byte[] from the
 * stream. 
 *
 * @author kenl
 *
 */
public class ByteFragmentInputStream extends java.io.ByteArrayInputStream  {
    
    /**
     * @param buf
     * @param offset
     * @param length
     */
    public ByteFragmentInputStream(byte[] buf, int offset, int length)    {
        super(buf, offset, length);
    }

    
    /**
     * @param bp
     */
    public ByteFragmentInputStream(ByteFragment bp)    {
        this(bp.getBuf(), bp.getOffset(), bp.getLength());
    }

    
    /**
     * @param buf
     */
    public ByteFragmentInputStream(byte[] buf)    {
        super(buf);
    }

    
    /**
     * @param roffset
     * @param len
     * @return
     * @throws IOException
     */
    public ByteFragment getFrag(int roffset, int len) throws IOException    {
        if (len > (count - roffset)) {
            throw new IOException("Not enough data in buffer");
        }
        return new ByteFragment(buf, roffset, len);
    }

    
    /**
     * @param len
     * @return
     * @throws IOException
     */
    public ByteFragment getFrag(int len) throws IOException    {
        if (len > (count - pos)) { 
            throw new IOException("Not enough data in buffer");
        }
        return new ByteFragment(buf, pos, len);
    }

    
    /**
     * @return
     */
    public ByteFragment getFrag()    {
        return new ByteFragment(buf, pos, count - pos);
    }

    
    /**
     * @return
     */
    public int getPos()        {                return pos;       }

    
    /**
     * @return
     */
    public byte[] getBuf()      {                return buf;       }
    
    
    
    
    
}

