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

import static com.zotoh.core.db.DBVendor.DB2;
import static com.zotoh.core.db.DBVendor.ORACLE;
import static com.zotoh.core.db.DBVendor.SQLSERVER;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.eq;
import static com.zotoh.core.util.StrUte.nsb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.PoolableConnection;
import org.apache.commons.pool.ObjectPool;

import com.zotoh.core.util.Logger;


/**
 * Pool management of jdbc connections.
 *
 * @author kenl
 *
 */
public final class JDBCPool implements DBVars {
    
    private transient Logger _log=getLogger(JDBCPool.class);   
    public Logger tlog() {    return _log;    }    

    private DBVendor _vendor= DBVendor.NOIDEA;
    private ObjectPool _pool;
    private JDBCInfo _info;
    
    /**
     * @param v
     * @param p
     */
    public JDBCPool(DBVendor v, JDBCInfo info, ObjectPool p)    {
        tstObjArg("db-vendor",v);
        tstObjArg("db-pool",p);
        _vendor= v;
        _pool=p;
        _info = info;
    }

    /**
     * @param p
     */
    public JDBCPool(ObjectPool p)    {
        this(DBVendor.NOIDEA, new JDBCInfo(), p) ;
    }

    /**
     * @return
     */
    public JDBCInfo getInfo() { return _info; }
    
    /**
     * @return
     */
    public JDBC newJdbc()    {        return  new JDBC(this);    }
        
    /**
     * 
     */
    public synchronized void finz()    {
        forceCloseAll();        
        try { _pool.clear(); } catch (Exception e) {}
        try { _pool.close(); } catch (Exception e) {}
    }
    
    /**
     * 
     */
    public synchronized void clear() {
        forceCloseAll();            	
    }
    
    /**
     * @return
     * @throws SQLException
     */
    public JConnection getNextFree() throws SQLException    {
        JConnection jc= next();
        tlog().debug("JDBCPool: Got a free jdbc connection from pool");
        return jc;
    }

    
    /**
     * @return
     */
    public int getVarcharMaxWidth()    {         return VARCHAR_WIDTH;    }

    
    /**
     * @return
     */
    public int getRetries()     {        return 2;    }

    
    /**
     * @return
     */
    public DBVendor getVendor()    {        return _vendor;    }

    
    /**
     * @param c
     */
    public void returnUsed(JConnection c)    {
        returnUsed(c, true);
    }

    
    /**
     * @param c
     * @param reuse
     */
    public void returnUsed(JConnection c, boolean reuse)    {
        
        if (c != null) { c.cancelQuietly(); }
        Object obj;        
        
        if (reuse && c != null && !c.isDead())        {
            obj = c==null ? null : c.getInternal();
            if (obj != null) {
                tlog().debug("JDBCPool: Returning a used jdbc connection to pool");
                try { _pool.returnObject(obj); } catch (Exception e) {}
            }
        }
        else  if (c != null)        {
            c.die();
            obj = c==null ? null : c.getInternal();
            if (obj != null) {
                tlog().debug("JDBCPool: Removing a bad jdbc connection from pool");
                try { _pool.invalidateObject(obj); } catch (Exception e) {}
            }
        }
    }

    
    /**
     * @param e
     * @return
     */
    public boolean isBadConnection(Exception e)    {
        
        String message= nsb(e.getMessage()).toLowerCase(),
        sqlState="";

        if (e instanceof SQLException)  {
            sqlState = ((SQLException) e).getSQLState();
        }

        if ( eq("08003",sqlState) || eq("08S01",sqlState) ) {
            return true;        
        } else {
        // take a guess...
            return ((message.indexOf("reset by peer"   ) >=0) ||
            (message.indexOf("aborted by peer" ) >=0) ||
            (message.indexOf("not logged on"   ) >=0) ||
            (message.indexOf("socket write error") >=0) ||
            (message.indexOf("communication error") >=0) ||
            (message.indexOf("error creating connection") >=0) ||
            (message.indexOf("connection refused") >=0) ||
            (message.indexOf("connection refused") >=0) ||
            (message.indexOf("broken pipe") >=0));
        }
        
    }

    
    /**
     * @param conn
     * @return
     */
    public boolean isBadConnection(Connection conn)    {
        
        DBVendor v= getVendor();
        Statement stmt = null;
        String sql=null;
        
        if (conn != null)
        try  {
            
            if (ORACLE.equals(v)) { sql= "select count(*) from user_tables"; }
            if (SQLSERVER.equals(v)) { sql= "select count(*) from sysusers"; }
            if (DB2.equals(v)) { sql= "select count(*) from sysibm.systables"; }
            
            if (sql != null) {
                stmt = conn.createStatement();
                stmt.execute(sql);
            }
        }
        catch(Exception e) {
            return true;
        }
        finally {
            DBUte.safeClose(stmt);
        }

        return false;
    }

    private void forceCloseAll()    {
        
        tlog().debug("JDBCPool: closing down all db connections...");        
        JConnection jc= null;
        while (true) {
            try             {
                jc= next();
            }
            catch (Exception e) {
                break;
            }
            if (jc != null) { 
                try                {
                    jc.getConnection().close();
                }
                catch (Exception e)            
                {}
            }
            else {
                break;
            }
        }
        
        tlog().debug("JDBCPool: database connections closed");
    }
    
    private JConnection next() throws SQLException    {
        try        {
            return new JConnection(this, 
                (PoolableConnection) _pool.borrowObject());
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLException("No free connection");
        }
        
    }

}
