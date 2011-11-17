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

import java.util.Comparator;
import java.util.TreeMap;

/**
 * A map that has case-ignored string keys. 
 *
 * @author kenl
 *
 * @param <T>
 */
public class NCMap<T> extends TreeMap<String, T> implements java.io.Serializable {
    
    private static final long serialVersionUID = -3637175588593032279L;

    /**
     * 
     */
    public NCMap()    {
        super(new NoCase<String>());
    }

    private static class NoCase<T> implements Comparator<T>     {
        public int compare(T o1, T o2)        {
            String s1 = o1 == null ? "" : o1.toString();
            String s2 = o2 == null ? "" : o2.toString();
            return s1.toUpperCase().compareTo(s2.toUpperCase());
        }
    }

}
