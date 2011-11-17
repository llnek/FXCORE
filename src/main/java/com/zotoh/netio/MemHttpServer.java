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

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.HttpServerCodec;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.StrArr;


/**
 * @author kenl
 *
 */
public class MemHttpServer extends MemXXXServer {
		
	private HttpMsgIO _cb;
	
	/**
	 * @param vdir
	 * @param key
	 * @param pwd
	 * @param host
	 * @param port
	 */
	public MemHttpServer(String vdir, URL key, String pwd, String host, int port) {
		super(vdir, key, pwd, host,port);
	}
	
	/**
	 * @param vdir
	 * @param host
	 * @param port
	 */
	public MemHttpServer(String vdir, String host, int port) {
		super(vdir,host,port);
	}

	public MemHttpServer bind(HttpMsgIO cb) {
		_cb=cb;return this;
	}
	
	@Override
	protected ChannelPipelineFactory getPipelineFac(final SSLEngine eg) {
		final ChannelGroup g= this.getChannels();
		return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe= pipeline();
                if (eg != null) {  pipe.addLast("ssl", new SslHandler(eg));   }
                pipe.addLast("decoder", new HttpServerCodec());
//                pipe.addLast("aggregator", new HttpChunkAggregator(65536));                
//                pipe.addLast("deflater", new HttpContentCompressor());
                pipe.addLast("chunker", new ChunkedWriteHandler());
                pipe.addLast("handler", new BasicChannelHandler(g){
                		protected void onReqPreamble(String mtd, String uri, Map<String,StrArr> headers) {
                    		if (_cb != null) { _cb.onPreamble(mtd,uri,headers); }                			
                		}                	
                    protected void doReqFinal(ChannelHandlerContext ctx, MessageEvent ev,StreamData inData) throws IOException {
                    		if (_cb != null) { _cb.onOK(200, "OK", inData); }
                    		super.doReqFinal(ctx, ev, inData);
                    }
                } );
                return pipe;
            }
        };
	}
    
}
