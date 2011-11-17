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
 
package com.zotoh.core.util;

import static com.zotoh.core.io.StreamUte.asStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.zotoh.core.io.ByteOStream;

/**
 * Utililties for handling byte[] conversions to/from numbers. 
 *
 * @author kenl
 *
 */
public enum ByteUte  {
;    

	/**
	 * @param ca
	 * @return
	 */
	public static byte[] convertCharsToBytes(char[] ca) { 
		byte[] ba = new byte[ ca.length * 2 ]; 
		java.nio.ByteBuffer.wrap(ba).asCharBuffer().put(ca); 
		return ba; 
	}
		
	/**
	 * @param ba
	 * @return
	 */
	public static char[] convertBytesToChars(byte[] ba) { 
		char[] ca = new char[ba.length / 2]; 
		java.nio.ByteBuffer.wrap(ba).asCharBuffer().get(ca); 
		return ca; 
	}
	
    /**
     * @param bits
     * @return
     * @throws IOException
     */
    public static long readAsLong(byte[] bits) throws IOException      {
        return new DataInputStream( asStream(bits)).readLong();      
    }

    /**
     * @param bits
     * @return
     * @throws IOException
     */
    public static int readAsInt(byte[] bits) throws IOException      {
        return new DataInputStream( asStream(bits)).readInt();      
    }
    
    /**
     * @param n
     * @return
     * @throws IOException
     */
    public static byte[] readAsBytes(long n) throws IOException     {
        ByteOStream baos= new ByteOStream();
        DataOutputStream ds= new DataOutputStream( baos);      
        ds.writeLong(n);
        ds.flush();
        return baos.asBytes();
    }

    /**
     * @param n
     * @return
     * @throws IOException
     */
    public static byte[] readAsBytes(int n) throws IOException     {
        ByteOStream baos= new ByteOStream();
        DataOutputStream ds= new DataOutputStream( baos);      
        ds.writeInt(n);
        ds.flush();
        return baos.asBytes();
    }
        
    
}
