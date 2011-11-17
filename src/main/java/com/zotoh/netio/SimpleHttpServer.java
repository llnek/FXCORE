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
 
package com.zotoh.netio;

import java.net.URL;


/**
 * @author kenl
 *
 */
class SimpleHttpServer extends MemHttpServer  {
	    
    /**
     * @param args
     */
    public static void main(String[] args)    {
    		xxx_main(false, "com.zotoh.netio.SimpleHttpServer", args);
    }
 
	/**
	 * @param vdir
	 * @param key
	 * @param pwd
	 * @param host
	 * @param port
	 */
	public SimpleHttpServer(String vdir, URL key, String pwd, String host, int port) {
		super(vdir, key, pwd, host,port);
	}
	
	/**
	 * @param vdir
	 * @param host
	 * @param port
	 */
	public SimpleHttpServer(String vdir, String host, int port) {
		super(vdir,host,port);
	}
    
}
