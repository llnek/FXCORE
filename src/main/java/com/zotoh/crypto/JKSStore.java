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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


/**
 * @author kenl
 *
 */
public class JKSStore extends CryptoStore {
    
    /**
     * 
     */
    public JKSStore() {
        //tlog().debug("JKSStore: ctor()");
    }

    /* (non-Javadoc)
     * @see com.zotoh.crypto.CryptoStore#createKeyStore()
     */
    @Override
    protected KeyStore createKeyStore()  throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException     {
        KeyStore ks= KeyStore.getInstance("JKS", Crypto.getSUN());
        ks.load(null);
        return ks;
    }
    
    
}
