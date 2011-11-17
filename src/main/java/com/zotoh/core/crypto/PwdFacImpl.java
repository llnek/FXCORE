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

import static com.zotoh.core.util.ByteUte.readAsInt;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.security.SecureRandom;

import com.zotoh.core.util.Logger;

/**
 * Implementation of passwords.
 * 
 * @author kenl
 *
 */
class PwdFacImpl {
    
    private static Logger ilog() { return _log = getLogger(PwdFacImpl.class);    }
    private static Logger _log= ilog();
    public static Logger tlog() { return _log==null ? ilog() : _log;    }    

    private static String PCHS= "abcdefghijklmnopqrstuvqxyz" +
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + 
    "`1234567890-_~!@#$%^&*()";
    private static char[] s_pwdChars= PCHS.toCharArray();

    private static String ACHS= "abcdefghijklmnopqrstuvqxyz" +
    "1234567890-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static char[] s_asciiChars= ACHS.toCharArray();
    
    /**
     * @param length
     * @return
     */
    protected static String createStrong(int length)     {
        return createXXX(length, s_pwdChars) ;
    }
    
    /**
     * @param length
     * @return
     */
    protected static String createRandom(int length)      {
        return createXXX(length, s_asciiChars) ;
    }

    /**
     * @param length
     * @param chars
     * @return
     */
    private static String createXXX(int length, char[] chars)      {        
        if (length < 0) { return null; }
        if (length==0) { return ""; }
        char[] str = new char[length];
        try   {
            SecureRandom r = SecureRandom.getInstance("SHA1PRNG");          
            byte[] bits= new byte[4];      
            int cl= chars.length;            
            for (int i = 0; i < length; ++i) {
                r.nextBytes(bits);
                str[i]= chars[Math.abs( readAsInt(bits) % cl)];                
            }            
        }
        catch (Exception e) {
            tlog().warn("", e) ;
        }        
        return new String(str);
    }
    
}
