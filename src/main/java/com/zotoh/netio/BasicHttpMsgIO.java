/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMessage;

import com.zotoh.core.util.StrArr;

/**
 * @author kenl
 *
 */
public abstract class BasicHttpMsgIO implements HttpMsgIO {

	/* (non-Javadoc)
	 * @see com.zotoh.netio.HttpMsgIO#onPreamble(java.lang.String, java.lang.String, java.util.Map)
	 */
	public void onPreamble(String mtd, String uri, Map<String,StrArr> headers) {}
	
    /* (non-Javadoc)
     * @see com.zotoh.netio.HttpMsgIO#onError(int, java.lang.String)
     */
    public void onError(int code, String reason) {
        System.out.println("Error: status=" + code + ", reason=" + reason);
    }            
	
	/* (non-Javadoc)
	 * @see com.zotoh.netio.HttpMsgIO#keepAlive()
	 */
	public boolean keepAlive() { return false; }
	
    /**
     * 
     */
    protected BasicHttpMsgIO() {}
    
    /* (non-Javadoc)
     * @see com.zotoh.netio.HttpMsgIO#configMsg(org.jboss.netty.handler.codec.http.HttpMessage)
     */
    public void configMsg(HttpMessage m) {}
    
}
