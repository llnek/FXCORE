/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author kenl
 *
 */
public enum SeqNumGen {
	
	INSTANCE ;
    
    /**
     * @return
     */
    public static SeqNumGen getInstance() { return INSTANCE; }
    
    private static AtomicInteger _numInt= new AtomicInteger(1);
    private static AtomicLong _num= new AtomicLong(1L);
    
    /**
     * @return
     */
    public int nextInt() {
        int n= _numInt.getAndIncrement();
        if (n==Integer.MAX_VALUE) {
        	_numInt.set(1) ;
        }
        return n;
    }
    
    /**
     * @return
     */
    public long next() {
        long ln= _num.getAndIncrement();
        if ( ln== Long.MAX_VALUE) {
        	_num.set(1L ) ;
        }
        return ln;
    }
    
}
