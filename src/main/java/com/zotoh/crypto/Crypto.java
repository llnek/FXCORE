/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public enum Crypto {
    
    INSTANCE ;
    
    private transient Logger _log= getLogger(Crypto.class);
    public Logger tlog() {  return _log;    }    
    private Provider _prov;
    
    private static String DEF_ALGO= "SHA1WithRSAEncryption";
    //private static String BLOWFISH= "Blowfish";
    private static String PKCS12= "PKCS12";    
    private static String JKS= "JKS";    
    public static final String SHA1= "SHA1";
    public static final String MD5= "MD5";    
    public static final String AES256_CBC = "AES256_CBC";
    public static final String RAS = "RAS";
    public static final String DES = "DES";
    private boolean _jceTested;
    
    /**
     *
     */
    public enum CertFormat {
        PEM, DER
    };
    
//    public enum SigningStyle    {
//        EXPLICIT,  IMPLICIT;
//    };
    
    /**
     * @param keyLength
     * @param dnStr
     * @param fmt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws IOException
     */
    public Tuple createCSR( int keyLength, String dnStr, CertFormat fmt) 
            throws NoSuchAlgorithmException
            ,InvalidKeyException, NoSuchProviderException
            , SignatureException, IOException     {
        
        tstPosIntArg("key-length", keyLength);
        tstEStrArg("subject-dn", dnStr);
        tstObjArg("cert-format", fmt) ;
        
        tlog().debug("Crypto: createCSR: dnStr= {}, key-len= {}", dnStr, keyLength);
        
        KeyPair kp= createKeyPair("RSA", keyLength);
        PrivateKey k= kp.getPrivate();
        byte[] bits = new PKCS10CertificationRequest(  DEF_ALGO, new X500Principal(dnStr), 
                kp.getPublic(), null, k )
        .getEncoded();            
        
        if (CertFormat.PEM== fmt) {
            bits= fmtPEM("-----BEGIN CERTIFICATE REQUEST-----\n", 
                    "\n-----END CERTIFICATE REQUEST-----\n", bits) ;
        }
        
        return new Tuple(bits, getPKey(k, fmt));
    }
    
    /**
     * @param key
     * @param fmt
     * @return
     * @throws IOException
     */
    public byte[] getPKey(PrivateKey key, CertFormat fmt) throws IOException {
    	tstObjArg("private-key", key);
    	byte[] bits= key.getEncoded();
        if (CertFormat.PEM== fmt) {
            bits= fmtPEM("-----BEGIN RSA PRIVATE KEY-----\n", 
                    "\n-----END RSA PRIVATE KEY-----\n", bits) ;
        }
        
        return bits;
    }
    
    
    /**
     * @param cert
     * @param fmt
     * @return
     * @throws CertificateEncodingException
     * @throws IOException
     */
    public byte[] getCertBytes(X509Certificate cert, CertFormat fmt) 
                throws CertificateEncodingException, IOException {
        tstObjArg("cert-format", fmt) ;
        tstObjArg("cert", cert) ;

        byte[] bits= cert.getEncoded();
        
        if (CertFormat.PEM== fmt) {
            bits= fmtPEM("-----BEGIN CERTIFICATE-----\n",
                    "-----END CERTIFICATE-----\n", bits);
        }
        
        return bits;
    }
    
    
    /**
     * @param friendlyName
     * @param keyPEM
     * @param certPEM
     * @param pwd
     * @param out
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableEntryException
     * @throws InvalidKeySpecException
     */
    public void createPKCS12(String friendlyName, byte[] keyPEM, byte[] certPEM, String pwd, File out) 
                throws KeyStoreException, NoSuchAlgorithmException
                , CertificateException, IOException, UnrecoverableEntryException
                , InvalidKeySpecException {        
        tstEStrArg("friendly-name", friendlyName);
        tstObjArg("private-key-pem", keyPEM);
        tstObjArg("cert-pem", certPEM);
        tstEStrArg("password", pwd);
        tstObjArg("output-file", out);
        
        Certificate ct= CryptoUte.bitsToCert(certPEM).getTrustedCertificate();
        KeyStore ss= new PKCSStore().createKeyStore();
        Reader rdr=new InputStreamReader( new ByteArrayInputStream(keyPEM));
        KeyPair kp = (KeyPair) new PEMReader(rdr).readObject();
        ss.setKeyEntry(friendlyName, kp.getPrivate(), pwd.toCharArray(), new Certificate[]{ct});
        ByteOStream baos= new ByteOStream();
        ss.store(baos, pwd.toCharArray());
        StreamUte.writeFile(out, baos.asBytes());
    }
    
    /**
     * Create a ROOT CA  PKCS-12 file.
     *  
     * @param friendlyName
     * @param start
     * @param end
     * @param dnStr
     * @param password
     * @param keyLength
     * @param out
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     * @throws KeyStoreException
     * @throws IOException
     */
    public void createSSV1PKCS12(String friendlyName, Date start, Date end, String dnStr, String password,
            int keyLength, File out) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, 
            SignatureException, CertificateException, NoSuchProviderException, GeneralSecurityException,
            KeyStoreException, IOException      {
        
        KeyStore ks= KeyStore.getInstance(PKCS12, getProvider());
        ks.load(null, null);
        KeyPair keyPair= createKeyPair("RSA", keyLength);                  
        create_SSV1(ks, keyPair, DEF_ALGO, friendlyName, start,end,dnStr,password,keyLength, out);
    }
        
    /**
     * Create a ROOT CA  JKS file.
     *  
     * @param friendlyName
     * @param start
     * @param end
     * @param dnStr
     * @param password
     * @param keyLength
     * @param out
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     * @throws KeyStoreException
     * @throws IOException
     */
    public void createSSV1JKS(String friendlyName, Date start, Date end, String dnStr, String password,
            int keyLength, File out) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, 
            SignatureException, CertificateException, NoSuchProviderException, GeneralSecurityException,
            KeyStoreException, IOException      {
        
        KeyStore ks= KeyStore.getInstance(JKS, "SUN");
        ks.load(null, null);
        KeyPair keyPair= createKeyPair("DSA", keyLength);                  
        create_SSV1(ks, keyPair, "SHA1withDSA", friendlyName, start,end,dnStr,password,keyLength, out);
    }

    private void create_SSV1(KeyStore ks, KeyPair keyPair, String algo, String friendlyName, Date start, Date end, String dnStr, String password,
            int keyLength, File out) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, 
            SignatureException, CertificateException, NoSuchProviderException, GeneralSecurityException,
            KeyStoreException, IOException      {
        
        tstEStrArg("friendly-name", friendlyName);
        tstEStrArg("DN", dnStr);
        tstEStrArg("password", password);
        tstObjArg("output-file", out);
        tstObjArg("start-date", start);
        tstObjArg("end-date", end);
        tstPosIntArg("key-length", keyLength) ;
    
        tlog().debug("Crypto:createSSV1: dn={}, key-len={}", dnStr, keyLength);
        
        Tuple props= createSSV1Cert(ks.getProvider(), keyPair, start, end, dnStr, keyLength, algo);        
        char[] pwd= password.toCharArray();
        ByteOStream baos= new ByteOStream();
        
        ks.setKeyEntry(friendlyName, (PrivateKey)props.get(1), pwd, new Certificate[]{ (Certificate)props.get(0) });        
        ks.store(baos, pwd);        
        
        writeFile(out, baos.asBytes());
    }
    
    /**
     * Create a server PKCS12 file.
     * 
     * @param friendlyName
     * @param start
     * @param end
     * @param dnStr
     * @param password
     * @param keyLength
     * @param issuerCerts
     * @param issuerKey
     * @param out
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws SignatureException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws KeyStoreException
     * @throws IOException
     */
    public void createSSV3PKCS12(String friendlyName, Date start, Date end, String dnStr, 
            String password, int keyLength, Certificate[] issuerCerts, PrivateKey issuerKey, File out) 
                    throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, GeneralSecurityException,
                    SignatureException, CertificateException, NoSuchProviderException, KeyStoreException, IOException     {
        KeyStore ks= KeyStore.getInstance(PKCS12, getProvider());
        ks.load(null, null);        
        KeyPair kp= createKeyPair("RSA", keyLength);        
        create_SSV3(ks, kp, DEF_ALGO, friendlyName,start,end,dnStr,password,keyLength,issuerCerts,issuerKey,out);
    }
    
    
    /**
     * @param friendlyName
     * @param start
     * @param end
     * @param dnStr
     * @param password
     * @param keyLength
     * @param issuerCerts
     * @param issuerKey
     * @param out
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws SignatureException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws KeyStoreException
     * @throws IOException
     */
    public void createSSV3JKS(String friendlyName, Date start, Date end, String dnStr, 
            String password, int keyLength, Certificate[] issuerCerts, PrivateKey issuerKey, File out) 
                    throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, GeneralSecurityException
                    ,SignatureException, CertificateException, NoSuchProviderException, KeyStoreException, IOException     {
        KeyStore ks= KeyStore.getInstance(JKS, "SUN");
        ks.load(null, null);
        KeyPair kp= createKeyPair("DSA", keyLength);        
        create_SSV3(ks, kp, "SHA1withDSA", friendlyName,start,end,dnStr,password,keyLength,issuerCerts,issuerKey,out);
    }

    
    private void create_SSV3(KeyStore ks, KeyPair keyPair,String algo, String friendlyName, Date start, Date end, String dnStr, 
            String password, int keyLength, Certificate[] issuerCerts, PrivateKey issuerKey, File out) 
                    throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, GeneralSecurityException
                    ,SignatureException, CertificateException, NoSuchProviderException, KeyStoreException, IOException     {
        
        tstEStrArg("friendly-name", friendlyName);
        tstEStrArg("DN", dnStr);
        tstEStrArg("password", password);
        tstObjArg("issuer-cert", issuerCerts);
        tstObjArg("issuer-key", issuerKey);
        tstObjArg("output-file", out);
        tstObjArg("start-date", start);
        tstObjArg("end-date", end);
        tstPosIntArg("key-length", keyLength) ;

        tlog().debug("Crypto:createSSV3: dn={}, key-len={}", dnStr, keyLength);

        Tuple props= createSSV3Cert(ks.getProvider(), keyPair, start, end, dnStr, issuerCerts[0], issuerKey, keyLength, algo);
        char[] pwd= password.toCharArray();
        List<Certificate> cs= CoreUte.asList(true, issuerCerts);
        ByteOStream baos= new ByteOStream();
        
        cs.add(0, (Certificate) props.get(0) );
        
        ks.setKeyEntry(friendlyName, (PrivateKey) props.get(1), pwd,  cs.toArray(new Certificate[0]));        
        ks.store(baos, pwd);        
        
        writeFile(out, baos.asBytes());
    }
    
    /**
     * From the given PKCS12 file, generate a corresponding PKCS7 file.
     * 
     * @param p12File
     * @param password
     * @param fileOut
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws CertificateException
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws CertStoreException
     * @throws GeneralSecurityException
     */
    public void exportPKCS7( File p12File, String password, File fileOut) 
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
            , CertificateException, IOException 
            ,InvalidAlgorithmParameterException, CertStoreException, GeneralSecurityException   {
        
        tstObjArg("pkcs7 output file", fileOut);
        tstObjArg("pkcs12 file", p12File);
        tstObjArg("password", password);
        
        KeyStore.PrivateKeyEntry key = loadPKCS12Key(p12File, password);
        Certificate[] cc= key.getCertificateChain();
        List<Certificate> cl= CoreUte.asList(true,cc);
        
        DigestCalculatorProvider cp;
        try {
            cp= new JcaDigestCalculatorProviderBuilder().setProvider(getProvider()).build();
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        JcaSignerInfoGeneratorBuilder bdr = new JcaSignerInfoGeneratorBuilder(cp);
        
//      "SHA1withRSA"
        ContentSigner cs;
        try {
            cs= new JcaContentSignerBuilder(CMSSignedDataGenerator.DIGEST_SHA512).setProvider(getProvider())
            .build(key.getPrivateKey());
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        
        try {
            gen.addSignerInfoGenerator( bdr.build(cs, (X509Certificate) cc[0] ));
            gen.addCertificates(new JcaCertStore(cl));
            byte[] bits = gen.generate(CMSSignedDataGenerator.DATA, 
                    new CMSProcessableByteArray("Hello".getBytes()), false, getProvider(), false).getEncoded();

            writeFile(fileOut, bits);
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);            
        }
                
    }
            
    /**
     * @return
     */
    public Provider getProvider() {        return maybe_assert_jce();    }

    /**
     * MD5, SHA-1, SHA-256, SHA-384, SHA-512.
     *  
     * @param algo
     * @return
     * @throws NoSuchAlgorithmException
     */
    public MessageDigest newDigestInstance(SigningAlgo algo) throws NoSuchAlgorithmException    {
        return MessageDigest.getInstance(algo.toString());
    }
    

    /**
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance( "SHA1PRNG" );
    }       
    
    /**
     * @return
     */
    public static Crypto getInstance() { 
        INSTANCE.maybe_assert_jce();
        return INSTANCE; 
    }    
            
    /**
     * @return
     */
    public static Provider getBouncyCastle() {
        return Security.getProvider("BC");
    }
    
    /**
     * @return
     */
    public static Provider getSUN() {
        return Security.getProvider("SUN") ;
    }
    
    /**
     * @return
     */
    public Crypto useBouncyCastle() {
        maybe_assert_jce();
        if ( !  (_prov instanceof BouncyCastleProvider)) {
//            _prov= new BouncyCastleProvider();
            _prov=Security.getProvider("BC") ;      
        }
        return this;
    }
    
    
    /**
     * @return
     */
    public Crypto useAten() {        
//        if ( !  (_prov instanceof sun.security.provider.Sun)) {
//        }
        _prov= Security.getProvider("SUN") ;            
        return this;
    }
    
    private KeyStore.PrivateKeyEntry loadPKCS12Key( File p12File, String password ) 
            throws KeyStoreException, NoSuchAlgorithmException
            , UnrecoverableEntryException, CertificateException, IOException    {
        
        KeyStore ks= KeyStore.getInstance(PKCS12, getProvider() );
        InputStream inp= new FileInputStream(p12File);
        char[] pwd= password.toCharArray();
        try        {
            ks.load(inp, pwd);
            return (PrivateKeyEntry) ks.getEntry( ks.aliases().nextElement() , new PasswordProtection(pwd));
        }
        finally {
            StreamUte.close(inp);
        }
    }    
        
    private KeyPair createKeyPair( String algo, int keyLength) throws NoSuchAlgorithmException     {
        KeyPairGenerator  kpg  = KeyPairGenerator.getInstance(algo, getProvider() );
        kpg.initialize( keyLength, getSecureRandom() );
        return kpg.generateKeyPair();
    }
    
    private Tuple createSSV1Cert(Provider pv, KeyPair keyPair, Date start, Date end, String dnStr,
            int keyLength, String algo) 
                throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException 
                ,SignatureException, CertificateException, NoSuchProviderException, GeneralSecurityException     {
        
        // generate self-signed cert
        X500Principal  dnName = new X500Principal(dnStr);
        PrivateKey prv= keyPair.getPrivate();
        PublicKey pub= keyPair.getPublic();
        X509Certificate cert;
        
        // self signed-> issuer is self
        JcaX509v1CertificateBuilder bdr= new JcaX509v1CertificateBuilder(dnName, getNextSerialNumber(),
                start,end, dnName, pub);
        ContentSigner cs;
        try {
            cs= new JcaContentSignerBuilder(algo).setProvider(pv).build(prv);
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        
        cert=new JcaX509CertificateConverter().setProvider(pv).getCertificate(bdr.build( cs));
        cert.checkValidity(new Date());
        cert.verify(pub);
        
        return new Tuple(cert, prv);
    }
    
    private Tuple createSSV3Cert(Provider pv, KeyPair keyPair, Date start, Date end, String dnStr,
            Certificate issuer,  PrivateKey issuerKey,
            int keyLength,   String algo) 
            throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException 
            ,SignatureException, CertificateException, NoSuchProviderException , GeneralSecurityException   {
        
        X500Principal  subject= new X500Principal(dnStr);
        PrivateKey prv= keyPair.getPrivate();
        PublicKey pub= keyPair.getPublic();
        X509Certificate cert, top= (X509Certificate) issuer;

        JcaX509v3CertificateBuilder bdr= new JcaX509v3CertificateBuilder(top, getNextSerialNumber(),  start,end, subject, pub);
        ContentSigner cs;
        try {
            cs=new JcaContentSignerBuilder(algo).setProvider(pv).build(issuerKey);
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        bdr.addExtension(X509Extension.authorityKeyIdentifier, false,
                new AuthorityKeyIdentifierStructure(top));
        bdr.addExtension(X509Extension.subjectKeyIdentifier, false,
                new SubjectKeyIdentifierStructure(pub));
        cert=new JcaX509CertificateConverter().setProvider(pv).getCertificate(bdr.build( cs));
        
        cert.checkValidity(new Date());
        cert.verify(top.getPublicKey());

        return new Tuple(cert,prv);
    }
    
    /**
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static void testJCEPolicy() throws NoSuchAlgorithmException, NoSuchPaddingException
    , InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // this function should fail if the non-restricted (unlimited-strength) jce files are not placed in jre-home
        KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
        kgen.init(256);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish" );

        Cipher cipher = Cipher.getInstance( "Blowfish" );
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        cipher.doFinal("This is just an example".getBytes());
    }

    private BigInteger getNextSerialNumber()     {
        Random r= new Random(new Date().getTime());
        long v= r.nextLong();
        v= Math.abs(v);
        return BigInteger.valueOf(v);
    }
    
    private byte[] fmtPEM(String top, String end, byte[] bits) throws IOException {
        ByteOStream baos= new ByteOStream();
        byte[] bb= new byte[1];
        int pos=0;
        baos.write( asBytes(top)) ;
        bits=Base64.encode(bits);
        for (int i=0; i < bits.length; ++i) {
            if (pos > 0 && (pos % 64) == 0) {
                baos.write( asBytes("\n"));
            }
            ++pos;
            bb[0]=bits[i];
            baos.write(bb);
        }
        baos.write( asBytes(end)) ;
        return baos.asBytes();        
    }
    
    private Crypto() {
        Security.addProvider( _prov=new BouncyCastleProvider() );        
    }
    
    private Provider maybe_assert_jce()    {
        
        if (!_jceTested)
        try {
            testJCEPolicy();
        }
        catch (Exception e) {
            System.err.println("JCE errors, probably due to jce policy not configured to be unlimited\n" + 
            "Download the unlimited jce policy files, and place them in JRE_HOME") ;
            System.exit(-99);
        }
        finally {
            _jceTested=true;            
        }
        
        return _prov;
    }
    
    @SuppressWarnings("unused")
    private static void main(String[] args) {
        try {
            // test code
            KeyStore ks= KeyStore.getInstance("PKCS12", new BouncyCastleProvider()) ;
            ks.load(CoreUte.rc2Stream("com/zotoh/crypto/zotoh.p12"), "Password1".toCharArray()) ;
            String nm= ks.aliases().nextElement();
            PrivateKeyEntry k= (PrivateKeyEntry) ks.getEntry( nm, new PasswordProtection("Password1".toCharArray())) ;
            ks= KeyStore.getInstance("JKS") ;
            ks.load(null, null);
            ks.setKeyEntry(nm, k.getPrivateKey(), "Password1".toCharArray(), k.getCertificateChain());
            ks.store(new FileOutputStream("w:/zotoh.jks"), "Password1".toCharArray()) ;
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
}
