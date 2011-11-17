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


import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.TM;
import static com.zotoh.core.util.StrUte.nsb;

import java.util.List;
import java.util.Map;

/**
 * A class that maps the state-code to the state-name. 
 *
 * @author kenl
 *
 */
public class USAState implements USStateSet, java.io.Serializable {
    
    private static final long serialVersionUID= 23123659349643L;
    private static Map<String, USAState> _names , _codes ;
    private static List<USAState> _ccs;
    private String _name, _code ;

    static    {
        iniz();
    }
    
    /**
     * @return
     */
    public static List<USAState> getCodes()    {        return _ccs;    }

    
    /**
     * @param code
     * @return
     */
    public static USAState getStateViaCode(String code)    {
        return _codes.get( nsb(code));
    }

    
    /**
     * @param nm
     * @return
     */
    public static USAState getStateViaName(String nm)    {
        return _names.get( nsb(nm));
    }

    
    /**
     * @param code
     * @param name
     */
    public USAState(String code, String name)    {
        tstEStrArg("state-code", code) ;
        tstEStrArg("state", name) ;
        _name= name;        
        _code= code;
    }
    
    
    /**
     * @return
     */
    public String getCode()    {        return _code;    }
    
    
    /**
     * @return
     */
    public String getName()    {        return _name;    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)    {
        return obj instanceof USAState && _code.equals( nsb(obj)) ;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()    {        return _code;    }
    
    private static void iniz()    {
        
        _names= TM();
        _codes= TM();
        
        USAState cc;
        String[] ss;
                
        for (int i=0; i < USSTATES.length; ++i) {
            ss= USSTATES[i];
            cc=new USAState( ss[0], ss[1] );
            _names.put( ss[1], cc);
            _codes.put( ss[0], cc);
        }
        
        _ccs=LT();
        _ccs.addAll( _names.values() ) ;
        
    }
    
    
    
}
