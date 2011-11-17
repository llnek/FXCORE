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

import static com.zotoh.core.db.DBUte.safeClose;
import static com.zotoh.core.util.CoreUte.genTmpDir;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.zotoh.core.util.Logger;

/**
 * @author kenl
 *
 */
abstract class HxxDB implements DBVars {
    
    private static Logger _log= getLogger(HxxDB.class);
    public static Logger tlog() { return _log; }    
    
    /**
     * 
     */
    protected HxxDB()
    {}
    
    /**
     * Shut down this database by issuing a SHUTDOWN call through this
     * connection.
     * 
     * @param c
     * @throws SQLException
     */
    public void shutdown(Connection c) throws SQLException    {
        Statement stmt=null;
        if (c != null)
        try  {
            c.setAutoCommit(true);
            stmt=c.createStatement();
            stmt.execute("SHUTDOWN");
        }
        finally {        
            safeClose(stmt);
        }                    
    }

    /**
     * Shutdown the database.
     * 
     * @param dbUrl
     * @param user
     * @param pwd
     * @throws SQLException
     */
    public void closeDB(String dbUrl, String user, String pwd) throws SQLException    {
        tstEStrArg("db-url", dbUrl ) ;
        tstEStrArg("user", user ) ;
        
        tlog().debug("Shutting down HxxDB: {}", dbUrl);        
        Connection c1= null;        
        try  {
            c1 = DriverManager.getConnection(dbUrl, user, nsb(pwd));
            shutdown(c1);
        }
        finally {
            safeClose(c1);
        }
        
    }
    
    /**
     * @param param
     * @throws SQLException
     */
    public void closeDB(JDBCInfo param) throws SQLException    {
        tstObjArg("jdbc-info", param ) ;
        closeDB(param.getUrl(), param.getUser(), param.getPwd());
    }

    /**
     * Create a database.
     * 
     * @param user
     * @param pwd
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public String createDB(String dbid, String user, String pwd) throws SQLException, IOException {
        return createDB( genTmpDir(), dbid, user, pwd);
    }
    
    /**
     * Clean up all the relevant database files in this folder, effectively
     * removing the database.
     * 
     * @param dbPath
     */
    public void dropDB(String dbPath) {
        onDropDB( trimLastPathSep(dbPath));
    }
    
    /**
     * Load a DDL from file and run it.
     * 
     * @param url
     * @param user
     * @param pwd
     * @param sql
     * @throws IOException
     * @throws SQLException
     */
    public void loadSQL( String dbUrl, String user, String pwd, File sql) 
                throws IOException, SQLException    {                
        tstEStrArg("db-url", dbUrl );
        tstObjArg("file", sql );
        tstObjArg("user", user );
        tlog().debug("Loading SQL: {}", niceFPath(sql));
        tlog().debug("JDBC-URL: {}", dbUrl);                    
        DDLUte.loadDDL( new JDBCInfo(dbUrl, user, pwd), sql);
    }
    
    /**
     * Create an in-memory database.
     * 
     * @param dbid
     * @param user
     * @param pwd
     * @throws SQLException
     */
    public String createMemDB(String dbid, String user, String pwd) 
                throws SQLException    {
        tstEStrArg("db-id", dbid );
        tstEStrArg("user", user );
        String dbUrl= getMemPfx() + dbid + getMemSfx();
        Connection c1= null;
        try {
            c1 = DriverManager.getConnection( dbUrl, user, nsb(pwd));
            c1.setAutoCommit(true);
        }
        finally {
            safeClose(c1);
        }
        return dbUrl;
    }

    /**
     * @return
     */
    protected String getMemSfx() { return ""; }
    
    /**
     * Create a database in the specified directory.
     * 
     * @param fileDir
     * @param user
     * @param pwd
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String createDB(File dbFileDir, String dbid, String user, String pwd) 
                throws IOException, SQLException {
        tstObjArg("file-dir", dbFileDir);
        tstEStrArg("db-id", dbid);
        tstEStrArg("user", user );

        String dbUrl= trimLastPathSep(niceFPath(dbFileDir)) + "/" + dbid;
        dbFileDir.mkdirs();
        dropDB(dbUrl);
        dbUrl= getEmbeddedPfx() + dbUrl;

        Connection c1= null;
        Statement s= null;
        tlog().debug("Creating HxxDB: {}", dbUrl);        
        try  {
            c1 = DriverManager.getConnection(dbUrl, user, nsb(pwd));
            c1.setAutoCommit(true);
            s=c1.createStatement();
            //s.execute("CREATE USER " + user+ " PASSWORD \"" + pwd + "\" ADMIN");
            onCreateDB(s);
            s.close();
            s=c1.createStatement();
            s.execute("SHUTDOWN");
        }
        finally {
            safeClose(s);
            safeClose(c1);
        }
        
        return dbUrl;
    }

    
    /**
     * Tests if there is a database.
     * 
     * @param dbPath
     * @return
     */
    public boolean existsDB(String dbPath)    {
        return onTestDB( trimLastPathSep(dbPath) );
    }

    /**
     * @param dbPath
     * @return
     */
    protected abstract boolean onTestDB(String dbPath);        
    
    /**
     * @param dbPath
     */
    protected abstract void onDropDB(String dbPath);        
 
    /**
     * @return
     */
    protected abstract String getEmbeddedPfx();
    
    /**
     * @return
     */
    protected abstract String getMemPfx();
        
    /**
     * @param s
     * @throws SQLException
     */
    protected abstract void onCreateDB(Statement s) throws SQLException ;
    
}
