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


import static com.zotoh.core.util.LangUte.LT;

import java.util.List;


/**
 * @author kenl
 *
 */
public class Tuple {
    
    private Object[] _objs;
    
    /**
     * @param objs
     */
    public Tuple(Object ...objs ) {
        List<Object> lst= LT();
        for (Object o : objs) {
            //CoreUte.tstObjArg("tuple value", o);
            lst.add(o);
        }
        _objs= lst.toArray();
    }
    
    
    /**
     * @return
     */
    public int size() { return _objs.length; }
    
    
    /**
     * @param pos
     * @return
     */
    public Object get(int pos) {
        if ( pos >=0 && pos < _objs.length ) {  
        	return _objs[pos]; 
    	}
        throw new IllegalArgumentException("Array out of bound: pos: " + pos);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StrUte.join(_objs, "|");
    }
    
    
    
    
}


