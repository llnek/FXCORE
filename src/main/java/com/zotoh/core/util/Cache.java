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

import static com.zotoh.core.util.LangUte.AA;
import static com.zotoh.core.util.LangUte.MP;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simple object cache.
 *  
 * @author kenl
 *
 */
public class Cache {
	
    private final ReadWriteLock _rwLock= new ReentrantReadWriteLock();
    private final Map<Object,Long> _timeInCache= MP() ;
    private final boolean _cacheEnabled;
    private final long _refreshWait;
    private final Map<Object,Object> _items= MP();
    
    /**
     * @param refreshWait
     */
    public Cache(long refreshWait)    {
        _cacheEnabled = (refreshWait > 0);
        _refreshWait = refreshWait;
    }

    /**
     * @param key
     * @return
     */
    public boolean contains(Object key)    {
    	
        boolean result = false;
        
        if ( !_cacheEnabled  || key == null) {  return result;  }
        markForRead();
        try  {
            if (_items.containsKey(key))  {
                result = (timeInCache(key) < _refreshWait);
            }
        }
        finally  {
            clsRead();
        }
        
        return result;
    }
    
    /**
     * @param key
     * @return
     */
    public Object get(Object key)    {
    	
        Object result= null;
        
        if ( !_cacheEnabled || key==null)  {  return result;  }
        markForRead();        
        try  {
            if (_items.containsKey(key))  {
                result = _items.get(key);
            }
        }
        finally  {
            clsRead();
        }
        
        return result;
    }

    /**
     * @param key
     * @param item
     */
    public void put(Object key, Object item)    {
    	
        if ( ! _cacheEnabled || key==null || item== null) {
            return;
        }
        markForWrite();
        try   {
            _items.put(key, item);
            _timeInCache.put(key, System.currentTimeMillis()) ;
        }
        finally {
            clsWrite();
        }
    }

    /**
     * 
     */
    public void removeAll()    {
    	
        if (! _cacheEnabled) { return; }
        markForWrite();
        try  {
            _timeInCache.clear();
            _items.clear();
        }
        finally  {
            clsWrite();
        }
    }

    /**
     * 
     */
    public void scanAndClear()     {
        if (! _cacheEnabled) { return; }
        markForWrite();
        try  {
            Object[] keys= AA( _items.keySet() );
            Object k;
            for (int i=0; i < keys.length; ++i) {
                k= keys[i] ;
                if (timeInCache( k) > _refreshWait) {
                    _timeInCache.remove(k) ;
                    _items.remove(k) ;
                }
            }
        }
        finally {
            clsWrite();
        }        
    }
    
    private long timeInCache(Object key)    {
        Object t =_timeInCache.get(key);
        return t ==null ? -1L : ( System.currentTimeMillis() - (Long)t);
    }

    private void markForRead()    {
        _rwLock.readLock().lock();
    }

    private void clsRead()    {
        _rwLock.readLock().unlock();
    }

    private void markForWrite()    {
        _rwLock.writeLock().lock();
    }

    private void clsWrite()    {
        _rwLock.writeLock().unlock();
    }
    
    
    
}
