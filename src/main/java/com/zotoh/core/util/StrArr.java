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
import static com.zotoh.core.util.StrUte.addAndDelim;

import java.util.List;

/**
 * Wrapper on top of a string[]. 
 *
 * @author kenl
 *
 */
public class StrArr implements java.io.Serializable {
    
    private static final long serialVersionUID= 981284723453L;    
    private final List<String> _strs= LT();

    /**
     * @param a
     */
    public StrArr(String... a)    {
        add(a);
    }
    
    
    /**
     * 
     */
    public StrArr()
    {}
    
    
    /**
     * @param s
     */
    public void add(String s)     {
        if (s != null) { _strs.add(s); }
    }
    
    
    /**
     * @param a
     */
    public void add(String... a)     {
        for (int i=0; i < a.length; ++i)        {
            add(a [i] ) ; 
        }
    }
    
    
    /**
     * @return
     */
    public String[] toArray()     {
        return _strs.toArray( new String[0] );
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()     {
        StringBuilder bf=new StringBuilder(512);
        for (int i=0; i < _strs.size(); ++i)        {
            addAndDelim(bf, "|", _strs.get(i)) ;
        }
        return bf.toString();
    }
    
    
    /**
     * @return
     */
    public int getSize()   {                return   _strs.size();       }
    
    
    /**
     * @return
     */
    public String getFirst()    {
        return _strs.size() > 0 ? _strs.get(0) : null;
    }

    
    /**
     * @return
     */
    public String getLast()    {
        return _strs.size() > 0 ? _strs.get( _strs.size()-1) : null;
    }

    
    /**
     * @param str
     * @return
     */
    public boolean contains(String str) {
        for (String s : _strs) {
            if (s.equals(str)) {             return true; }
        }
        return false;
    }

}
