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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BIT;
import static java.sql.Types.BLOB;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGNVARCHAR;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NULL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.NVARCHAR;
import static java.sql.Types.REAL;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARBINARY;
import static java.sql.Types.VARCHAR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import com.zotoh.core.crypto.Password;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Null;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 * 
 */
public enum JDBCUte {
    ;
    private static Logger _log = getLogger(JDBCUte.class);
    public static Logger tlog() {        return _log;    }

    /**
     * @param val
     * @return
     */
    public static boolean isNil(Object val) { return val==null || val==Null.NULL ; }
    
    /**
     * @param z
     * @return
     * @throws SQLException
     */
    public static int toSqlType (Class<?> z) throws SQLException {
        
        if (BigDecimal.class==z) return java.sql.Types.DECIMAL ;
        if (String.class==z) return java.sql.Types.VARCHAR;
        
        if (Boolean.class==z || boolean.class==z) return java.sql.Types.BOOLEAN;
        if (Integer.class==z|| int.class==z) return java.sql.Types.INTEGER;
        if (Long.class==z || long.class==z) return java.sql.Types.BIGINT;
        if (Double.class==z || double.class==z) return java.sql.Types.DOUBLE;
        if (Float.class==z || float.class==z) return java.sql.Types.FLOAT;
        
        if (java.sql.Date.class==z) return java.sql.Types.DATE;
        if (java.sql.Time.class==z) return java.sql.Types.TIME;
        if (java.sql.Timestamp.class==z) return java.sql.Types.TIMESTAMP;
        if (Date.class==z) return java.sql.Types.DATE;
        if (byte[].class==z) return java.sql.Types.BINARY ;
        
//        if (BigInteger.class==z) return java.sql.Types.DECIMAL ;
        
        throw new SQLException("JDBC Type not supported: " + z.getName()) ;
    }
    
    
    /**
     * @param rs
     * @param col
     * @param javaSqlType
     * @param target
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static Object getObject(ResultSet rs, int col, int javaSqlType, Class<?> target)
            throws SQLException, IOException {
        ResultSetMetaData mm = rs.getMetaData();
        Object cval = null;

        switch (javaSqlType) {

        case SMALLINT:
        case TINYINT:
            cval = sql_short(rs, col);
            break;
            
        case INTEGER:
            cval = sql_int(rs, col);
            break;

        case BIGINT:
            cval = sql_long(rs, col);
            break;

        case REAL:
        case FLOAT:
            cval = sql_float(rs, col);
            break;

        case DOUBLE:
            cval = sql_double(rs, col);
            break;

        case NUMERIC:
        case DECIMAL:
            cval = sql_bigdec(rs, col);
            break;

        case BOOLEAN:
            cval = sql_bool(rs, col);
            break;

        case TIME:
            cval = sql_time(rs, col);
            break;

        case DATE:
            cval = sql_date(rs, col);
            break;

        case TIMESTAMP:
            cval = sql_timestamp(rs, col);
            break;

        case LONGVARCHAR:
        case VARCHAR:
            cval = sql_string(rs, col);
            break;

        case LONGVARBINARY:
            cval = sql_stream(rs, col);
            break;

        case VARBINARY:
        case BINARY:
            cval = sql_bytes(rs, col);
            break;

        case BLOB:
            cval = sql_blob(rs, col);
            break;

        case BIT:
            cval = sql_bit(rs, col);
            break;

        case NULL:
            cval = sql_null(rs, col);
            break;

        case LONGNVARCHAR:
        case NVARCHAR:
        case CLOB:
        default:
            sql_notimpl(mm, col);
            break;
        }

        cval = safe_coerce(cval, target);
        return cval;
    }

    private static Object sql_short(ResultSet rs, int col) throws SQLException {
        return new Short(rs.getShort(col));
    }
    
    private static Object sql_int(ResultSet rs, int col) throws SQLException {
        return new Integer(rs.getInt(col));
    }

    private static Object sql_long(ResultSet rs, int col) throws SQLException {
        return new Long(rs.getLong(col));
    }

    private static Object sql_float(ResultSet rs, int col) throws SQLException {
        return new Float(rs.getFloat(col));
    }

    private static Object sql_double(ResultSet rs, int col) throws SQLException {
        return new Double(rs.getDouble(col));
    }

    private static Object sql_bigdec(ResultSet rs, int col) throws SQLException {
        return rs.getBigDecimal(col);
    }

    private static Object sql_bool(ResultSet rs, int col) throws SQLException {
        return new Boolean(rs.getBoolean(col));
    }

    private static Object sql_time(ResultSet rs, int col) throws SQLException {
        return rs.getTime(col);
    }

    private static Object sql_date(ResultSet rs, int col) throws SQLException {
        return rs.getDate(col);
    }

    private static Object sql_timestamp(ResultSet rs, int col) throws SQLException {
        return rs.getTimestamp(col);
    }

    private static Object sql_string(ResultSet rs, int col) throws SQLException {
        return rs.getString(col);
    }

    private static Object sql_bit(ResultSet rs, int col) throws SQLException {
        return new Byte(rs.getByte(col));
    }

    private static Object sql_null(ResultSet rs, int col) throws SQLException {
        return Null.NULL;
    }

    private static Object sql_notimpl(ResultSetMetaData rs, int col)
            throws SQLException {
        String cn = rs.getColumnName(col);
        int type = rs.getColumnType(col);
        throw new SQLException("Unsupported SQL Type: " + type
                + " for column: " + cn);
    }

    @SuppressWarnings("unused")
    private static Object sql_clob(ResultSet rs, int col) throws SQLException,
            IOException {
        return null;
    }

    private static Object sql_blob(ResultSet rs, int col) throws SQLException,
            IOException {
        Blob b = rs.getBlob(col);
        return b==null ? null : sql_stream( b.getBinaryStream() );
    }

    private static Object sql_stream(ResultSet rs, int col) throws SQLException,
            IOException {
        InputStream inp = rs.getBinaryStream(col);
        return sql_stream(inp);
    }

    private static Object sql_stream(InputStream inp) throws SQLException, IOException {
        StreamData rc = null;
        Tuple t;
        OutputStream os = null;
        try {
            t = createTempFile(true);
            os = (OutputStream) t.get(1);
            streamToStream(inp, os);
            rc = new StreamData(t.get(0));
        } finally {
            close(inp);
            close(os);
        }
        return rc;
    }

    private static Object sql_bytes(ResultSet rs, int col) throws SQLException {
        return rs.getBytes(col);
    }


    //------------ coerce

    private static Class<?> safe_primitive(Class<?> target) {
    	
        if (boolean.class==target) { target=Boolean.class; }
        else if (long.class==target) { target=Long.class; }
        else if (short.class==target) { target=Short.class; }
        else if (int.class==target) { target=Integer.class; }
        else if (float.class==target) { target=Float.class; }
        else if (double.class==target) { target=Double.class; }
        else if (byte.class==target) { target=Byte.class; }
        
        return target;
    }
    
    private static Object safe_coerce(Object cval, Class<?> target) 
    throws SQLException, IOException {
        if (cval == null || cval == Null.NULL) {            return null;        }
        if (target==null) { return cval; }
        
        target= safe_primitive( target ) ;
        Class<?> z = cval.getClass();
        
        if (BigDecimal.class == target || Number.class.isAssignableFrom(target)) {
            return num_coerce(cval, target);            
        }
        else if (Boolean.class==target ) {
            return bool_coerce(cval, target);                        
        }
        else if (byte[].class == target) {
            return bytes_coerce(cval,target);
        }
        else if (StreamData.class == target) {
            return stream_coerce(cval,target);
        }
        else if (String.class == target) {
            return string_coerce(cval,target);
        }
        else if (java.sql.Timestamp.class==target) {
            return tstamp_coerce(cval, target) ;
        }
        else if (java.sql.Time.class ==target) {            
            return time_coerce(cval, target) ;
        }
        else if (java.sql.Date.class ==target || java.util.Date.class==target) {
            return date_coerce(cval,target);
        }
        else {
            throw new SQLException("Cannot coerce coltype: " + z + " to target-class: " + target);
        }
        
    }

    private static Object tstamp_coerce(Object cval, Class<?> target)  
            throws IOException {
        Class<?> z= cval.getClass();
        if (cval instanceof Timestamp) {            return cval;        }        
        throw new IOException("Cannot convert coltype: " + z + " to sql.timestamp");
    }
    
    private static Object time_coerce(Object cval, Class<?> target)  
            throws IOException {
        Class<?> z= cval.getClass();
        java.util.Date dt;
        if (cval instanceof Time) {            return cval;        }
        else
        if (cval instanceof java.util.Date) {
            dt = (java.util.Date) cval;
            return new Time(dt.getTime()) ;
        }
        throw new IOException("Cannot convert coltype: " + z + " to sql.time");
    }
    
    private static Object date_coerce(Object cval, Class<?> target)  
            throws IOException {
        java.util.Date dt= (java.util.Date) cval;
        Class<?> z= cval.getClass();
        if (java.sql.Date.class == target) {
            if (java.sql.Date.class == z) { return cval; }
            return new java.sql.Date(dt.getTime()) ;        
        }
        else if (java.util.Date.class == target) {            
            return dt;
        } else {
            throw new IOException("Cannot convert coltype: " + z + " to Date");            
        }
    }
    
    private static Object string_coerce(Object cval, Class<?> target)  {        
        return cval.toString();
    }
    
    private static Object stream_coerce(Object cval, Class<?> target) throws IOException  {
        Class<?> z= cval.getClass();
        StreamData rc;
        if (cval instanceof StreamData) {
            rc= (StreamData) cval;
        } 
        else if (cval instanceof byte[]) {
            rc= new StreamData(cval);
        } else {
            throw new IOException("Cannot convert coltype: " + z + " to byte[]");
        }
        return rc;
    }
    
    private static Object bytes_coerce(Object cval, Class<?> target)  throws IOException {
        Class<?> z= cval.getClass();
        StreamData s;
        byte[] rc;
        if (cval instanceof StreamData) {
            s= (StreamData) cval;
            rc= s.getBytes();
        } 
        else if (cval instanceof byte[]) {
            rc= (byte[]) cval;
        } else {
            throw new IOException("Cannot convert coltype: " + z + " to byte[]");
        }
        return rc;
    }
    
    private static Object bool_coerce(Object cval, Class<?> target)  throws IOException {
        Class<?> z= cval.getClass();
        Boolean rc;
        
        if (cval instanceof Boolean) {
            rc= (Boolean) cval;
        } 
        else if (cval instanceof BigDecimal) {
            rc = ( (BigDecimal) cval).intValue() == 0 ? false : true;
        } 
        else if (cval instanceof Number) {
            rc = ( (Number) cval).intValue() == 0 ? false : true;
        } else {
            throw new IOException("Cannot convert coltype: " + z + " to boolean");            
        }
        return rc;
    }
    
    private static Object num_coerce(Object cval, Class<?> target)  throws SQLException {
        BigDecimal big=null;
        Number b=null;
        Object rc=cval;
        
        if (cval instanceof BigInteger) {
        	throw new SQLException("Don't support BigInteger class") ;
        } 
        
        if (cval instanceof BigDecimal) {
            big = (BigDecimal) cval;
        } else {
            b = (Number) cval;
        }
        
        if (Double.class == target || double.class == target) {
            rc= big==null ? b.doubleValue() : big.doubleValue();
        } else if ( Float.class==target || float.class == target) {
            rc= big== null ? b.floatValue() : big.floatValue();
        } else if ( Long.class== target || long.class == target) {
            rc= big==null ? b.longValue() : big.longValue();
        } else if ( Integer.class == target | int.class == target) {
            rc= big==null ? b.intValue() : big.intValue();
        } else if ( Short.class == target || short.class == target) {
            rc= big==null ? b.shortValue() : big.shortValue();
        } else if ( Byte.class == target || byte.class == target) {
            rc= big==null ? b.byteValue() : big.byteValue();
        }
        
        return rc;
    }
    

    // ------------------------- set --------------
    
    
    private static boolean javeToSQLBoolean(Object obj) throws SQLException {
        
        if (obj instanceof Boolean) { return ((Boolean)obj).booleanValue(); }
        if (obj instanceof Integer) { return ((Integer)obj).intValue() == 0; }
        if (obj instanceof Long) { return ((Long)obj).longValue() == 0L; }
        if (obj instanceof Short) { return ((Short)obj).shortValue() == 0; }
        
        throw new SQLException("Invalid datatype, expecting boolean") ;
    }

    
    private static int javeToSQLInt(Object obj) throws SQLException {
        
        if (obj instanceof Integer) { return ((Integer)obj).intValue() ; }
        if (obj instanceof Short) { return ((Short)obj).intValue() ; }
        if (obj instanceof Boolean) { return ((Boolean)obj).booleanValue() ? 1 : 0 ; }
        
        throw new SQLException("Invalid datatype, expecting int/short") ;
    }
    
    
    private static long javeToSQLLong(Object obj) throws SQLException {
        
        if (obj instanceof Integer) { return ((Integer)obj).longValue() ; }
        if (obj instanceof Long) { return ((Long)obj).longValue() ; }
        if (obj instanceof Short) { return ((Short)obj).longValue() ; }
        if (obj instanceof Boolean) { return ((Boolean)obj).booleanValue() ? 1L : 0L ; }
        
        throw new SQLException("Invalid datatype, expecting long/int/short") ;
    }
    
    
    private static BigDecimal javeToSQLDecimal(Object obj) throws SQLException {
    	
    	
    	if ( obj instanceof BigDecimal ) {
    		return (BigDecimal) obj ;
    	}
    	
    	if ( obj instanceof BigInteger ) {
    		throw new SQLException("Unsupport BigInteger value type") ;
    	}
    	
    	if ( obj instanceof Number) {
    		Number n= (Number) obj;
    		return new BigDecimal( n.doubleValue() );
    	}
    	
        throw new SQLException("Invalid datatype, expecting number type, got: " + obj.getClass() ) ;
    }
    
    
    private static double javeToSQLDouble(Object obj) throws SQLException {

        if (obj instanceof Double) { return ((Double)obj).doubleValue(); }
        if (obj instanceof Float) { return ((Float)obj).doubleValue(); }

        throw new SQLException("Invalid datatype, expecting double/float") ;
    }
    
    
    private static float javeToSQLFloat(Object obj) throws SQLException {

        if (obj instanceof Double) { return ((Double)obj).floatValue(); }
        if (obj instanceof Float) { return ((Float)obj).floatValue(); }

        throw new SQLException("Invalid datatype, expecting double/float") ;
    }
    
    
    private static java.sql.Date javaToSQLDate(Object obj) throws SQLException {

        if (obj instanceof java.sql.Date) { return (java.sql.Date) obj ; }
        if (obj instanceof java.util.Date) { 
            java.util.Date d= (java.util.Date) obj;
            return new java.sql.Date( d.getTime() );
        }

        throw new SQLException("Invalid datatype, expecting date") ;
    }
    
    
    private static java.sql.Time javaToSQLTime(Object obj) throws SQLException {

        if (obj instanceof java.sql.Time) { return (java.sql.Time) obj ; }
        if (obj instanceof java.util.Date) { 
            java.util.Date d= (java.util.Date) obj;
            return new java.sql.Time( d.getTime() );
        }

        throw new SQLException("Invalid datatype, expecting date/time") ;
    }
    
    
    private static java.sql.Timestamp javaToSQLTimestamp(Object obj) throws SQLException {

        if (obj instanceof java.sql.Timestamp) { return (java.sql.Timestamp) obj ; }
        if (obj instanceof java.util.Date) { 
            java.util.Date d= (java.util.Date) obj;
            return new java.sql.Timestamp( d.getTime() );
        }

        throw new SQLException("Invalid datatype, expecting date/timestamp") ;
    }
    
    /**
     * @param stmt
     * @param pos
     * @param sqlType
     * @param value
     * @throws SQLException
     * @throws IOException
     */
    public static void setStatement(PreparedStatement stmt, int pos, int sqlType, Object value)    
                    throws SQLException, IOException    {
        
        Class<?> z= null;
        
        if (isNil(value)) {
            stmt.setNull(pos, sqlType) ;
        } else {
            z= value.getClass();
        }
        
        if (z != null)
        switch (sqlType) {
            
            case java.sql.Types.BOOLEAN:            {
                stmt.setBoolean(pos, javeToSQLBoolean(value));
            }
            break;
            
                // numbers
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC: {
                stmt.setBigDecimal(pos, javeToSQLDecimal(value)) ;
            }
            break;
            
                // ints
            case java.sql.Types.BIGINT:            {
                stmt.setLong(pos, javeToSQLLong(value)) ;                                                
            }
            break;
            
            case java.sql.Types.INTEGER:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:            {
                stmt.setInt(pos, javeToSQLInt(value)) ;                                
            }
            break;
            
                // real numbers
            case java.sql.Types.DOUBLE:            {
                stmt.setDouble(pos, javeToSQLDouble(value)) ;                
            }
            break;
            
            case java.sql.Types.REAL:            
            case java.sql.Types.FLOAT:            {
                stmt.setFloat(pos, javeToSQLFloat(value)) ;                                
            }
            break;
            
                // date time
            case java.sql.Types.DATE:            {
                stmt.setDate(pos, javaToSQLDate(value)) ;
            }
            break;
            
            case java.sql.Types.TIME:            {
                stmt.setTime(pos, javaToSQLTime(value)) ;
            }
            break;
                
            case java.sql.Types.TIMESTAMP:            {               
                stmt.setTimestamp(pos, javaToSQLTimestamp(value)) ;
            }
            break;
            
                
                // byte[]
            case java.sql.Types.VARBINARY:
            case java.sql.Types.BINARY:            {
                byte[] b;
            	if (StreamData.class==z) {
            		StreamData d= (StreamData) value;
            		b=d.getBytes() ;
            	} else if (byte[].class == z) {
                    b= (byte[]) value;            		
            	} else {
                    throw new SQLException("Expecting byte[] , got : " + z);
                }
                stmt.setBytes(pos, b) ;
            }
            break;
            
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.CLOB:           
            case java.sql.Types.NVARCHAR:
                throw new SQLException("Unsupported SQL type: " + sqlType) ;
            
                // strings
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.VARCHAR: {
                String s;
                if (value instanceof Password) {
                	Password pwd= (Password) value;
                	s= pwd.getAsEncoded() ;
                }
                else if ( value instanceof String) {
                    s= (String) value;
                }  else {
                    s= value.toString();
                }
                
                stmt.setString(pos, s) ;
            }
            break;
            
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.BLOB:            {
                InputStream inp= null;
                long len=0L;
            	if (StreamData.class==z) {
            		StreamData d = (StreamData) value ;
            		inp= d.getStream() ;
            	} else if (byte[].class == z) {
            		byte[] b = (byte[]) value ;
                    inp = StreamUte.asStream( b);            		
            	} else {
                    throw new SQLException("Expecting byte[] , got : " + z);            		
            	}
                len = inp.available();
                stmt.setBinaryStream(pos, inp, len);
            }
            break;
                        
            case java.sql.Types.NULL:
            default:
            break;
            
        }
        
    }

    /**
     * @param stmt
     * @param pos
     * @param value
     * @throws SQLException
     * @throws IOException
     */
    public static void setStatement(PreparedStatement stmt, int pos, Object value)    
                    throws SQLException, IOException    {
        if (value == null) { throw new SQLException("Unexpected null object") ; }
        
        Class<?> z= value.getClass();
        
        if (Boolean.class==z) {
            stmt.setBoolean(pos, (Boolean) value );            
        }
        else if (BigDecimal.class==z) {
            stmt.setBigDecimal(pos, (BigDecimal) value ) ;            
        }
        else if (BigInteger.class==z) {
        	throw new SQLException("Don't support BigInteger class") ;
        }
        else if (Long.class==z) {
            stmt.setLong(pos, (Long) value ) ;                                                            
        }
        else if (Integer.class==z) {
            stmt.setInt(pos, (Integer) value ) ;                                            
        }
        else if (Short.class==z) {
            stmt.setShort(pos, (Short) value ) ;                                
        }
        else if (Double.class==z) {
            stmt.setDouble(pos, (Double) value ) ;                
        }
        else if (Float.class==z) {
            stmt.setFloat(pos, (Float) value ) ;                                
        }
        else if (java.sql.Timestamp.class==z) {
            stmt.setTimestamp(pos, (Timestamp) value ) ;
        }
        else if (java.sql.Time.class==z) {
            stmt.setTime(pos, (Time) value ) ;
        }
        else if (java.sql.Date.class==z) {
            stmt.setDate(pos, (java.sql.Date) value ) ; 
        }
        else if (Date.class==z) {
        	Date dt= (Date) value ; 
            stmt.setDate(pos, new java.sql.Date(  dt.getTime()  )) ;
        }
        else if (byte[].class==z) {
            stmt.setBytes(pos,  (byte[]) value) ;
        }
        else if (Password.class== z) {
        	Password pwd= (Password) value ;
        	stmt.setString(pos, pwd.getAsEncoded() ) ;
        }
        else if (String.class== z) {
            stmt.setString(pos, (String) value );                    	
        }
        else if (StreamData.class== z) {
        	StreamData d= (StreamData) value ;
        	if (d.isDiskFile()) {
        		stmt.setBinaryStream(pos, d.getStream() );
        	} else {
        		stmt.setBytes(pos,  d.getBytes()) ;
        	}
        }
        else {
        	throw new SQLException("Unsupport value class: " + z) ;
        }
        
    }
    
    
    
    
    
}
