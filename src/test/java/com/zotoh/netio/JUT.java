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
 
package com.zotoh.netio;

import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.getTmpDir;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.ByteUte;
import com.zotoh.core.util.StrArr;

public final class JUT {

    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception     {
    }

    @AfterClass
    public static void finz()     {
    }

    @Before
    public void open() throws Exception     {
    }

    @After
    public void close() throws Exception     {
    }

    //@Test
    @SuppressWarnings("unused")
	private void testGetEC2() throws Exception {
        final Object join= new Object(); 
        String url= "http://169.254.169.254/2008-09-01/meta-data/instance-id" ;
        HttpClientr t= HttpUte.simpleGET( new URI(url), new BasicHttpMsgIO(){
            public void onOK(int code, String reason, StreamData out) {
                try {
                    System.out.println("Success: code="+code) ;
                    System.out.println( new String( out.getBytes() ) );
                } catch (Exception e) 
                {}
                synchronized(join) {            join.notify();        }
            }
            public void onError(int code, String reason) {
                System.out.println("Error: code="+code) ;
                synchronized(join) {            join.notify();        }
            }            
        });
        synchronized(join) {            join.wait();        }
    }

    @Test
    public void testUte() throws Exception {
        String s;
        
        s=NetUte.canonicalizeEmailAddress("DonaldDuck@ABC.cOm");
        assertEquals("DonaldDuck@abc.com",s);
        
        s=NetUte.getHostPartUri("http://bing.com:80/search");
        assertEquals("bing.com",s);
        
        assertEquals(80,NetUte.getPort("http://bing.com:80/search"));
        
        byte[] bits=NetUte.ipv4ToBytes("192.168.220.250");
        InetAddress a= InetAddress.getByAddress(bits);
        s=a.getHostAddress();
        assertEquals(s, "192.168.220.250");
        
        String s1=System.getProperty("user.timezone");
        String s2="file://c:/abc/def/${user.timezone}/bbb/%user.timezone%/jjj";
        String s3="file://c:/abc/def/"+s1+"/bbb/"+s1+"/jjj";        
        assertEquals(NetUte.resolveAndExpandFileUrl(s2), s3);
    }
    
    @Test
    public void testFileUpload() throws Exception {
    		final String tmpDir=niceFPath(getTmpDir());
        File f1=StreamUte.createTempFile();
        File f2=StreamUte.createTempFile();
        File a1=StreamUte.createTempFile();
        File a2=StreamUte.createTempFile();
        writeFile(f1, "hello", "utf-8");
        writeFile(f2, "world", "utf-8");
        writeFile(a1, "goodbye", "utf-8");
        writeFile(a2, "joe", "utf-8");        
        FileUploader ldr= new FileUploader();
        ldr.addField("fname", "Joe");
        ldr.addField("lname", "Bloggs");
        ldr.addFile(f1);
        ldr.addFile(f2, "f2");
        ldr.addAtt(a1);
        ldr.addAtt(a2, "a2");
        ldr.setUrl("http://localhost:9090/takethis");
                
        MemHttpServer m= new MemHttpServer(tmpDir, "localhost", 9090);
    		final ByteOStream baos= new ByteOStream( 100000);
        m.bind(new BasicHttpMsgIO() {
			public void onOK(int code, String reason, StreamData data) {
				try {
				baos.write(data.getBytes() ); } catch (Exception e) {}
			}        	
        }).start(false);
        
        Thread.sleep(2000);
        
        ldr.send(null);
        
        Thread.sleep(2000);        
        
        m.stop();
        
        Thread.sleep(5000);        
        
        String data= new String(baos.toByteArray(), "utf-8");
        f1.delete();
        f2.delete();
        a1.delete();
        a2.delete();
        
        assertTrue(data.indexOf("hello") > 0);
        assertTrue(data.indexOf("world") > 0);
        assertTrue(data.indexOf("goodbye") > 0);
        assertTrue(data.indexOf("joe") > 0);
        assertTrue(data.indexOf("fname") > 0);
        assertTrue(data.indexOf("lname") > 0);
        
    }
    
    @Test
    public void testFileServer() throws Exception {
    	File f=StreamUte.createTempFile();
    	StreamUte.writeFile(f, "hello world", "utf-8");
    	byte[] bits;
    	String s="";
    	SimpleFileServer svr=new SimpleFileServer("localhost", 9090);
    	svr.start();
    	Thread.sleep(5000);
    	Socket soc= new Socket("localhost", 9090);
    	soc.getOutputStream().write(  ("rcp " + f.getCanonicalPath() + "\r\n").getBytes("utf-8") );
    	soc.getOutputStream().flush();
    	Thread.sleep(1000);
    	bits=new byte[8];
    	soc.getInputStream().read(bits);
    	long clen=ByteUte.readAsLong(bits) ;
    	if (clen > 0L) {
    		bits= new byte[ (int) clen];
    		soc.getInputStream().read(bits);
    		s= new String(bits, "utf-8");
    	}
    	
    	soc.getOutputStream().write(  ("rrm " + f.getCanonicalPath() + "\r\n").getBytes("utf-8") );
    	soc.getOutputStream().flush();
    	Thread.sleep(1000);
    	assertFalse(f.exists()) ;
    	
    	soc.getOutputStream().write(  "stop\r\n".getBytes("utf-8") );
    	soc.close();
    	svr.stop();
    	
    	assertTrue("hello world".equals(s)) ;
    }
    
    @Test
    public void testHttpSend() throws Exception {
	    	final ByteOStream baos= new ByteOStream( 100000);
	    	final String tmpDir= niceFPath(getTmpDir());    
	    	MemHttpServer m= new MemHttpServer(tmpDir, "localhost", 9090);
	    	m.bind(new BasicHttpMsgIO(){
	    			public void onPreamble(String mtd, String uri, Map<String,StrArr> headers) {
					try { baos.write(uri.getBytes());} catch (Exception e) {}	    				
	    			}	    		
				public void onOK(int code, String reason, StreamData data) {
					try { baos.write(data.getBytes());} catch (Exception e) {}
				}    		
	    	}).start(false);
	    	
	    	Thread.sleep(2000);
	    	
	    	HttpUte.simpleGET(new URI("http://localhost:9090/dosomething?a=b&c=e"), new BasicHttpMsgIO() {
	            public void onOK(int code, String reason, StreamData res) {}            
	            public void configMsg(HttpMessage m) {
	                m.setHeader("content-transfer-encoding", "base64");
	            }    	    
	    	});
	    	
	    	Thread.sleep(3000);
	    	
	    	String s=new String(baos.asBytes());
	    	//System.out.println("-->" + s);
	    	assertTrue( s.indexOf("a=b") > 0 );
	
	    	baos.reset();
        HttpUte.simpleGET(false, "localhost", 9090, "/dosomething", "x=y&j=k", null);         
        Thread.sleep(3000);
        assertTrue( new String(baos.asBytes()).indexOf("x=y") > 0 );

        StreamData in= new StreamData("hello world");
        baos.reset();
        HttpUte.simplePOST(new URI("http://localhost:9090/hereyougo"), in,
                new BasicHttpMsgIO(){
            public void configMsg(HttpMessage m) { m.setHeader("content-type", "text/plain");}            
            public void onOK(int code, String reason, StreamData res) {}            
            public void onError(int code, String reason) {}                                
        });
        
        Thread.sleep(3000);
        assertTrue( new String(baos.asBytes()).indexOf("hello") > 0 );
        
        baos.reset();
        HttpUte.simplePOST(false, "localhost", 9090, "/hereyougo", in,
                new BasicHttpMsgIO(){
            public void configMsg(HttpMessage m) { m.setHeader("content-type", "text/plain");}            
            public void onOK(int code, String reason, StreamData res) {}            
            public void onError(int code, String reason) {}                                  
        });
        Thread.sleep(3000);
        assertTrue( new String(baos.asBytes()).indexOf("hello") > 0 );

        
        baos.reset();
        HttpUte.simplePOST(false, "localhost", 9090, "/hereyougo", in,
                new BasicHttpMsgIO(){
            public void configMsg(HttpMessage m) { m.setHeader("content-type", "text/plain");
            		m.setHeader("content-transfer-encoding", "binary");
            }            
            public void onOK(int code, String reason, StreamData responseData) {}            
            public void onError(int code, String reason) {}                                   
        });
        Thread.sleep(3000);
        assertTrue( new String(baos.asBytes()).indexOf("world") > 0 );
        
        m.stop();
        
    		Thread.sleep(3000);
    }
    
    
    
    
    
    
    
    
    
}
