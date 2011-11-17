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
 

package com.zotoh.core.mime;

import junit.framework.JUnit4TestAdapter;

import static com.zotoh.core.mime.MimeUte.*;
import static org.junit.Assert.*;
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
    
    @Test
    public void testIsSigned() throws Exception {
        assertTrue( isSigned("sflsdkjf; multipart/signed; sdf;lsdk"));
        assertTrue( isSigned("df;lsdkl;gs application/pkcs7-mime ; dsfsdf ; signed-data"));
        assertTrue( isSigned("sfls x-application/pkcs7-mime ; dsfsdf ; signed-data"));
        assertFalse( isSigned("sflsdkjf"));
    }
    
    @Test
    public void testIsEncrypted() throws Exception {
        assertTrue( isEncrypted("df;lsdkl;gs application/pkcs7-mime ; dsfsdf ; enveloped-data"));
        assertTrue( isEncrypted("sfls x-application/pkcs7-mime ; dsfsdf ; enveloped-data"));
        assertFalse( isEncrypted("sflsdkjf"));
    }
    
    @Test
    public void testIsCompressed() throws Exception {
        assertTrue( isCompressed("df;lsdkl;gs application/pkcs7-mime ; dsfsdf ; compressed-data"));
        assertFalse( isCompressed("sflsdkjf"));
    }
    
    @Test
    public void testIsMDN() throws Exception {
        assertTrue( isMDN("df;lsdkl;gs multipart/report ; dsfsdf ; disposition-notification"));
        assertFalse( isMDN("sflsdkjf"));
    }
    
    @Test
    public void testUrlEnc() throws Exception {
        assertEquals("abc",urlEncode("abc"));
        assertFalse("ab=c".equals(urlEncode("ab=c")));
    }
    
    @Test
    public void testUrlDec() throws Exception {
        assertEquals("abc",urlDecode("abc"));
        assertEquals("ab=c",urlDecode( urlEncode("ab=c")));
    }
    
    
    
}
