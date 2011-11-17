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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableList;
import static com.zotoh.core.util.StrUte.*;
import static com.zotoh.core.util.CoreUte.*;

/**
 * A class that maps the country-code to the country-name. 
 *
 * @author kenl
 *
 */
public class CountryCode implements CCodeSet, java.io.Serializable {
    
    private static final long serialVersionUID= 23123659349643L;
    private static Map< String,CountryCode> _names, _codes;
    private static List<CountryCode> _ccs;
    private String _name, _code;

    // -------------------------------- code starts here ------------------------------------
    
    static    {
        iniz();
    }
    
    /**
     * @return
     */
    public static List<CountryCode> getCodes()    {        return _ccs;    }

    /**
     * @param code
     * @return
     */
    public static boolean isUSA(String code)    {
        return eq( getUSACode() , code);
    }
    
    
    /**
     * @return
     */
    public static String getUSACode()    {        return "US";    }
    
    
    /**
     * @param code
     * @return
     */
    public static CountryCode getCountryViaCode(String code)    {
        return code==null ? null : _codes.get(code);
    }
    
    /**
     * @param nm
     * @return
     */
    public static CountryCode getCountryViaName(String nm)    {
        return nm==null ? null : _names.get(nm);
    }

    /**
     * @param code
     * @param name
     */
    public CountryCode(String code, String name)    {
        tstEStrArg("country-code", code) ;
        tstEStrArg("country", name) ;
        
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
    public boolean equals(Object obj)  {
        return obj instanceof CountryCode &&  _code.equals(obj.toString());
    }
        
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()    {        return _code;    }
    
    private static void iniz()    {
        CountryCode cc;
        String[] ss;
        
        _names= new TreeMap<String,CountryCode>();
        _codes= new TreeMap<String,CountryCode>();
        
        for (int i=0; i < CCODES.length; ++i) {
            ss= CCODES[i];
            cc=new CountryCode(ss[0], ss[1]);
            _names.put(ss[1], cc);
            _codes.put(ss[0], cc);
        }
        
        _ccs= unmodifiableList(
                new ArrayList<CountryCode>(_names.values())
                );
    }
    
    
}
