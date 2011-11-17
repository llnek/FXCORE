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

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.io.StreamUte.safeReset;
import static com.zotoh.core.util.CoreUte.asList;
import static com.zotoh.core.util.CoreUte.safeGetClzname;
import static com.zotoh.core.util.CoreUte.tstArgIsType;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.mail.DefaultAuthenticator;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSCompressedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.mail.smime.SMIMECompressedGenerator;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.mime.MimeUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * Helper functions related to Crypto. 
 *
 * @author kenl
 *
 */
public enum CryptoUte {
;

    private static Logger ilog() { return _log = getLogger(CryptoUte.class);    }
    private static Logger _log= ilog();
    public static Logger tlog() { return _log==null ? ilog() : _log;    }    
    static {      inizMapCap(); }    
    
    /**
     * @param inp
     * @return
     * @throws MessagingException
     */
    public static MimeMessage newMimeMsg(InputStream inp) throws MessagingException     {
        tstObjArg("inp-stream", inp);
        return new MimeMessage(newSession(), inp);
    }

    
    /**
     * @param user
     * @param pwd
     * @return
     */
    public static MimeMessage newMimeMsg(String user, String pwd)     {
        return new MimeMessage(newSession(user, pwd));
    }

    
    /**
     * @return
     */
    public static MimeMessage newMimeMsg()     {
        return   new MimeMessage(newSession());
    }

    
    /**
     * @param user
     * @param pwd
     * @return
     */
    public static Session newSession(String user, String pwd)     {
        return Session.getInstance(System.getProperties(),
                isEmpty(user) ?  null : new DefaultAuthenticator(user, pwd) );
    }

    
    /**
     * @return
     */
    public static Session newSession()       {       
        return newSession("","");    
    }

	
    /**
     * @param obj
     * @return
     * @throws Exception
     */
    public static boolean isSigned(Object obj) throws Exception    {
        InputStream inp= MimeUte.maybeAsStream(obj);

        if (inp==null) {
            if (obj instanceof Multipart) {
                return MimeUte.isSigned( ((Multipart)obj).getContentType());
            }
            throw new Exception("Invalid content: " + safeGetClzname(obj));
        }
        // else
        try {
            return isSigned(newMimeMsg(inp).getContentType());
        }
        finally {
            safeReset(inp);
        }
    }

    
    /**
     * @param obj
     * @return
     * @throws Exception
     */
    public static boolean isCompressed(Object obj) throws Exception    {
        InputStream inp= MimeUte.maybeAsStream(obj);
        
        if (inp==null) {         
            if (obj instanceof Multipart) {            
                return MimeUte.isCompressed(((Multipart) obj).getContentType());
            }
            if (obj instanceof BodyPart) {            
                return MimeUte.isCompressed(((BodyPart) obj).getContentType());
            }
            throw new Exception("Invalid content: " + safeGetClzname(obj));
        }
        // else
        try {
            return MimeUte.isCompressed(newMimeMsg(inp).getContentType());
        }
        finally {
            safeReset(inp);
        }
    }

    
    /**
     * @param obj
     * @return
     * @throws Exception
     */
    public static boolean isEncrypted(Object obj) throws Exception    {
        InputStream inp= MimeUte.maybeAsStream(obj);
        
        if ( inp==null) {
            if (obj instanceof Multipart) {            
                return MimeUte.isEncrypted( ((Multipart)obj).getContentType());
            }            
            if (obj instanceof BodyPart) {            
                return MimeUte.isEncrypted( ((BodyPart)obj).getContentType());
            }
            throw new Exception("Invalid content: " + safeGetClzname(obj));
        }
        // else
        try {
            return MimeUte.isEncrypted(newMimeMsg(inp).getContentType());
        }
        finally {
            safeReset(inp);
        }
    }

    
    /**
     * @param cType
     * @param deFcs
     * @return
     */
    public static String getCharset(String cType, String deFcs)     {
        String cs = getCharset(cType);
        return isEmpty(cs) ? MimeUtility.javaCharset(deFcs) : cs;
    }

    
    /**
     * @param cType
     * @return
     */
    public static String getCharset(String cType)     {
        String rc= null;
        
        if ( ! isEmpty(cType))
        try   {
            String charset = (new ContentType(cType)).getParameter("charset");
            rc= MimeUtility.javaCharset(charset);
        }
        catch (Exception e) {        
            tlog().warn("",e);
        }
        
        return rc;
    }
    
    
    /**
     * @param key
     * @param certs
     * @param algo
     * @param mp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws MessagingException
     * @throws CertificateEncodingException
     * @throws GeneralSecurityException
     */
    public static Multipart smimeDigSig(PrivateKey key, Certificate[] certs, SigningAlgo algo, Multipart mp) 
        throws NoSuchAlgorithmException,  CertStoreException 
        ,InvalidAlgorithmParameterException, MessagingException
        , CertificateEncodingException , GeneralSecurityException    {
        
        tstObjArg("certificate(s)", certs) ;
        tstObjArg("private-key", key) ;
        tstObjArg("multipart", mp) ;
        tstObjArg("algo", algo) ;
        
        SMIMESignedGenerator gen= makeSignerGentor(key, certs, algo) ;
        MimeMessage mm= newMimeMsg();                
        mm.setContent(mp);
        try {
            mp= gen.generate(mm, Crypto.getInstance().getProvider());
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
/*                
        MimeBodyPart dummy= new MimeBodyPart();
        dummy.setContent(mp);
        mp= gen.generate(dummy, PROV);
*/        
        return mp;
    }

    
    /**
     * @param key
     * @param certs
     * @param algo
     * @param bp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws CertificateEncodingException
     * @throws GeneralSecurityException
     */
    public static Multipart smimeDigSig(PrivateKey key, Certificate[] certs, SigningAlgo algo, BodyPart bp) 
    throws NoSuchAlgorithmException 
    ,CertStoreException, InvalidAlgorithmParameterException
    ,CertificateEncodingException, GeneralSecurityException     {
        
        tstArgIsType("bodypart", bp, MimeBodyPart.class) ;
        tstObjArg("certificate(s)", certs) ;
        tstObjArg("private-key", key) ;
        tstObjArg("algo", algo) ;
        
        try {
            return makeSignerGentor(key, certs, algo).generate( (MimeBodyPart) bp, Crypto.getInstance().getProvider());
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
    }

    
    /**
     * @param key
     * @param part
     * @return
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static StreamData smimeDecrypt(PrivateKey key, BodyPart part) throws MessagingException, 
    GeneralSecurityException, IOException     {
        
        tstArgIsType("bodypart", part, MimeBodyPart.class) ;
        tstObjArg("private-key", key) ;
        CMSTypedStream cms=null;        
        try {
            SMIMEEnveloped env= new SMIMEEnveloped( (MimeBodyPart) part);
            cms= smime_decrypt(key, env);        
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        if (cms == null) { throw new GeneralSecurityException("Failed to decrypt: no matching decryption key"); }
        //else
        return readStream(cms.getContentStream());
    }

    private static CMSTypedStream smime_decrypt(PrivateKey key, SMIMEEnveloped env) 
        throws MessagingException, GeneralSecurityException, IOException     {
        
        Recipient r= new JceKeyTransEnvelopedRecipient(key)
        .setProvider(Crypto.getInstance().getProvider());
        
        for (Object obj : env.getRecipientInfos().getRecipients()) {                
            try    {
                return ((RecipientInformation) obj).getContentStream(r); 
            }
            catch (CMSException e)                     
            {}
        }

        return null;
    }
    

    /**
     * @param keys
     * @param msg
     * @return
     * @throws GeneralSecurityException
     * @throws MessagingException
     * @throws IOException
     */
    public static StreamData smimeDecryptAsStream(PrivateKey[] keys, MimeMessage msg) 
        throws GeneralSecurityException, MessagingException, IOException     {
        
        tstObjArg("mime-message", msg) ;
        tstObjArg("private-key(s)", keys) ;
        
        CMSTypedStream cms= null;
        SMIMEEnveloped env;
        try {
            env=new SMIMEEnveloped(msg);
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        
        for (int n=0; n < keys.length; ++n) {                    
            cms=smime_decrypt(keys[n],env);
            if (cms != null) { break; }
            cms=null;
        }
        
        if (cms == null) { throw new GeneralSecurityException("Failed to decrypt: no matching decryption key"); }
        //else
        return readStream(cms.getContentStream()) ;
    }

    
    /**
     * @param mp
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws GeneralSecurityException
     */
    public static Object peekSmimeSignedContent(Multipart mp) 
        throws IOException, MessagingException, GeneralSecurityException     {
        
        tstArgIsType("mulitpart", mp, MimeMultipart.class) ;
        try {
            return  new SMIMESignedParser( (MimeMultipart) mp, 
                getCharset( mp.getContentType(), "binary")).getContent().getContent();
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
    }
    
    
    /**
     * @param mp
     * @param certs
     * @param cte
     * @return
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public static Tuple verifySmimeDigSig( Multipart mp,  Certificate[] certs,  String cte) 
        throws MessagingException, GeneralSecurityException, IOException, CertificateEncodingException     {
        
        tstArgIsType("multipart", mp, MimeMultipart.class) ;
        tstObjArg("certs", certs) ;
        
        MimeMultipart mmp= (MimeMultipart) mp;
        SMIMESigned sc;          
        SignerInformation si;        
        byte[] digest= null;
        
        try {
            sc= isEmpty(cte) ? new SMIMESigned( mmp) : new SMIMESigned(mmp, cte);
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        
        Provider prov= Crypto.getInstance().getProvider();
        Store s= new JcaCertStore(asList(true, certs));
        Collection<?> c;
        JcaSimpleSignerInfoVerifierBuilder bdr;
        for (Object obj : sc.getSignerInfos().getSigners())            
        try    {
            si= (SignerInformation) obj;
            c=s.getMatches(si.getSID());
            for (Iterator<?> it= c.iterator(); it.hasNext();) {
            		bdr=new JcaSimpleSignerInfoVerifierBuilder().setProvider(prov);
	        		if ( si.verify( bdr.build( (X509CertificateHolder) it.next()) )) {
	        			digest=si.getContentDigest();
	        			break;
	        		}            	
            } 
            if (digest != null) {break; }
        }
        catch (Exception e)
        {}
            
        if ( digest == null) { throw new GeneralSecurityException("Failed to verify signature: no matching certificate" ); }
        //else
        return new Tuple(sc.getContentAsMimeMessage(newSession()).getContent(), digest);
    }
    
    
    /**
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecureRandom newRandom() throws NoSuchAlgorithmException     {
        return Crypto.getInstance().getSecureRandom();     
    }
    
    
    /**
     * @param mp
     * @param certs
     * @return
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public static Tuple verifySmimeDigSig( Multipart mp,  Certificate[] certs)   
        throws MessagingException,  GeneralSecurityException, IOException, CertificateEncodingException     {
        return verifySmimeDigSig(mp, certs, "");
    }

    
    /**
     * @param part
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws GeneralSecurityException
     */
    public static StreamData decompress(BodyPart part) throws IOException 
    ,MessagingException, GeneralSecurityException     {
        return decompressAsStream( part ==null ? null : part.getInputStream());
    }

    
    /**
     * @param inp
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static StreamData decompressAsStream(InputStream inp) throws GeneralSecurityException, IOException     {
        CMSTypedStream cms= null; 
        StreamData r= null;
        
        if (inp != null)
        try  {
            cms= new CMSCompressedDataParser(inp).getContent(new ZlibExpanderProvider());            
            if (cms==null) { throw new GeneralSecurityException("Failed to decompress stream: corrupted content"); }
            r=readStream(cms.getContentStream());        
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        
        return r != null ? r : new StreamData(); 
    }

    
    /**
     * @param cert
     * @param algo
     * @param bp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws GeneralSecurityException
     */
    public static MimeBodyPart smimeEncrypt(Certificate cert, EncryptionAlgo algo, BodyPart bp) 
        throws NoSuchAlgorithmException
        , CertificateEncodingException, GeneralSecurityException    {        
        
        tstArgIsType( "body-part" , bp, MimeBodyPart.class) ;
        tstObjArg("cert", cert);
        tstObjArg("algo", algo);
        
        SMIMEEnvelopedGenerator gen= new SMIMEEnvelopedGenerator();
        Provider prov=Crypto.getInstance().getProvider();
        RecipientInfoGenerator g;
        try {
            g=new JceKeyTransRecipientInfoGenerator((X509Certificate) cert).setProvider(prov);
            gen.addRecipientInfoGenerator(g );        
            return gen.generate((MimeBodyPart) bp, 
                    new JceCMSContentEncryptorBuilder(algo.getOID()).setProvider(prov).build());
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
        
    }

    
    /**
     * @param cert
     * @param algo
     * @param msg
     * @return
     * @throws Exception
     */
    public static MimeBodyPart smimeEncrypt(Certificate cert, EncryptionAlgo algo, MimeMessage msg) 
    throws Exception     {
        
        tstObjArg( "mime-message" , msg) ;
        tstObjArg("cert", cert);
        tstObjArg("algo", algo);

        SMIMEEnvelopedGenerator gen= new SMIMEEnvelopedGenerator();
        Provider prov=Crypto.getInstance().getProvider();
        RecipientInfoGenerator g=new JceKeyTransRecipientInfoGenerator((X509Certificate) cert).setProvider(prov);
        gen.addRecipientInfoGenerator(g );        

        return gen.generate(msg, 
        		new JceCMSContentEncryptorBuilder(algo.getOID()).setProvider(prov).build());
    }

    
    /**
     * @param cert
     * @param algo
     * @param mp
     * @return
     * @throws MessagingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws GeneralSecurityException
     * @throws CertificateEncodingException
     */
    public static MimeBodyPart smimeEncrypt( Certificate cert, EncryptionAlgo algo, Multipart mp) 
        throws MessagingException, NoSuchAlgorithmException 
        ,NoSuchProviderException, GeneralSecurityException
        , CertificateEncodingException     {
        
        tstObjArg("multi-part", mp) ;
        tstObjArg("cert", cert) ;
        tstObjArg("algo", algo) ;
        
        try {
            
            SMIMEEnvelopedGenerator gen= new SMIMEEnvelopedGenerator();
            Provider prov=Crypto.getInstance().getProvider();
            RecipientInfoGenerator g=new JceKeyTransRecipientInfoGenerator((X509Certificate) cert).setProvider(prov);
            gen.addRecipientInfoGenerator(g );        
            MimeMessage mm= newMimeMsg();        
            mm.setContent(mp);

            return gen.generate(mm, 
                    new JceCMSContentEncryptorBuilder(algo.getOID()).setProvider(prov).build());
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
        catch (CMSException e) {            
            throw new GeneralSecurityException(e);
        }
    }

    
    /**
     * @param contentType
     * @param msg
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws GeneralSecurityException
     */
    public static MimeBodyPart compressContent(String contentType, StreamData msg) 
    throws IOException, MessagingException, GeneralSecurityException    {
        
        tstEStrArg("content-type", contentType) ;
        tstObjArg("input-content", msg) ;
        
        SMIMECompressedGenerator gen= new SMIMECompressedGenerator();
        MimeBodyPart bp= new MimeBodyPart();
        SmDataSource ds;
        
        if (msg.isDiskFile()) {            
            ds= new SmDataSource(msg.getFileRef(), contentType) ;
        }
        else {            
            ds = new SmDataSource(msg.getBytes(), contentType) ;
        }
        
        bp.setDataHandler( new DataHandler(ds) );
        try {
            return  gen.generate(bp, SMIMECompressedGenerator.ZLIB);
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
    }

    
    /**
     * @param msg
     * @return
     * @throws GeneralSecurityException
     */
    public static MimeBodyPart compressContent(MimeMessage msg) throws GeneralSecurityException     {
        tstObjArg("mime-message", msg) ;        
        try {
            return new SMIMECompressedGenerator().generate(msg, SMIMECompressedGenerator.ZLIB);
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
    }

    
    /**
     * @param cType
     * @param cte
     * @param contentLoc
     * @param cid
     * @param msg
     * @return
     * @throws MessagingException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static MimeBodyPart compressContent(String cType, String cte, 
            String contentLoc, String cid, StreamData msg) 
    throws MessagingException, IOException, GeneralSecurityException    {
        
        tstEStrArg("content-type", cType) ;
        tstEStrArg("content-id", cid) ;
        tstObjArg("input-content", msg) ;
        
        SMIMECompressedGenerator gen= new SMIMECompressedGenerator();
        MimeBodyPart bp= new MimeBodyPart();
        SmDataSource ds; 
        
        if (msg.isDiskFile()) {            
            ds = new SmDataSource(msg.getFileRef(), cType ) ;
        }
        else {            
            ds = new SmDataSource(msg.getBytes(), cType ) ;
        }

        if ( ! isEmpty(contentLoc))        {
            bp.setHeader("content-location", contentLoc);
        }
        
        try {
            bp.setHeader("content-id", cid);
            bp.setDataHandler( new DataHandler( ds) );
            bp= gen.generate(bp, SMIMECompressedGenerator.ZLIB);
        }
        catch (SMIMEException e) {
            throw new GeneralSecurityException(e);
        }
        
        if (true) {
            int pos= cid.lastIndexOf(">");
            if (pos >= 0) { cid= cid.substring(0,pos) + "--z>"; }
            else
            { cid= cid + "--z"; }
        }

        if ( !isEmpty(contentLoc)) { bp.setHeader( "content-location", contentLoc); }
        bp.setHeader( "content-id", cid);

        // always base64
        cte="base64";

        if ( ! isEmpty(cte)) {
            bp.setHeader( "content-transfer-encoding", cte);            
        }

        return bp;
    }

    
    /**
     * @param key
     * @param certs
     * @param algo
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws CertStoreException
     * @throws IOException
     * @throws CertificateEncodingException
     * @throws GeneralSecurityException
     */
    public static byte[] pkcsDigSig(PrivateKey key, Certificate[] certs, SigningAlgo algo, StreamData data ) 
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException 
    ,CertStoreException,  IOException
    , CertificateEncodingException, GeneralSecurityException    {
        
        tstObjArg("input-content", data) ;
        tstObjArg("private-key", key) ;
        
        CMSSignedDataGenerator gen=new CMSSignedDataGenerator();
        Provider prov= Crypto.getInstance().getProvider();
        List<Certificate> lst= asList(true,certs);
        CMSTypedData cms;
        X509Certificate cert= (X509Certificate) lst.get(0);
        
        try {        
            ContentSigner cs = new JcaContentSignerBuilder(algo.toString())
            .setProvider(prov).build(key);
    
            JcaSignerInfoGeneratorBuilder bdr = new JcaSignerInfoGeneratorBuilder(
            		new JcaDigestCalculatorProviderBuilder().setProvider(prov).build());
            bdr.setDirectSignature(true);
    
            gen.addSignerInfoGenerator(bdr.build(cs, cert));
            gen.addCertificates( new JcaCertStore(lst));
            
            if ( data.isDiskFile()) {
                cms=new CMSProcessableFile( data.getFileRef());
            }
            else {
                cms= new CMSProcessableByteArray( data.getBytes());
            }
    
            return gen.generate(cms, false).getEncoded();
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
        catch (CMSException e) {            
            throw new GeneralSecurityException(e);
        }
        
    }

    
    /**
     * @param cert
     * @param data
     * @param signature
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public static byte[] verifyPkcsDigSig( Certificate cert, StreamData data, byte[] signature) 
    throws GeneralSecurityException, IOException, CertificateEncodingException    {
        
        tstObjArg("digital-signature", signature) ;
        tstObjArg("cert", cert) ;
        tstObjArg("input-content", data) ;
                
        Provider prov= Crypto.getInstance().getProvider() ;
        SignerInformation si;
        CMSProcessable cproc;
        CMSSignedData cms;
        byte[] digest;
        
        if ( data.isDiskFile()) {
            cproc= new CMSProcessableFile( data.getFileRef());
        }
        else {
            cproc= new CMSProcessableByteArray( data.getBytes());
        }
        
        try {
            cms=new CMSSignedData(cproc, signature);
            digest= null;
        }
        catch (CMSException e) {
            throw new GeneralSecurityException(e);
        }
        
        List<Certificate> cl= LT();
        cl.add(cert);
        Store s= new JcaCertStore(cl);        
        Collection<?> c;
        JcaSimpleSignerInfoVerifierBuilder bdr;
        
        for (Object obj : cms.getSignerInfos().getSigners())            
        try    {
            si= (SignerInformation) obj;
            c=s.getMatches(si.getSID());
            for (Iterator<?> it= c.iterator(); it.hasNext();) {
            		bdr=new JcaSimpleSignerInfoVerifierBuilder().setProvider(prov);
	        		if ( si.verify( bdr.build( (X509CertificateHolder) it.next()) )) {
	        			digest=si.getContentDigest();
	        			break;
	        		}            	
            } 
            if (digest != null) {break; }
        }
        catch (Exception e)
        {}
        
        if (digest==null) { throw new GeneralSecurityException("Failed to decode signature: no matching certificate"); }
        // else
        return digest;
    }

    
    /**
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA1FingerPrint(byte[] data) throws NoSuchAlgorithmException     {
        return getFingerPrint(data, "SHA-1");
    }

    
    /**
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5FingerPrint(byte[] data) throws NoSuchAlgorithmException     {
        return getFingerPrint(data, "MD5");
    }

    @SuppressWarnings("unused")
    private static String getSigningAlgoAsString(String algo)     {        
        if ("SHA-512".equals(algo)) return SMIMESignedGenerator.DIGEST_SHA512;
        if ("SHA-1".equals(algo)) return SMIMESignedGenerator.DIGEST_SHA1;
        if ("MD5" .equals(algo)) return SMIMESignedGenerator.DIGEST_MD5;        
        throw new IllegalArgumentException("Unsupported signing algo:  " + algo) ;
    }

    /**
     * @param privateKeyBits
     * @param pwd
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    public static Tuple getCertDesc(byte[] privateKeyBits, String pwd) 
    throws NoSuchAlgorithmException, UnrecoverableEntryException 
    ,KeyStoreException, CertificateException, IOException     {
        
        tstObjArg("private-key-bytes", privateKeyBits) ;
        tstEStrArg("password", pwd) ;
        
        return getCertDesc( bitsToKey(privateKeyBits, pwd).getCertificate() );
    }

	
    /**
     * @param algo
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static MessageDigest newDigestInstance(String algo) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algo);
    }

    
    /**
     * @param certBits
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableEntryException
     * @throws IOException
     */
    public static Tuple getCertDesc(byte[] certBits) 
    throws KeyStoreException, NoSuchAlgorithmException, 
    CertificateException, UnrecoverableEntryException, IOException     {
        tstObjArg("cert-bytes", certBits) ;        
        return getCertDesc( bitsToCert( certBits).getTrustedCertificate() );
    }

    
    /**
     * @param cert
     * @return
     */
    public static Tuple getCertDesc( Certificate cert)     {
        
        tstArgIsType("cert", cert, X509Certificate.class) ;
        
        X509Certificate x509= (X509Certificate) cert;
        X500Principal issuer= x509.getIssuerX500Principal();
        X500Principal subj= x509.getSubjectX500Principal();
        Date vs = x509.getNotBefore();
        Date ve = x509.getNotAfter();
        
        return new Tuple(subj, issuer, vs, ve);
    }

    
    /**
     * @param os
     */
    public static void dbgProviderProps(PrintStream os)     {
        try        {            
            Crypto.getInstance().getProvider().list(os) ;
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    
    /**
     * @param privateKeyBits
     * @param pwd
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    public static PrivateKeyEntry bitsToKey(byte[] privateKeyBits, String pwd) 
    throws NoSuchAlgorithmException, UnrecoverableEntryException 
    ,KeyStoreException, CertificateException, IOException     {
        tstObjArg("privatekey-bits", privateKeyBits) ;
        tstEStrArg("password", pwd) ;
        CryptoStore cs= new PKCSStore();
        cs.init("xxx");
        cs.addKeyEntity(asStream(privateKeyBits), pwd);
        return cs.getKeyEntity( cs.getKeyAliases().iterator().next(), pwd);
    }
    
    
    /**
     * @param certBits
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableEntryException
     */
    public static TrustedCertificateEntry bitsToCert(byte[] certBits) 
    throws KeyStoreException, NoSuchAlgorithmException, CertificateException 
    ,IOException, UnrecoverableEntryException     {
        tstObjArg("cert-bits", certBits) ;
        CryptoStore cs= new PKCSStore();
        cs.init("xxx");
        cs.addCertEntity( asStream( certBits));
        return cs.getCertEntity( cs.getCertAliases().iterator().next() ); 
    }
    
    
    /**
     * @param bits
     * @param pwd
     * @return
     * @throws Exception
     */
    public static boolean tstPKeyValid(byte[] bits, String pwd) throws Exception     {
        tstObjArg("privatekey-bits", bits) ;
        tstEStrArg("password", pwd) ;        
        
        PrivateKeyEntry k= bitsToKey(bits, pwd);
        X509Certificate c= null;
        
        if (k != null) {
            c= (X509Certificate) k.getCertificate();
        }
        
        return tstCertValid(c);
    }

    
    /**
     * @param bits
     * @return
     * @throws Exception
     */
    public static boolean tstCertValid(byte[] bits) throws Exception     {
        
        tstObjArg("cert-bits", bits) ;
        
        TrustedCertificateEntry t= bitsToCert(bits);
        X509Certificate c= null;
        
        if (t != null) {
            c= (X509Certificate) t.getTrustedCertificate();
        }
        
        return tstCertValid(c);
    }

    
    /**
     * @param x
     * @return
     */
    public static boolean tstCertValid(X509Certificate x)     {
        
        tstObjArg("cert", x) ;
        
        boolean ok=false;        
        try        {
            x.checkValidity(new Date());
            ok=true;
        }
        catch (Exception e) {            
        }
            
        return ok;        
    }

   	
	/**
	 * @param certs
	 * @return
	 */
	public static Certificate[] toCerts(TrustedCertificateEntry[] certs) {
		Certificate[] cs= new Certificate[0];
		if (certs != null) {
			for (int i=0; i < certs.length; ++i) {
				cs[i] = certs[i].getTrustedCertificate();
			}
		}
		return cs;
	}
 
	
	/**
	 * @param keys
	 * @return
	 */
	public static PrivateKey[] toPKeys(PrivateKeyEntry[] keys) {
		PrivateKey[] ks= new PrivateKey[0];
		if (keys != null) {
			for (int i=0; i < keys.length; ++i) {
				ks[i] = keys[i].getPrivateKey();
			}
		}
		return ks;
	}
	
    private static SMIMESignedGenerator makeSignerGentor(PrivateKey key, Certificate[] certs, SigningAlgo algo) 
    throws CertStoreException, NoSuchAlgorithmException 
    ,InvalidAlgorithmParameterException
    , GeneralSecurityException, CertificateEncodingException     {
        
        SMIMESignedGenerator gen= new SMIMESignedGenerator("base64");
        List<Certificate> lst= asList(true,certs);

        ASN1EncodableVector         signedAttrs = new ASN1EncodableVector();
        SMIMECapabilityVector       caps = new SMIMECapabilityVector();

        caps.addCapability(SMIMECapability.dES_EDE3_CBC);
        caps.addCapability(SMIMECapability.rC2_CBC, 128);
        caps.addCapability(SMIMECapability.dES_CBC);

        signedAttrs.add(new SMIMECapabilitiesAttribute(caps));

        X509Certificate x0= (X509Certificate) certs[0] ;
        X509Certificate issuer=x0;
        X500Principal issuerDN;
        
        if (certs.length > 1) {
            issuer= (X509Certificate) certs[1];
        }
        
        issuerDN= issuer.getSubjectX500Principal();                
        x0= (X509Certificate) certs[0] ;                
        
        //
        // add an encryption key preference for encrypted responses -
        // normally this would be different from the signing certificate...
        //
        
        IssuerAndSerialNumber   issAndSer = new IssuerAndSerialNumber(
                X500Name.getInstance(issuerDN.getEncoded()),
                x0.getSerialNumber());
        Provider prov= Crypto.getInstance().getProvider();
        
        signedAttrs.add(new SMIMEEncryptionKeyPreferenceAttribute(issAndSer));
                
        try {
            JcaSignerInfoGeneratorBuilder bdr = 
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider(prov).build());
            bdr.setDirectSignature(true);
    
            ContentSigner cs = new JcaContentSignerBuilder(algo.toString())
            .setProvider(prov).build(key);
            
            bdr.setSignedAttributeGenerator(
            new DefaultSignedAttributeTableGenerator(new AttributeTable(signedAttrs)));
            
            gen.addSignerInfoGenerator(bdr.build(cs, x0));
            gen.addCertificates( new JcaCertStore(lst));        
            
            return gen;
        }
        catch (OperatorCreationException e) {
            throw new GeneralSecurityException(e);
        }
    }
    
    private static String getFingerPrint(byte[] data, String algo) throws NoSuchAlgorithmException     {
        
        MessageDigest md5 = MessageDigest.getInstance(algo);        
        StringBuilder ret = new StringBuilder(256);
        byte[] hash= md5.digest(data);
        int tail= hash.length-1;
        String n;
        
        for(int i = 0; i < hash.length; ++i)        {
            n = Integer.toString((hash[i]&0xff), 16).toUpperCase();
            ret.append(n.length() == 1 ? ("0"+n) :n).append(   i != tail ? ":" : ""  );
        }
        
        return ret.toString();
    }

    private static void inizMapCap()    {
        MailcapCommandMap mc=  (MailcapCommandMap) 
        CommandMap.getDefaultCommandMap();

        mc.addMailcap("application/pkcs7-signature;; " +
		"x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        
        mc.addMailcap("application/pkcs7-mime;; " + 
        "x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        
        mc.addMailcap("application/x-pkcs7-signature;; " + "" +
		"x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        
        mc.addMailcap("application/x-pkcs7-mime;; " + 
        "x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        
        mc.addMailcap("multipart/signed;; " + 
        "x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
        
    }
    

    
}
