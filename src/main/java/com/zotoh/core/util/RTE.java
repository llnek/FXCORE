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
public class RTE extends RuntimeException {
    
    private static final long serialVersionUID = -5485369950583115337L;

    /**
     * @param msg
     * @param e
     */
    public RTE(String msg, Throwable e)     {
        super(msg,e);
    }

    /**
     * @param msg
     */
    public RTE(String msg)     {
        super(msg);
    }

    /**
     * @param e
     */
    public RTE(Throwable e)     {
        super(e);
    }

    /**
     * 
     */
    public RTE() {}
    
    
}
