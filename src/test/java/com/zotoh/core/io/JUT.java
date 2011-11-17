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

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.io.StreamUte.available;
import static com.zotoh.core.io.StreamUte.bytesFromFile;
import static com.zotoh.core.io.StreamUte.copyStream;
import static com.zotoh.core.io.StreamUte.createByteStream;
import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.io.StreamUte.different;
import static com.zotoh.core.io.StreamUte.fromGZipedB64;
import static com.zotoh.core.io.StreamUte.getBytes;
import static com.zotoh.core.io.StreamUte.getDataFromStream;
import static com.zotoh.core.io.StreamUte.gunzip;
import static com.zotoh.core.io.StreamUte.gzip;
import static com.zotoh.core.io.StreamUte.readBytes;
import static com.zotoh.core.io.StreamUte.readFile;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.io.StreamUte.toGZipedB64;
import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asBytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.util.FileUte;
import com.zotoh.core.util.Tuple;

public final class JUT {
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception    {
    }

    @AfterClass
    public static void finz()    {
    }

    @Before
    public void open() throws Exception    {
    }

    @After
    public void close() throws Exception    {
    }

    //@Test
    public void testCmdLineSeq() throws Exception {
        CmdLineQuestion q3= new CmdLineQuestion("bad", "oh dear, too bad") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                return "";
            }};
        CmdLineQuestion q2= new CmdLineQuestion("ok", "great, bye") {
            protected String onAnswerSetOutput( String answer, Properties props) {
                return "";
            }};
        CmdLineQuestion q1= new CmdLineQuestion("a", "hello, how are you?", "ok/bad", "ok") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                if ("ok".equals(answer)) {
                    props.put("state", true);
                    return "ok";
                }
                else {
                    props.put("state", false);
                    return "bad";
                }
            }
        };
        
        CmdLineSequence seq= new CmdLineSequence(q1,q2,q3) {
            protected String onStart() {
                return "a";
            }
        };
        Properties props= new Properties();
        seq.start(props);
        if (seq.isCanceled()) {
        	assertFalse(false);
        }
        else {
        	assertTrue(props.containsKey("state"));
        }
        
    }
    
    @Test
    public void testUte() throws Exception {
    		File f=createTempFile();
    		String s;
    		byte[] b;
    		InputStream inp;
    		StreamData dd;
    		
    		writeFile(f, "hello", "utf-8");
    		b=readFile(f);
    		assertTrue(b.length==5);
    		s = readFile(f, "utf-8");
    		assertTrue(s.length()==5);
    		assertArrayEquals(gzip("hello","utf-8"), gzip("hello".getBytes("utf-8")));
    		assertArrayEquals(gunzip(gzip("hello","utf-8")), "hello".getBytes("utf-8"));
    		inp=asStream("hello".getBytes());
    		assertNotNull(inp);
    		assertArrayEquals(getBytes(inp), "hello".getBytes());
    		
    		inp=asStream("hello".getBytes());
    		dd=getDataFromStream(inp);
    		assertNotNull(dd);
    		assertNull(dd.getFileRef());
    		assertArrayEquals(dd.getBytes(), "hello".getBytes());
    		
    		inp=readStream(f);
    		assertTrue(inp instanceof SmartFileInputStream);    		
    		assertArrayEquals(getBytes(inp), "hello".getBytes());
    		inp.close();
    		assertTrue(f.exists());
    		
    		assertArrayEquals(readBytes(f), "hello".getBytes("utf-8"));
    		
    		s=toGZipedB64("hello".getBytes());
    		assertArrayEquals(fromGZipedB64(s), "hello".getBytes());
    		
    		assertArrayEquals(bytesFromFile(f), "hello".getBytes());
    		assertTrue( 5==available(asStream("hello".getBytes())));
    		
    		ByteArrayOutputStream out= new ByteArrayOutputStream();
    		streamToStream( asStream("hello".getBytes()), out, false);
    		assertArrayEquals(out.toByteArray(), "hello".getBytes());
    		
    		f=copyStream(asStream("hello".getBytes()), true);
    		assertTrue(5==f.length());
    		f.delete();
    		f=copyStream(asStream("hello".getBytes()) );
    		assertTrue(5==f.length());
    		f.delete();
    		
    		ByteOStream baos= createByteStream(5000);
    		b=new byte[10000];
    		streamToStream(asStream(b), baos, 9492);
    		assertTrue(baos.asBytes().length==9492);
    		
    		assertFalse(different(asStream("abc".getBytes()), asStream("abc".getBytes())));
            assertTrue(different(asStream("abc".getBytes()), asStream("ABC".getBytes())));
    		
            StreamUte.READ_STREAM_LIMIT=6;
            dd=readStream(asStream("hello world".getBytes()));
            assertNotNull(dd.getFileRef());
    }
    
    @Test
    public void testByteFrags() throws Exception {
        ByteFragmentInputStream is;
        ByteFragment r;
        byte[] b,bits= new byte[20]; bits[3]='a'; bits[13]='z';
        is= new ByteFragmentInputStream(bits, 3, 15);
        r=is.getFrag();
        assertNotNull(r);
        assertTrue(r.getBuf()==bits);
        assertTrue(3==is.getPos());
        
        r=is.getFrag(5);
        assertNotNull(r);

        r= is.getFrag(7, 5);
        assertNotNull(r);
        
        b=new byte[11];
        is.read(b);
        assertTrue(b[0]=='a' && b[10]=='z');
        assertTrue(is.getPos()==14);
    }
    
    @Test
    public void testEmptyStreamData() throws Exception {
        StreamData s= new StreamData();
        assertTrue(s.getSize()==0L);
        assertNull(s.getFileRef());
        assertNull(s.getMsgContent());
        assertNull(s.getStream());
        assertNull(s.getFp());
        assertNull(s.getBin());
        assertNull(s.getBytes());        
    }
    
    @Test
    public void testBytesStreamData() throws Exception {
        StreamData s= new StreamData();        
        String data= "hello world";
        s.resetMsgContent(data) ;
        assertTrue(s.getMsgContent()==s.getBin());
        assertTrue(s.getBytes()==s.getBin());
        assertTrue(s.getSize()== asBytes(data).length);
        assertFalse(s.isZiped());
        assertTrue(Arrays.areEqual(s.getBin(), asBytes(data)));
    }
    
    @Test
    public void testLargeBytesStreamData() throws Exception {
        StreamData s= new StreamData();        
        StringBuilder b= new StringBuilder(4000000);
        for (int i=0; i < 5000000; ++i) {
            b.append("x") ;
        }
        byte[] bits=b.toString().getBytes("UTF-8");
        s.resetMsgContent(b) ;
        assertTrue(s.getMsgContent() !=s.getBin());
        assertTrue(s.getBytes() !=s.getBin());
        assertTrue(s.getSize()== bits.length);
        assertTrue(s.isZiped());
        assertTrue(Arrays.areEqual(s.getBin(), bits));
    }
    
    @Test
    public void testStreamStreamData() throws Exception {
        StreamData s= new StreamData();        
        String data= "hello world";        
        s.resetMsgContent(data) ;
        assertTrue( s.getStream() instanceof ByteArrayInputStream);
        assertTrue(Arrays.areEqual(getBytes(s.getStream()), asBytes(data)));        
    }
        
    @Test
    public void testFileStreamData() throws Exception {
        StreamData s= StreamUte.createFileSData();
        OutputStream os= new FileOutputStream(s.getFileRef());
        File fout= s.getFileRef();
        String data= "hello world";        
        os.write(asBytes(data));
        os.close();
        try {
            assertTrue(s.getStream() instanceof SmartFileInputStream);
            assertTrue(Arrays.areEqual(getBytes(s.getStream()), asBytes(data)));        
            assertTrue(Arrays.areEqual(s.getBytes(), asBytes(data)));        
            assertTrue(s.getBin()==null);        
            assertTrue(s.getSize()==data.length());        
        }
        finally {
            FileUte.delete(fout) ;                    
        }
    }
    
    @Test
    public void testFileRefStreamData() throws Exception {
        StreamData s= StreamUte.createFileSData();
        File fout= s.getFileRef();
        OutputStream os= new FileOutputStream(fout);
        String data= "hello world";        
        os.write(asBytes(data));
        os.close();
        try {
            s.destroy();
            assertFalse(fout.exists());
        }
        finally {
        }
    }
    
    @Test
    public void testFileRefStreamData2() throws Exception {
        StreamData s= StreamUte.createFileSData();
        File fout= s.getFileRef();
        OutputStream os= new FileOutputStream(fout);
        String data= "hello world";        
        os.write(asBytes(data));
        os.close();
        try {
            s.setDeleteFile(false) ;
            s.destroy();
            assertTrue(fout.exists());
        }
        finally {
            FileUte.delete(fout) ;                    
        }
    }
    
    @Test
    public void testSmartFileIS() throws Exception {
        Tuple t= StreamUte.createTempFile( true);
        File fout= (File) t.get(0);
        OutputStream os= (OutputStream) t.get(1);
        String data= "hello world";        
        os.write(asBytes(data));
        os.close();
        try {
            SmartFileInputStream inp= new SmartFileInputStream(fout);
            byte[] bits= new byte[1024];
            int c=inp.read(bits);
            assertTrue(c==11);
            inp.close();
            c=inp.read(bits);
            assertTrue(c==11);
            inp.delete();
            assertTrue(fout.exists()) ;
        }
        finally {
            FileUte.delete(fout) ;                    
        }
    }
    
    @Test
    public void testSmartFileIS2() throws Exception {
        Tuple t= StreamUte.createTempFile(true);
        File fout= (File) t.get(0);
        OutputStream os= (OutputStream) t.get(1);
        String data= "hello world";        
        os.write(asBytes(data));
        os.close();
        try {
            SmartFileInputStream inp= new SmartFileInputStream(fout, true);
            char[] bits= new char[1024];
            int c=inp.read(bits);
            assertTrue(c==11);
            inp.close();
            c=inp.read(bits);
            assertTrue(c==11);
            inp.delete();
            assertFalse(fout.exists()) ;
        }
        finally {
        }
    }
    
    @Test
    public void testRangeStream() throws Exception {
        Tuple t= StreamUte.createTempFile(true);
        File fout= (File) t.get(0);
        OutputStream os= (OutputStream) t.get(1);
        String data= "hello world";
        long cnt; 
        os.write(asBytes(data));
        os.close();
        try {
            SmartFileInputStream inp= new SmartFileInputStream(fout);
            BoundedInputStream cip= new BoundedInputStream(inp, (int)fout.length()) ;
            assertTrue(cip.available()==fout.length());
            assertTrue(cip.hasMore());
            cnt= fout.length();
            while (cip.hasMore()) {
                cip.read();
                --cnt;
            }
            assertTrue(cnt==0L);
        }
        finally {
            FileUte.delete(fout) ;                    
        }
    }
        
    
}

