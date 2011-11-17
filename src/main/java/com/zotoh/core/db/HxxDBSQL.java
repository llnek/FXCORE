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

import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


/**
 * @author kenl
 *
 */
abstract class HxxDBSQL {

    private String _app, _db;
    
    /**
     * @param args
     * @return
     * @throws Exception
     */
    protected String start(String[] args) throws Exception     {
        
        boolean usage=false, create= false;
        
        String dbpath="", dbid="", user="",
        pwd="",
        sql="",
        url="",
        s;
        
        for (int i=0; i < args.length; ++i)         {
            
            s= args[i];
            
            if (s.startsWith("-help")) {
                usage=true;
                break;
            }
            
            if (s.startsWith("-create:")) {
                dbpath=niceFPath( args[i].substring(8));
                create=true;
            }
            else
            if (s.startsWith("-url:")) {
                url=args[i].substring(5);                
                dbpath=niceFPath(url);                
            }
            else
            if (s.startsWith("-user:")) {
                user=args[i].substring(6);
            }            
            else
            if (s.startsWith("-password:")) {
                pwd=args[i].substring(10);
            }            
            else
            if (s.startsWith("-sql:")) {
                sql=args[i].substring(5);
            }            
        }

        if (usage)         {
            showUsage();
            return "";
        }

        if (create)         {
            int pos= dbpath.lastIndexOf('/'); 
            if (pos >= 0 ) {
                dbid= dbpath.substring(pos+1);
                dbpath=dbpath.substring(0,pos);
            }
            url = xxCreateDB(new File(dbpath), dbid,user, pwd);
        }
        
        if (! isEmpty(sql)) {
            xxLoadSQL(url, user, pwd, new File(sql));            
        }
        
        if (true) {
            xxCloseDB(url, user, pwd);            
        }
        
        return url;
    }
    
    /**
     * @param fp
     * @param dbid
     * @param user
     * @param pwd
     * @return
     * @throws SQLException
     * @throws IOException
     */
    protected abstract String xxCreateDB(File fp, String dbid, String user, String pwd) throws SQLException, IOException ;
    
    /**
     * @param url
     * @param user
     * @param pwd
     * @param sql
     * @throws SQLException
     * @throws IOException
     */
    protected abstract void xxLoadSQL(String url, String user, String pwd, File sql) throws SQLException, IOException ;
    
    /**
     * @param url
     * @param user
     * @param pwd
     * @throws SQLException
     * @throws IOException
     */
    protected abstract void xxCloseDB(String url, String user, String pwd) throws SQLException, IOException ;
        
    /**
     * @param app
     * @param db
     */
    protected HxxDBSQL(String app, String db) {
        _app=app;
        _db=db;
    }
        
    /**
     * @param app
     * @param args
     * @return
     */
    protected static int runMain(HxxDBSQL app, String[] args) {
        int rc=-1;
        try  {
            app.createDatabase(args);
            rc=0;
        }
        catch (Throwable t) {        
            t.printStackTrace();
        }     
        return rc;
    }
    
    /**
     * @param args
     * @return
     * @throws Exception
     */
    public String createDatabase(String[] args) throws Exception {
        return start(args);
    }
    
    private void showUsage()    {
        System.out.println("Usage: " + _app + " { -create:<db-dir> | -url:<db-url> } -user:<user> -pwd:<password> -sql:<ddl-file>");
        System.out.println("Example:");
        System.out.println("create a " + _db + " database>");
        System.out.println("  -create:/home/user1/db -user:user1 -pwd:secret");
        System.out.println("create a " + _db + " database & load sql>");
        System.out.println("  -create:/home/user1/db -user:user1 -pwd:secret -sql:/home/user1/ddl.sql");
        System.out.println("load sql only>");
        System.out.println("  -url:jdbc:hsqldb:file:/home/user1/db -user:user1 -pwd:secret -sql:/home/user1/ddl.sql");
    }
    
}
