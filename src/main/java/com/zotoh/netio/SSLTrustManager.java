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

import static com.zotoh.core.util.CoreUte.isNilArray;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.zotoh.crypto.CryptoStore;

/**
 * @author kenl
 *
 */
public class SSLTrustManager implements X509TrustManager {
    
    private X509TrustManager _def;
    
    /**
     * @param cs
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     */
    public SSLTrustManager(CryptoStore cs) 
    throws NoSuchAlgorithmException, KeyStoreException, IOException    {
        super();  iniz(cs);
    }

    
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
    throws CertificateException    {
        
        if ( ! isNilArray(chain))
        try        {
            _def.checkClientTrusted(chain, authType);
        }
        catch (Exception e)
        {}
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
    throws CertificateException    {
        if ( !isNilArray(chain))
        try        {
            _def.checkClientTrusted(chain, authType);
        }
        catch (Exception e) 
        {}
    }

    
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers()    {
        return _def.getAcceptedIssuers();
    }

    private void iniz(CryptoStore cs) 
    throws NoSuchAlgorithmException, KeyStoreException, IOException     {
        
        TrustManager[] tms= cs.getTrustManagerFactory().getTrustManagers();
        
        if ( ! isNilArray(tms)) {            
            for (int i=0; i < tms.length; ++i) {
                if (tms[i] instanceof X509TrustManager) {
                    _def= (X509TrustManager) tms[i];
                    break;
                }
            }            
        }
                               
        if (_def==null) throw new IOException("No SSL TrustManager available");
    }
    
}
