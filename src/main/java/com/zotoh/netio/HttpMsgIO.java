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

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.StrArr;

/**
 * @author kenl
 *
 */
public interface HttpMsgIO {

	/**
	 * @param mtd
	 * @param uri
	 * @param headers
	 */
	public void onPreamble(String mtd, String uri, Map<String,StrArr> headers);
	
    /**
     * @param code
     * @param reason
     * @param resOut
     */
    public void onOK(int code, String reason, StreamData resOut);
    
    /**
     * @param code
     * @param reason
     */
    public void onError(int code, String reason);
    
    /**
     * @param m
     */
    public void configMsg(HttpMessage m);

    /**
     * @return
     */
    public boolean keepAlive();
}
