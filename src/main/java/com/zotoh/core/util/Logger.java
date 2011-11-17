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
 
package com.zotoh.core.util;


/**
 * @author kenl
 *
 */
public final class Logger {

    /**
     * 
     */
    public static final Logger Dummy = new Logger(null);
    private org.slf4j.Logger _logr;
    
    
    /**
     * @param lg
     */
    protected Logger(org.slf4j.Logger lg) {
        _logr=lg;
    }
    
    
    /**
     * @return
     */
    public  boolean isDebugEnabled() {
        return _logr==null ? false : _logr.isDebugEnabled();
    }

    
    /**
     * @return
     */
    public boolean isWarnEnabled() {
        return _logr==null ? false : _logr.isWarnEnabled();
    }
    
    
    /**
     * @return
     */
    public boolean isInfoEnabled() {
        return _logr==null ? false : _logr.isInfoEnabled();
    }
    
    
    /**
     * @return
     */
    public boolean isErrorEnabled() {
        return _logr==null ? false : _logr.isErrorEnabled();
    }

    /**
     * @param msg
     * @param args
     */
    public void error(String msg, Object... args) {
        if(_logr != null) if (_logr.isErrorEnabled()) _logr.error(msg, args);
    }

    /**
     * @param msg
     * @param t
     */
    public void error(String msg, Throwable t) {
        if(_logr != null) if (_logr.isErrorEnabled()) _logr.error(msg, t);
    }

    /**
     * @param t
     */
    public void error(Throwable t) {
        if(_logr != null) if (_logr.isErrorEnabled()) _logr.error("", t);
    }
    
    /**
     * @param msg
     * @param args
     */
    public void debug(String msg, Object... args) {
        if(_logr != null) if (_logr.isDebugEnabled()) _logr.debug(msg,args);
    }

    /**
     * @param msg
     * @param t
     */
    public void debug(String msg, Throwable t) {
        if(_logr != null) if (_logr.isDebugEnabled())  _logr.debug(msg,t);
    }
    
    /**
     * @param t
     */
    public void debug(Throwable t) {
        if(_logr != null) if (_logr.isDebugEnabled())  _logr.debug("",t);
    }
    
    /**
     * @param msg
     * @param args
     */
    public void info(String msg, Object... args) {
        if(_logr != null) if (_logr.isInfoEnabled())  _logr.info(msg,args);
    }
    
    /**
     * @param msg
     * @param t
     */
    public void info(String msg, Throwable t) {
        if(_logr != null) if (_logr.isInfoEnabled()) _logr.info(msg,t);
    }
    
    /**
     * @param t
     */
    public void info(Throwable t) {
        if(_logr != null) if (_logr.isInfoEnabled())   _logr.info("",t);
    }

    /**
     * @param msg
     * @param args
     */
    public void warn(String msg, Object... args) {
        if(_logr != null) if (_logr.isWarnEnabled()) _logr.warn(msg,args);
    }
    
    /**
     * @param msg
     * @param t
     */
    public void warn(String msg, Throwable t) {
        if(_logr != null) if (_logr.isWarnEnabled()) _logr.warn(msg,t);
    }
    
    /**
     * @param t
     */
    public void warn(Throwable t) {
        if(_logr != null) if (_logr.isWarnEnabled()) _logr.warn("",t);
    }
    
}