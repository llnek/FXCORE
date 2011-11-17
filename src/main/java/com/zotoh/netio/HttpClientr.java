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

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static org.jboss.netty.channel.Channels.pipeline;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ByteBufferBackedChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedStream;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.GUID;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public class HttpClientr {

    private transient Logger _log=getLogger(HttpClientr.class); 
    public Logger tlog() {  return _log;    }            
    private ClientBootstrap _boot;
    private ChannelGroup _chs;
    private Tuple _curScope;
    private Object _lock= new Object();

    public static void main(String[] args) {
    		try {
    			final StreamData data= new StreamData(new File("/tmp/play.zip"));
    			data.setDeleteFile(false);
    			final HttpClientr c= new HttpClientr();
    			c.connect(new URI("http://localhost:8080/p.zip"));
    			c.post(new BasicHttpMsgIO(){
					public void onOK(int code, String reason, StreamData resOut) {
						System.out.println("COOL");
						c.wake();
					}    				
    			}, data);
    			c.block();
    			c.destroy();
    			System.exit(0);
    		} catch (Throwable t) {
    			t.printStackTrace();
    		}
    }
    
    /**
     * 
     */
    public void block()  {
    	synchronized(_lock) {
			try {
	    		_lock.wait();
	    	} catch (Throwable t) {}
    	}
    }
    
    /**
     * 
     */
    public void wake() { 
    	synchronized(_lock) {
			try {
	    		_lock.notify();
	    	} catch (Throwable t) {}
    	}
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
	protected void finalize() throws Throwable {
    	if (_boot != null) { _boot.releaseExternalResources(); }
		super.finalize();
	}

	/**
     * 
     */
    public HttpClientr() {
        iniz();
    }
    
    /**
     * @param remote
     * @throws Exception
     */
    public void connect(URI remote) throws Exception {
        boolean ssl= "https".equals(remote.getScheme());
        String host= remote.getHost();
        int port= remote.getPort();
        if (port < 0) {            port= ssl ? 443 : 80;        }
        
        tlog().debug("HttpClientr: connecting to host: {}, port: {}", host, port);
        inizPipeline(ssl);
        
        ChannelFuture cf= _boot.connect(new InetSocketAddress(host, port));        
        // wait until the connection attempt succeeds or fails.
        cf.awaitUninterruptibly();
        
        if (cf.isSuccess()) {
            _curScope= new Tuple(remote, cf);
            _chs.add(cf.getChannel());
        } else {
            onError(cf.getCause()) ;
        }
        
        tlog().debug("HttpClientr: connected OK to host: {}, port: {}", host, port);        
    }
    
    /**
     * @param cfg
     * @param data
     * @throws IOException
     */
    public void post(HttpMsgIO cfg, StreamData data) throws IOException  {
	    	tstObjArg("scope-data", _curScope);
	    	tstObjArg("payload-data", data);
	    	
        send( create_request(HttpMethod.POST) , cfg, data);
    }
    
    /**
     * @param cfg
     * @throws IOException
     */
    public void get(HttpMsgIO cfg) throws IOException  {
    		tstObjArg("scope-data", _curScope);
        
    		send( create_request(HttpMethod.GET), cfg, null);
    }
    
    private void send(HttpRequest req, HttpMsgIO cfg, StreamData data) 
            throws IOException {
        
    		tlog().debug("HttpClientr: {} {}", (data==null?"GET":"POST"), _curScope.get(0)) ;
        
        ChannelFuture cf= (ChannelFuture) _curScope.get(1);
        URI uri= (URI) _curScope.get(0);
        long clen= data==null ? 0L : data.getSize();
        
        if (cfg == null) {
            cfg=new BasicHttpMsgIO() {
                public void onOK(int code, String reason, StreamData res) {}                
                public void onError(int code, String reason) {}                                
            };
        }
        
        req.setHeader(HttpHeaders.Names.CONNECTION,
        				( cfg.keepAlive() ? HttpHeaders.Values.KEEP_ALIVE :	HttpHeaders.Values.CLOSE) );        
        
        req.setHeader(HttpHeaders.Names.HOST, uri.getHost());
        
        cfg.configMsg(req);

        if (data!= null && isEmpty( req.getHeader("content-type"))) {
            req.setHeader("content-type", "application/octet-stream") ;
        }
        
        tlog().debug("HttpClientr: content has length: {}", clen);
        req.setHeader("content-length", Long.toString(clen)) ;
        
        Channel cc= cf.getChannel();
        ChannelFuture f;
        HttpResponseHdlr h= (HttpResponseHdlr) cc.getPipeline().get("handler");
        h.bind(cfg);
                 
        tlog().debug("HttpClientr: about to flush out request (headers)");
        f= cc.write(req);
        f.addListener(new ChannelFutureListener() {
        		public void operationComplete(ChannelFuture fff) {
        			tlog().debug("HttpClientr: req headers flushed") ;
        		}
        }) ;
        
        if (clen > 0L) {
            if (clen > HttpUte.getDefThreshold() ) {
                f=cc.write(new ChunkedStream( data.getStream())) ;                    
            } else {
                f=cc.write( new ByteBufferBackedChannelBuffer( ByteBuffer.wrap(data.getBytes()) ));
            }
            f.addListener(new ChannelFutureListener() {
	        		public void operationComplete(ChannelFuture fff) {
	        			tlog().debug("HttpClientr: req payload flushed") ;
	        		}
            }) ;
        }
                
    }
    
    /**
     * 
     */
    public void destroy() {
        tlog().debug("HttpClientr: destory()");
        close();
        try { _boot.releaseExternalResources(); } finally { _boot=null; }
    }
    
    /**
     * 
     */
    public void close() {
        tlog().debug("HttpClientr: close()");
        if (_curScope != null)
        try {
    			_chs.close();
        }
        finally {
            _curScope=null;
        }
    }
    
    private HttpRequest create_request(HttpMethod m) {
		URI uri= (URI) _curScope.get(0); 
        return new DefaultHttpRequest( HttpVersion.HTTP_1_1, m, uri.toASCIIString());
    }
    
    private void inizPipeline(final boolean ssl) {
        
        _boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe= pipeline();
                if (ssl) {
                    SSLEngine engine = HttpUte.getClientSSL().createSSLEngine();
                    engine.setUseClientMode(true);
                    pipe.addLast("ssl", new SslHandler(engine));
                }
                pipe.addLast("codec", new HttpClientCodec());
//                pipe.addLast("inflater", new HttpContentDecompressor());
                //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
                pipe.addLast("chunker", new ChunkedWriteHandler());
                pipe.addLast("handler", new HttpResponseHdlr(_chs));                    
                return pipe;
            }            
        });
        
    }
        
    private void onError(Throwable t) throws Exception {
        
        if (t instanceof Exception) {
             throw (Exception)t;
        } else {
            throw new Exception(t==null ? "Failed to connect" : t.getMessage()) ;                    
        }
        
    }
    
    private void iniz() {
        _boot = new ClientBootstrap( new NioClientSocketChannelFactory (
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));        
        _boot.setOption("tcpNoDelay" , true);
        _boot.setOption("keepAlive", true);
        _chs= new DefaultChannelGroup(GUID.generate());         
    }
    
}
