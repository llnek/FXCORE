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
import static com.zotoh.core.util.LangUte.MP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.util.FileUte;
import com.zotoh.core.util.Tuple;
import com.zotoh.core.util.WWID;

public final class JUT {

    private static String _tmpDir,_dbDir,_dbID, _user, _pwd;
    private static String SQL=    
    "CREATE CACHED TABLE star (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"+
    "firstname VARCHAR(20),"+
    "lastname VARCHAR(20));\n"+
    "-- :\n" +
    "INSERT INTO star (id, firstname, lastname) VALUES (DEFAULT, 'Felix', 'the Cat');";
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception    {
        _tmpDir=niceFPath(System.getProperty("java.io.tmpdir"));
        _dbDir=_tmpDir +"/jutHxxDB";
        _dbID="test007";
        _user="zeus";
        _pwd="zeus123";
    }

    @AfterClass
    public static void finz()    {
    }

    @Before
    public void open() throws Exception    {
        //System.out.println("tempdir= "+_tmpDir);
    }

    @After
    public void close() throws Exception    {
    }
    
    @Test
    public void testCreateHyperDB() throws Exception {
        String path= _dbDir+ "/"+ _dbID;
        new HsqlDBSQL().createDatabase(new String[]{        
                "-create:"+path,
                "-user:" + _user,
                "-pwd:" + _pwd
        });
        assertTrue(HsqlDB.getInstance().existsDB(path));
    }

    @Test
    public void testCreateH2DB() throws Exception {
        String path= _dbDir+ "/"+ _dbID;
        new H2DBSQL().createDatabase(new String[]{        
                "-create:"+path,
                "-user:" + _user,
                "-pwd:" + _pwd
        });
        assertTrue(H2DB.getInstance().existsDB(path));
    }

    @Test
    public void testCreateHyperWithSQL() throws Exception {
        File f= new File(_tmpDir+"/"+WWID.generate()+".sql");
        FileUte.writeFile(f, SQL);
        String path= _dbDir+ "/"+ _dbID;
        try {
            new HsqlDBSQL().createDatabase(new String[]{        
                    "-create:"+ path,
                    "-user:" + _user,
                    "-pwd:"+ _pwd,
                    "-sql:"+f.getCanonicalPath()
            });
        } finally {
            f.delete();
        }
        assertTrue(new File(path+".data").exists());
    }

    @Test
    public void testCreateH2WithSQL() throws Exception {
        File f= new File(_tmpDir+"/"+WWID.generate()+".sql");
        FileUte.writeFile(f, SQL);
        String path= _dbDir+ "/"+ _dbID;
        try {
            new H2DBSQL().createDatabase(new String[]{        
                    "-create:"+ path,
                    "-user:" + _user,
                    "-pwd:"+ _pwd,
                    "-sql:"+f.getCanonicalPath()
            });
        } finally {
            f.delete();
        }
        assertTrue(new File(path+".h2.db").exists());
    }
    
    @Test
    public void testLoadHyperWithSQL() throws Exception {
        File f= new File(_tmpDir+"/"+WWID.generate()+".sql");
        FileUte.writeFile(f, SQL);
        String path= _dbDir+ "/"+ _dbID;
        String url;
        try {
            url = new HsqlDBSQL().createDatabase(new String[]{        
                    "-create:"+path,
                    "-user:"+_user,
                    "-pwd:"+_pwd
            });
            new HsqlDBSQL().createDatabase(new String[]{        
                    "-url:"+url,
                    "-user:"+_user,
                    "-pwd:"+_pwd,
                    "-sql:"+f.getCanonicalPath()
            });
        } finally {
            f.delete();
        }
        assertTrue(new File(path+".data").exists());
    }

    @Test
    public void testLoadH2WithSQL() throws Exception {
        File f= new File(_tmpDir+"/"+WWID.generate()+".sql");
        FileUte.writeFile(f, SQL);
        String path= _dbDir+ "/"+ _dbID;
        String url;
        try {
            url = new H2DBSQL().createDatabase(new String[]{        
                    "-create:"+path,
                    "-user:"+_user,
                    "-pwd:"+_pwd
            });
            new H2DBSQL().createDatabase(new String[]{        
                    "-url:"+url,
                    "-user:"+_user,
                    "-pwd:"+_pwd,
                    "-sql:"+f.getCanonicalPath()
            });
        } finally {
            f.delete();
        }
        assertTrue(new File(path+".h2.db").exists());
    }
    
    @Test
    public void testDBRow() throws Exception {
        Map<String,Object> m= MP();
        DBRow r;
        r=new DBRow("table1");
        assertTrue(r.getSQLTable().equals("table1"));
        m.put("c1", 25);
        r= new DBRow(m);
        assertTrue(r.exists("c1"));
        assertTrue(r.get("c1").equals(25));
        r= new DBRow("t1");
        r.add(m);
        assertTrue(r.get("c1").equals(25));
        assertTrue(r.values().size()== r.size());
        r.clear();
        assertTrue(r.isEmpty());
        r= new DBRow(m);
        assertTrue(r.remove("c1").equals(25));
        assertTrue(r.size()==0);
    }
        
    @Test
    public void testMemDB() throws Exception {
        String url= HsqlDB.getInstance().createMemDB(_dbID, _user, _pwd);
        assertTrue( url != null && url.length() > 0);
        url=H2DB.getInstance().createMemDB(_dbID, _user, _pwd);
        assertTrue( url != null && url.length() > 0);
    }
    
    @Test
    public void testLoadDDL() throws Exception {
        String url= H2DB.getInstance().createMemDB("a", _user, _pwd);
        DDLUte.loadDDL(new JDBCInfo(url, _user, _pwd), getDDLStr());
        // no errors assume works :)
    } 
    private String getDDLStr() throws Exception {
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        DDLUte.ddlToStream(baos, "com/zotoh/core/db/ddl.sql", this.getClass().getClassLoader());
        return new String(baos.toByteArray(), "UTF-8");
    }
     
    @Test
    public void testJDBC() throws Exception {
    	
        String dbUrl= H2DB.getInstance().createMemDB(_dbID, _user, _pwd);
        JDBCInfo j= new JDBCInfo(dbUrl, _user, _pwd);
        DDLUte.loadDDL(j, getDDLStr());
        
        JDBCPoolManager pm= new JDBCPoolManager();
        assertFalse(pm.existsPool("x"));
        JDBCPool pp= pm.createPool("x", j);
        assertNotNull(pp);
        assertTrue(pm.existsPool("x"));
        pp= pm.getPool("x");
        assertNotNull(pp);
        
        JDBC jj= pp.newJdbc();
        DBRow r;
        Map<?,?> m= jj.getTableMetaData("user_accounts");        
        assertTrue(m.size()==14);
        assertTrue(0 == jj.countRows("user_accounts"));
        
        // insert
        r= new DBRow("user_accounts");
        r.add("user_id", "id1");
        assertTrue( 1== jj.insertOneRow(r));
        
        // update
        r.clear();
        r.add("user_role", "admin");
        assertTrue(1==jj.updateOneRow(r, "user_id=?", new Tuple("id1") ));
        
        // select
        SELECTStmt s = SELECTStmt.simpleQry("user_accounts");
        r=jj.fetchOneRow(s);
        assertTrue(r != null && r.size()==14);
        
        s= new SELECTStmt("select * from user_accounts where user_id=?", 
        				new Tuple("id1") );
        r=jj.fetchOneRow(s);
        assertTrue( r != null && r.size()==14);
        
        s= new SELECTStmt("select * from user_accounts where user_id='id1' ");
        r=jj.fetchOneRow(s);
        assertTrue(r!=null && r.size()==14);
        
        s= new SELECTStmt("user_role", "user_accounts");
        r=jj.fetchOneRow(s);
        assertNotNull(r != null && r.size()==1);
        
        s= new SELECTStmt("user_role", "user_accounts", "user_id=?",
        				new Tuple("id1") );
        r=jj.fetchOneRow(s);
        assertNotNull(r != null && r.size()==1);
        
        s= new SELECTStmt("user_role,user_id", "user_accounts", "user_id=?", "order by user_role",
        				new Tuple("id1") );
        r=jj.fetchOneRow(s);
        assertNotNull(r != null && r.size()==2);
        
        assertTrue(jj.existRows("user_accounts"));
        assertTrue(jj.existRows("user_accounts", "user_id=?", 
        					new Tuple("id1") ));
        
        assertTrue(1==jj.countRows("user_accounts" ));
        assertTrue(1==jj.countRows("user_accounts", "user_id=?", new Tuple("id1")));
        
        // delete
        DELETEStmt d= DELETEStmt.simpleDelete("user_accounts");
        assertTrue(1==jj.deleteRows(d));
        
        d=new DELETEStmt("delete from user_accounts where user_id=?",
        				new Tuple("id1") );

        assertTrue(0==jj.deleteRows(d));
        d=new DELETEStmt("delete from user_accounts where user_id='id1' "); 
        assertTrue(0==jj.deleteRows(d));
        
        d= new DELETEStmt("user_accounts", "user_id=?" , new Tuple("id1") );
        assertTrue(0==jj.deleteRows(d));
        
        d= new DELETEStmt("user_accounts", "user_id='id1' " , new Tuple() );
        assertTrue(0==jj.deleteRows(d));
        
        assertFalse(jj.existRows("user_accounts"));
    }
    
    @Test
    public void testUte() throws Exception {
        JDBCInfo jp= new JDBCInfo("jdbc:h2:mem:xxx;DB_CLOSE_DELAY=-1", _user, _pwd);
        Connection c;
        c=DBUte.createConnection(jp);
        assertNotNull(c);
        assertFalse(DBUte.tableExists(jp, "xyz"));
        Statement stmt=null;
        try { 
            stmt=c.createStatement();
            stmt.executeUpdate("create table xyz ( fname varchar(255))"); 
        } catch (SQLException e) {
           assertTrue("load-ddl-failed", false); 
        } finally {
            DBUte.safeClose(stmt);
        }
        DBUte.safeClose(c);
        try {DBUte.testConnection(jp); } catch (SQLException e) {
            assertTrue(false);
        }
        DBVendor v=DBUte.getDBVendor(jp);
        assertTrue(v.equals(DBVendor.H2));
        assertNotNull(DBUte.loadDriver("org.h2.Driver"));        
        assertTrue(DBUte.tableExists(jp, "xyz"));        
        assertFalse(DBUte.rowExists(jp, "xyz"));
        
        c=DBUte.createConnection(jp);
        try { 
            stmt=c.createStatement();
            stmt.executeUpdate("insert into xyz values('jerry')"); 
        } catch (SQLException e) {
           assertTrue("load-ddl-failed", false); 
        } finally {
            DBUte.safeClose(stmt);
        }
        DBUte.safeClose(c);
        assertTrue(DBUte.rowExists(jp, "xyz"));        
        assertTrue( DBUte.firstRow(jp, "select * from xyz").size()==1);
    }
    
    @Test
    public void testTransaction() throws Exception {
        JDBCInfo jp= new JDBCInfo("jdbc:h2:mem:zzz;DB_CLOSE_DELAY=-1", _user, _pwd);
        Connection c;
        c=DBUte.createConnection(jp);
        assertNotNull(c);
        Statement stmt=null;
        try { 
            stmt=c.createStatement();
            stmt.executeUpdate("create table xyz ( fname varchar(255))"); 
        } catch (SQLException e) {
            //e.printStackTrace();
           assertTrue("load-ddl-failed", false); 
        } finally {
            DBUte.safeClose(stmt);
        }
        DBUte.safeClose(c);

        JDBCPoolManager pm= new JDBCPoolManager();
        JDBCPool p=pm.createPool(jp);
        JDBC j= p.newJdbc();
        DBRow r= new DBRow("xyz");
        JConnection jc= j.beginTransaction();
        r.add("fname", "jerry");
        j.insertOneRow(jc, r);
        j.cancelTransaction(jc);
        j.closeTransaction(jc);
        assertFalse(DBUte.rowExists(jp, "xyz"));
        
        jc= j.beginTransaction();
        r.add("fname", "jerry");
        j.insertOneRow(jc, r);
        j.commitTransaction(jc);
        j.closeTransaction(jc);
        assertTrue(DBUte.rowExists(jp, "xyz"));
        
    }
    
}