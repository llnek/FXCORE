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

/**
 * DB Constants.
 *  
 * @author kenl
 *
 */
public interface DBVars {
    
    public static final String POSTGRESQL_DRIVER= "org.postgresql.Driver";
    public static final String MYSQL_DRIVER= "com.mysql.jdbc.Driver";
    public static final String H2_DRIVER= "org.h2.Driver";
    public static final String HSQLDB_DRIVER= "org.hsqldb.jdbcDriver";
    public static final String HSQLDB_DRIVER_2= "org.hsqldb.jdbc.JDBCDriver" ;
    
    public static final String DERBY_E_DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DERBY_C_DRIVER= "org.apache.derby.jdbc.ClientDriver";
    
    public static final String SQL_PARAM= " = ? ";    
    public static final int VARCHAR_WIDTH= 255;
    
    public static final String ROWLOCK="rowlock";
    public static final String NOLOCK="nolock";
    
    public static final String HSQLDB_MEM_URL="jdbc:hsqldb:mem:";
    public static final String HSQLDB_FILE_URL="jdbc:hsqldb:file:";

    //public static final String H2_SERVER_URL = "jdbc:h2:tcp://host/path/db";
    public static final String H2_FILE_URL = "jdbc:h2:";
    public static final String H2_MEM_URL = "jdbc:h2:mem:";
    
    public static final String S_POSTGRESQL= "postgresql";
    public static final String S_ORACLE= "oracle";
    public static final String S_MSSQL= "mssql";
    public static final String S_MYSQL= "mysql";
    public static final String S_H2= "h2";
    public static final String S_HSQLDB= "hsql";
    public static final String S_HYPERSQL= "hypersql";
    public static final String S_DERBY= "derby";
    public static final String S_DB2= "db2";

//    public static final String COL_VERCNT = "_VERCNT_";
//    public static final String COL_OID= "_OBJOID_";
//    public static final String COL_LASTCHG= "_LASTCHG_";

//    public static final String COL_ASSOC= "_ASSOC_";
//    public static final String COL_RHS= "_RHS_";
//    public static final String COL_LHS= "_LHS_";
//    
//    public static final String TBL_SHARED= "EDB_SHARED";
    public static final String CSEP= "?";
    public static final String S_DDLSEP= "-- :";  // watch out there's a space before the colon

    public static final String TS_CURRENT= "current_timestamp";
}
