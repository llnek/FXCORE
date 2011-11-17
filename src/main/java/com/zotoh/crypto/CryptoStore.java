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

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.SeqNumGen;


/**
 * @author kenl
 *
 */
public abstract class CryptoStore {
    
    private Logger ilog() { return log = getLogger(CryptoStore.class);    }
    private transient Logger log= ilog();
    public Logger tlog() { return log==null ? ilog() : log;    }    
    
    protected KeyStore _store;
    private String _pwd;

    /**
     * @param bits
     * @param password
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    public void addKeyEntity( InputStream bits, String password ) 
                throws NoSuchAlgorithmException, UnrecoverableEntryException 
                ,KeyStoreException, CertificateException, IOException     {
        
        tstObjArg("entity-input-stream", bits);
        tstEStrArg("password", password);
        
        // we load the p12 content into an empty keystore, then extract the entry
        // and insert it into the current one.
        
        char[] ch= password.toCharArray();
        KeyStore tmp= createKeyStore();
        PrivateKeyEntry key;
        ProtectionParameter param = new PasswordProtection(ch) ;
   
        tmp.load(bits, ch);
        key= (PrivateKeyEntry) tmp.getEntry( tmp.aliases().nextElement(),  param );
        
        onNewKey( newAlias(), key, param);
    }
    
    /**
     * @param bits
     * @throws CertificateException
     * @throws KeyStoreException
     */
    public void addCertEntity( InputStream bits) throws CertificateException, KeyStoreException     {
        tstObjArg("entity-input-stream", bits) ;
        
        Certificate c= CertificateFactory.getInstance( "X.509").generateCertificate(bits);
        if (c instanceof X509Certificate) {
            _store.setCertificateEntry( newAlias() , c);
        }
    }
    
    /**
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public TrustManagerFactory getTrustManagerFactory() throws NoSuchAlgorithmException, KeyStoreException     {
        TrustManagerFactory m= TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()); 
        m.init( _store );
        return m;
    }
    
    
    /**
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     */
    public KeyManagerFactory getKeyManagerFactory() 
    throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException     {
        KeyManagerFactory m= KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
        m.init( _store,  _pwd.toCharArray() );
        return m;
    }
    
    
    /**
     * @return
     * @throws KeyStoreException
     */
    public Set<String> getCertAliases() throws KeyStoreException {
        Set<String> ret= ST(); 
        String alias;
        for (Enumeration<String> en = _store.aliases(); en.hasMoreElements(); ) {
            alias=en.nextElement();
            if ( _store.isCertificateEntry(alias)) {
                ret.add(alias);
            }
        }
        return ret;
    }
    
    
    /**
     * @return
     * @throws KeyStoreException
     */
    public Set<String> getKeyAliases() throws KeyStoreException {
        Set<String> ret= ST();
        String alias;
        for (Enumeration<String> en = _store.aliases(); en.hasMoreElements(); ) {
            alias=en.nextElement();
            if ( _store.isKeyEntry(alias)) {
                ret.add(alias);
            }
        }
        return ret;
    }
    
    
    /**
     * @param alias
     * @param password
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     */
    public PrivateKeyEntry getKeyEntity(String alias, String password)  
                throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException    {
        tstEStrArg("password", password);
        tstEStrArg("alias", alias);
        Object ent= _store.getEntry( alias, new PasswordProtection( password.toCharArray()) );
        return ent instanceof PrivateKeyEntry ? (PrivateKeyEntry) ent : null;
    }
    
    
    /**
     * @param alias
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     */
    public TrustedCertificateEntry getCertEntity(String alias)  
                throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException    {        
        tstEStrArg("alias", alias);
        Object ent= _store.getEntry( alias, null );
        return ent instanceof TrustedCertificateEntry ? (TrustedCertificateEntry) ent : null;
    }
    
    
    /**
     * @param alias
     * @throws KeyStoreException
     */
    public void removeEntity(String alias) throws KeyStoreException   {        
        if ( alias != null && _store.containsAlias(alias) ) {        
            _store.deleteEntry(alias);
        }
    }
    
    
    /**
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     */
    public Set<X509Certificate> getIntermediateCAs() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException    {
        return getCAs(true, false);
    }
    
    
    /**
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     */
    public Set<X509Certificate> getRootCAs() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException    {
        return getCAs(false, true);        
    }
    
    /**
     * returns a list of X509Certificates
     * 
     * @return
     * @throws Exception
     */
    public Set<X509Certificate> getTrustedCerts() throws Exception   {
        
        Set<X509Certificate> ret = ST();
        Object obj;
        String alias;
        
        for (Enumeration<String> en = _store.aliases(); en.hasMoreElements(); ) {
            alias= en.nextElement();
            if (_store.isCertificateEntry(alias)) {
                obj= _store.getEntry(alias, null);
                obj= ( (TrustedCertificateEntry) obj).getTrustedCertificate();
                if (obj instanceof X509Certificate) {
                    ret.add( (X509Certificate) obj);
                }
            }
        }
        
        return ret;
    }
    
    /**
     * @param file
     * @param password
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public void init( File file, String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {
        
        tstObjArg("file", file);        
        
        InputStream inp= null;
        try {
            init(inp= new FileInputStream(file), password);
        }
        finally {
            StreamUte.close(inp);
        }        
    }
    
    
    /**
     * @param inp
     * @param password
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public void init( InputStream inp, String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {        
        tstObjArg("input-stream", inp);        
        init(password);          
        _store.load ( inp, password.toCharArray() ) ;
    }
    
    
    /**
     * @param password
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public void init ( String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {     
        tstEStrArg("password", password);
        _store= createKeyStore() ;
        _pwd= password;
        _store.load ( null, _pwd.toCharArray() ) ;
    }
    
    
    /**
     * @return
     */
    public KeyStore getAndDetach() { 
        KeyStore ks= _store;
        _store=null;
        return ks;
    }
    
    /**
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    protected abstract KeyStore createKeyStore()  throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException;     
    
    
    /**
     * 
     */
    protected CryptoStore()    {
        Crypto.getInstance();
    }
    
    private Set<X509Certificate> getCAs( boolean tca, boolean root ) 
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException     {
        
        Set<X509Certificate> ret= ST();        
        String alias;
        Object obj;
        X509Certificate c;
        boolean matched;
        X500Principal issuer, subj;
        
        for (Enumeration<String> en = _store.aliases(); en.hasMoreElements(); ) {
            alias= en.nextElement();
            if ( _store.isCertificateEntry(alias)) {
                obj=((TrustedCertificateEntry)_store.getEntry(alias, null)).getTrustedCertificate();
                if (obj instanceof X509Certificate) {
                    c= (X509Certificate) obj;
                    issuer = c.getIssuerX500Principal();
                    subj = c.getSubjectX500Principal();
                    matched = issuer != null && issuer.equals(subj);                                    
                    if (root &&  !matched) { c= null; }
                    if (tca && matched) { c= null; }
                    if (c != null) ret.add(c);
                }
            }
        }
        
        return ret;
    }
        
    
    /**
     * @return
     */
    protected String newAlias()    {
        return "" + System.currentTimeMillis() + SeqNumGen.getInstance().next();
    }
        
    
    /**
     * @param alias
     * @param key
     * @param param
     * @throws KeyStoreException
     */
    private void onNewKey( String alias, PrivateKeyEntry key, ProtectionParameter param) throws KeyStoreException {
        Certificate[] cs= key.getCertificateChain();

        for (int i=0; i < cs.length; ++i) {
            _store.setCertificateEntry( newAlias(), cs[i]);
        }
        
        _store.setEntry( alias, key, param );        
    }
    
    
    
    
    
    
}
