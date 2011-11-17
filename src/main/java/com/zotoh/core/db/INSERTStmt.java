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
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.StrUte.addAndDelim;

import java.util.Map;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.core.util.Tuple;

/**
 * Wrapper abstracting a SQL Insert statement. 
 *
 * @author kenl
 *
 */
public class INSERTStmt extends WritableStmt {
        
    /**
     * Create an insert stmt from this sql and params.
     * 
     * @param sql
     * @param params
     */
    public INSERTStmt(String sql, Tuple pms)    {
        super(sql, pms);
    }
    
    /**
     * Create an insert stsmt from this row.
     * 
     * @param row
     */
    public INSERTStmt(DBRow row)    {
        
        tstObjArg("db-row", row);
        tstEStrArg("db-table", row.getSQLTable());
        
        dbgData(row);        
        iniz(row);
    }

    /**
     * Get the set of columns in this insert stmt.
     * 
     * @return
     */
    public String[] getCols()    {        
    	return AA( String.class, _cols ); }
    
    private void iniz(DBRow row)     {
        
        Map<String,Object> m= row.values();
        String table = row.getSQLTable();
        
        StringBuilder b2= new StringBuilder(512),
        b1= new StringBuilder(512),
        bf= new StringBuilder(1024);
        
        Object v;        
        String s;

        bf.append("INSERT INTO  ").append(table)
        .append(" (");
        
        for (String k : m.keySet())         {
            v= m.get(k);            
            if (isNull(v)) {
                s= "NULL";
            }   else  { 
                s= "?"; _values.add(v); 
            }            
            
            addAndDelim(b2, ",", s) ;
            addAndDelim(b1, ",", k) ;
            _cols.add(k);                       
        }

        bf.append(b1).append(") VALUES (").
        append(b2).append(")");
        
        setSQL(bf.toString() );
    }
    
    
    
    
}
