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

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.GUID;
import com.zotoh.core.util.Tuple;
import com.zotoh.crypto.Crypto;
import com.zotoh.crypto.CryptoStore;
import com.zotoh.crypto.JKSStore;
import com.zotoh.crypto.PKCSStore;


/**
 * @author kenl
 *
 */
public enum NettyHplr {
;

	/**
	 * @param boot
	 * @param fac
	 * @return
	 */
	public static Bootstrap inizServerPipeline(Bootstrap boot, ChannelPipelineFactory fac) { 
        boot.setPipelineFactory(fac);
        return boot;
    }
	
	/**
	 * @param eng
	 * @param chunk
	 * @param zip
	 * @param boot
	 * @return
	 * @throws Exception
	 */
	public static ChannelPipeline inizServerPipeline(final SSLEngine eng, 
					final boolean chunk, final boolean zip, 
					Bootstrap boot) throws Exception {
        boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe= pipeline();
                if (eng != null) {  pipe.addLast("ssl", new SslHandler(eng));   }
                pipe.addLast("decoder", new HttpRequestDecoder());
                if(chunk) { pipe.addLast("aggregator", new HttpChunkAggregator(65536)); }                
                pipe.addLast("encoder", new HttpResponseEncoder());
                if (zip) { pipe.addLast("deflater", new HttpContentCompressor()); }
                pipe.addLast("chunker", new ChunkedWriteHandler());
//                pipeline.addLast("handler", null);
                return pipe;
            }
        });		
        return boot.getPipelineFactory().getPipeline();
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static Tuple newServerBoot() throws Exception {
        Object c= new DefaultChannelGroup(GUID.generate()); 
        Object b= new ServerBootstrap( new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()) );
        return new Tuple(b,c);
	}
	
	public static SSLEngine newServerSSLContext(URL key, String pwd) 
					throws Exception {
		
        boolean jks = key.getFile().endsWith(".jks");
        InputStream inp= key.openStream();
        CryptoStore s;        
        try {
        	s= jks ? new JKSStore() : new PKCSStore();
            s.init(pwd ) ;            
            s.addKeyEntity(inp, pwd );            
        }
        finally {
            StreamUte.close(inp);
        }
        
        SSLContext c = SSLContext.getInstance( "TLS");
        c.init( s.getKeyManagerFactory().getKeyManagers(),
                s.getTrustManagerFactory().getTrustManagers(),
                Crypto.getInstance().getSecureRandom() );                 
        
        SSLEngine engine = c.createSSLEngine();
        engine.setUseClientMode(false);
        return engine;
	}
	

}
