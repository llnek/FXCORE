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
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.sql.Connection;

import com.zotoh.core.util.Logger;

/**
 * @author kenl
 *
 */
public class JDBCInfo implements java.io.Serializable {
    
    private static final long serialVersionUID = 6871654777100857463L;
    private transient Logger _log=getLogger(JDBCInfo.class); 
    public Logger tlog() {         return _log;    }
    
    private int _isolation= Connection.TRANSACTION_READ_COMMITTED;
    private String _driver, _url, _user, _pwd;
    
    /**
     * @param driver
     * @param url
     * @param user
     * @param pwd
     */
    public JDBCInfo(String driver, String url, String user, String pwd) {        
//        tlog().debug( "JDBC: driver = {}, url = {}, user = {}, pwd=****", driver, url, user ) ;
        setDriver(driver);
        setUrl(url);
        setUser(user);
        setPwd(pwd) ;
    }
    
    /**
     * @param url
     * @param user
     * @param pwd
     */
    public JDBCInfo(String url, String user, String pwd) {
        setUrl(url);
        setUser(user);
        setPwd(pwd) ;
    }
    
    /**
     * 
     */
    public JDBCInfo() {
    	this("?","", "");
    }
    
    /**
     * 
     * @return
     */
    public int getIsolation() {        return _isolation;    }
        
    /**
     * @param n
     */
    public void setIsolation(int n) {        _isolation= n;    }
    
    /**
     * @param driver
     */
    public void setDriver( String driver) { 
        tstEStrArg("jdbc-driver", driver) ;
        _driver= driver; 
    }
    
    /**
     * @return
     */
    public String getDriver() { return _driver; }
    
    /**
     * @param url
     */
    public void setUrl( String url) { 
        tstEStrArg("jdbc-url", url) ;
        _url= url; 
    }
    
    /**
     * @param user
     */
    public void setUser( String user) { _user= nsb(user); }
    
    /**
     * @param pwd
     */
    public void setPwd( String pwd) { _pwd= nsb(pwd); }
    
    /**
     * @return
     */
    public String getUrl() { return _url; }
    
    /**
     * @return
     */
    public String getUser() { return _user; }
    
    /**
     * @return
     */
    public String getPwd() { return _pwd; }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Driver: " + getDriver() + ", Url: " + getUrl() + ", User: " + getUser() + "Pwd: ****";
    }
    
    
    
}
