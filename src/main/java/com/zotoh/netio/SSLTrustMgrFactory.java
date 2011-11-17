/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

import com.zotoh.core.util.Logger;


/**
 * @author kenl
 *
 */
public class SSLTrustMgrFactory extends TrustManagerFactorySpi {

    private static Logger _log=getLogger(SSLTrustMgrFactory.class);  
    public static Logger tlog() { return _log;  }    
	
    private static final TrustManager _mgr = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
        public void checkClientTrusted( X509Certificate[] chain, String authType) 
        				throws CertificateException {
        		tlog().warn("SkipCheck: CLIENT CERTIFICATE: {}" , chain[0].getSubjectDN());
        }

        public void checkServerTrusted( X509Certificate[] chain, String authType) 
        				throws CertificateException {
            tlog().warn("SkipCheck: SERVER CERTIFICATE: {}" , chain[0].getSubjectDN());
        }
    };

    /**
     * @return
     */
    public static TrustManager[] getTrustManagers() {
        return new TrustManager[] { _mgr };
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.TrustManagerFactorySpi#engineGetTrustManagers()
     */
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return getTrustManagers();
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.TrustManagerFactorySpi#engineInit(java.security.KeyStore)
     */
    @Override
    protected void engineInit(KeyStore keystore) throws KeyStoreException {
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.TrustManagerFactorySpi#engineInit(javax.net.ssl.ManagerFactoryParameters)
     */
    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
            throws InvalidAlgorithmParameterException {
    }
    
}
