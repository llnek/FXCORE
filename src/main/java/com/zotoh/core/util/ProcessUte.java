/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import static com.zotoh.core.util.LoggerFactory.getLogger;


/**
 * @author kenl
 *
 */
public enum ProcessUte {
;
    
    private static Logger log= getLogger(ProcessUte.class);
    public static Logger tlog() {  return log; }    
    
    
    /**
     * @param r
     */
    public static void asyncExec(Runnable r) {
		if ( r != null) {
	        Thread t=new Thread(r);
	        t.setDaemon(true);
	        t.start();			
		}
    }
    
    /**
     * @param millisecs
     */
    public static void safeThreadWait(long millisecs) {
        try    {
            if ( millisecs > 0L) { Thread.sleep(millisecs); }
        }
        catch (InterruptedException e)
        {}
    }
    
    /**
     * Block and wait on the object.
     * 
     * @throws Exception
     */
    public static void blockAndWait(Object lock, long waitMillis)     {        
        synchronized(lock) {
            try {
                if (waitMillis > 0L) {
                    lock.wait(waitMillis);
                } else {
                    lock.wait(); }
            }
            catch (Exception e)
            {}
        }
        
    }

    
    /**
     * 
     */
    public static void blockForever()     {        
        while (true) {
            try {
                Thread.sleep(5000);
            }
            catch (Exception e) 
            {}
        }        
    }
    
    
    
}
