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

import static com.zotoh.core.util.CoreUte.*;

/**
 * Simple structure/wrapper that associates a name with an object. 
 *
 * @author kenl
 *
 * @param <T>
 */
public class NmValue<T> implements Comparable<T> {
    
    private String _id="";
    private T _obj;

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0)    {
        if (arg0 instanceof NmValue) {        
            return _id.compareTo( 
                    ((NmValue<?>) arg0).getName());
        }
        return 0;
    }

    
    /**
     * @param n
     */
    public void setName(String n)    {        
        _id=n;    
    }

    
    /**
     * @param obj
     */
    public void setObject(T obj)        {        
        _obj=obj;    
    }

    
    /**
     * @return
     */
    public String getName()   {         return _id;    }

    
    /**
     * @return
     */
    public T getObject()    {         return _obj;    }

    
    /**
     * @param id
     * @param obj
     */
    public NmValue(String id, T obj)     {
        tstEStrArg("id", id) ;
        _obj=obj;
        _id=id;
    }

    
    /**
     * 
     */
    public NmValue()
    {}

}
