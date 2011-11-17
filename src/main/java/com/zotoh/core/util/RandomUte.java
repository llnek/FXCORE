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


import static com.zotoh.core.util.LangUte.ST;

import java.util.Random;
import java.util.Set;

/**
 * Helper functions to generate random numbers. 
 *
 * @author kenl
 *
 */
public enum RandomUte {
;

    @SuppressWarnings("unused")
    private static void main(String[] args) {
        int[] n;
        n=genNumsBetween(1,56, 5);
        for (int i=0; i < n.length; ++i)
        System.out.println( n[i]);
        System.out.println( "-----------------------");
        n=genNumsBetween(1,46, 1);
        for (int i=0; i < n.length; ++i)
        System.out.println( n[i]);
    }
    
    /**
     * @param start
     * @param end
     * @param howMany
     * @return
     */
    public  static int[] genNumsBetween(int start, int end, int howMany )     {
    	
        Random r= new Random(System.currentTimeMillis());
        Set<Integer> mp= ST();
        int[] rc= new int[0];
        int n, pos;
                
        if (start >= end ||
                ((end-start) < howMany)) {
        return rc; 
        }
                
        if (end < Integer.MAX_VALUE) { 
        end=end+1;
        }
                
        while (true) {            
            if (mp.size()==howMany) { break; }             
            // else
            n = r.nextInt(end);            
            if ( n == 0 || mp.contains(n)) { continue; }
            //else
            mp.add(n);
        }
        
        rc= new int[mp.size()];
        pos=0;        
        for (Integer v : mp) {
            rc[pos++] = v;
        }
        
        return rc;        
    }

}
