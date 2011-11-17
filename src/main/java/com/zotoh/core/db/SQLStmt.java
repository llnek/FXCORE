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

import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;

import java.util.List;
import java.util.Map;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * Abstract a SQL statement. 
 *
 * @author kenl
 *
 */
public abstract class SQLStmt {
    
    protected final List<Object> _values= LT();
    private boolean _skipCache;
    private String _tbl="", _sql="";

    private transient Logger _log= getLogger(SQLStmt.class);
    public Logger tlog() { return _log; }    

    /**
     * @param b
     */
    public void setDirect(boolean b)    {        _skipCache= b;    }
    
    /**
     * @return
     */
    public boolean isDirect()    {        return  _skipCache;    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()    {        return _sql;    }

    /**
     * @param pms
     */
    public SQLStmt setQParams( Tuple pms)    {
        _values.clear();
        return addQParams(pms);
    }
    
    /**
     * @return
     */
    public Object[] getQParams()    {
        return _values.toArray() ;
    }

    
    /**
     * @param sql
     * @param params
     */
    protected SQLStmt(StringBuilder sql, Tuple pms)    {
        this(sql.toString(), pms);
    }
        
    /**
     * @param sql
     * @param params
     */
    protected SQLStmt(String sql, Tuple pms)    {
        setQParams(pms);
        setSQL(sql);
    }
    
    /**
     * @param sql
     */
    protected SQLStmt(String sql)    {
        setSQL(sql);
    }

    /**
     * 
     */
    protected SQLStmt()
    {}
    
    /**
     * @param sql
     */
    protected void setSQL(String sql)    {
        _sql= nsb(sql);
        if (isEmpty(_sql)) {
            getTable();
        }
    }
    
    /**
     * @param vals
     */
    public SQLStmt addQParams( Tuple pms)    {
    	
    	for (int i=0; i < pms.size(); ++i) {
    		_values.add( pms.get(i) );
    	}
    	return this;
    }
        
    /**
     * @param row
     */
    protected void dbgData(DBRow row)     {
        
        if ( ! tlog().isDebugEnabled())
        return;
        
        StringBuilder msg= new StringBuilder(1024);
        Map<String,Object> m= row.values();
        msg.append("SQLStmt: DbRow: ###############################################\n");
        for (Map.Entry<String,Object> en : m.entrySet()) {
            msg.append("fld= ").append(en.getKey()).append(",value= ").append(en.getValue()).append("\n");
        }
        msg.append("###############################################");
        tlog().debug(msg.toString());
    }
    
    /**
     * @param table
     */
    protected void setTable(String table) { _tbl= nsb(table); }
    
    
    /**
     * @return
     */
    public String getTable()     {
        if ( isEmpty(_tbl)) {
            String sql= toString(), s= sql.toLowerCase();
            int pos= s.indexOf("from");
            if (pos > 0) {
                s= sql.substring(pos+4).trim();
                int b= s.indexOf('\t');
                int a= s.indexOf(' ');
                if (b < 0) { pos = a; }
                else
                if (a < 0) { pos = b; }
                else {
                    pos= Math.min(a, b);
                }
            }
            if (pos > 0) {
                _tbl= s.substring(0,pos);
            }
        }
        
        return _tbl;
    }
    
    
}
