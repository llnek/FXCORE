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

import static com.zotoh.core.util.CoreUte.newRandom;
import static com.zotoh.core.util.StrUte.left;
import static com.zotoh.core.util.StrUte.right;

import java.net.InetAddress;
import java.util.Random;

/**
 * Generates a unique GUID.
 * Length = 48
 * 
 * @author kenl
 *
 */
public enum WWID {
;

    private static int _SEED;
    private static long _ip;

    static    {
        try        {
            InetAddress net= InetAddress.getLocalHost();            
            Random rnd = newRandom();
            byte[] addr= new byte[0];
            long n=0;
            int i;
            _SEED= rnd.nextInt(Integer.MAX_VALUE); 
            if ( ! net.isLoopbackAddress()) {
            	addr= net.getAddress();
            }
            if (addr.length == 0)        {
                n= rnd.nextInt(Integer.MAX_VALUE);
            }
            else
            if (addr.length == 4)        {
                int[] pow= new int[] {256*256*256, 256*256, 256, 1} ;
                n=0L;
                for (int j=0; j < 4; ++j) {
                    i= addr[j];
                    if ( addr[j] < 0) { i = (addr[j] - Byte.MIN_VALUE) - Byte.MIN_VALUE; }
                    n = n + (i * pow[j]);
                }
            }
            else  { // ipv6   
                n= rnd.nextLong();
            }
            _ip=n;            
        }
        catch (Exception e)        {
            e.printStackTrace();
        }
    }

    
    /**
     * @return
     */
    public static String generate()    {        return  format();    }

    private static String format()    {
        String[] ts= splitHiLoTime();        
        return ts[0] + formatAsString(_ip) + formatAsString(_SEED) +  formatAsString(seqno()) + ts[1];
    }

    private static int seqno()    {
        return SeqNumGen.getInstance().nextInt() ;
    }
    
    private static String formatAsString(long n)    {
        StringBuilder b = new StringBuilder("0000000000000000");
        String ls = Long.toHexString(n);
        b.replace(16 - ls.length(), 16, ls);
        return b.toString();
    }

    private static String formatAsString(int n)    {
        StringBuilder b = new StringBuilder("00000000");
        String ls = Long.toHexString(n);
        b.replace(8 - ls.length(), 8, ls);
        return b.toString();
    }

    private static String[] splitHiLoTime()    {
        String s= formatAsString(System.currentTimeMillis()); 
        String[] rc= new String[2];
        int l, r, n = s.length();
        
        l= n / 2;
        r= Math.max(0, n-l);
        
        rc[1]= right(s, r);
        rc[0]= left(s, l);
        
        return rc; 
    }

    
}
