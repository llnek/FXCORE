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

import java.net.InetAddress;
import java.util.GregorianCalendar;

/**
 * Generates a unique GUID - largely a clone of SUN's implementation.
 * 
 * @author kenl
 *
 */
public enum GUID {
;

    private static String _base64 = 
        "-_0123456789"+"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
        "abcdefghijklmnopqrstuvwxyz" ;
    

    /**
     * @return
     */
    public static String generate()       {    
    
        String s="";
        try        {
            s= fill();
        }
        catch (Exception e) 
        {}
        return s;
    }
    
    private static String fill() throws Exception     {
        
        GregorianCalendar now = new GregorianCalendar();
        InetAddress inet = InetAddress.getLocalHost();
        StringBuilder bd= new StringBuilder(64);

        int counter = randomNo() & 0xFFFFFF;
        int ip = inet.hashCode();
        int key;

        /* 1st 6 bytes are derived from a random 36 bit number */

        key = randomNo() << 16;
        key = key | ((randomNo() >> 16) & 0xFFFF);
        bd.append(binary64ascii(key, (randomNo() & 0xF)));

        /* 7th and 8th bytes are derived from a random 12 bit number */

        key = randomNo() & 0xFFF00000;
        bd.append(binary64ascii(key, 0), 0, 2);

        /* next 6 bytes are derived from the 32 bit timestamp */

        key = (int) (now.getTime().getTime() / 1000);
        bd.append(binary64ascii(key, 0));

        /* next 4 bytes are derived from the low order 24 bits of the
           counter */

        ++counter;
        key = counter << 8;
        bd.append(binary64ascii(key, 0), 0, 4);

        /* next 6 bytes are derived from the 16 bit of the process id
           and higher order 16 bit of ip address */

        key = (ip & 0xFFFF0000) | (randomNo() & 0xFFFF);
        bd.append(binary64ascii(key, 0));

        /* last 3 bits are derived from the lower 16 bit of ip address */

        key = (ip & 0xFFFF) << 16;
        bd.append(binary64ascii(key, 0));
        
        /* only take 27 characters for the OID */        
        return bd.toString().substring(0, 27);
    }

    private static int randomNo(int n)     {        
        return (int)   (Math.random() * n);    
    }
    
    private static int randomNo()        {        
        return   randomNo(1000000000);    
    }

    private static char[] binary64ascii(int num, int aux)     {
        char[] result = new char[6];
        int n;

        /* bits 32 - 27 */

        n = (num >> 26) & 63;
        result[0] = _base64.charAt(n);

        /* bits 26 - 21 */

        n = (num >> 20) & 63;
        result[1] = _base64.charAt(n);

        /* bits 20 - 15 */

        n = (num >> 14) & 63;
        result[2] = _base64.charAt(n);

        /* bits 14 - 9 */

        n = (num >> 8) & 63;
        result[3] = _base64.charAt(n);

        /* bits 8 - 3 */

        n = (num >> 2) & 63;
        result[4] = _base64.charAt(n);

        /* bits 2 - 1 */
        /* p_aux is contributing 4 additional bits */

        n = (num & 3);
        n = (n << 4) | (aux & 0xF);

        result[5] = _base64.charAt(n);
        return result;
    }

}
