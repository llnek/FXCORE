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

import static com.zotoh.core.util.FileUte.delete;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility functions relating to the management of a H2 instance. 
 *
 * @author kenl
 *
 */
public  final class H2DB extends HxxDB {
        
    private static H2DB _dft = new H2DB();
    
    /**
     * @return
     */
    public static H2DB getInstance() {        return _dft;           }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#getEmbeddedPfx()
     */
    protected String getEmbeddedPfx() { return H2_FILE_URL; }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#getMemPfx()
     */
    protected String getMemPfx() { return  H2_MEM_URL;  }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#getMemSfx()
     */
    protected String getMemSfx() { return ";DB_CLOSE_DELAY=-1"; }
        
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onTestDB(java.lang.String)
     */
    protected boolean onTestDB(String p)    {
        return p==null ? false : new File(p + ".h2.db").exists() ;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onDropDB(java.lang.String)
     */
    protected void onDropDB(String p)    {
        delete(""+p+".h2.lock");
        delete(""+p+".h2.db");            
    }

    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onCreateDB(java.sql.Statement)
     */
    protected void onCreateDB(Statement s) throws SQLException {
        if (s != null) { s.execute("SET DEFAULT_TABLE_TYPE CACHED"); }
    }
    
    private H2DB()
    {}

    
}
