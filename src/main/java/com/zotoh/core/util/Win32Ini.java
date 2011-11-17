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


import static com.zotoh.core.util.LangUte.* ;

import static com.zotoh.core.util.StrUte.isBlank;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.Map;

import com.zotoh.core.util.Logger;
 
/**
 * Config class that can parse a MS-Windows style .INI file. 
 *
 * @author kenl
 *
 */
public class Win32Ini implements java.io.Serializable {
    
    private Logger ilog() {       return _log=getLogger(Win32Ini.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {         return _log==null ? ilog() : _log;    }    
    
    private static final long serialVersionUID= -873895734543L;
    private final NCMap< NCMap<String> > _secs;

    /**
     * @param iniFilePath
     * @throws IOException
     */
    public Win32Ini(String iniFilePath) throws IOException     {
        _secs= new NCMap< NCMap<String> >();
        parse(iniFilePath);
    }

    
    /**
     * @param section
     * @return
     */
    public NCMap<String> getSection(String section)    {
        return _secs.get(section);
    }
    
    /**
     * @return
     */
    public String[] getSections()    {
        return AA(String.class,  _secs.keySet() );
    }

    
    /**
     * @param section
     * @param key
     * @return
     */
    public String getValueAsString(String section, String key)    {
        Map<?,?> m = getSection(section);
        return nsb( m==null ? null : m.get(key) );
    }

    
    /**
     * @param section
     * @param key
     * @return
     */
    public int getValueAsInt(String section, String key)    {
        return 
        Integer.parseInt(getValueAsString(section, key));
    }

    
    /**
     * @param ps
     */
    public void dbgShow(PrintStream ps)    {
        Map<String,String> assocs;
        String value;
        for (String key : _secs.keySet())        {
            ps.println("[" + key + "]");
            assocs= _secs.get(key);
            for (String nm : assocs.keySet())            {
                value=assocs.get(nm);
                ps.println(key + "=" + value);
            }
        }
    }

    /**
     * @param iniFilePath
     * @throws IOException
     */
    protected void parse(String iniFilePath) throws IOException     {
        
        LineNumberReader rdr;
        NCMap<String> hdr;
        String line, s;
        int pos;

        rdr= new LineNumberReader(new FileReader(iniFilePath));
        hdr=null;
        while ((line = rdr.readLine()) != null)        {
            
            line= line.trim();
            
            // look for a section 
            if (line.startsWith("[") && line.endsWith("]")) {
                
                s = line.substring(1, line.length() - 1).trim();
                
                if ( isEmpty(s)) 
                    throw new IOException("Bad INI line: " + rdr.getLineNumber());
                
                _secs.put(s,hdr= new NCMap<String>());
            }
            else
            if ( line.length() == 0 || line.charAt(0) == '#' || hdr==null) {
                // skip blank or comment lines                
            }
            else
            if ((pos = line.indexOf('=')) > 0)            {
                s= line.substring(0, pos).trim();
                if ( !isBlank(s)) {
                    hdr.put(s, line.substring(pos + 1).trim() );
                }
            }
            else {
                throw new IOException("Bad INI line: " + rdr.getLineNumber());                
            }
            
        }
    }

}
