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
 
package com.zotoh.core.util;

import static com.zotoh.core.util.CoreUte.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


/**
 * @author kenl
 *
 */
public enum UUID {
;

    private static char[] CHARS= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * @return
     * @throws IOException
     */
    public static String newUUID() throws IOException    {
        // rfc4122, version 4 form
        return generate(-1, 2);
    }

    
    private static String generate(int length, int radix) throws IOException    {
        
        SecureRandom rnd = newRandom();
        char[] uuid= new char[36];
        int r, pos;

        if (length >= 0) {
            uuid= new char[length];
            for (int i = 0; i < length; ++i) {
                pos = (int) ( rnd.nextDouble() * radix);
                pos = 0 | pos;
                uuid[i] = CHARS[pos];
            }
        }   else {
            for (int i = 0; i < 36; ++i) {
                if (i==8 || i==13 || i==18 || i==23) { uuid[i] = '-' ; }
                else if (i==14) { uuid[i] = '4' ; }
                else {
                    r = (int) ( rnd.nextDouble() * 16);
                    r = 0 | r;
                    // At i==19 set the high bits of clock sequence as per rfc4122, sec. 4.1.5
                    uuid[i] = CHARS  [ (i == 19) ? ((r & 0x3) | 0x8) : r & 0xf ];
                }
            }
        }

        return new String(uuid);
    }

    @SuppressWarnings("unused")
    private static void main(String[] args)    {
        try        {
            Map<String,String> m= new HashMap<String,String>();
            String s;

            for (int i=0; i < 100; ++i) {
                s= UUID.newUUID();
                if (m.containsKey(s)) {
                    System.out.println("NOT GOOD!!!!!!!!!!!!!!!!!!!!!!");
                }
                Thread.sleep(50);
                System.out.println( s);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
