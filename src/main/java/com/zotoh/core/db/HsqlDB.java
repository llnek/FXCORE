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
 * Utility functions relating to the management of a HSQLDB instance. 
 *
 * @author kenl
 *
 */
public  final class HsqlDB extends HxxDB {
    
    private static HsqlDB _dft = new HsqlDB();
    
    /**
     * @return
     */
    public static HsqlDB getInstance() {        return _dft;            }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#getEmbeddedPfx()
     */
    protected String getEmbeddedPfx() { return HSQLDB_FILE_URL; }

    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#getMemPfx()
     */
    protected String getMemPfx() { return  HSQLDB_MEM_URL;  }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onTestDB(java.lang.String)
     */
    protected boolean onTestDB(String p)    {
        return p==null ? false : new File(p + ".properties").exists() 
            && new File(p + ".script").exists();        
    }

    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onDropDB(java.lang.String)
     */
    protected void onDropDB(String p)    {        
        delete(""+p+".properties");
        delete(""+p+".script");
        delete(""+p+".data");
        delete(""+p+".db");
        delete(""+p+".lock");
        delete(""+p+".lck");
        delete(""+p+".log");
        delete(""+p+".backup");
    }

    /* (non-Javadoc)
     * @see com.zotoh.core.db.HxxDB#onCreateDB(java.sql.Statement)
     */
    protected void onCreateDB(Statement s) throws SQLException {
        if (s != null) { s.execute("SET DATABASE DEFAULT TABLE TYPE CACHED"); }        
    }
    
    private HsqlDB()
    {}
        
}
