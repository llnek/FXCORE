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

import static com.zotoh.core.db.JDBCUte.setStatement;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.trim;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.StrUte;
import com.zotoh.core.util.Tuple;

/**
 * Higher level abstraction of a java jdbc object. 
 *
 * @author kenl
 *
 */
public final class JDBC implements DBVars {
    
    private transient Logger _log= getLogger(JDBC.class); 
    public Logger tlog() {  return _log; }    
    private JDBCPool _pool;    
        
    /**
     * Constructor.
     * 
     * @param pool
     */
    public JDBC(JDBCPool pool)    {
        tstObjArg("jdbc-pool", pool);
        _pool=pool;
    }

    /**
     * @param tbl Table.
     * @return
     * @throws SQLException
     */
    public Map<String, Properties> getTableMetaData(String tbl) throws SQLException    {
        
        Map< String,Properties > ret= MP();
        int tries= _pool.getRetries();
        SQLException bc=null;                
        do {
            JConnection jc= null;
            ResultSet rset= null;
            Properties props;
            try  {
                tbl= _pool.getVendor().assureTableCase(tbl);
                jc= _pool.getNextFree();
                rset= jc.getConnection().getMetaData().getColumns(null,null, tbl, null);
                ret.clear();
                while (rset.next()) {            
                    props= new Properties();
                    props.put("COLUMN_SIZE",  rset.getInt("COLUMN_SIZE"));
                    props.put("DATA_TYPE", rset.getInt("DATA_TYPE"));
                    ret.put(rset.getString("COLUMN_NAME").toUpperCase(), props);
                }
                return ret;
            }            
            catch (Exception e) {        
                bc= onError(jc, e);
            }
            finally {        
                DBUte.safeClose(rset);
                drop(jc);
            }
            --tries;
        }
        while ( tries >= 0);
        
        throw bc;
    }

    /**
     * Do a "select count(*) from tbl where....".
     * 
     * @param tbl
     * @param where
     * @param pms
     * @return
     * @throws SQLException
     */
    public boolean existRows(String tbl, String where, Tuple pms)
                throws SQLException    {
        return countRows(tbl, where, pms ) > 0;
    }

    /**
     * Do a "select count(*) from tbl".
     * 
     * @param tbl Table.
     * @return
     * @throws SQLException
     */
    public boolean existRows(String tbl)     throws SQLException    {
        return countRows(tbl) > 0;        
    }
    
    /**
     * Do a "select count(*) from tbl".
     * 
     * @param tbl Table.
     * @return
     * @throws SQLException
     */
    public int countRows(String tbl) throws SQLException    {
        return countRows(tbl, "", new Tuple() );
    }
    
    /**
     * Do a "select count(*) from tbl where....".
     * 
     * @param tbl
     * @param where
     * @param params
     * @return
     * @throws SQLException
     */
    public int countRows(String tbl, String where, Tuple pms) 
                throws SQLException    {
    	
        SELECTStmt sql= new SELECTStmt("COUNT(*)", tbl, where, pms);
        int tries= _pool.getRetries();
        SQLException bc=null;
        
        do {                     
            JConnection jc= _pool.getNextFree() ;
            Connection conn= jc.getConnection();
            PreparedStatement stmt= null;
            ResultSet rset=null;
            try  {
                stmt= conn.prepareStatement(prepareSQL(sql));
                int cnt=0;
                Object[] args= sql.getQParams() ;
                for (int i=0; i < args.length; ++i ) {
                	if (args[i] != null) {
                		setStatement(stmt, ++cnt, args[i] );
                	}
                }
                rset= stmt.executeQuery();
                return ( rset != null && rset.next()) ? rset.getInt(1) : 0;
            }
            catch (Exception e) {          
                bc=onError(jc, e);
            }
            finally {
                DBUte.safeClose(rset);
                DBUte.safeClose(stmt);
                drop(jc);
            }
            --tries;
        } 
        while ( tries >= 0);
        
        throw bc;
    }

    /**
     * Get & prepare a connection for a transaction.
     * 
     * @return
     * @throws SQLException
     */
    public JConnection beginTransaction() throws SQLException {
        JConnection c=_pool.getNextFree();
        c.begin();
        return c;
    }
    
    /**
     * Commit the transaction bound to this connection.
     * 
     * @param c
     * @throws SQLException
     */
    public void commitTransaction(JConnection c) throws SQLException {
        if (c != null) {
            c.getConnection().commit();
        }
    }
    
    /**
     * Rollback the transaction bound to this connection.
     * 
     * @param c
     * @throws SQLException
     */
    public void cancelTransaction(JConnection c) throws SQLException {
        if (c != null) {
            c.getConnection().rollback();
        }
    }
    
    /**
     * Close the transaction.  The connection SHOULD not be used afterwards.
     * 
     * @param c
     * @throws SQLException
     */
    public void closeTransaction(JConnection c) throws SQLException {
        _pool.returnUsed(c);
    }
    
    /**
     * Do a "select * ...".
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    public DBRow fetchOneRow(SELECTStmt sql) throws SQLException    {
        List<DBRow> rows= fetchRows(sql);
        return rows.isEmpty() ? null : rows.get(0);
    }
    
    /**
     * Do a "select * ...".
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<DBRow> fetchRows(SELECTStmt sql) throws SQLException    {
        
        if (sql==null) { return Collections.emptyList(); }        
        int tries= _pool.getRetries();
        SQLException bc= null;
        
        do {
            JConnection jc = _pool.getNextFree();
            try   {
                return selectXXX(jc, sql);
            }
            catch (Exception e) {
                bc= onError(jc,e);
            }
            finally {
                drop(jc);
            }
            --tries;
        }
        while (tries >=0);

        throw bc;
    }

    /**
     * Do a "delete from ...".
     * 
     * @param jc
     * @param sql
     * @return
     * @throws SQLException
     */
    public int deleteRows(JConnection jc, DELETEStmt sql) throws SQLException    {
        return delete(jc, sql);
    }

    /**
     * Do a "delete from ...".
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    public int deleteRows(DELETEStmt sql) throws SQLException    {

    	if (sql==null) { return 0; }
    	
        int tries= _pool.getRetries();
        SQLException bc= null;
        
        do {
           JConnection jc = _pool.getNextFree();
           try  {               
               jc.begin();               
               int cnt= delete(jc, sql);
               jc.flush();
               return cnt;
           }
           catch (Exception e) {               
               bc= onError(jc, e);
           }
           finally {
               drop(jc);
           }
           --tries;
        }
        while (tries >= 0);

        throw bc;
    }

    /**
     * Do a "insert into ...".
     * 
     * @param jc
     * @param row
     * @throws SQLException
     */
    public void insertOneRow(JConnection jc, DBRow row) throws SQLException    {
        insert(jc, row);
    }

    /**
     * Do a "insert into ...".
     * 
     * @param row
     * @return
     * @throws SQLException
     */
    public int insertOneRow(DBRow row) throws SQLException    {
        
    	tstObjArg("row-tobe-inserted", row);
    	
        int tries = _pool.getRetries();
        SQLException bc= null;
        
        do {
            JConnection jc= _pool.getNextFree();
            try  {
                jc.begin();               
                int cnt= insert(jc, row);
                jc.flush();
                return cnt;
            }
            catch (Exception e) {
                bc=onError(jc,e);
            }
            finally {      
                drop(jc);
            }
            --tries;
        }        
        while (tries >= 0);

        throw bc;
    }

    /**
     * Do a "update set...".
     * 
     * @param jc
     * @param row
     * @param where
     * @param pms
     * @return
     * @throws SQLException
     */
    public int updateOneRow(JConnection jc, DBRow row, String where, Tuple pms) 
            throws SQLException     {
        return update(jc, row, where, pms);
    }

    /**
     * Do a "update set...".
     * 
     * @param row
     * @param where
     * @param pms
     * @return
     * @throws SQLException
     */
    public int updateOneRow(DBRow row, String where, Tuple pms) 
    				throws SQLException     {        
    	tstObjArg("row-tobe-updated", row);
    	tstObjArg("params-lst", pms);
        int tries = _pool.getRetries();
        SQLException bc= null;
        
        do {
            JConnection jc= _pool.getNextFree();
            try   {
                jc.begin();                
                int cnt= update(jc, row, where, pms);
                jc.flush();
                return cnt;
            }
            catch (Exception e) {
                bc= onError(jc,e);
            }
            finally {         
                drop(jc);
            }
            --tries;
        }
        while (tries >= 0);

        throw bc;
    }

    private int update(JConnection jc, DBRow row, String where, Tuple pms) 
    				throws SQLException    {
        UPDATEStmt sql= new UPDATEStmt(row, where, pms);
        Connection conn=jc.getConnection();
        PreparedStatement stmt=null;
        try   {
            stmt=conn.prepareStatement( prepareSQL(sql));
            Object[] args= sql.getQParams() ;
            int pos=0;
            for (int i=0; i < args.length; ++i ) {
            	if (args[i] != null) {
            		setStatement( stmt, ++pos, args[i] );
            	}
            }
            return stmt.executeUpdate();
        }
        catch (Exception e) {
            throw onError(jc, e);
        }
        finally {
            DBUte.safeClose(stmt);
        }
    }

    private List<DBRow> buildRows(String tbl, ResultSet rset) throws SQLException, IOException     {
        List<DBRow> lst= LT();
        if (rset != null) 
            while ( rset.next())  {
                lst.add( buildOneRow(tbl, rset));
            }
        tlog().debug("Fetched from table: \"{}\" : rows= {}" , tbl, lst.size());
        return lst;
    }

    private DBRow buildOneRow(String tbl, ResultSet rset) throws SQLException, IOException    {
    	
        ResultSetMetaData meta= rset.getMetaData();
        int cnt = meta.getColumnCount();
        DBRow row= new DBRow(tbl);
        String column;
        Object obj;
        Reader rdr;
        InputStream inp;
        
        for (int i=0; i < cnt; ++i)        {
            column= meta.getColumnName(i+1).toUpperCase();
            obj=rset.getObject(i+1);
            
            inp=null;
            rdr=null;
            
            if (obj instanceof Blob) {
            	Blob bb=(Blob) obj;
            	inp=bb.getBinaryStream() ;
            }
            else if (obj instanceof InputStream) {
            	inp = (InputStream) obj ;
            }
            else if (obj instanceof Clob) {
            	Clob cc= (Clob) obj;
            	rdr= cc.getCharacterStream() ;
            }
            else if (obj instanceof Reader) {
            	rdr= (Reader) obj;
            }
            
            if (inp != null) try {
            	obj= StreamUte.readStream( inp, false );
            } finally { StreamUte.close(inp); }
            
            if (rdr != null) try {
                obj= StrUte.readStream( rdr, false );
            } finally { StreamUte.close(rdr); }
 
            row.add(column, obj);
        }

        return row;
    }

    private String prepareSQL(SQLStmt stmt)    {
        
        DBVendor v= _pool.getVendor();
        String sql= trim(stmt.toString());        
        String tst=sql.toLowerCase();

        if (tst.startsWith("select"))        {
            sql=v.tweakSELECT(sql);
        }
        else  if (tst.startsWith("update"))        {
            sql=v.tweakUPDATE(sql);
        }
        else  if (tst.startsWith("delete"))        {
            sql=v.tweakDELETE(sql);
        }

        tlog().debug(sql);
        return sql;
    }
    
    private boolean maybeDealWithBadConn( Connection conn, Exception e)    {
        return (_pool.isBadConnection(e) || _pool.isBadConnection(conn));       
    }
    
    private void drop(JConnection jc)    {
        _pool.returnUsed(jc, jc.isDead() ? false : true);
    }

    private DBBadConnError onError( JConnection jc,   Connection con, Exception e) 
            throws SQLException    {        
        if (maybeDealWithBadConn( con, e)) {
            jc.die();
            return new DBBadConnError(e);
        }        
        else if (e instanceof SQLException) {        
            throw (SQLException) e;
        }
        
        throw new SQLException(e);        
    }
    
    private List<DBRow> selectXXX(JConnection jc, SELECTStmt sql) 
            throws SQLException    {        
        Connection conn=jc.getConnection();
        ResultSet rset=null;
        PreparedStatement stmt= null;
        
        try   {
            stmt=conn.prepareStatement(prepareSQL(sql));
            Object[] pms= sql.getQParams() ;
            int pos=0;
            for (int i=0; i < pms.length; ++i ) {
            	if ( pms[i] != null) {
            		setStatement( stmt, ++pos, pms[i] );
            	}
            }
            rset= stmt.executeQuery();
            return buildRows(sql.getTable(), rset);
        }
        catch (Exception e) {        
            throw onError(jc, e);
        }
        finally {        
            DBUte.safeClose(rset);
            DBUte.safeClose(stmt);
        }
    }
        
    private int delete(JConnection jc, DELETEStmt sql) 
            throws SQLException     {
        Connection conn= jc.getConnection();
        PreparedStatement stmt= null;
        try  {
            stmt= conn.prepareStatement(prepareSQL(sql));
            Object[] pms= sql.getQParams() ;
            int pos=0;
            for (int i=0; i < pms.length; ++i ) {
            	if (pms[i] != null) {
            		setStatement( stmt, ++pos, pms[i] );
            	}
            }
            return stmt.executeUpdate();
        }
        catch (Exception e) {
            throw onError(jc, e);
        }
        finally {        
            DBUte.safeClose(stmt);
        }
    }

    private int insert(JConnection jc, DBRow row)     throws SQLException     {
    	
        INSERTStmt sql= new INSERTStmt(row);
        Connection conn= jc.getConnection();
        PreparedStatement stmt=null;
        try {
            stmt= conn.prepareStatement(prepareSQL(sql));
            Object[] pms= sql.getQParams() ;
            int pos=0;
            for (int i=0; i < pms.length; ++i ) {
            	if (pms[i] != null) {
            		setStatement( stmt, ++pos, pms[i] );
            	}
            }
            return stmt.executeUpdate();
        }
        catch (Exception e) {
            throw onError(jc, e);
        }
        finally {        
            DBUte.safeClose(stmt);
        }
    }

    private SQLException onError(JConnection jc, Exception e) 
            throws SQLException {        
        SQLException bc= null;
        
        if (e instanceof DBBadConnError) {
            bc= (DBBadConnError) e;
        }  else {
            bc= onError(jc, jc.getConnection(), e);                   
        }
        
        jc.cancelQuietly();
        
        return bc;
    }
    
    
    
}
