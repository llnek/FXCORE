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
 
package com.zotoh.core.db;

import static com.zotoh.core.util.CoreUte.isNull;
import static com.zotoh.core.util.CoreUte.nilToNull;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.StrUte;

/**
 * Wrapper for a row of SQL table data.  
 *
 * @author kenl
 *
 */
public class DBRow implements DBVars, java.io.Serializable {
    
    private static final long serialVersionUID = -1112175967176488069L;
    private Map<String, Object> _map= MP();
    
    private Logger ilog() { return _log= getLogger(DBRow.class); }
    private transient Logger _log= ilog();
    public Logger tlog() { return _log==null ? ilog() : _log ;   }
    private String _tbl="";

    /**
     * Add col & data to the row.
     * 
     * @param  nameVals
     */
    public void add(Map<String,Object> colVals) {
        if ( colVals != null) for (Map.Entry<String,Object> en :  colVals.entrySet()) {
            add( en.getKey(), en.getValue()) ;
        }        
    }
    
    /**
     * Add a column & value.
     * 
     * @param col
     * @param value
     */
    public void add(String col, Object value)    {
        if (! StrUte.isEmpty(col)) { 
        	_map.put( col.toUpperCase(), nilToNull(value)); }
    }

    /**
     * Add a column with NULL value.
     * 
     * @param col
     */
    public void add(String col)    {
        add(col, null);
    }
    
    /**
     * @return
     */
    public boolean isEmpty() { return _map.size()==0; }
    
    /**
     * 
     * @param col
     * @return
     */
    public Object remove(String col)    {
        Object obj= col==null ? null : _map.remove(col.toUpperCase()) ;
        return isNull(obj) ? null : obj;
    }
    
    /**
     * @param col
     * @return
     */
    public boolean exists(String col)    {
        return col==null ? false : _map.containsKey( col.toUpperCase() );
    }
    
    /**
     * @return Table name.
     */
    public String getSQLTable()  {    return _tbl;   }

    /**
     * 
     */
    public void clear () {        _map.clear();    }
    
    /**
     * @return immutable map
     */
    public Map<String,Object> values()    {        
        return unmodifiableMap(_map);    
    }

    /**
     * Get value of column, if column is DB-NULL, returns null.
     * 
     * @param col
     * @return 
     */
    public Object get(String col)    {
        Object obj= col==null ? null : _map.get(col.toUpperCase() ) ;
        return isNull(obj) ? null : obj;
    }

    /**/
    public void dbg()    {
        
        if ( ! tlog().isDebugEnabled()) 
        return;
        
        StringBuilder bf = new StringBuilder(1024);        
        for (Map.Entry<String,Object> en : _map.entrySet()) {
            
            if (bf.length() > 0) { bf.append("\n") ; }                        
            bf.append( en.getKey() )
            .append("=\"")
            .append(nsb( en.getValue() ))
            .append( "\"" ) ;
            
        }
        
         tlog().debug(bf.toString());
    }
    
    /**
     * Constructor.
     * 
     * @param tbl Table name.
     */
    public DBRow(String tbl) { 
        _tbl= nsb(tbl); 
    }
    
    /**
     * Constructor.
     * 
     * @param bagOfNameValues columns & values.
     */
    public DBRow(Map<String,Object> colVals)  {
        add(colVals);
    }    
    
    /**
     * @return
     */
    public int size() {         return _map.size();    }
    
}
