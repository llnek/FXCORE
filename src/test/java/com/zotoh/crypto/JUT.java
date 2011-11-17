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
 
package com.zotoh.crypto;

import static com.zotoh.core.io.StreamUte.getBytes;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.CoreUte.rc2Stream;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import junit.framework.JUnit4TestAdapter;

import org.bouncycastle.asn1.ASN1InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.crypto.BaseOfuscator;
import com.zotoh.core.crypto.CaesarCipher;
import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.mime.MimeUte;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.FileUte;
import com.zotoh.core.util.Tuple;
import com.zotoh.crypto.Crypto.CertFormat;

/**/
public final class JUT {
    
    private static final String DNSTR_2= "C=AU,ST=NSW,L=Sydney,O=Test (Sample Only),CN=www.yoyo.com";
    private static final String DNSTR= "C=US,ST=California,L=San Francisco,O=Test (Sample Only),CN=Bouncy Castle Root CA";
    private static final String PWD_2= "Password2";
    private static final String PWD= "Password1";
    private static Date _start, _end;
    
    private static PrivateKeyEntry _PKE;

    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception     {
        long now= new Date().getTime();
        long m3= 1000L*60*60*24*90; // 3mths
        _start= new Date(now-m3) ;
        _end= new Date(now+m3) ;
        
        PKCSStore pkcs=new PKCSStore();
        pkcs.init(new File(CoreUte.rc2Url("com/zotoh/crypto/test1.p12").getFile()), "Password1");
        PrivateKeyEntry pk= pkcs.getKeyEntity( pkcs.getKeyAliases().iterator().next(), "Password1");
        assertNotNull(pk);
        _PKE=pk;        
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
    public void testCaesar() throws Exception {
    	Random r= new Random();
    	String base= "I am convinced that He (God) does not play dice.";
    	CaesarCipher cc;
    	for ( int i = 0; i < 18; ++i) {
    		cc=new CaesarCipher( r.nextInt(1024) );
    		assertTrue( base.equals( cc.decode( cc.encode(base) ) ));    		
    	}
    	for ( int i = 0; i < 18; ++i) {
    		cc=new CaesarCipher( -1 * r.nextInt(1024) );
    		assertTrue( base.equals( cc.decode( cc.encode(base) ) ));    		
    	}
    }
    
    @Test
    public void testOfuscator() throws Exception {
        BaseOfuscator bo= new BCOfuscator();
        String[] data= { "holy batman", "", null };
        String c, e;
        for (int i=0; i < data.length; ++i) {
            c= data[i] ;
            e= bo.obfuscate(c) ;
            if (c==null) {
                assertNull(e) ;
                continue;
            }
            if (e==null) {
                assertNull(c);
                continue;
            }
            assertFalse(c.length() > 0 && c.equals(e)) ;
            assertTrue(c.equals( bo.unobfuscate(e) )) ;
        }
    }
    
    @Test
    public void testCryptoCSR() throws Exception {
        byte[] pem= (byte[]) Crypto.getInstance().createCSR( 1024, DNSTR,  CertFormat.PEM).get(0) ;
        assertTrue(pem != null && pem.length > 0) ; 
        String pemStr= asString(pem);
        assertTrue(pemStr.indexOf("BEGIN CERTIFICATE REQUEST") > 0);
        assertTrue(pemStr.indexOf("END CERTIFICATE REQUEST") > 0);        
        byte[] der= (byte[]) Crypto.getInstance().createCSR( 1024,  DNSTR,   CertFormat.DER).get(0) ;
        assertTrue(der != null && der.length > 0) ; 
        assertFalse(pem.length == der.length);
    }
    
    @Test
    public void testCryptoSSV1() throws Exception {
        File out= StreamUte.createTempFile();
        Crypto.getInstance().createSSV1PKCS12("ssv1",    _start, _end,  DNSTR, PWD, 1024, out) ;
        assertTrue(out.exists()) ;
        assertTrue(out.length() > 0) ;
        FileUte.delete(out) ;
        
        out= StreamUte.createTempFile();
        Crypto.getInstance().createSSV1JKS("ssv1", _start, _end,  DNSTR, PWD, 1024, out) ;
        assertTrue(out.exists()) ;
        assertTrue(out.length() > 0) ;
        FileUte.delete(out) ;
    }
    
    @Test
    public void testCryptoSSV3PKCS12() throws Exception {
        File root= StreamUte.createTempFile();
        Crypto.getInstance().createSSV1PKCS12("ssv1",    _start, _end,  DNSTR, PWD, 1024, root) ;
        assertTrue(root.exists()) ;
        assertTrue(root.length() > 0) ;
        KeyStore ks= KeyStore.getInstance("PKCS12") ;
        InputStream inp= readStream(root) ;
        try {
            ks.load(inp, PWD.toCharArray()) ;
        }
        finally {
            StreamUte.close(inp) ;
        }
        assertTrue(ks.aliases().hasMoreElements()) ;
        PrivateKeyEntry rk= (PrivateKeyEntry) ks.getEntry( ks.aliases().nextElement(), new PasswordProtection(PWD.toCharArray())) ;
        Certificate[] cc= rk.getCertificateChain();
        File s3=  StreamUte.createTempFile();
        Crypto.getInstance().createSSV3PKCS12("ssv3", _start, _end, DNSTR_2, PWD_2, 1024, 
                cc,   rk.getPrivateKey(),   s3) ;
        assertTrue(s3.exists()) ;
        assertTrue(s3.length() > 0) ;
        
        FileUte.delete(root) ;
        FileUte.delete(s3) ;
    }
    
    @Test
    public void testCryptoSSV3JKS() throws Exception {
        File root= StreamUte.createTempFile();
        Crypto.getInstance().createSSV1JKS("ssv1", _start, _end,  DNSTR, PWD, 1024, root) ;
        assertTrue(root.exists()) ;
        assertTrue(root.length() > 0) ;
        KeyStore ks= KeyStore.getInstance("JKS") ;
        InputStream inp= readStream(root) ;
        try {
            ks.load(inp, PWD.toCharArray()) ;
        }
        finally {
            StreamUte.close(inp) ;
        }
        assertTrue(ks.aliases().hasMoreElements()) ;
        PrivateKeyEntry rk= (PrivateKeyEntry) ks.getEntry( ks.aliases().nextElement(), new PasswordProtection(PWD.toCharArray())) ;
        Certificate[] cc= rk.getCertificateChain();
        File s3=  StreamUte.createTempFile();
        Crypto.getInstance().createSSV3JKS("ssv3", _start, _end, DNSTR_2, PWD_2, 1024, 
                cc,   rk.getPrivateKey(),   s3) ;
        assertTrue(s3.exists()) ;
        assertTrue(s3.length() > 0) ;
        
        FileUte.delete(root) ;
        FileUte.delete(s3) ;
    }
    
    @Test
    public void testDigestAlgo() throws Exception {
        
        SigningAlgo[] s= { SigningAlgo.MD_5, SigningAlgo.SHA_1, SigningAlgo.SHA_256, SigningAlgo.SHA_512 };
        
        for (int i=0; i < s.length; ++i) {
            assertNotNull( Crypto.getInstance().newDigestInstance(s[i])  );
        }
        
        try {
            Crypto.getInstance().newDigestInstance(null) ;
            assertTrue("Unexpected new digest for (xxx) was OK!", false) ;
        }
        catch (Exception e) {
            assertNotNull(e) ;
        }
        
    }
    
    @Test
    public void testCertBytes() throws Exception {
        KeyStore ks= KeyStore.getInstance("PKCS12") ;
        InputStream inp=null;
        try {
            inp= rc2Stream("com/zotoh/crypto/zotoh.p12") ;
            ks.load(inp, PWD.toCharArray());
        }
        finally {
            StreamUte.close(inp);
        }
        assertTrue( ks.aliases().hasMoreElements()) ;
        PrivateKeyEntry pk= (PrivateKeyEntry) ks.getEntry( ks.aliases().nextElement(), 
                new PasswordProtection(PWD.toCharArray()) );
        Certificate c= pk.getCertificate();
        assertTrue( c instanceof X509Certificate);
        X509Certificate x= (X509Certificate) c;
        byte[] p= Crypto.getInstance().getCertBytes( x, CertFormat.PEM);
        assertTrue(p != null && p.length > 0) ;
        String pem= asString(p);
        assertTrue(pem.indexOf("BEGIN CERTIFICATE") > 0);
        assertTrue(pem.indexOf("END CERTIFICATE") > 0);
        byte[] d= Crypto.getInstance().getCertBytes( x, CertFormat.DER);
        assertTrue(d != null && d.length > 0) ;        
        assertTrue(p.length != d.length);        
    }
    
    @Test
    public void testCreateStores() throws Exception {
        CryptoStore c= new PKCSStore();
        c.init("xxx");
        assertTrue(c.getCertAliases().size()==0);
        assertTrue(c.getKeyAliases().size()==0);
        c= new JKSStore();
        c.init("xxx");
        assertTrue(c.getCertAliases().size()==0);
        assertTrue(c.getKeyAliases().size()==0);
    }
    
    @Test
    public void testAddPKey() throws Exception     {        
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        String[] fps = { "zotoh.p12", "zotoh.jks" };
        InputStream inp;
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            inp= rc2Stream("com/zotoh/crypto/" + fps[i]) ;
            try { crs[i].addKeyEntity( inp, PWD); } finally { StreamUte.close(inp); }
            Object o= crs[i].getKeyEntity(crs[i].getKeyAliases().iterator().next(), PWD);
            assertTrue(o instanceof PrivateKeyEntry);                    
        }
    }
    
    @Test
    public void testRemoveXXX() throws Exception     {        
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        String[] fps = { "zotoh.p12", "zotoh.jks" };
        InputStream inp;
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            inp= rc2Stream("com/zotoh/crypto/" + fps[i]) ;
            try { crs[i].addKeyEntity( inp, PWD); } finally { StreamUte.close(inp); }
            String nm= crs[i].getKeyAliases().iterator().next();
            Object o= crs[i].getKeyEntity(nm, PWD);
            assertTrue(o instanceof PrivateKeyEntry);            
            crs[i].removeEntity(nm) ;
            assertTrue(crs[i].getKeyAliases().size()==0);            
        }
    }
    
    @Test
    public void testAddP7b() throws Exception     {        
        InputStream inp= rc2Stream("com/zotoh/crypto/test2.p7b") ;
        PKCSStore[] crs = { new PKCSStore() };
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            try { crs[i].addPKCS7Entity( inp); } finally { StreamUte.close(inp); }
            Object o= crs[i].getCertEntity(crs[i].getCertAliases().iterator().next() );
            assertTrue(o instanceof TrustedCertificateEntry);                    
        }
    }
    
    @Test
    public void testAddCert() throws Exception     {        
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.cer") ;
        CryptoStore[] crs = { new PKCSStore() };
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            try { crs[i].addCertEntity( inp); } finally { StreamUte.close(inp); }
            Object o= crs[i].getCertEntity(crs[i].getCertAliases().iterator().next() );
            assertTrue(o instanceof TrustedCertificateEntry);                    
        }
    }
    
    @Test
    public void testGetRootCAs() throws Exception     {        
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        String[] fps= {"zotoh.p12", "zotoh.jks"};
        InputStream inp;
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            inp= rc2Stream("com/zotoh/crypto/" + fps[i]) ;
            try { crs[i].addKeyEntity(inp, PWD); } finally { StreamUte.close(inp); }
            assertTrue(crs[i].getRootCAs().size() > 0);
            assertTrue(crs[i].getIntermediateCAs().size() ==0) ;
        }
    }
    
    @Test
    public void testGetTrustedCerts() throws Exception     {        
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        String[] fps= {"zotoh.p12", "zotoh.jks"};
        InputStream inp;
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            inp= rc2Stream("com/zotoh/crypto/" + fps[i]) ;
            try { crs[i].addKeyEntity(inp, PWD); } finally { StreamUte.close(inp); }
            assertTrue(crs[i].getTrustedCerts().size() > 0);
        }
    }
    
    @Test
    public void testInitVerify() throws Exception     {        
        InputStream inp;
        Signature sig;
        Certificate cc;
        CryptoStore[] crs = { new PKCSStore() , new JKSStore()};
        String[] fps= {"zotoh.p12", "zotoh.jks"};
        String[] sigs= { "MD2withRSA", "MD5withRSA", "SHA1withRSA", "SHA256withRSA", "SHA384withRSA", "SHA512withRSA"  };
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx");
            inp= rc2Stream("com/zotoh/crypto/" + fps[i]) ;
            try { crs[i].addKeyEntity( inp, PWD); } finally { StreamUte.close(inp); }
            cc= crs[i].getKeyEntity(crs[i].getKeyAliases().iterator().next(), PWD).getCertificate();
            // just test pkcs12, most algo don't work with SUN
            if (i==0) for (int j=0; j < sigs.length; ++j) {
                sig = Signature.getInstance(sigs[j], Crypto.getInstance().getProvider());
                sig.initVerify(cc);                
            }
        }
    }

    @Test
    public void testKeyFac() throws Exception     {
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx") ;
            assertTrue( crs[i].getKeyManagerFactory()  != null);
        }
    }

    @Test
    public void testCertFac() throws Exception     {
        CryptoStore[] crs = { new PKCSStore(), new JKSStore() };
        for (int i=0; i < crs.length; ++i) {
            crs[i].init("xxx") ;
            assertTrue( crs[i].getTrustManagerFactory()  != null);
        }
    }
    
    @Test
    public void testListCertsInP7B() throws Exception     {        
        InputStream inp= rc2Stream("com/zotoh/crypto/test2.p7b") ;        
        CertificateFactory cf = CertificateFactory.getInstance("X.509", Crypto.getInstance().getProvider());
        try {
            assertTrue( cf.generateCertificates(inp).size() > 0);
        } 
        finally {
            StreamUte.close(inp);
        }
    }

    @Test
    public void testReadASN1Object() throws Exception     {        
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.p12") ;        
        try {
            assertTrue(new ASN1InputStream(inp).readObject() != null);
        }
        finally {
            StreamUte.close(inp);
        }
    }

    
    @Test
    public void testBitsToCert() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.cer");
        try {
            assertTrue(CryptoUte.bitsToCert(getBytes(inp)) != null);
        }
        finally {
            StreamUte.close(inp) ;
        }
    }
    
    @Test
    public void testBitsToKey() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.p12");
        try {
            assertTrue(CryptoUte.bitsToKey(getBytes(inp), PWD) != null);
        }
        finally {
            StreamUte.close(inp) ;
        }
    }
    
    @Test
    public void testGetCertDesc() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.cer");
        byte[] bits;
        TrustedCertificateEntry tc;
        Certificate c;
        Tuple props, props2;
        try {
            bits= getBytes(inp);
            tc= CryptoUte.bitsToCert(bits);
            assertTrue(tc != null) ;
            c= tc.getTrustedCertificate() ;
            assertTrue(c != null);
            props= CryptoUte.getCertDesc(bits) ;
            assertTrue(props.size() > 0 );
            props2= CryptoUte.getCertDesc(c) ;
            assertTrue(props.size() == props2.size() );
        }
        finally {
            StreamUte.close(inp) ;
        }
    }
    
    @Test
    public void testNewRandom() throws Exception {
        assertTrue(CryptoUte.newRandom() != null);
    }
    
    @Test
    public void testFingerPrints() throws Exception {
        String s1 = CryptoUte.getSHA1FingerPrint(asBytes("hello world"));
        assertTrue(s1 != null && s1.length() > 0) ;
        String s2 = CryptoUte.getMD5FingerPrint(asBytes("hello world"));
        assertTrue(s2 != null && s2.length() > 0) ;
        assertFalse(s1.equals(s2)) ;
    }
        
    @Test
    public void testTstCertValid() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.cer");
        boolean ok;
        // zotoh.cer is valid between 6/27/2010 -> 6/27/2110
        // so should be true, unless you are stilling using this lib 100 years from now.
        try {
            ok=CryptoUte.tstCertValid(getBytes(inp));
            assertTrue(ok);
        }
        finally {
            StreamUte.close(inp) ;
        }
    }
    
    @Test
    public void testTstKeyValid() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/crypto/zotoh.p12");
        boolean ok;
        // zotoh.p12 is valid between 6/27/2010 -> 6/27/2110
        // so should be true, unless you are stilling using this lib 100 years from now.
        try {
            ok=CryptoUte.tstPKeyValid(getBytes(inp), PWD);
            assertTrue(ok);
        }
        finally {
            StreamUte.close(inp) ;
        }
    }

    @Test
    public void testSessions() throws Exception {
        Session s0= CryptoUte.newSession("popeye", PWD) ;
        Session s1= CryptoUte.newSession();
        assertTrue(s0 != null);
        assertTrue(s1 != null);
    }
    
    @Test
    public void testMimeMsgs() throws Exception {
        MimeMessage m0= CryptoUte.newMimeMsg("popeye", PWD) ;
        MimeMessage m2= CryptoUte.newMimeMsg();
        assertTrue(m0 != null);
        assertTrue(m2 != null);       
    }

    @Test
    public void testDSigMimeMsg() throws Exception {
        MimeMessage m=CryptoUte.newMimeMsg(CoreUte.rc2Stream("com/zotoh/crypto/mime.txt")) ;
        Object c=m.getContent();
        Multipart mp;
        BodyPart bp;
        assertTrue(c instanceof Multipart);
        mp=CryptoUte.smimeDigSig(_PKE.getPrivateKey(), _PKE.getCertificateChain(), 
                SigningAlgo.SHA512, (Multipart) c);
        MimeUte.isSigned(mp.getContentType());

//        ByteOStream os= new ByteOStream();
//        m=CryptoUte.newMimeMsg();
//        m.setContent(mp);
//        m.saveChanges();
//        m.writeTo(os);
//        System.out.println(new String(os.asBytes()));
                
        m=CryptoUte.newMimeMsg(CoreUte.rc2Stream("com/zotoh/crypto/mime.txt")) ;
        mp=(Multipart) m.getContent();
        bp=mp.getBodyPart(0);
        mp= CryptoUte.smimeDigSig(_PKE.getPrivateKey(), _PKE.getCertificateChain(), SigningAlgo.MD5, bp);
        MimeUte.isSigned(mp.getContentType());

        ByteOStream os= new ByteOStream();
        m=CryptoUte.newMimeMsg();
        m.setContent(mp);
        m.saveChanges();
        m.writeTo(os);

        m=CryptoUte.newMimeMsg(StreamUte.asStream(os.asBytes())) ;
        mp=(Multipart) m.getContent();
        
        c=CryptoUte.peekSmimeSignedContent(mp);
        assert( c instanceof String);
        
        Tuple t= CryptoUte.verifySmimeDigSig(mp, _PKE.getCertificateChain());
        assertTrue(t.get(0) instanceof String);
        assertNotNull(t.get(1));
        
    }

    @Test
    public void testEncryptMimeMsg() throws Exception {
        MimeMessage msg;
        StreamData dd;
        DataSource s=new SmDataSource("hello world".getBytes("utf-8"), "text/plain");
        Multipart mp;
        ByteOStream os;
        BodyPart bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        // encrypt one part
        bp=CryptoUte.smimeEncrypt(_PKE.getCertificate(), EncryptionAlgo.DES_EDE3_CBC, bp);        
        assertTrue(MimeUte.isEncrypted(bp.getContentType()));
        msg= CryptoUte.newMimeMsg();
        msg.setContent(bp.getContent(), bp.getContentType());
        
        os= new ByteOStream();
        msg.saveChanges();
        msg.writeTo(os);
        msg= CryptoUte.newMimeMsg( StreamUte.asStream(os.asBytes()));
        dd=CryptoUte.smimeDecryptAsStream(new PrivateKey[]{_PKE.getPrivateKey()}, msg) ;            
        assertNotNull(dd);
        assertTrue(new String(dd.getBytes(),"utf-8").indexOf("hello world") > 0);
        
        s=new SmDataSource("hello world".getBytes("utf-8"), "text/plain");
        bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        bp=CryptoUte.smimeEncrypt(_PKE.getCertificate(), EncryptionAlgo.DES_EDE3_CBC, bp);
        dd=CryptoUte.smimeDecrypt(_PKE.getPrivateKey(), bp);
        dd=CryptoUte.smimeDecryptAsStream(new PrivateKey[]{_PKE.getPrivateKey()}, msg) ;            
        assertNotNull(dd);
        assertTrue(new String(dd.getBytes(),"utf-8").indexOf("hello world") > 0);

        // encrypt many parts
        mp= new MimeMultipart();
        s=new SmDataSource("hello world".getBytes("utf-8"), "text/plain");
        bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        mp.addBodyPart(bp);
        s=new SmDataSource("hello hello hello".getBytes("utf-8"), "text/plain");
        bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        mp.addBodyPart(bp);
        bp=CryptoUte.smimeEncrypt(_PKE.getCertificate(), EncryptionAlgo.AES256_CBC, mp);
        assertTrue(MimeUte.isEncrypted(bp.getContentType()));
        dd=CryptoUte.smimeDecrypt(_PKE.getPrivateKey(), bp);
        assertNotNull(dd);
        assertTrue(new String(dd.getBytes(),"utf-8").indexOf("hello hello hello") > 0);
        
        // encrypt one message
        mp= new MimeMultipart();
        s=new SmDataSource("hello world".getBytes("utf-8"), "text/plain");
        bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        mp.addBodyPart(bp);
        s=new SmDataSource("hello hello hello".getBytes("utf-8"), "text/plain");
        bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler( s));
        mp.addBodyPart(bp);
        msg = CryptoUte.newMimeMsg();
        msg.setContent(mp);
        bp=CryptoUte.smimeEncrypt(_PKE.getCertificate(), EncryptionAlgo.AES256_CBC, msg);
        //
//        msg=  CryptoUte.newMimeMsg();
//        msg.setContent(bp.getContent(), bp.getContentType());
//        msg.saveChanges();
//        os= new ByteOStream();
//        msg.writeTo(os);
//        System.out.println(new String(os.asBytes()));
        //
        assertTrue(MimeUte.isEncrypted(bp.getContentType()));
        dd=CryptoUte.smimeDecrypt(_PKE.getPrivateKey(), bp);
        assertNotNull(dd);
        assertTrue(new String(dd.getBytes(),"utf-8").indexOf("hello hello hello") > 0);
        
    }
    
    @Test
    public void testPKCSDSig() throws Exception {
        StreamData dd= new StreamData("hello world");
        byte[] sig=CryptoUte.pkcsDigSig(_PKE.getPrivateKey(), _PKE.getCertificateChain(), 
                SigningAlgo.SHA512, dd);
        byte[] dig=CryptoUte.verifyPkcsDigSig( _PKE.getCertificate(), dd, sig); 
        assertTrue(dig != null && dig.length > 0);
    }
    
    @Test
    public void testCmpzion() throws Exception {
        MimeMessage msg= CryptoUte.newMimeMsg();
        StreamData dd;
        BodyPart bp;
        SmDataSource s;
        
        bp= new MimeBodyPart();
        s=new SmDataSource("hello world".getBytes("utf-8"), "text/plain");
        bp.setDataHandler(new DataHandler( s));
        msg.setContent(bp.getContent(), bp.getContentType());        
        bp=CryptoUte.compressContent(msg);
        assertTrue(MimeUte.isCompressed(bp.getContentType()));
        
//        msg= CryptoUte.newMimeMsg();
//        msg.setContent(bp.getContent(), bp.getContentType());        
//        msg.saveChanges();
//        os= new ByteOStream();
//        msg.writeTo(os);
//        System.out.println(new String(os.asBytes()));
        
        dd= new StreamData("hello world");
        bp=CryptoUte.compressContent("text/plain", dd);
        assertTrue(MimeUte.isCompressed(bp.getContentType()));
        
        dd=CryptoUte.decompressAsStream( bp.getInputStream() );
        assertTrue(new String(  dd.getBytes()  ).indexOf("hello world") > 0);
        
        bp=CryptoUte.compressContent("text/plain", "base64", "zzz", "mmm", dd);
        assertTrue(MimeUte.isCompressed(bp.getContentType()));
        
        dd=CryptoUte.decompress(bp);
        assertTrue(new String(  dd.getBytes()  ).indexOf("zzz") > 0   );
                        
    }
       
    
    
    
     
}