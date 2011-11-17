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

import org.jboss.netty.channel.group.ChannelGroup;

import com.zotoh.core.io.StreamData;

/**
 * @author kenl
 *
 */
class HttpResponseHdlr extends BasicChannelHandler {

    private HttpMsgIO _cb;
    
    /**
     * @param threshold
     * @param out
     */
    public HttpResponseHdlr(ChannelGroup g) {
    		super(g);
    }
    
    /**
     * @param cb
     * @return
     */
    public HttpResponseHdlr bind(HttpMsgIO cb) {
        _cb= cb; return this;
    }

	protected void doResFinal(int code, String reason, StreamData out) {
		if (_cb != null)  {
			_cb.onOK(code, reason, out);
		}
	}
	
	protected void onResError(int code, String reason) {
		if (_cb != null) {
			_cb.onError(code, reason) ;
		}
	}
    
    
}

