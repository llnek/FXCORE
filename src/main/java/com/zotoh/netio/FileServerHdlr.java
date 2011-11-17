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

import java.io.IOException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.stream.ChunkedStream;

import com.zotoh.core.io.StreamData;


/**
 * @author kenl
 *
 */
public class FileServerHdlr extends BasicChannelHandler {

	private MemFileServer _svr;
	private String _file;
	
	/**
	 * @param g
	 */
	public FileServerHdlr(MemFileServer g) {
		super(g.getChannels());
		_svr=g;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.netio.BasicChannelHandler#onRecvRequest(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	protected boolean onRecvRequest( ChannelHandlerContext ctx, MessageEvent ev)
			throws IOException {
		HttpRequest msg= (HttpRequest) ev.getMessage();
		HttpMethod m=msg.getMethod();
		String p, uri= msg.getUri();		
		int pos= uri.lastIndexOf('/') ;
		boolean rc=true;
		
		if (pos >=0) {
			p=uri.substring(pos+1);
		} else { p= uri; }
		
		tlog().debug("FileServerHdlr: Input Method = {}", m.getName());
		tlog().debug("FileServerHdlr: Input Uri = {}", uri);
		tlog().debug("FileServerHdlr: File = {}", p);
	
		_file=p;
		
		if (HttpMethod.POST.equals( m) || HttpMethod.PUT.equals(m)) { 			
		}
		else
		if (HttpMethod.GET.equals( m)) {
			doGet(ctx,ev) ;
			rc=false;
		}
		
		return rc;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.netio.BasicChannelHandler#doReqFinal(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent, com.zotoh.core.io.StreamData)
	 */
	@Override
	protected void doReqFinal(ChannelHandlerContext ctx, MessageEvent ev,
					StreamData inData) throws IOException {
		if (inData  != null) {
			super.doReqFinal(ctx, ev, inData);
			doPut(inData);
		}
	}

	private void doGet(ChannelHandlerContext ctx, MessageEvent ev) 
			throws IOException {
		StreamData data= _svr.getFile(_file);
		if (data==null) {
			doReplyError(ctx,ev, HttpResponseStatus.NOT_FOUND);
		}
		else if (data.getSize() == 0L) {
			doReplyError(ctx,ev, HttpResponseStatus.NO_CONTENT);			
		} else {
			do_reply_file(ctx,ev,data);
		}
	}

	private void do_reply_file(ChannelHandlerContext ctx, MessageEvent ev, StreamData data) 
			throws IOException {
	    	DefaultHttpResponse res= new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
	    	long clen= data.getSize();
	    	Channel c= ev.getChannel();
	    	ChannelFuture f;
	    	final FileServerHdlr me=this;
	    	
	    	if (c == null) {
	    		c= ctx.getChannel();
	    	}
	    	res.setHeader("content-type", "application/octet-stream");
	    	res.setHeader("content-length", Long.toString(clen));
	    	c.write(res);
    	
        f=c.write(new ChunkedStream( data.getStream())) ;                    
        f.addListener(new ChannelFutureListener() {
        		public void operationComplete(ChannelFuture fff) {
        			me.write_complete(fff);
        		}
        }) ;    	
	}
	
	private void write_complete(ChannelFuture fff) {
		if (isKeepAlive()) {
			fff.getChannel().close();
		}
	}
	
	private void doPut(StreamData in) throws IOException {
		_svr.saveFile(_file, in);
	}
		
}

