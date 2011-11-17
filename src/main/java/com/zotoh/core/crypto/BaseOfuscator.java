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

package com.zotoh.core.crypto;

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.CoreUte.*;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import com.zotoh.core.util.Logger;

import com.zotoh.core.util.CoreVars;

/**
 * @author kenl
 *
 */
public abstract class BaseOfuscator implements CoreVars {
    
    private Logger ilog() {  return _log=getLogger(BaseOfuscator.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    

    private static final String KEY= "ed8xwl2XukYfdgR2aAddrg0lqzQjFhbs";
    private static final String T3_DES= "TripleDES";
    //AES/ECB/PKCS5Padding/TripleDES
    private static final String ALGO= T3_DES; // default javax supports this
    private static byte[] _key;
    static {    
        setKey( Base64.decodeBase64(KEY));
    }
    
    /**
     * Decrypt the given text.
     * 
     * @param encryptedText
     * @return
     * @throws Exception
     */
    public abstract String unobfuscate(String encryptedText) throws Exception;
    
    /**
     * Encrypt the given text string.
     * 
     * @param clearText
     * @return
     * @throws Exception
     */
    public abstract String obfuscate(String clearText) throws Exception;
        
    /**
     * Set the encryption key for future obfuscation operations.  Typically this is
     * called once only at the start of the main application.
     * 
     * @param key
     */
    public static void setKey(byte[] key )      {
        tstNEArray("enc-key",key);
        int len= key.length;
        if (T3_DES.equals(ALGO) ) {
            if (key.length < 24) {
                throw new IllegalArgumentException("Encryption key length must be 24, using TripleDES");
            }
            if (key.length > 24)
            { len=24; }
        }
        _key = Arrays.copyOfRange(key, 0, len);
    }
    
    /**
     * 
     */
    protected BaseOfuscator()
    {}    
    
    /**
     * @return
     */
    protected String getAlgo() {        return ALGO;    }
        
    /**
     * @return
     */
    protected byte[] getKey() {        return _key;    }
    
}
