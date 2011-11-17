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

import static com.zotoh.core.util.CoreUte.isNil;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.nsn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author kenl
 *
 * @param <K>
 * @param <V>
 */
public class MValsMap<K,V>  implements Map<K, List<V>> {

    private final Map<K, List<V>> _map = new LinkedHashMap<K, List<V>>();

    /**
     * 
     */
    public MValsMap()         
    {}

    /**
     * @param m
     */
    public MValsMap(Map<K, V> m) {
        if (m!=null) for (K key : m.keySet()) {
            add(key, m.get(key));
        }
    }

    /**
     * @param key
     * @param value
     */
    public void add(K key, V value) {
        maybeGet(key).add(value);
    }

    
    /**
     * @param key
     * @return
     */
    public V getFirst(K key) {
        List<V> lst = get(key);
        return isNil(lst) ? null : lst.get(0);
    }

    private List<V> maybeGet(K key) {
        List<V> lst = get(key);
        if (lst == null) {
            put(key, lst = createValueList());
        }
        return lst;
    }

    private List<V> createValueList() {
        return new ArrayList<V>();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + dbg() + "]";
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {        _map.clear();    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return _map.containsKey(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return _map.containsValue(value);
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, List<V>>> entrySet() {
        return _map.entrySet();
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return (obj instanceof Map) ? _map.equals(obj) : false;
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public List<V> get(Object key) {        return _map.get(key);    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {        return _map.hashCode();    }

    
    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {        return _map.isEmpty();    }

    
    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {        return _map.keySet();    }

    
    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public List<V> put(K key, List<V> value) {
        return _map.put(key, value);
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends List<V>> t) {
        _map.putAll(t);
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public List<V> remove(Object key) {
        return _map.remove(key);
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {        return _map.size();    }

    
    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection<List<V>> values() {
        return _map.values();
    }
    
    private String dbg() {
        StringBuilder b = new StringBuilder();
        for (Object key : keySet()) {
            for (Object value : get(key)) {
                if ( b.length()>0) { b.append('|'); }
                b.append(nsn(key));
                if (value != null) {
                    b.append('=').append(nsb(value));
                }
            }
        }                
        return b.toString();
    }

}



