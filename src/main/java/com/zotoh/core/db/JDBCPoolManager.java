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


import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.asLong;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.GUID.generate;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.PropertiesEx;

/**
 * @author kenl
 *
 */
public final class JDBCPoolManager implements DBVars {
    
    private transient Logger _log= getLogger(JDBCPoolManager.class);
    public Logger tlog() {         return _log;  }    

    private Map<String,JDBCPool> _ps= MP();
    
    /**
     * @param pool
     * @param param
     * @param props
     * @return
     * @throws SQLException
     */
    public JDBCPool createPool(String pool, JDBCInfo param, Properties props) 
                throws SQLException  {
        return create(pool, param, props);   
    }
    
    /**
     * @param param
     * @param props
     * @return
     * @throws SQLException
     */
    public JDBCPool createPool(JDBCInfo param, Properties props) 
                throws SQLException  {
        return create( generate(), param, props);   
    }
    
    /**
     * @param param
     * @return
     * @throws SQLException
     */
    public JDBCPool createPool(JDBCInfo param) 
                throws SQLException    {
        return createPool( generate(), param);   
    }
    
    /**
     * @param pool
     * @param param
     * @return
     * @throws SQLException
     */
    public JDBCPool createPool(String pool, JDBCInfo param)
                throws SQLException    {        
        tstObjArg("jdbc-info", param) ;
        tstEStrArg("pool-id", pool) ;
        
        return create(pool, param, new PropertiesEx()
                .put("username", param.getUser())
                .put("user", param.getUser())
                .put("password", param.getPwd()));
    }

    /**/
    private synchronized JDBCPool create(String pool,  JDBCInfo param, Properties props)
                throws SQLException    {        
        if (existsPool(pool)) {
            throw new SQLException("Jdbc Pool already exists: " + pool);
        }
        
        PoolableConnectionFactory pcf;
        DriverConnectionFactory dcf;
        GenericObjectPool gop;
        DBVendor dbv;
        ObjectPool p;
        Driver d;

        tlog().debug("JDBCPoolMgr: Driver : {}" , param.getDriver());
        tlog().debug("JDBCPoolMgr: URL : {}" , param.getUrl());            

//        Ute.loadDriver(param.getDriver());
        d= DriverManager.getDriver(param.getUrl());
        dbv= DBUte.getDBVendor(param);
                
        dcf= new DriverConnectionFactory(d, param.getUrl(), props);
        gop= new GenericObjectPool();
        gop.setMaxActive(asInt(props.getProperty("max-conns"), 10));
        gop.setTestOnBorrow(true);
        gop.setMaxIdle(gop.getMaxActive());
        gop.setMinIdle(asInt(props.getProperty("min-conns"), 2));
        gop.setMaxWait(asLong(props.getProperty("max-wait4-conn-millis"), 1500L)) ;
        gop.setMinEvictableIdleTimeMillis(asLong(props.getProperty("evict-conn-ifidle-millis"), 300000L)) ;
        gop.setTimeBetweenEvictionRunsMillis(asLong(props.getProperty("check-evict-every-millis"), 60000L));
        
        pcf=new PoolableConnectionFactory(dcf, gop, null, null, true, false);
        pcf.setDefaultReadOnly(false);
        p= pcf.getPool();
        
        JDBCPool j= new JDBCPool(dbv, param, p);
        _ps.put(pool, j);

        tlog().debug("JDBCPoolMgr: Added db pool: {}, info= {}", pool, param); 
        return j;
    }

    /**
     * 
     */
    public void finz()    {
        synchronized (this) {
            for (JDBCPool p : _ps.values())  { p.finz(); }
            _ps.clear();            
        }
    }

    /**
     * @param n
     * @return
     */
    public boolean existsPool(String n)    {          return _ps.containsKey(nsb(n));    }

    /**
     * @param n
     * @return
     */
    public JDBCPool getPool(String n)    {        return _ps.get(nsb(n));    }

    /**
     * 
     */
    public JDBCPoolManager()
    {}

    
}
