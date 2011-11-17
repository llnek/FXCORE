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
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.util.Map;

import com.zotoh.core.crypto.Password;
import com.zotoh.core.util.Tuple;

/**
 * Wrapper on top of a SQL update statement. 
 *
 * @author kenl
 *
 */
public class UPDATEStmt extends WritableStmt {

    /**
     * @param row
     * @param where
     * @param vals
     */
    public UPDATEStmt(DBRow row, String where, Tuple pms)    {
        tstObjArg("sql-params", pms) ;
        tstObjArg("db-row", row) ;
        set(row, where, pms);
    }
    
    /**
     * @param sql
     * @param params
     */
    public UPDATEStmt(String sql, Tuple pms)    {
        super(sql, pms);
    }

    private void set(DBRow row, String where, Tuple pms)    {
        dbgData(row);        
        iniz(row, where, pms);        
    }
        
    private void iniz(DBRow row, String where, Tuple pms)     {
        
        StringBuilder b1= new StringBuilder(512);
        String table= row.getSQLTable();
        Password pwd;
        Map<String,Object> m = row.values();
        Object v;
        
        StringBuilder bf= new StringBuilder(1024)
        .append("UPDATE ")
        .append(table).
        append(" SET ");

        for (String k : m.keySet())         {
            addAndDelim(b1, " , ", k);
            v= m.get(k);                        
            
            if (v instanceof Password) {
                pwd= (Password) v;
                v= pwd.getAsEncoded();
            }
            
            if (isNull(v)) {
                b1.append("=NULL");
            }    else {
                b1.append("=?");
                _values.add(v);
                _cols.add(k);
            }
        }

        bf.append(b1);

        if (! isEmpty(where))         {
            bf.append(" WHERE ").append(where);            
            addQParams(pms);
        }
        
        setSQL(bf.toString());
    }
    
    
    
    
}
