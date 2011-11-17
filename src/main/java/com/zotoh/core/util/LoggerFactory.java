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

import java.lang.reflect.Method;


/**
 * @author kenl
 *
 */
public class LoggerFactory {
	
    private static boolean NOTSKIPCHECK=true;
    private static Class<?> SLFJ;
    
    static {
        init();
    }
    
    private static void init() {
    	if (SLFJ==null && NOTSKIPCHECK)
        try {
            SLFJ=Class.forName("org.slf4j.LoggerFactory");
        }
        catch (Throwable t) {
        	NOTSKIPCHECK=false;
        }        
    }
    
    /**
     * 
     * @param z
     * @return
     */
    public static Logger getLogger(Class<?> z) {
        Logger rc= Logger.Dummy;
        init();
        if (SLFJ != null && z != null) try {
            Method m = SLFJ.getDeclaredMethod("getLogger", new Class<?>[] { z.getClass() } );
            rc = new Logger( (org.slf4j.Logger) m.invoke(null, z) );
        }
        catch (Throwable t) {
        }
        return rc;
    }
    
}


