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

import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.netio.NettyHplr.newServerBoot;
import static com.zotoh.netio.NettyHplr.newServerSSLContext;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;

import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.MetaUte;
import com.zotoh.core.util.Tuple;

public abstract class MemXXXServer implements NetConsts {

    private transient Logger _log=getLogger(MemXXXServer.class); 
    public Logger tlog() {  return _log;    }            
	
	protected ServerBootstrap _boot;
	protected Object _lock=null;
	protected File _vdir;
	protected String _host, 
	_keyPwd;
	protected ChannelGroup _chs;
	protected Channel _root;
	protected int _port;
	protected URL _keyFile;

	protected static void xxx_main(boolean block, String cz, String[] args) {
		try {
			Map<String,String> m=parseArgs(args);
			MemXXXServer svr;
			Class<?> z=MetaUte.loadClass(cz);
			
			final int port= CoreUte.asInt(nsb(m.get("port")),0);
			final String host=nsb(m.get("host"));
			String key=nsb(m.get("key"));
			String pwd=nsb(m.get("pwd"));
			String vdir=nsb(m.get("vdir"));
			
			if (!isEmpty(key)) {
				svr= (MemXXXServer) z.getConstructor(String.class,URL.class, String.class, String.class, int.class)
				.newInstance(vdir,new URI(key).toURL(), pwd, host,port);
			} else {
				svr= (MemXXXServer) z.getConstructor(String.class,String.class, int.class)
				.newInstance(vdir,host,port);
			}
			
			svr.start(block);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static Map<String,String> parseArgs(String[] args) {
		Map<String,String> m= MP();
		for (int i=0; i < args.length; ++i) {
			if ("-host".equals(args[i])) { m.put("host", args[i+1]); ++i; }
			else if ("-port".equals(args[i]))  { m.put("port", args[i+1]); ++i; }
			else if ("-key".equals(args[i]))  { m.put("key", args[i+1]); ++i; }
			else if ("-pwd".equals(args[i]))  { m.put("pwd", args[i+1]); ++i; }
			else if ("-vdir".equals(args[i]))  { m.put("vdir", args[i+1]); ++i; }
		}
		return m;
	}
	
		
	/**
	 * @param vdir
	 * @param key
	 * @param pwd
	 * @param host
	 * @param port
	 */
	protected MemXXXServer(String vdir, URL key, String pwd, String host, int port) {
		this(vdir,host,port);
		_keyFile=key;
		_keyPwd= pwd;
	}
	
	/**
	 * @param vdir
	 * @param host
	 * @param port
	 */
	protected MemXXXServer(String vdir, String host, int port) {
		_vdir=new File(vdir);
		_vdir.mkdirs();
		_host=host;
		_port=port;
	}

    	/**
	 * @return
	 */
	public ChannelGroup getChannels() { return _chs; }

	public void start( boolean block) throws Exception {
		
		tlog().debug("MemXXXServer: starting...");
		
		start_0();
		start_1();
		start_2();

		if (block) {
			_lock=new Object();
			synchronized(_lock) {
				_lock.wait();
			}
		}
	}

    	/**
	 * 
	 */
	public void stop() {
		
		final MemXXXServer me=this;

		_chs.close().addListener(new ChannelGroupFutureListener(){
			public void operationComplete(ChannelGroupFuture f)
							throws Exception {
				me.stop_final();
			}
		});
		
	}
	
	private void stop_final() {
		
		tlog().debug("MemXXXServer: stopped");
		_boot.releaseExternalResources();
		reset();
		if (_lock != null) {
			synchronized(_lock) {
				_lock.notify();
			}
		}
	}
	
	private void reset() {
		_lock= null;
		_boot=null;
		_host=null; 
		_keyPwd=null;
		_chs=null;
		_root=null;
		_port=-1;
		_keyFile=null;		
	}
	
	private void start_2() throws Exception {
		tlog().debug("MemFileServer: running on host {}, port {}", _host, _port);
        _root= _boot.bind(new InetSocketAddress( _host, _port));
        _chs.add(_root);		
	}
	
	private void start_1() {
        _boot.setOption("child.receiveBufferSize", 2*1024*1024);
        _boot.setOption("child.tcpNoDelay", true);
        _boot.setOption("reuseAddress", true);
	}
	
	private void start_0() throws Exception {
		
		final SSLEngine eg=(_keyFile != null) ? newServerSSLContext(_keyFile,_keyPwd) : null ;
		Tuple t= newServerBoot() ;
		
		_boot= (ServerBootstrap) t.get(0);
		_chs= (ChannelGroup) t.get(1);		
        _boot.setPipelineFactory( getPipelineFac(eg) );		
		
	}
	

	/**
	 * @param eg
	 * @return
	 */
	protected abstract ChannelPipelineFactory getPipelineFac(SSLEngine eg);
	
}
