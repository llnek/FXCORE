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

import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.StrUte.nsb;

import java.util.StringTokenizer;

/**
 * Cardinality definition.  Accepted syntaxes are:
 * {n,m} or (n,m) or {n} or (n)
 *  
 * @author kenl
 *
 */
public class Cardinality implements java.io.Serializable  {
    
    private static final long serialVersionUID= -7593300202211134L;
    private int _max, _min;

    /**
     * @return
     */
    public boolean isRequired()    {         return _min > 0;    }
    
    /**
     * @return
     */
    public int getMaxOccurs()     {        return _max;    }


    /**
     * @return
     */
    public int getMinOccurs()     {         return _min;    }

    
    /**
     * @param c
     */
    public Cardinality(String c)      {        
        StringTokenizer tkz= new StringTokenizer( nsb(c), "{}(), \t\n\b\r\f");
        String s0, s, N= "N";
        int sz= tkz.countTokens();

        _max= _min = -1;
        switch (sz) {
            case 1:
                s= tkz.nextToken();
                _min= _max= (N.equalsIgnoreCase(s)) ?  Integer.MAX_VALUE : asInt(s, -1) ;
            break;
            
            case 2:
                s0= tkz.nextToken();
                s= tkz.nextToken();
                _max= (N.equalsIgnoreCase(s)) ?  Integer.MAX_VALUE : asInt(s, -1) ;
                _min= asInt(s0, -1);
            break;
        }

        _max= Math.max(_max, 0);
        _min= Math.max(_min, 0);
        
    }

    
    /**
     * @param min
     * @param max
     */
    public Cardinality(int min, int max)     {
        this("{" + min + "," + max + "}");
    }
    
    
}
