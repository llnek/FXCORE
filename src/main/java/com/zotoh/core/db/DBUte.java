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

import static com.zotoh.core.db.DBVendor.DERBY;
import static com.zotoh.core.db.DBVendor.H2;
import static com.zotoh.core.db.DBVendor.HSQLDB;
import static com.zotoh.core.db.DBVendor.MYSQL;
import static com.zotoh.core.db.DBVendor.ORACLE;
import static com.zotoh.core.db.DBVendor.POSTGRESQL;
import static com.zotoh.core.db.DBVendor.SQLSERVER;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.eq;
import static com.zotoh.core.util.StrUte.hasWithin;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.strstr;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;

/**
 * Helper db functions. 
 *
 * @author kenl
 *
 */
public enum DBUte implements DBVars {    
;
    
    private static Logger _log= getLogger(DBUte.class);
    public static Logger tlog() {   return _log; }    

    /**
     * @param c
     * @return null
     */
    public static Connection safeClose(Connection c)    {
        closeQuietly(c); 
        return null;
    }

    /**
     * @param s
     * @return null
     */
    public static PreparedStatement safeCloseEx(Statement s)    {
        closeQuietly(s); 
        return null;
    }

    /**
     * @param s
     * @return null
     */
    public static Statement safeClose(Statement s)    {
        closeQuietly(s); 
        return null;
    }
    
    /**
     * @param s
     * @return null
     */
    public static ResultSet safeClose(ResultSet s)    {
        closeQuietly(s); 
        return null;
    }

    /**
     * @param objs
     * @return
     */
    public static <T> List<T> asList(T[] objs)    {
        return CoreUte.asList(true, objs);
    }
    
    /**
     * @param jp
     * @return
     * @throws SQLException
     */
    public static Connection createConnection(JDBCInfo jp) throws SQLException    {
        
        tstObjArg("jdbc-info", jp);
/*
        Class<?> c= loadDriver(jp.getDriver());
        if (c == null) { 
            throw new SQLException("Failed to load jdbc-driver class: " + jp.getDriver() ) ; 
        }
                        */
        Connection con= isEmpty(jp.getUser()) ? DriverManager.getConnection(jp.getUrl()) : safeGetConn(jp);
        if (con == null) { 
            throw new SQLException("Failed to create db connection: " + jp.getUrl() ) ; 
        }
        
        con.setTransactionIsolation( jp.getIsolation() );
        return con;
    }

    /**
     * @param jp
     * @throws SQLException
     */
    public static void testConnection(JDBCInfo jp) throws SQLException    {
        Connection con=null;
        try        {
            con=createConnection(jp);
        }
        finally {        
            safeClose(con);
        }
    }

    /**
     * @param jp
     * @return
     * @throws SQLException
     */
    public static DBVendor getDBVendor(JDBCInfo jp) throws SQLException    {
        
        Connection con= createConnection(jp);
        DatabaseMetaData md;
        String v,name;
        DBVendor dbv= null;        
        try  {
            md= con.getMetaData();
            name= md.getDatabaseProductName();
            v= md.getDatabaseProductVersion();
            dbv= maybeGetVendor(name);
            if (dbv != null) {
                dbv.setProductName(name);
                dbv.setProductVer(v);
                dbv.setCase(md.storesUpperCaseIdentifiers(),md.storesLowerCaseIdentifiers(), md.storesMixedCaseIdentifiers());
            }
        }
        finally {        
            safeClose(con);
        }
        
        return dbv==null ? DBVendor.NOIDEA : dbv;
    }
    
    /**
     * @param jp
     * @param table
     * @return
     * @throws SQLException
     */
    public static boolean tableExists(JDBCInfo jp, String table) throws SQLException    {
        
        Connection con = null;
        ResultSet res=null;
        boolean ok=false;
        
        if ( jp != null && ! isEmpty(table))
        try  {
            con=createConnection(jp);
            DatabaseMetaData mt=con.getMetaData();
            if (mt.storesUpperCaseIdentifiers()) { table=table.toUpperCase(); }
            else
            if (mt.storesLowerCaseIdentifiers()) { table=table.toLowerCase();  }
            res=mt.getColumns(null,null, table, null);
            ok= res != null && res.next()==true;
        }
        catch (SQLException e) {        
            return false;
        }
        catch (Exception e) {        
            throw new SQLException(e);
        }
        finally {        
            safeClose(res);
            safeClose(con);
        }
        
        return ok;
    }

    /**
     * @param jp
     * @param table
     * @return
     * @throws SQLException
     */
    public static boolean rowExists(JDBCInfo jp, String table) throws SQLException    {
        
        Connection con = null;
        Statement stm=null;
        ResultSet res=null;
        String sql;
        boolean ok=false;

        if ( jp != null && ! isEmpty(table))
        try        {
            sql="SELECT COUNT(*) FROM " + table.toUpperCase();
            con=createConnection(jp);
            stm= con.createStatement();
            res= stm.executeQuery(sql);
            int rc=(res != null && res.next()) ? res.getInt(1) : 0;
            ok= rc > 0;
        }
        catch (SQLException e) {
            return false;
        }
        catch (Exception e) {        
            throw new SQLException(e);
        }
        finally {        
            safeClose(stm);
            safeClose(con);
        }
        
        return ok;
    }

    /**
     * @param jp
     * @param sql
     * @return
     * @throws SQLException
     */
    public static DBRow firstRow(JDBCInfo jp, String sql) throws SQLException    {
        
        DBRow row= new DBRow("");
        Connection con = null;
        ResultSet res=null;
        Statement stm=null;
        int pos,cnt;
        ResultSetMetaData me;
        
        if (jp != null && ! isEmpty(sql))
        try        {
            con=createConnection(jp);
            stm= con.createStatement();
            res=  stm.executeQuery(sql);
            if (res != null && res.next()) {            
                me= res.getMetaData();
                cnt=me.getColumnCount();
                for (int i=0; i < cnt; ++i) {                
                    pos=i+1;
                    row.add(me.getColumnName(pos), res.getObject(pos)) ;
                }
            }            
        }
        catch (SQLException e) {        
            throw e;
        }
        catch (Exception e) {        
            throw new SQLException(e);
        }
        finally {        
            safeClose(stm);
            safeClose(con);
        }
        
        return row;
    }

    /**/
    public static String nocaseMatch(String col, String val)    {
        return new StringBuilder(256)        
        .append(" UPPER(").append( nsb(col)).append(")")
        .append(" LIKE")
        .append(" UPPER('").append( nsb(val)).append("') ")        
        .toString();
    }

    /**/
    public static String likeMatch(String col, String val)    {
        return new StringBuilder(256)        
        .append( nsb(col))
        .append(" LIKE")
        .append(" '").append( nsb(val)).append("' ")        
        .toString();
    }

    /**/
    public static String wildcardMatch(String col, String filter)     {
        return new StringBuilder(256)        
        .append(" UPPER(").append( nsb(col)).append(")")
        .append(" LIKE")
        .append(" UPPER('").append( nsb(strstr(  nsb(filter), "*", "%"))).append("') ")        
        .toString();
    }

    /**/
    public static Class<?> loadDriver(String s) throws SQLException    {
        try        {
            return Class.forName(s);
        }
        catch (ClassNotFoundException e) {
            throw new SQLException("Drive class not found: " + s);
        }
    }
    
    /**
     * Get a connection.
     * 
     * @param z the driver class.
     * @param jp
     * @return
     * @throws SQLException
     */
    private static Connection safeGetConn(JDBCInfo jp) throws SQLException    {
        
        Properties props=new Properties();
        Driver d= null;        
        String dz, j="",p="", u="", n="";
        
        j= jp.getDriver();
        p= jp.getPwd();
        u= jp.getUrl();
        n=  jp.getUser();
        
        if ( ! isEmpty(u)) {
            d= DriverManager.getDriver(u);
        }
        if (d==null) {
            throw new SQLException("Can't load Jdbc Url : " + u);
        }
        
        if ( ! isEmpty(j))  {
            dz=d.getClass().getName();
            if ( ! eq(j, dz))         
            tlog().warn("DBUte: Expected : " + j + " , loaded with driver : " + dz);
        }

        if ( ! isEmpty(n))        {
            props.put("password", p);
            props.put("username", n);
            props.put("user", n);
            setProps(u, props);
        }
        
        return d.connect(u, props);
    }

    private static void setProps(String url, Properties prop)
    {}

    private static void closeQuietly(Connection c) {
        try { c.close(); } catch (Throwable e) {}
    }

    private static void closeQuietly(Statement s) {
        try { s.close(); } catch (Throwable e) {}
    }

    private static void closeQuietly(ResultSet r) {
        try { r.close(); } catch (Throwable e) {}
    }

    private static DBVendor maybeGetVendor(String product)    {
        
        product= nsb(product).toLowerCase();
        
        if (hasWithin(product,"microsoft")) return SQLSERVER;
        if (hasWithin(product,"hypersql")) return HSQLDB;
        if (hasWithin(product,"hsql")) return HSQLDB;
        if (hasWithin(product,"h2")) return H2;
        if (hasWithin(product,"oracle")) return ORACLE;
        if (hasWithin(product,"mysql")) return MYSQL;
        if (hasWithin(product,"derby")) return DERBY;
        if (hasWithin(product,"postgresql")) return POSTGRESQL;
        
        return  DBVendor.NOIDEA;
    }
    
}
