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
 
package com.zotoh.core.crypto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public final class JUT {
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception     {
//        System.out.println("@BeforeClass iniz()");
        byte[] key= Arrays.copyOfRange("use-this-as-the-key-for-testing".getBytes("UTF-8"), 0, 24);
        BaseOfuscator.setKey(key) ;
    }

    @AfterClass
    public static void finz()     {
//        System.out.println("@AfterClass finz()");
    }

    @Before
    public void open() throws Exception     {
//        System.out.println("@Before open()");
    }

    @After
    public void close() throws Exception     {
//        System.out.println("@Before close()");
    }

    @Test
    public void testOfuscator() throws Exception {
        BaseOfuscator bo = new JavaOfuscator() ;
        String[] data= { "holy batman", "", null };
        String c, e;
        for (int i=0; i < data.length; ++i) {
            c= data[i] ;
            e= bo.obfuscate(c) ;
            if (c==null) {
                assertTrue(e==null) ;
                continue;
            }
            if (e==null) {
                assertTrue(c==null);
                continue;
            }
            assertFalse(c.length() > 0 && c.equals(e)) ;
            assertTrue(c.equals( bo.unobfuscate(e) )) ;
        }
    }
    
    @Test
    public void testPassword() throws Exception     {        
        PwdFactory fac= PwdFactory.getInstance();
        String[] data= {"holy batman", "", null };
        String c,e;
        Password p1, p2;
        
        for (int i=0; i < data.length; ++i) {
            p1= fac.create(data[i]) ;
            c= p1.getAsClearText();
            e= p1.getAsEncoded();
            if (data[i]==null) {
                assertTrue(c==null) ; 
                continue;
            }
            if (c==null) {
                assertTrue(data[i]==null);
                continue;
            }
            if ( e != null && e.length() > 0) {
                assertTrue(e.startsWith(Password.PWD_PFX));
                assertFalse(data[i].equals(e)) ;
            }
            assertTrue(data[i].equals(c)) ;
            
            p2= PwdFactory.getInstance().copy(p1) ;
            assertFalse(p1==p2);
            assertTrue(p1.equals(p2)) ;
            
            p2= PwdFactory.getInstance().create(e) ;
            assertFalse(p1==p2);
            assertTrue(p1.equals(p2)) ;
        }
    }
      
    @Test
    public void testPwdGen() throws Exception {
        int[] data = { 0, -1, 15};
        Password p;
        String c;
        for (int i=0; i < data.length; ++i) {
            p= PwdFactory.getInstance().createStrongPassword( data[i] );
            c= p.getAsClearText();
            if (data[i] < 0) assertTrue(c == null);
            else
            if (data[i] == 0) assertTrue(c != null && c.length() ==0) ;
            else
            assertTrue(c != null && c.length() == data[i]) ;
        }
    }
    
    @Test
    public void testRandomTextGen() throws Exception {
        int[] data = { 0, -1, 255};
        String c;
        for (int i=0; i < data.length; ++i) {
            c=PwdFactory.getInstance().createRandomText( data[i] );
            if (data[i] < 0) assertTrue(c == null);
            else
            if (data[i] == 0) assertTrue(c != null && c.length() ==0) ;
            else
            assertTrue(c != null && c.length() == data[i]) ;
        }
    }
    
}