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

import java.lang.reflect.Array;


/**
 * A rough clone of some Hash code in public domain.
 * 
 * @author kenl
 *
 */
public enum HashUte  {
;

    private static final int fODD_PRIME_NUMBER = 83;
    private static final int SEED = 71;

    /**
     * @param b
     * @return
     */
    public static int hash( boolean b ) {
      return firstTerm( SEED ) + ( b ? 1 : 0 );
    }

    /**
     * @param c
     * @return
     */
    public static int hash(char c ) {
      return firstTerm( SEED ) + (int)c;
    }

    /**
     * @param n
     * @return
     */
    public static int hash( int n ) {        
        return hash(SEED, n);
    }

    /**
     * @param n
     * @return
     */
    public static int hash(  long n ) {
      return firstTerm(SEED)  + (int)( n ^ (n >>> 32) );
    }

    /**
     * @param f
     * @return
     */
    public static int hash( float f ) {
      return hash( SEED, Float.floatToIntBits(f) );
    }

    /**
     * @param d
     * @return
     */
    public static int hash( double d ) {
      return hash( SEED, Double.doubleToLongBits(d) );
    }

    /**
     * @param obj
     * @return
     */
    public static int hash( Object obj ) {
        return hash(SEED, obj) ;
    }

    private static int hash( int seed, Object obj) {
        
        int len, result = seed;
        if ( obj == null) {
          result = hash(result, 0);
        }
        else   if ( ! isArray(obj) ) {
          result = hash(result, obj.hashCode());
        }
        else     {
          len= Array.getLength(obj);
          for ( int i = 0; i < len; ++i ) {
             result = hash(result, Array.get(obj,i));
          }
        }
        
        return result;
    }
    
    private static int hash(int seed, int n) {
        return firstTerm( seed ) + n;        
    }
    
    private static int firstTerm( int aSeed ){
      return fODD_PRIME_NUMBER * aSeed;
    }

    private static boolean isArray(Object obj){
      return obj.getClass().isArray();
    }
    
  } 

