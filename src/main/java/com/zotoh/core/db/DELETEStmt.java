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
import static com.zotoh.core.util.StrUte.nsb;

import com.zotoh.core.util.Tuple;

/**
 * Simple wrapper abstracting a delete SQL statement. 
 *
 * @author kenl
 *
 */
public class DELETEStmt extends SQLStmt {
    
    /**
     * Create a simple delete stmt such as "delete from XYZ".
     * 
     * @param table
     * @return
     */
    public static DELETEStmt simpleDelete(String table)    {         
        return new DELETEStmt("DELETE FROM " + nsb(table) , new Tuple() );
    }
    
    /**
     * Create a delete stmt based on the SQL provided.
     * 
     * @param sql   e,g.  "delete from XYZ where name=?"
     * @param params  e.g. [ "john" ]
     */
    public DELETEStmt(String sql , Tuple pms)    {
        super(sql , pms);
        getTable();
    }

    /**
     * @param sql
     */
    public DELETEStmt(String sql )    {
        this(sql , new Tuple() );
    }
    
    /**
     * Create a delete stmt and construct the sql inside based on the parameters
     * provided.
     * 
     * @param table e.g. XYZ
     * @param where e.g. name=? and age=?
     * @param params e.g. [ 'john' , 21 ]
     */
    public DELETEStmt(String table, String where, Tuple pms)    {
        tstEStrArg("db-table", table) ;
        setWhere( table, nsb(where));
        addQParams(pms);
    }

    private void setWhere(String tbl, String where)     {
        
        StringBuilder bd= new StringBuilder(512)
        .append("DELETE FROM ")
        .append( tbl );

        if (! isEmpty(where)) {
            bd.append(" WHERE ").append(where);
        }
        
        setSQL(bd.toString());
    }

}
