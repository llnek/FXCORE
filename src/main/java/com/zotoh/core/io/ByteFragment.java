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

/**
 * Simple structure to store a view of a chunk of bytes. 
 *
 * @author kenl
 *
 */
public class ByteFragment implements java.io.Serializable {
    
    private static final long serialVersionUID = -3157516993124229948L;
    private int _len, _offsetPtr;
    private byte[] _buf;

    /**
     * @param bf
     * @param offset
     * @param length
     */
    public ByteFragment(byte[] bf, int offset, int length)    {
        _offsetPtr= offset; _buf=bf;  _len=length;
    }

    
    /**
     * @param buf
     */
    public ByteFragment(byte[] buf)    {
        this(buf, 0, buf.length);
    }

    
    /**
     * @return
     */
    public byte[] getBuf() { return _buf; }
    
    
    /**
     * @return
     */
    public int getOffset() { return _offsetPtr; }
    
    
    /**
     * @return
     */
    public int getLength() { return _len; }
    
    
    
    
}
