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
 
package com.zotoh.core.io;

import static com.zotoh.core.io.StreamData.getWDir;
import static com.zotoh.core.util.ProcessUte.safeThreadWait;
import static com.zotoh.core.util.CoreUte.tstNonNegLongArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreVars.UTF8;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.xml.sax.InputSource;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * Util functions related to stream/io. 
 *
 * @author kenl
 *
 */
public enum StreamUte {  
;
    
    public static int READ_STREAM_LIMIT=1024*1024*8; // if > 8M switch to file
    
    private static Logger _log= getLogger(StreamUte.class);
    public static Logger tlog() {         return _log; }    

    /**
     * @param fn
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public  static byte[] readFile(File fn) throws IOException, FileNotFoundException    {
        return readBytes(fn);        
    }
    
    /**
     * 
     * @param fn
     * @param encoding
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String readFile(File fn, String encoding) throws IOException
            , FileNotFoundException    {
        return readTextFile(fn, encoding) ;        
    }

    
    /**
     * @param content
     * @param encoding
     * @return
     * @throws IOException
     */
    public static byte[] gzip(String content, String encoding) throws IOException    {
        return isEmpty(content) ? null : gzip( content.getBytes(encoding));
    }
    
    /**
     * Calls InputStream.reset().
     * 
     * @param inp
     */
    public static void safeReset(InputStream inp)    {
        try { if (inp != null) inp.reset(); } catch (Exception e) {}
    }

    /**
     * @param bits
     * @return
     * @throws IOException
     */
    public static byte[] gzip(byte[] bits) throws IOException    {
        
        ByteOStream baos = new ByteOStream();
        GZIPOutputStream g = new GZIPOutputStream(baos);
        
        if (bits != null && bits.length > 0) {
            g.write(bits, 0, bits.length);
            g.close();            
        }
        
        return baos.asBytes();    
    }

    
    /**
     * @param bits
     * @return
     * @throws IOException
     */
    public static byte[] gunzip(byte[] bits) throws IOException    {
        return  bits==null ? null 
                : bits.length==0 ? new byte[0] 
                        : getBytes(new GZIPInputStream( asStream(bits)));
    }

    
    /**
     * @param bits
     * @return
     */
    public static InputStream asStream(byte[] bits) {
        return bits==null || bits.length==0 ? null : new ByteArrayInputStream(bits);
    }
    
    
    /**
     * @param ins
     * @return
     * @throws IOException
     */
    public static InputStream gunzip(InputStream ins) throws IOException    {
        return ins==null ? null : new GZIPInputStream(ins);
    }

    
    /**
     * @param ins
     * @return
     * @throws IOException
     */
    public static byte[] getBytes(InputStream ins) throws IOException    {
        
        ByteOStream baos = new ByteOStream();
        byte[] cb= new byte[4096];
        int n = 0;
        
        if (ins != null) while ((n = ins.read(cb)) > 0) { 
            baos.write(cb, 0, n);        
        }
        
        return baos.asBytes();
    }
    
    
    /**
     * @param ins
     * @return
     * @throws IOException
     */
    public static StreamData getDataFromStream(InputStream ins) throws IOException    {
        return ins==null ? null : readStream(ins, false);
    }

    
    /**
     * @param fp
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream readStream(File fp) throws FileNotFoundException    {
        return  fp==null ? null : new SmartFileInputStream(fp,false);
    }

    
    /**
     * @param path
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static byte[] readBytes(File path) throws IOException, FileNotFoundException    {
        return bytesFromFile(path);
    }

    
    /**
     * @param s
     * @return
     * @throws IOException
     */
    public static byte[] fromGZipedB64(String s) throws IOException    { 
        return s==null ? null : s.length()==0 ? new byte[0] : gunzip(Base64.decodeBase64(s));
    }

    
    /**
     * @param bits
     * @return
     * @throws IOException
     */
    public static String toGZipedB64(byte[] bits) throws IOException    {
        return bits == null ? null : bits.length==0 ? "" : Base64.encodeBase64String( gzip(bits));
    }

    
    /**
     * @param url
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static byte[] bytesFromFile(File url) throws IOException, FileNotFoundException    {
        InputStream ins=null;
        byte[] bits= null;
        if (url != null)
        try        {
            bits=  getBytes(ins= new FileInputStream(url));
        }
        finally {        
            close(ins);
        }
        return bits;
    }

    
    /**
     * @param inp
     * @return
     * @throws IOException
     */
    public static int available(InputStream inp) throws IOException    {
        return inp==null ? 0 : inp.available();
    }

    
    /**
     * @param inp
     * @return
     * @throws IOException
     */
    public static StreamData readStream(InputStream inp) throws IOException    {
        return readStream(inp, false);
    }
        
    /**
     * @param inp
     * @param useFileAlways
     * @return
     * @throws IOException
     */
    public static StreamData readStream(InputStream inp, boolean useFileAlways) 
            throws IOException    {        
        ByteOStream baos= new ByteOStream(10000);
        byte[] bits= new byte[4096];
        int lmt= READ_STREAM_LIMIT;
        int cnt=0, c;
        OutputStream os= baos;
        StreamData rc=new StreamData();

        if (useFileAlways) { lmt=1; }

        if (inp != null)
        try        {
            while ( (c=inp.read(bits)) > 0) {
                
                os.write(bits, 0, c);
                cnt += c;
                
                if ( lmt > 0 && cnt > lmt) {
                    os=swap(baos, rc);
                    lmt= -1;
                }
                
            }
            
            if (!rc.isDiskFile() ) {
                rc.resetMsgContent(baos) ;
            }
            
        }
        catch (IOException e) {            
            throw e;            
        }
        finally {
            close(os);
        }
                
        return rc;
    }
    
    
    /**
     * @param out
     * @param s
     * @param encoding
     * @throws IOException
     */
    public static void writeFile(File out, String s, String... encoding) throws IOException {
        if ( ! isEmpty(s)) {
            writeFile( out, s.getBytes( encoding.length==0 ? "utf-8" : encoding[0])) ;
        }
    }

    
    /**
     * @param out
     * @param bits
     * @throws IOException
     */
    public static void writeFile(File out, byte[] bits) throws IOException {
        
        if (bits != null && bits.length > 0) {
            
            tstObjArg("out-file", out) ;            
            OutputStream os= null;        
            try {
                os= new FileOutputStream(out);
                os.write(bits) ;
            }
            finally {
                close(os);
            }
        }
        
    }
    
    
    /**
     * @param src
     * @param out
     * @param reset
     * @throws IOException
     */
    public static void streamToStream(InputStream src, OutputStream out,
            boolean reset) throws IOException     {
        
        tstObjArg("out-stream", out) ;
        tstObjArg("in-stream", src) ;
        
        byte[] bits = new byte[4096];
        int cnt;
        
        while ((cnt = src.read(bits)) > 0) {
            out.write(bits, 0, cnt);
        }
        safeFlush(out);
        
        if (reset && src.markSupported()) {
            src.reset();
        }
        
    }

    
    /**
     * @param src
     * @param out
     * @throws IOException
     */
    public static void streamToStream(InputStream src, OutputStream out) throws IOException {
        streamToStream(src, out, false);
    }
    
    
    /**
     * @param src
     * @param cloze
     * @return
     * @throws IOException
     */
    public static File copyStream(InputStream src, boolean cloze) throws IOException    {
        tstObjArg("input-stream",src);
        OutputStream os= null;
        Tuple t;
        try        {
            t=createTempFile(true);
            os= (OutputStream) t.get(1);
            streamToStream(src, os);
        }
        finally {            
            if (cloze) close(src);            
            close(os);
        }        
        
        return (File)t.get(0);
    }

    
    /**
     * @param src
     * @return
     * @throws IOException
     */
    public static File copyStream(InputStream src) throws IOException    {
        return copyStream(src, false);
    }
    
    
    /**
     * @param src
     * @param out
     * @param bytesToCopy
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static void streamToStream(InputStream src, OutputStream out, long bytesToCopy) throws IOException    {
        
        tstNonNegLongArg("bytes-to-read", bytesToCopy);
        tstObjArg("out-stream", out) ;            
        tstObjArg("in-stream", src) ;            
        
        DataInputStream dis = new DataInputStream(src);
        byte[] buff = new byte[4096];
        long cl= bytesToCopy;
        int bc= 0, c,
        tries=0;

        while (cl > 0L)        {
            c=dis.read(buff,0,  (int) Math.min(4096L, cl) );
            if (c > 0)            {
                bc += c;
                cl -= c;
                out.write(buff,0,c);
            } else {
                // if we can't read all the bytes, then we dont want to loop forever, try 3 times, then come out
                if (tries == 3) { break; }
                safeThreadWait(500L);
                ++tries;
            }
        }

        safeFlush( out);
    }
    

    /**
     * @param iso
     */
    public static void resetInputSource(InputSource iso)    {

        if (iso != null) {            
            Reader      rdr = iso.getCharacterStream();
            InputStream ism = iso.getByteStream();
            try { if (ism != null) ism.reset(); } catch (Exception e) {}
            try { if (rdr != null) rdr.reset(); } catch (Exception e) {}
        }
        
    }

    /**
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static File createTempFile(String prefix, String suffix) throws IOException    {        
        if ( isEmpty(prefix)) { prefix="temp-"; }
        if ( isEmpty(suffix)) { suffix=".dat"; }
        File dir= getWDir();
        dir.mkdirs();
        return File.createTempFile(prefix, suffix, dir);
    }

    
    /**
     * @return
     * @throws IOException
     */
    public static File createTempFile() throws IOException    {        
        return createTempFile("","");
    }
    
    
    /**
     * @return
     * @throws IOException
     */
    public static StreamData createFileSData() throws IOException    {
        return new StreamData(createTempFile());
    }

    
    /**
     * @param open
     * @return
     * @throws IOException
     */
    public static Tuple createTempFile(boolean open) throws IOException    {
        File f= createTempFile();
        return open ? new Tuple(f, new FileOutputStream(f)) : new Tuple(f) ;
    }
    

    /**
     * @param sz
     * @return
     */
    public static ByteOStream createByteStream(int sz)    {
        return new ByteOStream(sz);
    }

    /**
     * @param r
     * @return
     */
    public static Reader close(Reader r)    {
        try { if (r != null) r.close();  } 
        catch (Throwable t) 
        {}
        return null;
    }

    
    /**
     * @param o
     * @return null
     */
    public static OutputStream close(OutputStream o)    {
        try        { 
            if ( o != null) o.close();
        }
        catch (Throwable t)
        {}
        return null;
    }

    
    /**
     * @param w
     * @return null
     */
    public static Writer close(Writer w)    {
        try        { 
            if ( w != null) w.close();
        }
        catch (Throwable t)
        {}
        return null;
    }
    
    /**
     * @param i
     * @return null
     */
    public static InputStream close(InputStream i)    {
        try        { 
            if (i != null) i.close();
        }
        catch (Throwable t)
        {}
        return null;
    }

    /**
     * Tests if both streams are the same or different at byte level.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public static boolean different(InputStream s1, InputStream s2)    {
        
        if (s1 != null && s2 != null)
        try        {
            int a2= s2.available();           
            int a1= s1.available();
            
            if (a1 != a2) 
            return true;
            
            byte[] b2= new byte[4096];
            byte[] b1= new byte[4096];
            int c1, c2;
            while (true) {
                c2= s2.read(b2, 0, b2.length);                
                c1= s1.read(b1, 0, b1.length);                
                
                if (c1 != c2)
                    { return true; }
                
                for (int i=0; i < c1; ++i) {
                    if (b1[i] != b2[i]) { return true; }
                }
                
                if (c1 <= 0 || c2 <= 0)
                { break; }
            }            
            
            return false;
        }
        catch (Exception e)             
        {}
        finally {
            close(s1);
            close(s2);
        }
        
        return (s1==null && s2==null) ? false : true;
    }
    
    private static String readTextFile(File fn, String encoding) throws IOException {
        
        Reader rdr = new InputStreamReader(readStream(fn), 
                isEmpty(encoding) ? UTF8 : encoding);
        StringBuilder sb= new StringBuilder(4096);
        char[] cs= new char[4096];
        int n;
        
        try            {
            while ((n = rdr.read(cs)) > 0) {
                sb.append(cs, 0, n);                    
            }
            return sb.toString();
        }
        finally {
            close(rdr);
        }
    }
    
    private static void safeFlush(OutputStream os)    {
        try         {
            if (os != null) { os.flush(); }
        }
        catch (Exception e) 
        {}
    }
    
    private static OutputStream swap(ByteArrayOutputStream baos, StreamData data)
                throws IOException    {
        Tuple t= createTempFile(true);
        OutputStream os= (OutputStream) t.get(1);
        byte[] bits=baos.toByteArray();
        if (bits != null && bits.length > 0) {
            os.write(bits);
            safeFlush(os);
        }
        baos.close();
        data.resetMsgContent(t.get(0)) ;
        return os;
    }
    
    @SuppressWarnings("unused")
    private static void streamToStream(long bytesToCopy, InputStream inp, OutputStream os) throws IOException    {
        
        tstNonNegLongArg("bytes-to-read", bytesToCopy);
        tstObjArg("out-stream", os) ;
        tstObjArg("in-stream", inp) ;
        
        byte[] bf= new byte[4096];
        int n;
                
        while (bytesToCopy > 0L) {
            n= inp.read(bf);
            if (n < 0) { break; }
            if (n > 0) {
                os.write(bf,0,n);
                bytesToCopy -= n;
            }
        }
        
        safeFlush( os);
    }
    
}
