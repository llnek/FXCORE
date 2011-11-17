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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.getBytes;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.core.util.Logger;

/**
 * Utility functions related to DDL creation/execution via JDBC. 
 *
 * @author kenl
 *
 */
public enum DDLUte implements DBVars {   
;
    
    private static Logger _log= getLogger(DDLUte.class);
    public static Logger tlog() { return _log; }    

    /**
     * Write the ddl resource to output stream.
     * 
     * @param resourcePath e.g. "com/acme/ddl.sql"
     * @param out
     * @param ldr optional.
     * @throws IOException
     */
    public static void ddlToStream(OutputStream out, String resourcePath, ClassLoader... ldr)
    throws IOException    {                
        if (out != null) {
            out.write( asBytes(rc2Str(resourcePath, "utf-8", ldr)));
            out.flush();
        }        
    }
    
    /**
     * Write ddl resource to file.
     * 
     * @param fpOut
     * @param resourcePath
     * @param ldr optional.
     * @throws IOException
     */
    public static void ddlToFile(File fpOut, String resourcePath, ClassLoader... ldr) throws IOException    {
        if (fpOut  != null) {
            OutputStream out=null;
            try {
                out=new FileOutputStream(fpOut);
                ddlToStream(out, resourcePath, ldr); 
            }
            finally {        
                close(out);
            }
        }
    }

    /**
     * Load a ddl from file and run that against a database.
     * 
     * @param jp
     * @param fp
     * @throws IOException
     * @throws SQLException
     */
    public static  void loadDDL( JDBCInfo jp, File fp)  throws IOException, SQLException   {
        if (jp != null && fp != null) {
            InputStream inp=null;
            try   {
                loadDDL(jp, inp=readStream(fp));
            }
            finally {        
                close(inp);
            }
        }        
    }

    /**
     * Load ddl from stream and run that against a database.
     * 
     * @param jp
     * @param inp
     * @throws IOException
     * @throws SQLException
     */
    public static void loadDDL( JDBCInfo jp, InputStream inp)
    throws IOException, SQLException     {
        if (jp != null) {            
            loadDDL(jp, asString( getBytes(inp)) );
        }        
    }

    /**
     * Load ddl from string and run that against a database.
     * 
     * @param jp
     * @param ddl
     * @throws SQLException
     */
    public static void loadDDL( JDBCInfo jp, String ddl) throws SQLException     {     
        if ( jp != null && !isEmpty(ddl)) {            
            Connection con = null;
            try  {
                tlog().debug(ddl);
                loadDDL(con = DBUte.createConnection(jp), ddl);
            }
            finally {        
                DBUte.safeClose(con);
            }            
        }        
    }

    private static void loadDDL(Connection con, String ddl) throws SQLException    {        
        String[] lines= splitLines(ddl);        
        if ( isNilArray(lines))
        { return; }
        DatabaseMetaData dm= con.getMetaData();
        boolean oldc= con.getAutoCommit();
        Statement stmt= null;
        Exception ee= null;
        String ln;

        con.setAutoCommit(true);        
        try   {
            for (int i =0; i < lines.length; ++i) {                            
                ln= trim( trim(lines[i]), ";");
                if ( !isEmpty(ln) &&  !"go".equalsIgnoreCase(ln) )    {
                    try  {
                        stmt=con.createStatement();
                        stmt.executeUpdate(ln);                            
                    }
                    catch (SQLException e) {                    
                        maybeContinue(dm.getDatabaseProductName(), e);
                    }
                    finally {                    
                        stmt= DBUte.safeClose(stmt);
                    }
                }
            }
        }
        catch (SQLException e) {
            tlog().error("",e);
            ee=e;
            throw e;
        }
        finally {
            try { if (ee != null) con.rollback(); } catch (Exception e) {}
            DBUte.safeClose(stmt);
            con.setAutoCommit(oldc);
        }
    }

    private static String[] splitLines(String ddl)    {
        int pos = ddl.indexOf(S_DDLSEP);
        List<String> rc= LT() ;
        int w= S_DDLSEP.length();
        while (pos >= 0) {
            rc.add( trim(ddl.substring(0,pos)) );
            ddl= ddl.substring(pos+w) ;
            pos= ddl.indexOf(S_DDLSEP);
        }
        rc.add( trim(ddl));
        return AA(String.class, rc); 
    }

    private static boolean maybeContinue(String db, SQLException e) 
            throws SQLException     {        

        db= nsb(db).toLowerCase();

        boolean oracle=db.indexOf("oracle") >= 0;
        boolean db2=db.indexOf("db2") >= 0;
        boolean derby=db.indexOf("derby") >= 0;

        if ( ! (oracle || db2 || derby))
        { throw e; }

        Throwable t= e.getCause();
        int ec= -1;
        SQLException ee= e;

        if (t instanceof SQLException) { ee= (SQLException) t; }
        ec= ee.getErrorCode();

        if (oracle && ( 942==ec || 1418==ec || 2289==ec || 0==ec)) {
        	return true; }
        
        if (db2 && ( -204==ec))        { return true; }        
        
        if (derby && (30000==ec))        { return true; }
        
        throw e;
    }

}
