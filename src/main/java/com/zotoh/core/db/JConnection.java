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

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.PoolableConnection;
import com.zotoh.core.util.Logger;

/**
 * Wrapper class on top of a pooled jdbc connection.
 * 
 * @author kenl
 *
 */
public final class JConnection {
    
    private transient Logger _log= getLogger(JConnection.class);
    public Logger tlog() {  return _log;    }    

    private PoolableConnection _pc;
    private JDBCPool _pool;
    private boolean _dead;
    
    /**
     * @return
     */
    public Connection getConnection()    {        return _pc.getDelegate();    }

    /**
     * @return
     */
    public DBVendor getVendor()    {        return _pool.getVendor();    }

    
    /**
     * @throws SQLException
     */
    protected void begin() throws SQLException    {        
        tlog().debug("JConnection: Starting db transaction...");
        reset();        
        getConnection().setAutoCommit(false) ;
    }

    /**
     * Commit changes.
     * 
     * @throws SQLException
     */
    protected void flush() throws SQLException    {
        getConnection().commit();
    }

    /**
     * Rollback changes, ignore any errors.
     */
    protected void cancelQuietly()    {
        try        { 
            cancel(false); 
        } 
        catch (Exception e) {
            tlog().warn("",e);
        }
    }

    /**
     * Rollback changes.
     * 
     * @throws SQLException
     */
    protected void cancel() throws SQLException    {
        cancel(true);
    }

    /**
     * Tests if this connection should not be used anymore.
     * 
     * @return
     */
    public boolean isDead()    {        return _dead;    }
    
    /**
     * Render this connection un-unsable, closes the underlying connection.
     */
    public void die()    {
        DBUte.safeClose(getConnection());
        _dead=true;
    }

    /**
     * @return
     */
    protected PoolableConnection getInternal()    {        return _pc;    }

    /**
     * @param pool
     * @param c
     */
    protected JConnection(JDBCPool pool, PoolableConnection c)    {
        tstObjArg("jdbc-pool", pool) ;
        tstObjArg("conn", c) ;
        _pool=pool;
        _pc=c;
    }

    private void cancel(boolean wantError) throws SQLException    {
        try        {
            getConnection().rollback();
        }
        catch (SQLException e) {
            if (wantError) throw e;
            else
            tlog().warn("",e);
        }
    }

    private void reset()
    {}
    
}
