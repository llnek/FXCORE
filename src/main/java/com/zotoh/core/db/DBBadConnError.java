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

import java.sql.SQLException;

/**
 * Exception for bad DB connections. 
 *
 * @author kenl
 *
 */
public class DBBadConnError extends SQLException {
    
    private static final long serialVersionUID = 123241635256073760L;

    /**
     * @param msg
     * @param t
     */
    public DBBadConnError(String msg, Throwable t)     {
        super(msg, t);
    }

    /**
     * @param msg
     */
    public DBBadConnError(String msg)     {
        super(msg);
    }

    /**
     * @param t
     */
    public DBBadConnError(Throwable t)     {
        super(t);
    }

    /**
     * 
     */
    public DBBadConnError()
    {}

}
