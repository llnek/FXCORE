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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;

/**
 * Encryption algo constants. 
 *
 * @author kenl
 *
 */
public enum EncryptionAlgo {
    
    DES_EDE3_CBC(CMSAlgorithm.DES_EDE3_CBC),    
    RC2_CBC(CMSAlgorithm.RC2_CBC),         
    IDEA_CBC(CMSAlgorithm.IDEA_CBC),        
    CAST5_CBC(CMSAlgorithm.CAST5_CBC),       

    AES128_CBC(CMSAlgorithm.AES128_CBC),      
    AES192_CBC(CMSAlgorithm.AES192_CBC),      
    AES256_CBC(CMSAlgorithm.AES256_CBC),      

    CAMELLIA128_CBC(CMSAlgorithm.CAMELLIA128_CBC), 
    CAMELLIA192_CBC(CMSAlgorithm.CAMELLIA192_CBC), 
    CAMELLIA256_CBC(CMSAlgorithm.CAMELLIA256_CBC), 

    SEED_CBC(CMSAlgorithm.SEED_CBC),        

    DES_EDE3_WRAP(CMSAlgorithm.DES_EDE3_WRAP),   
    AES128_WRAP(CMSAlgorithm.AES128_WRAP),     
    AES256_WRAP(CMSAlgorithm.AES256_WRAP),     
    CAMELLIA128_WRAP(CMSAlgorithm.CAMELLIA128_WRAP), 
    CAMELLIA192_WRAP(CMSAlgorithm.CAMELLIA192_WRAP), 
    CAMELLIA256_WRAP(CMSAlgorithm.CAMELLIA256_WRAP), 
    SEED_WRAP(CMSAlgorithm.SEED_WRAP),       
  
    ECDH_SHA1KDF(CMSAlgorithm.ECDH_SHA1KDF);    
    
    public ASN1ObjectIdentifier getOID() { return _algo;}
    private ASN1ObjectIdentifier _algo;
    private EncryptionAlgo(ASN1ObjectIdentifier a) {
        _algo=a;
    }
    
}
