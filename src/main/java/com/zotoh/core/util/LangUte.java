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

import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public enum LangUte {
;

	private static Logger _log = getLogger(LangUte.class);
	public static Logger tlog() {        return _log;    }


	/**
	 * @return
	 */
	public static <T> List<T> LT() {
		return new ArrayList<T>();
	}
	
	/**
	 * @return
	 */
	public static <T> Set<T> ST() {
		return new HashSet<T>();
	}
	
	/**
	 * @return
	 */
	public static <K,V> Map<K,V> MP() {
		return new HashMap<K,V>();
	}
	
	/**
	 * @return
	 */
	public static <K,V> Map<K,V> TM() {
		return new TreeMap<K,V>();
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static <T> T[] AA( Class<T> z  , List<T> lst) {
	    T[] a = (T[]) Array.newInstance(z, lst.size());
	    for(int i = 0; i < a.length; ++i) {
	           a[i] = lst.get(i);
	    }
	    return a;
	}
	
    /**
     * @param lst
     * @return
     */
    public static <T> Object[] AA( List<T> lst) {
        return lst.toArray() ;
    }
	
	/**
	 * @param z
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] AA( Class<T> z  , Set<T> s) {
	    T[] a = (T[]) Array.newInstance(z, s.size());
	    int pos=0 ;
	    for(T t : s) {
           a[ pos++ ] = t ;	    	
	    }
	    return a;
	}
	
	/**
	 * @param z
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] AC( Class<T> z  , Collection<T> c) {
	    T[] a = (T[]) Array.newInstance(z, c.size());
	    int pos=0 ;
	    
	    for ( Iterator<T> it = c.iterator() ; it.hasNext(); ) {
	    	a[ pos++] = it.next();
	    }
	    
	    return a;
	}
	
    /**
     * @param s
     * @return
     */
    public static <T> Object[] AA( Set<T> s) {
        return s.toArray() ;
    }
	
}
