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

import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.iceq;

/**
 * Constants for list of common db vendors. 
 *
 * @author kenl
 *
 */
public enum DBVendor implements DBVars {
    
    POSTGRESQL(S_POSTGRESQL),
    SQLSERVER( S_MSSQL),
    ORACLE(S_ORACLE),
    MYSQL(S_MYSQL),
    H2(S_H2),
    HSQLDB(S_HSQLDB),
    DB2(S_DB2),
    DERBY(S_DERBY),
    NOIDEA("?");

    private boolean _upper=true, 
            _lower=false, 
            _mixed=false;
    private String _prod,
        _ver,
        _id;
    
    /**
     * @param s
     * @return
     */
    public static DBVendor fromString(String s)     {
        if ( iceq(S_POSTGRESQL,s)) return POSTGRESQL;
        if ( iceq(S_MSSQL,s)) return SQLSERVER;
        if ( iceq(S_H2,s)) return H2;
        if ( iceq(S_HYPERSQL,s)) return HSQLDB;
        if ( iceq(S_HSQLDB,s)) return HSQLDB;
        if ( iceq(S_MYSQL,s)) return MYSQL;
        if ( iceq(S_ORACLE,s)) return ORACLE;
        if ( iceq(S_DB2,s)) return DB2;
        if ( iceq(S_DERBY,s)) return DERBY;
        return null;
    }

    /**
     * @param n
     */
    public void setProductName(String n)    {
        _prod= n;
    }

    /**
     * @return
     */
    public String getProductName()    {        return _prod;    }

    /**
     * @param v
     */
    public void setProductVer(String v)    {
        _ver= v;
    }

    /**
     * @return
     */
    public String getProductVer()    {        return _ver;    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()     {         return _id;    }

    /**
     * @param sql
     * @return
     */
    public String tweakSQL(String sql) {
    	String s=sql.toLowerCase();
    	
    	if (s.indexOf("insert") >=0 && s.indexOf("into") > 0) {}
    	else
    	if (s.indexOf("update") >=0 && s.indexOf("set") > 0) { 
    		return tweakUPDATE(sql); }
    	else
    	if (s.indexOf("delete") >=0 ) { 
    		return tweakDELETE(sql); }
    	else
    	if (s.indexOf("select") >=0 && s.indexOf("from") > 0) { 
    		return tweakSELECT(sql); }
    	
    	return sql;
    }
    
    /**
     * @param sql
     * @return
     */
    public String tweakSELECT(String sql) {
        return SQLSERVER.equals(this) ? 
                tweakMSSQL(sql, "where", NOLOCK) : sql;
    }
    
    /**
     * @param sql
     * @return
     */
    public String tweakUPDATE(String sql) {
        return SQLSERVER.equals(this) ? 
                tweakMSSQL(sql, "set", ROWLOCK) : sql;
    }

    /**
     * @param sql
     * @return
     */
    public String tweakDELETE(String sql) {
        return SQLSERVER.equals( this) ? 
                tweakMSSQL(sql, "where", ROWLOCK) : sql;
    }
    
    /**
     * @param upper
     * @param lower
     * @param mixed
     */
    public void setCase(boolean upper, boolean lower, boolean mixed) {
        _upper=upper; _lower=lower; _mixed=mixed;
    }
    
    /**
     * @param table
     * @return
     */
    public String assureTableCase(String table) {
        if (_upper) return table.toUpperCase();
        if (_lower) return table.toLowerCase();
        return table;
    }
    
    /**
     * @param col
     * @return
     */
    public String assureColCase(String col) {
        if (_upper) return col.toUpperCase();
        if (_lower) return col.toLowerCase();
        return col;        
    }
    
    /**
     * @return
     */
    public boolean isUpperCase() { return _upper; }
    
    /**
     * @return
     */
    public boolean isLowerCase() { return _lower; }
    
    /**
     * @return
     */
    public boolean isMixedCase() { return _mixed; }
    
    
    private static String tweakMSSQL(String sql, String token, String cmd)     {        
        if ( ! isEmpty(sql))
        try  {
            int pos = sql.toLowerCase().indexOf(token);
            String head, tail;
            if (pos >= 0)             {
                head = sql.substring(0, pos);
                tail = sql.substring(pos);
            }
            else             {
                head = sql;
                tail = "";
            }
            sql = head + " WITH (" + cmd + ") " + tail;
        }
        catch (Exception e)
        {}
        return sql;
    }

    private DBVendor(String v)    {
        _prod= v;
        _id=v;
        _ver= "?";
    }

}
