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

import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import com.zotoh.core.crypto.BaseOfuscator;
import com.zotoh.core.io.ByteOStream;

/**
 * Obfuscation using BouncyCastle. 
 *
 * @author kenl
 *
 */
public class BCOfuscator extends BaseOfuscator {
    
    /* (non-Javadoc)
     * @see com.zotoh.core.crypto.BaseOfuscator#unobfuscate(java.lang.String)
     */
    public String unobfuscate(String encrypted) throws Exception     {
        return decrypt(encrypted);
    }
    
    
    /* (non-Javadoc)
     * @see com.zotoh.core.crypto.BaseOfuscator#obfuscate(java.lang.String)
     */
    public String obfuscate(String clearText) throws Exception     {
        return encrypt(clearText);
    }

    
    /**
     * 
     */
    public BCOfuscator()    {
        Crypto.getInstance();
    }

    
    private String decrypt(String encrypted) throws Exception    {
        if (isEmpty(encrypted)) { return encrypted; }
        PaddedBufferedBlockCipher cipher= new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new DESedeEngine())
        );
        byte[] p = Base64.decodeBase64(encrypted) ,
        out = new byte[1024];
        ByteOStream baos = new ByteOStream();
        int c;
        
        // initialise the cipher with the key bytes, for encryption
        cipher.init(false, new KeyParameter( getKey()));
        c= cipher.processBytes(p, 0, p.length, out, 0);
        if (c > 0) {
            baos.write(out, 0, c);
        }
        
        c = cipher.doFinal(out,0);
        if (c > 0) {
            baos.write(out, 0, c);
        }
        
        return asString( baos.asBytes()) ;
    }

    
    private String encrypt(String clearText) throws Exception     {
        if (isEmpty(clearText)) { return clearText; }        
        PaddedBufferedBlockCipher cipher= new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new DESedeEngine())
        );        
        ByteOStream baos = new ByteOStream();
        byte[] p = asBytes(clearText),
        out = new byte[4096];
        int c;
        
        // initialise the cipher with the key bytes, for encryption
        cipher.init(true, new KeyParameter( getKey()));
        c= cipher.processBytes(p, 0, p.length, out, 0);
        if (c > 0) {
            baos.write(out, 0, c);
        }
        
        c = cipher.doFinal(out,0);
        if (c > 0) {
            baos.write(out, 0, c);
        }
        
        return Base64.encodeBase64String(  baos.asBytes());        
    }
        
    @SuppressWarnings("unused")
    private static void genkey() throws Exception     {        
        // create 2 things for the generation of a key
        // 1. random gen
        // 2. key length in bits
        SecureRandom rnd = Crypto.getInstance().getSecureRandom();
        // DESede key must be 192 or 128 bits long only
        int strength = DESedeParameters.DES_EDE_KEY_LENGTH*8;
        
        KeyGenerationParameters kgp = new KeyGenerationParameters( rnd, strength);
        DESedeKeyGenerator kg = new DESedeKeyGenerator();
        kg.init(kgp);

        Base64.encodeBase64( kg.generateKey());
    }
    
}
