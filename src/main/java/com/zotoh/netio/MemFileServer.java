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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.SSLEngine;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpServerCodec;

import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;


/**
 * @author kenl
 *
 */
public class MemFileServer extends MemXXXServer {


	public static void main(String[] args) {
		args=new String[] {
		        "-host", "localhost", "-port", "8080", "-vdir", "/tmp/wdrive"
		};
		xxx_main(true, "com.zotoh.netio.MemFileServer", args);

	}
	
	
	public void saveFile(String file, StreamData data) throws IOException {
		File r, fp= new File(_vdir, file);
		fp.delete();
		if (data.isDiskFile()) {
			r=data.getFileRef();
			// move may fail, should then try copy
			FileUtils.moveFile(r, fp);
		} else {
			StreamUte.writeFile(fp, data.getBytes());
		}
	}
	
	public StreamData getFile(String file) throws IOException {
		File fp= new File(_vdir, file);
		StreamData out= null;
		
		if (fp.exists() && fp.canRead()) {
			out= new StreamData();
			out.resetMsgContent(fp, false);
		}
		
		return out;
	}
	
	/**
	 * @param vdir
	 * @param key
	 * @param pwd
	 * @param host
	 * @param port
	 */
	public MemFileServer(String vdir, URL key, String pwd, String host, int port) {
		super(vdir,key,pwd,host,port);
	}
	
	/**
	 * @param vdir
	 * @param host
	 * @param port
	 */
	public MemFileServer(String vdir, String host, int port) {
		super(vdir,host,port);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.netio.MemXXXServer#getPipelineFac(javax.net.ssl.SSLEngine)
	 */
	protected ChannelPipelineFactory getPipelineFac(final SSLEngine eg) {
		final MemFileServer me=this;
		return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe= pipeline();
                if (eg != null) {  pipe.addLast("ssl", new SslHandler(eg));   }
                pipe.addLast("decoder", new HttpServerCodec());
//                pipe.addLast("aggregator", new HttpChunkAggregator(65536));                
//                pipe.addLast("deflater", new HttpContentCompressor());
                pipe.addLast("chunker", new ChunkedWriteHandler());
                pipe.addLast("handler", new FileServerHdlr(me) );
                return pipe;
            }
        };
	}
	
}


