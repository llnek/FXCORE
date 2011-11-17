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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author kenl
 *
 */
public class HsqlDBSQL extends HxxDBSQL {
    
    /**
     * @param args
     */
    public static void main(String[] args)    {
        System.exit( runMain(new HsqlDBSQL(), args));
    }

    /**
     * 
     */
    public HsqlDBSQL() {
        super("HyperSQL", "hsqldb");
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDBSQL#xxCreateDB(java.io.File, java.lang.String, java.lang.String, java.lang.String)
     */
    protected String xxCreateDB(File dbFileDir, String dbid, String user, String pwd) 
                throws SQLException,IOException {
        return HsqlDB.getInstance().createDB(dbFileDir, dbid, user, pwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDBSQL#xxLoadSQL(java.lang.String, java.lang.String, java.lang.String, java.io.File)
     */
    protected void xxLoadSQL(String dbUrl, String user, String pwd, File sql) 
            throws SQLException,IOException {
        HsqlDB.getInstance().loadSQL(dbUrl, user, pwd, sql);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDBSQL#xxCloseDB(java.lang.String, java.lang.String, java.lang.String)
     */
    protected void xxCloseDB(String dbUrl, String user, String pwd) 
                throws SQLException,IOException {
        HsqlDB.getInstance().closeDB(dbUrl, user, pwd);
    }
    

}
