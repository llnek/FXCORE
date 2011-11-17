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

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.getBytes;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.equalsOneOfIC;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.replace;
import static com.zotoh.core.util.StrUte.strstr;
import static com.zotoh.core.util.StrUte.trim;
import static com.zotoh.core.util.WWID.generate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamUte;

/**
 * Utility functions. 
 *
 * @author kenl
 *
 */
public enum CoreUte  implements CoreVars {
;

    private static final String[] _bools= { "true", "yes", "on", "ok", "active", "1" };    
    private static boolean _isUNIX= false;
    
    private static Logger _log= getLogger(CoreUte.class);
    public static Logger tlog() { return _log; }    
            
    static {    
        cacheEnvVars(1500);
    }
        
    /**
     * @param props
     * @param envs
     * @return
     */
    public static Properties filterEnvVars(Properties props, String... envs)     {
        
        Properties rc= new Properties();
        String key, val;
        
        if (props != null) for (Object o : props.keySet()) {
            key= o.toString() ;
            val= props.getProperty( key);                        
            rc.put(key,  filterEnvVars(val, envs) );
        }
        
        return rc;        
    }
    
    /**
     * @param value
     * @param envs
     * @return
     */
    public static String filterEnvVars(String value, String... envs)     {
        
        String e, v= value;
        
        if ( value != null && envs != null ) {                        
            for (int i=0; i < envs.length; ++i) {
                e = envs[i];
                v= strstr(value, "${" +  e + "}",  nsb(System.getenv( e ))  );                
            }            
        }
        
        return v;
    }
    
    /**
     * @param c
     * @param times
     * @return
     */
    public static String makeString(char c, int times) {
        StringBuilder b=new StringBuilder(1024);
        for (int i=0; i < times; ++i) {
            b.append(c);
        }
        return b.toString();
    }
    
    
    /**
     * @return
     */
    public static Map<String,Object> newMapObj() {
    	return MP();
    }
    
    
    /**
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    public static Map<String,Map> newMapMap() {
        return MP() ; 
    }
    
    
    /**
     * @return
     */
    public static File getUserHomeDir()     {
        return new File( System.getProperties().getProperty("user.home"));
    }
    
    /**
     * Get the current directory.
     * 
     * @return
     */
    public static File getCWD()    {
        return new File(System.getProperties().getProperty("user.dir"));
    }
    
    /**
     * @param path
     * @return
     */
    public static String trimLastPathSep(String path) {
        String p= "[/]+$";
        return nsb(path).replaceFirst(p,"").replaceFirst(p,"");
    }
    
    /**
     * Get the classloader used by this object.
     * 
     * @return
     */
    public static ClassLoader getCZldr(ClassLoader... ldr)     {
        return ldr.length==0 ? 
                Thread.currentThread().getContextClassLoader()  : ldr[0];
    }
    
    /**
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Serializable obj) throws IOException     {
        ByteOStream baos = new ByteOStream(512);
        serialize(obj, baos);
        return baos.asBytes();
    }

    
    /**
     * @param bits
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bits) throws IOException, ClassNotFoundException     {
        return bits==null ? null : deserialize(asStream(bits));
    }

    /**
     * Convert string into a valid Timestamp object.
     * 
     * @param t conforming to the format "yyyy-mm-dd hh:mm:ss.[fff...]"
     * @return null if bad data.
     */
    public static Timestamp parseTimestamp(String t)     {
        try {
            return Timestamp.valueOf(t);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses datetime in ISO8601 format.
     * 
     * @param t string content adhering to ISO8601.
     * @return null if bad data.
     */
    public static Date parseDate(String t)    {
        
        if (isEmpty(t)) { return null; }
        
        String fmt;        
        if (t.indexOf(':') < 0)        { 
            fmt= DATE_FMT;
        }   else  {
            fmt= (t.indexOf('.') > 0) ? DT_FMT_MICRO : DT_FMT;                            
            if (t.indexOf("+-") > 0) { fmt= fmt+"Z"; }
        }        
        
        return parseDate(t, fmt);
    }
    
    /**
     * Convert string into a Date object.
     * 
     * @param t
     * @param fmt the expected format.
     * @return null if bad data.
     */
    public static Date parseDate(String t, String fmt)     {
        if (isEmpty(t) || isEmpty(fmt)) {
            return null;
        } else {
            return new SimpleDateFormat(fmt).parse(t, new ParsePosition(0));
        }
    }

    /**
     * Convert Timestamp into a string value.
     * 
     * @param t
     * @return
     */
    public static String fmtTimestamp(Timestamp t)    {
        return t==null ? null : t.toString();
    }

    /**
     * Convert Date object into a string - GMT timezone.
     * 
     * @param t
     * @return
     */
    public static String fmtDateGMT(Date t)     {
        return fmtDate(t, DT_FMT_MICRO, new SimpleTimeZone(0, "GMT"));
    }

    /**
     * Convert Date into string value.
     * 
     * @param t
     * @param fmt expected format.
     * @return
     */
    public static String fmtDate(Date t, String fmt)     {
        return fmtDate(t, fmt, null);
    }
        
    /**
     * Convert Date into string value, 
     * using the built-in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     * 
     * @param t
     * @return
     */
    public static String fmtDate(Date t)     {
        return fmtDate(t, DT_FMT_MICRO, null);
    }
    
    /**
     * Convert Date into its string value.
     * 
     * @param dt
     * @param pattern expected format.
     * @param tz timezone used.
     * @return
     */
    public static String fmtDate(Date dt, String pattern, TimeZone tz)     {
        
        if (dt==null || isEmpty(pattern)) {            return null;        }
        
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        if (tz != null)  { formatter.setTimeZone(tz); }
        
        return formatter.format(dt);
    }

    /**
     * Get the current machine/interface's IP Address.
     * 
     * @return
     * @throws UnknownHostException 
     */
    public static String getMachineIPAddr() throws UnknownHostException     {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Get the current machine/interface's name.
     * 
     * @return
     * @throws UnknownHostException 
     */
    public static String getMachineHost() throws UnknownHostException     {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * Get the class name this object belongs to.
     * 
     * @param o
     * @return
     */
    public static String safeGetClzname(Object o)     {
        return o==null ? "(null)" : o.getClass().getName();
    }

    /**
     * Get the byte[] representation of this object by calling toString() on the object, 
     * then turning it into byte[].  Unless if object is already byte[], then simply return
     * it as byte[].
     * 
     * @param obj
     * @param encoding
     * @return
     */
    public static byte[] convBytes(Object obj)     {
        
        String s=null;
        
        if (obj instanceof String)        {
            s= (String) obj;
        }
        else if ( obj instanceof byte[])  {
            return (byte[]) obj;
        }
        else if (obj != null)         {
            s= obj.toString();
        }

        return s==null ? null : toBytes(s,"utf-8");
    }

    /**
     * Get the canonical path name.
     * 
     * @param fp
     * @return
     * @throws IOException 
     */
    public static String getFilePath(File fp) throws IOException     {
        return niceFPath(fp);
    }
    
    
    /**
     * @return
     */
    public static boolean isWindows()     {        return !isUnix() ;    }

    
    /**
     * @return
     */
    public static boolean isUnix()     {        return _isUNIX;    }

    
    /**
     * @param o
     * @return
     */
    public static boolean isNilArray(Object[] o)     {
        return (o == null || o.length == 0);
    }

    
    /**
     * @param o
     * @return
     */
    public static boolean isNil(byte[] o)     {
        return (o == null || o.length == 0);
    }

    
    /**
     * @param o
     * @return
     */
    public static boolean isNil(char[] o)     {
        return (o == null || o.length == 0);
    }
    
    
    /**
     * @param lst
     * @return
     */
    public static boolean isNil(List<?> lst)     {
        return lst == null || lst.size() == 0;
    }

    
    /**
     * @param m
     * @return
     */
    public static boolean isNil(Map<?,?> m)     {
        return m == null || m.size() == 0;
    }

    
    /**
     * @param props
     * @return
     */
    public static boolean isNil(Properties props)    {
        return props == null || props.isEmpty();
    }

    /**
     * Convert string to int.
     * 
     * @param s
     * @param def default if string is null/empty.
     * @return
     */
    public static int asInt(String s, int def)     {
        int ret = 0;
        try  {
            ret = isEmpty(s) ? def :  Double.valueOf(s).intValue();
        }
        catch (NumberFormatException e) {        
            ret = def;
        }        
        return ret;
    }

    /**
     * Convert string to long.
     * 
     * @param s
     * @param def default if string is null/empty.
     * @return
     */
    public static long asLong(String s, long def)     {
        long ret = 0;
        try  {
            ret = isEmpty(s) ? def : Double.valueOf(s).longValue();
        }
        catch (NumberFormatException e) {        
            ret = def;
        }
        return ret;
    }

    /**
     * Convert string to double.
     * 
     * @param s
     * @param def default if string is null/empty.
     * @return
     */
    public static double asDouble(String s, double def)     {
        double ret = 0;
        try  {
            ret = isEmpty(s) ? def : Double.parseDouble(s);
        }
        catch (NumberFormatException e) {        
            ret = def;
        }
        return ret;
    }

    /**
     * Convert string to float.
     * 
     * @param s
     * @param def default if string is null/empty.
     * @return
     */
    public static float asFloat(String s, float def)     {
        float ret = 0;
        try  {
            ret = isEmpty(s) ? def : Double.valueOf(s).floatValue();
        }
        catch (NumberFormatException e) {        
            ret = def;
        }
        return ret;
    }

    /**
     * Convert string to boolean.
     * 
     *  Valid values for Boolean.TRUE => "true", "yes", "on", "ok", "active", "1".
     *  
     * @param s
     * @param def default if string is null/empty.
     * @return
     */
    public static boolean asBool(String s, boolean def)     {
        boolean ret = false;
        try  {
            ret= equalsOneOfIC(s, _bools);
        }
        catch (Exception e) {        
            ret = def;
        }
        return ret;
    }

    /**
     * Get the resource bundle.
     * 
     * @param id
     * @param loc
     * @return
     */
    public static ResourceBundle getBundle(String id, Locale loc)     {
        tstEStrArg("resource-id", id);
        tstObjArg("locale", loc) ;
        return ResourceBundle.getBundle(id, loc);
    }
    
    /**
     * Get the string from the resource-bundle.
     * 
     * @param rc
     * @param key
     * @param params
     * @return
     */
    public static String getResourceStr(ResourceBundle rc, String key,  Object... params)     {
        tstObjArg("resource-bundle", rc) ;
        tstEStrArg("resource-key", key) ;
        
        String val= nsb( rc.getString(key));
        for (int i = 0; i < params.length; ++i)  {
            val=replace(val, "{}", params[i].toString(),1);
        }        
        return val;
    }

    /**
     * Parse the string into int.
     * 
     * @param s
     * @return
     */
    public static int getPortAsInt(String s)    {        return asInt(s, -1);    }

    /**
     * Convert bits to Properties.
     * 
     * @param bin
     * @return
     * @throws IOException 
     */
    public static Properties asProperties(byte[] bin) throws IOException     {
        return asProperties(asStream(bin));
    }

    /**
     * @param skipNull
     * @param objs
     * @return
     */
    @SuppressWarnings("serial")
    public static <T> List<T> asList(final boolean skipNull, final T ...objs ) {
        return new ArrayList<T>() {{ 
            for (int i=0; i < objs.length; ++i) {  
                if (skipNull && objs[i]==null) {} else { add( objs[i]); }}
        }};
    }
    
    /**
     * Convert to bytes[].
     * 
     * @param p
     * @return
     * @throws IOException 
     */
    public static byte[] asBytes(Properties p) throws IOException     {
        ByteOStream baos = new ByteOStream(4096);
        if (p != null) {p.store(baos, null);}
        return baos.asBytes();
    }

    /**
     * Load the properties as a resource.
     * 
     * @param rc
     * @param ldr
     * @return
     * @throws IOException 
     */
    public static Properties asProperties(String rc, ClassLoader... ldr) throws IOException     {
        InputStream inp = rc2Stream(rc, ldr);
        try {
            return asProperties(inp);
        }
        finally {        
            close(inp);
        }
    }

    /**
     * Read the stream as Properties.
     * 
     * @param inp
     * @return
     * @throws IOException 
     */
    public static Properties asProperties(InputStream inp) throws IOException    {
        Properties p = new Properties();
        if (inp != null) { p.load(inp); }
        return p;
    }

    /**
     * Load the resource as a stream.
     * 
     * @param rc
     * @param ldr
     * @return
     */
    public static InputStream rc2Stream(String rc, ClassLoader... ldr)     {        
        return isEmpty(rc) ? null : getCZldr(ldr).getResourceAsStream(rc);
    }

   
    /**
     * @param rc
     * @param ldr
     * @return
     */
    public static URL rc2Url(String rc, ClassLoader... ldr)     {        
        return isEmpty(rc) ? null : getCZldr(ldr).getResource(rc) ;
    }
    
    /**
     * Load resource as a string.
     * 
     * @param rc
     * @param encoding
     * @param ldr
     * @return
     * @throws IOException
     */
    public static String rc2Str(String rc, String encoding, ClassLoader... ldr) throws IOException    {        
        InputStream inp = rc2Stream(rc, ldr);
        try  {
            return toString(getBytes(inp),encoding);            
        }
        finally {        
            close(inp);
        }
    }

    /**
     * Load resource as byte[].
     * 
     * @param rc
     * @param ldr optional.
     * @return
     * @throws IOException
     */
    public static byte[] rc2bytes(String rc, ClassLoader... ldr) throws IOException    {
        InputStream inp = rc2Stream(rc, ldr);
        try  {
            return getBytes(inp);
        }
        finally {        
            close(inp);
        }
    }

    /**
     * Compress the byte[].
     * 
     * @param b
     * @return
     * @throws IOException
     */
    public static byte[] deflate(byte[] b) throws IOException     {
        
        if (isNil(b))  { return b; }

        Deflater compressor = new Deflater();
        byte[] buf = new byte[1024];
        ByteOStream bos;
        int count;
        
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(b);
        compressor.finish();

        bos = new ByteOStream(b.length);
        while ( !compressor.finished())        {
            count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }

        return bos.asBytes();
    }

    /**
     * Decompress the byte[].
     * 
     * @param b
     * @return
     * @throws IOException
     */
    public static byte[] inflate(byte[] b) throws IOException     {
        
        if (isNil(b)) {        return b;}
        
        Inflater decompressor = new Inflater();
        byte[] buf = new byte[1024];
        ByteOStream bos;
        int count;

        bos = new ByteOStream(b.length);        
        decompressor.setInput(b);
        while ( !decompressor.finished())        {
            try            {
                count = decompressor.inflate(buf);
            }
            catch (DataFormatException e) {            
                // maybe data stored was not compressed,
                // return it back verbatim
                return b;
            }
            
            bos.write(buf, 0, count);
        }
        
        return bos.asBytes();
    }

    /**
     * Normalize the input by converting file-system unfriendly characters to hex values.
     * 
     * @param val
     * @return
     */
    public static String normalize(String val)     {
        
        StringBuilder buf = new StringBuilder(256);
        char ch;
        
        if (! isEmpty(val)) for (int i = 0; i < val.length(); ++i) {
            ch = val.charAt(i) ;
            if (((ch >= 'A') && (ch <= 'Z'))
                    || ((ch >= 'a') && (ch <= 'z'))
                    || ((ch >= '0') && (ch <= '9'))
                    || (ch == '_' || ch == '-' || ch == '.' || ch == ' '
                    || ch == '(' || ch == ')'))             {
                buf.append(ch);
            }
            else  {
                buf.append("_0x" + Integer.toString(ch, 16) + "_");
            }

        }
        
        return buf.toString();
    }

    /**
     * Convert byte characters to chars.
     * 
     * @param b
     * @param ch
     * @param len
     */
    public static void bytesToChars(byte[] b, char[] ch, int len)     {
        if (isNil(b) || isNil(ch) || len <= 0) { return; }
        len=Math.min(len, b.length);
        
        if (ch.length < len) { throw new RuntimeException("output char-array size is too small"); }
        
        byte b1;
        for (int i = 0; i < len; ++i)         {
            b1 = b[i];
            ch[i] = (char) (b1 < 0 ? 256 + b1 : b1);
        }
    }

    /**
     * Convert string to byte[], taking care of encoding.
     * 
     * @param s
     * @param encoding
     * @return
     */
    private static byte[] toBytes(String s, String... encoding)     {        
        try  {
            return isEmpty(s) ? null : s.getBytes(encoding.length==0 ? "utf-8" : encoding[0]);
        }
        catch (UnsupportedEncodingException e) {        
            throw new RuntimeException(e);
        }
    }

    private static String toString(byte[] b, String... enc)  {
        try {
			return b==null ? null : b.length==0 ? "" : new String(b, enc.length==0 ? "utf-8" : enc[0]);
		} 
        catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Convert byte[] to string, using utf-8 as encoding.
     * 
     * @param b
     * @return
     */
    public static String asString(byte[] b)     {
        return toString(b);
    }

    /**
     * Convert string to byte[], using utf-8 as encoding.
     * 
     * @param s
     * @return
     */
    public static byte[] asBytes(String s)     {
        return toBytes(s, "utf-8");
    }

    /**
     * Get the current time in millisecs, using the supplied timezone.
     * 
     * @param tz
     * @return
     */
    public static long nowMillis(String tz)     {
        return isEmpty(tz) ? java.lang.System.currentTimeMillis() :        
            (new GregorianCalendar(TimeZone.getTimeZone(tz))).getTimeInMillis();
    }

    /**
     * @param fileUrl
     * @return
     */
    public static String asFilePathOnly(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith("file:")) {
            fileUrl= fileUrl.substring(5);
        }
        return fileUrl;
    }
    
    /**
     * Convert the file path into nice format without backslashes.
     * 
     * @param dir
     * @return
     * @throws IOException 
     */
    public static String niceFPath(File dir) throws IOException     {
        return dir==null ? "" : niceFPath(dir.getCanonicalPath());
    }
    
    /**
     * @param path
     * @return
     * @throws IOException
     */
    public static String asFileUrl(String path) throws IOException     {
        String pfx= isWindows() ? "file:/" : "file:";
        return path==null ? null : pfx + path;
    }
    
    /**
     * @param path
     * @return
     * @throws IOException
     */
    public static String asFileUrl(File path) throws IOException     {
        return path==null ? null : asFileUrl(niceFPath(path));
    }
    
    /**
     * Convert the file path into nice format without backslashes.
     * 
     * @param path
     * @return
     */
    public static String niceFPath(String path)     {
        boolean unc = false;    // for windows
        path = trim(path);
        if (path.startsWith("\\\\"))
        { unc = true; }

        StringTokenizer tz= new StringTokenizer(path, "\\");
        StringBuilder sb= new StringBuilder(256);
        while (tz.hasMoreTokens()) {
            StrUte.addAndDelim(sb, PATHSEP, tz.nextToken());
        }
        path= sb.toString();
        if (unc) {  path = "\\\\" + path;}        
        return path;
    }

    /**
     * Create a temporary directory.
     * 
     * @return
     */
    public static File genTmpDir()     {
        File dir=new File( System.getProperties().getProperty("java.io.tmpdir")
                + "/" + generate() );
        dir.mkdirs();
        return dir;
    }

    
    /**
     * @return
     */
    public static File getTmpDir()     {
        File dir=new File(System.getProperties().getProperty("java.io.tmpdir"));
        dir.mkdirs();
        return dir;
    }
    
    /**
     * Given a string of the form <host name>[:<TCP port>] return the host name
     * (or IP address)
     *
     * @param token
     * @return (host, port) as Tuple
     */
    public static Tuple parseHostPort(String token)     {
        int pos = (token=nsb(token)).lastIndexOf(":") ;
        Tuple rc;
        if (pos >=0) {
            rc= new Tuple(token.substring(0,pos), token.substring(pos+1));
        }  else {
            rc= new Tuple(token);
        }
        return rc;
    }

    /**
     * Converts IPv4 address in its textual presentation form into its numeric
     * binary form.
     *
     * @param ipv4
     * @return
     */
    public static byte[] ipv4AsBytes(String ipv4)     {

        char[] srcb = (ipv4=nsb(ipv4)).toCharArray();
        boolean gotDigit = false;
        int width=4, i=0, cur=0,
        octets= 0 ;
        char ch;
        byte[] dst = new byte[width];

        while (i < srcb.length)         {
            ch = srcb[i++];
            if (Character.isDigit(ch))             {
                // java byte is signed, need to convert to int
                int sum = (dst[cur] & 0xff) * 10
                        + (Character.digit(ch, 10) & 0xff);

                if (sum > 255) {  return null;   }

                dst[cur] = (byte) (sum & 0xff);

                if (! gotDigit) {
                    if (++octets > width) {  return null;     }
                    gotDigit = true;
                }
            }
            else if (ch == '.' && gotDigit) {
                if (octets == width) {  return null;   }
                ++cur;
                dst[cur] = 0;
                gotDigit = false;
            }
            else {  return null;  }
        }
        
        return (octets < width) ? null : dst;
    }

   
    /**
     * @param root
     * @return
     */
    public static Throwable findRootCause(Throwable root)     {
        Throwable t = root == null ? null : root.getCause();
        while (t != null) {        
            root = t;   t = t.getCause();
        }
        return root;
    }

    
    /**
     * @param root
     * @return
     */
    public static String findRootCauseMsgWithClassInfo(Throwable root)     {
        Throwable e = findRootCause(root);
        return e==null ? "" :  e.getClass().getName()
                + ": " + e.getMessage();
    }

    
    /**
     * @param root
     * @return
     */
    public static String findRootCauseMsg(Throwable root)     {
        Throwable e = findRootCause(root);
        return e==null ? "" :  e.getMessage();
    }

    
    /**
     * @param var
     * @return
     */
    public static String getEnvVar(String var)     {
        return isEmpty(var) ? null : System.getenv(var);
    }

    
    /**
     * @param bits
     * @return
     */
    public static int genHash(byte[] bits)     {
        return HashUte.hash(bits) ;
    }

    
    /**
     * @param b
     * @return
     * @throws IOException
     */
    public static long getLongNumber(byte[] b) throws IOException     {
        return ByteUte.readAsLong(b);
    }

    
    /**
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean isSame(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        else if ((obj1 == null) || (obj2 == null)) {
            return false;
        }
        else {
        	return obj1.equals(obj2);
        }
    }
    
    /**
     * @param out
     * @param n
     * @throws IOException
     */
    public static void writeInt(OutputStream out, int n) throws IOException     {
        out.write(ByteUte.readAsBytes(n));
        out.flush();
    }

    
    /**
     * @param ch
     * @param cs
     * @return
     */
    public static boolean matchChar(char ch, char[] cs)     {
        if (cs != null) for (int i=0; i < cs.length; ++i) {         
            if (ch == cs[i]) { return true; }
        }
        return false;
    }
        
    /**
     * @param obj
     * @return
     */
    public static Object nilToNull(Object obj)     {
        return obj==null ? Null.NULL : obj;
    }    

    
    /**
     * @param o
     * @return
     */
    public static boolean isNull(Object o)    {
        return  (o==null || o instanceof Null) ;
    }

    /**
     * @param param
     * @param value
     */
    public static void tstEStrArg(String param, String value) {
        if ( isEmpty(value)) 
            throw new IllegalArgumentException( "" + param + " is empty") ;        
    }

    
    /**
     * @param param
     * @param value
     */
    public static void tstNStrArg(String param, String value) {
        tstObjArg(param,value);
    }
       
    /**
     * @param param
     * @param value
     */
    public static void tstObjArg(String param, Object value) {
        if ( value==null) 
            throw new IllegalArgumentException( "" + param + " is null") ;        
    }
    
   
    /**
     * @param msg
     */
    public static void errBadArg(String msg) {
        throw new IllegalArgumentException(msg) ;        
    }
    
    
    /**
     * @param param
     * @param value
     */
    public static void tstNonNegIntArg(String param, Integer value) {
        if ( value==null || value < 0) 
            throw new IllegalArgumentException( "" + param + " must be >= 0 ") ;        
    }
    
   
    /**
     * @param param
     * @param value
     */
    public static void tstNonNegLongArg(String param, Long value) {
        if ( value==null || value < 0L) 
            throw new IllegalArgumentException( "" + param + " must be >= 0L ") ;        
    }
    
    
    /**
     * @param param
     * @param value
     */
    public static void tstPosLongArg(String param, Long value) {
        if ( value==null || value <= 0L) 
            throw new IllegalArgumentException( "" + param + " must be a positive long") ;        
    }
    
    
    /**
     * @param param
     * @param value
     */
    public static void tstPosIntArg(String param, Integer value) {
        if ( value==null || value <= 0) 
            throw new IllegalArgumentException( "" + param + " must be a positive integer") ;        
    }
    
    /**
     * @param param
     * @param value
     */
    public static void tstNEArray(String param, Object[] value) {
        if ( value==null || value.length == 0) 
            throw new IllegalArgumentException( "" + param + " must be non empty") ;        
    }
    
    /**
     * @param param
     * @param value
     */
    public static void tstNEArray(String param, byte[] value) {
        if ( value==null || value.length == 0) 
            throw new IllegalArgumentException( "" + param + " must be non empty") ;        
    }
    
    
    /**
     * @param param
     * @param child
     * @param parent
     */
    public static void tstArgIsType(String param, Class<?> child, Class<?> parent) {
        if ( child==null || !parent.isAssignableFrom(child )) 
            throw new IllegalArgumentException( "" + param + " not-isa " + parent.getName() ) ;        
    }
    
    
    /**
     * @param param
     * @param value
     * @param ref
     */
    public static void tstArgIsType(String param, Object value, Class<?> ref) {
        if ( value==null || !ref.isAssignableFrom(value.getClass() )) 
            throw new IllegalArgumentException( "" + param + " not-isa " + ref.getName() ) ;        
    }

    
    /**
     * @return
     * @throws IOException
     */
    public static SecureRandom newRandom() throws IOException {
        return new SecureRandom(  ByteUte.readAsBytes(System.currentTimeMillis()));
    }

    /**
     * @param f
     * @return
     * @throws IOException
     */
    public static String toFileUrl(File f) throws IOException {
    	return getFileUrlPfx() + niceFPath(f);
    }
    
    /**
     * @return
     */
    public static String getFileUrlPfx() {
    	return isWindows() ? "file:/" : "file:";
    }
    
    /**
     * @param waitLoadTimeInMillis
     */
    @SuppressWarnings("unused")
    protected static void cacheEnvVars(long waitLoadTimeInMillis)     {
        String os = System.getProperties().getProperty("os.name").toLowerCase();
        boolean w32 = os.indexOf("windows") >= 0;
        Properties p = new Properties();
        File tmp=null;
        String cmdStr = w32 ? "cmd.exe /c set > " : "env > ";

        _isUNIX = (w32==false);
        if (false)
        try        {            
            
            tmp = StreamUte.createTempFile();
            cmdStr = cmdStr + tmp.getCanonicalPath();
            
            Runtime.getRuntime().exec(cmdStr);
            Thread.sleep(waitLoadTimeInMillis);

            StringBuilder buf = new StringBuilder(1024);
            byte[] b = StreamUte.readFile(tmp);
            String str = asString(b);
            char c;
            for (int i = 0; i < str.length(); ++i) {            
                c = str.charAt(i);
                if ('\\' == c) buf.append("\\\\");
                else
                buf.append(c);
            }
            b= toBytes(buf.toString(),"utf-8");
            
            if (b != null)
            p.load(StreamUte.asStream(b));
        }
        catch (Throwable t) {        
            tlog().warn("",t);
        }
        finally {        
            FileUte.delete(tmp);
        }
    }

    private static void serialize(Serializable obj, OutputStream out) throws IOException     {
        
        ObjectOutputStream os = null;
        if (obj != null)
        try {
            os = new ObjectOutputStream(out);
            os.writeObject(obj);      
            os.flush();
        } 
        finally {
            StreamUte.close(os);  // closes baos too
        }
    }

    private static Object deserialize(InputStream inp) throws IOException, ClassNotFoundException     {        
        ObjectInputStream in = null;
        Object rc=null;
        if (inp != null)
        try {
            rc= ( in = new ObjectInputStream(inp)).readObject();
        } 
        finally {
            StreamUte.close(in); // closes inp too
        }
        return rc;
    }
    
}
