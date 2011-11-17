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

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.StrUte.isEmpty;

import com.zotoh.core.util.Tuple;


/**
 * Wrapper on top of  a SQL select statement. 
 *
 * @author kenl
 *
 */
public class SELECTStmt extends SQLStmt {
    
    private String _select="";

    /**
     * Create Query.
     * 
     * @param tbl the table name.
     * @return a stmt => "select * from tbl".
     */
    public static SELECTStmt simpleQry(String tbl)    {         
        return new SELECTStmt("*", tbl);
    }

    /**
     * Create Query.
     *  
     * @param sql   e.g. "select * from TABLE where a=?"
     * @param pms  list of one value.
     */
    public SELECTStmt(String sql, Tuple pms)    {        
        super(sql, pms);        
        getTable();
    }

    /**
     * @param sql
     */
    public SELECTStmt(String sql)    {
    	this (sql, new Tuple()) ;
    }
    
    /**
     * Create Query.
     * 
     * @param selects "col-1, col-2" or "*"
     * @param tbl from this table.
     */
    public SELECTStmt(String selects, String tbl)    {
        this(selects, tbl, "", new Tuple() );
    }
    
    /**
     * Create Query.
     * 
     * @param selects "col-1, col-2" or "*"
     * @param tbl from this table.
     * @param where empty or "col-3=? and col-4=?"
     * @param vals empty or list of 2 values.
     */
    public SELECTStmt(String selects, String tbl, String where, Tuple pms)     {
        this(selects, tbl, where, "" , pms);
    }

    /**
     * Create Query.
     * 
     * @param selects "col-1, col-2" or "*"
     * @param tbl from this table.
     * @param where empty or "col-3=? and col-4=?"
     * @param extra empty or " group by col-2 " or  " order by col-1 "
     * @param pms empty or list of 2 values.
     */
    public SELECTStmt(String selects, String tbl, 
            String where, String extra, Tuple pms)     {
        
        tstEStrArg("selects", selects);
        tstEStrArg("db-table", tbl);
        
        _select= selects;
        setTable( tbl);
        setWhere(where, extra);
        
        addQParams(pms);
    }

    private void setWhere(String where, String extra)     {
        StringBuilder bd= new StringBuilder(512)
        .append("SELECT ")
        .append(_select)
        .append(" FROM ")
        .append(getTable());

        if (! isEmpty(where)) {
            bd.append(" WHERE ").append(where);
        }
        
        if (! isEmpty(extra)) {
            bd.append(" ").append(extra);
        }

        setSQL(bd.toString());
    }

    
    
    
}
