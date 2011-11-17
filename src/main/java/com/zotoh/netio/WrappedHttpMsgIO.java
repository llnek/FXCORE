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
public class WrappedHttpMsgIO implements HttpMsgIO {

	private HttpMsgIO _inner;
	
	/**
	 * @param m
	 */
	public WrappedHttpMsgIO( HttpMsgIO m) {
		_inner=m;
	}
	
	@Override
	public void onOK(int code, String reason, StreamData resOut) {
		if(_inner != null) { _inner.onOK(code, reason, resOut) ; }
	}

	@Override
	public void onError(int code, String reason) {
		if (_inner!=null) { _inner.onError(code, reason); }
	}

	@Override
	public void configMsg(HttpMessage m) {
		if ( _inner != null) { _inner.configMsg(m); }
	}

	@Override
	public boolean keepAlive() {
		return _inner==null ? false : _inner.keepAlive();
	}

	@Override
	public void onPreamble(String mtd, String uri, Map<String, StrArr> headers) {
		if ( _inner != null) { _inner.onPreamble(mtd, uri, headers); }
	}

}
