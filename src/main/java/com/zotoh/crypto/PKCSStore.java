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

import static com.zotoh.core.util.CoreUte.tstObjArg;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;


/**
 * @author kenl
 *
 */
public class PKCSStore extends CryptoStore {
    
    /**
     * @param bits
     * @throws CertificateException
     * @throws KeyStoreException
     */
    public void addPKCS7Entity( InputStream bits) throws CertificateException, KeyStoreException     {        
        tstObjArg("entity-input-stream", bits) ;
        
        Collection<? extends Certificate> certs= CertificateFactory.getInstance( "X.509").generateCertificates(bits) ;        
        for (Certificate c : certs) {
            _store.setCertificateEntry( newAlias(), c );            
        }
    }
    
	
    
    /**
     * 
     */
    public PKCSStore() {        
        //tlog().debug("PKCSStore: ctor()");
    }

    /* (non-Javadoc)
     * @see com.zotoh.crypto.CryptoStore#createKeyStore()
     */
    @Override
    protected KeyStore createKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException     {
        KeyStore ks= KeyStore.getInstance("PKCS12", 
                Crypto.getBouncyCastle());
        ks.load(null);
        return ks;
    }
    
    // -------------------------------------------- private ------------------------------------------------
    
}
