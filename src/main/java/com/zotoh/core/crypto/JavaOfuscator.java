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

import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.StrUte.isEmpty;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.zotoh.core.io.ByteOStream;

import javax.crypto.spec.SecretKeySpec;

/**
 * Obfuscation using SUN-java.
 *  
 * @author kenl
 *
 */
public final class JavaOfuscator extends BaseOfuscator  {
        
    /* (non-Javadoc)
     * @see com.zotoh.core.crypto.BaseOfuscator#unobfuscate(java.lang.String)
     */
    public String unobfuscate(String encoded) throws Exception      {
        return decrypt(encoded) ;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.crypto.BaseOfuscator#obfuscate(java.lang.String)
     */
    public String obfuscate(String clearText) throws Exception       {
        return encrypt(clearText) ;
    }

    /**
     * 
     */
    public JavaOfuscator() 
    {}
    
    private String encrypt(String clearText) throws Exception      {
        if (isEmpty(clearText)) { return clearText; }
        Cipher c= getCipher(Cipher.ENCRYPT_MODE);        
        ByteOStream baos = new ByteOStream();        
        byte[] p = asBytes(clearText);
        byte[] out= new byte[ Math.max(4096, c.getOutputSize(p.length)) ];
        int n= c.update(p, 0, p.length, out, 0);
        if (n > 0) {            baos.write(out, 0, n);        }        
        n = c.doFinal(out,0);
        if (n > 0) {            baos.write(out, 0, n);        }
        
        return Base64.encodeBase64URLSafeString(baos.asBytes()) ;
    }

    private String decrypt(String encoded) throws Exception      {
        if (isEmpty(encoded)) { return encoded; }        
        Cipher c= getCipher(Cipher.DECRYPT_MODE);
        ByteOStream baos = new ByteOStream();        
        byte[] p = Base64.decodeBase64(encoded) ;
        byte[] out= new byte[ Math.max(4096, c.getOutputSize(p.length)) ];
        int n= c.update(p, 0, p.length, out, 0);             
        if (n > 0) {            baos.write(out, 0, n);        }        
        n = c.doFinal(out,0);
        if (n > 0) {            baos.write(out, 0, n);        }
        
        return asString(baos.asBytes()); 
    }
        
    private Cipher getCipher(int mode) throws Exception     {
        SecretKeySpec key= new SecretKeySpec(getKey(), getAlgo()); 
        Cipher c= Cipher.getInstance( getAlgo());      
        c.init(mode, key);        
        return c;
    }
    
}
