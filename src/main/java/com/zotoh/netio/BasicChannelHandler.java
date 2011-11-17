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

import static com.zotoh.core.util.CoreUte.safeGetClzname;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.StrArr;
import com.zotoh.core.util.Tuple;


/**
 * @author kenl
 *
 */
public class BasicChannelHandler extends SimpleChannelHandler {

    private transient Logger _log=getLogger(BasicChannelHandler.class);       
    public Logger tlog() {  return _log;    }    

    private long _clen, _thold= HttpUte.getDefThreshold();
    private CookieEncoder _cookies;
    private ChannelGroup _grp;
    private OutputStream _os;
    
    private Properties _props= new Properties();
    private boolean _keepAlive;
    private File _fOut;
        
    /**
     * @return
     */
    public CookieEncoder getCookie() { return _cookies; }

    /**
     * @return
     */
    public boolean isKeepAlive() { return _keepAlive; }
    
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent ev)
					throws Exception {
        Channel c= maybeGetChannel(ctx,ev) ;
        		
        tlog().debug("BasicChannelHandler: channelClosed - ctx {}, channel {}", 
    						ctx, (c==null ? "?" : c.toString()) );
        
        if (c != null) { _grp.remove(c); }
        
		super.channelClosed(ctx, ev);
	}

//	@Override
//	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent ev)
//					throws Exception {
//        Channel c= maybeGetChannel(ctx,ev);
//        tlog().debug("BasicChannelHandler: channelConnected - ctx {}, channel {}", 
//        						ctx, (c==null ? "?" : c.toString()) );		
//		super.channelConnected(ctx, ev);
//	}

//	@Override
//	public void channelDisconnected(ChannelHandlerContext ctx,
//					ChannelStateEvent ev) throws Exception {
//        Channel c= maybeGetChannel(ctx,ev);
//        tlog().debug("BasicChannelHandler: channelDisconnected - ctx {}, channel {}", 
//        						ctx, (c==null ? "?" : c.toString()) );		
//		super.channelDisconnected(ctx, ev);
//	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#channelOpen(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent ev)
					throws Exception {
        Channel c= maybeGetChannel(ctx,ev) ;
        tlog().debug("BasicChannelHandler: channelOpen - ctx {}, channel {}", 
        						ctx, ( c==null ? "?" : c.toString()) );
        		
        if (c != null) { _grp.add(c); }
        
		super.channelOpen(ctx, ev);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ev)
					throws Exception {

		tlog().error("", ev.getCause());
	
        Channel c= maybeGetChannel(ctx, ev);
        if (c != null) 
        	try {
        		c.close();
        } finally {
        		_grp.remove(c) ;        	
        }
//		super.exceptionCaught(ctx, e);
	}

//	@Override
//	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
//					throws Exception {
//		super.handleDownstream(ctx,e);
//	}

//	@Override
//	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
//					throws Exception {
//		super.handleUpstream(ctx,e);
//	}

	/**
	 * @param ctx
	 * @param ev
	 * @return
	 * @throws IOException
	 */
	protected boolean onRecvRequest( ChannelHandlerContext ctx, MessageEvent ev) 
			throws IOException {
		// false to stop further processing
		return true; 
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev)
					throws Exception {
		
        Object msg = ev.getMessage();

        if (msg instanceof HttpRequest || msg instanceof HttpResponse) {
        		HttpMessage m= (HttpMessage) msg;
        		msg_recv_0(m) ;
            _os= new ByteArrayOutputStream(4096);
            _props.clear();
        }
        
        if (msg instanceof HttpResponse) {
        	
    			HttpResponse res= (HttpResponse)msg;
    			HttpResponseStatus s= res.getStatus();
    			String reason= s.getReasonPhrase();
    			int code= s.getCode();    			
    			
        		tlog().debug("BasicChannelHandler: got a response: code {} {}", code, reason);
            
    			_props.put("reason", reason);
    			_props.put("dir", -1);
        		_props.put("code", code);
    			
    			if (code >= 200 && code < 300) {
        			onRes(s,ctx,ev);    				
    			}
//    			else if (code >= 300 && code < 400) {
//    				// TODO: handle redirect
//    			}
    			else {
    				onResError(ctx,ev);
    			}
        } 
        else
        if (msg instanceof HttpRequest) {
    			
        		tlog().debug("BasicChannelHandler: got a request: ");
    			
        		HttpRequest req= (HttpRequest)msg;
        		onReqIniz(ctx,ev);
            _keepAlive = HttpHeaders.isKeepAlive(req);            
			_props.put("dir", 1);
			
	        	if ( onRecvRequest(ctx,ev) ) {
	        		onReq(ctx,ev);
	        	}
        }
        else
        if (msg instanceof HttpChunk) {
            onChunk(ctx,ev) ;
        }
        else {
            throw new IOException(
				"BasicChannelHandler:  unexpected msg type: " + safeGetClzname(msg)) ;                
        }

	}

	private void onReq(ChannelHandlerContext ctx, MessageEvent ev) throws Exception {		
		HttpRequest msg = (HttpRequest)ev.getMessage();		
        if (msg.isChunked()) {
            tlog().debug("BasicChannelHandler: request is chunked");
        } else {
            sockBytes(msg.getContent());
            onMsgFinal(ctx,ev);                	
        }		
	}

	private void onRes(HttpResponseStatus rc, ChannelHandlerContext ctx, MessageEvent ev) throws Exception {
		HttpResponse msg = (HttpResponse)ev.getMessage();		        
		onResIniz(ctx,ev);		
		
		if (msg.isChunked()) {
            tlog().debug("BasicChannelHandler: response is chunked");
        } else {
            sockBytes(msg.getContent());
            onMsgFinal(ctx,ev);                	
        }		
	}
	
	protected void onReqIniz(ChannelHandlerContext ctx, MessageEvent ev ) {
		HttpRequest msg= (HttpRequest) ev.getMessage();
		String uri= msg.getUri();
		String m= msg.getMethod().getName();		
		tlog().debug("BasicChannelHandler: onReqIniz: Method {}, Uri {}", m, uri) ;
		onReqPreamble(m, uri, iterHeaders(msg)) ;
	}	
	
	protected void onResIniz(ChannelHandlerContext ctx, MessageEvent ev ) {
		HttpResponse msg= (HttpResponse) ev.getMessage();
		onResPreamble( iterHeaders(msg)) ;
	}	
	
	protected void onReqPreamble(String mtd, String uri, Map<String,StrArr> headers) {}
	protected void onResPreamble(Map<String,StrArr> headers) {}
	
	protected void doReqFinal(int code, String reason, StreamData out) {}
	protected void doResFinal(int code, String reason, StreamData out) {}
	protected void onResError(int code, String reason) {}
	
	private void onResError(ChannelHandlerContext ctx, MessageEvent ev) throws Exception {
		Channel cc= maybeGetChannel(ctx,ev);
		onResError( (Integer) _props.get("code"), (String) _props.get("reason"));
		if (!isKeepAlive()) {
			cc.close();
		}
	}
	
    private void sockBytes(ChannelBuffer cb) throws Exception {
        int c;
        if (cb != null) while ( (c=cb.readableBytes() ) > 0) {
            sockit_down(cb, c);
        }
    }
    
    private void sockit_down(ChannelBuffer cb, int count) throws Exception {
        
        byte[] bits= new byte[4096] ;
        int total=count;
        int len;
        
//        tlog().debug("BasicChannelHandler: socking it down {} bytes", count);
        
        while (total > 0) {
            len = Math.min(4096, total) ;
            cb.readBytes(bits, 0, len) ;
            _os.write(bits, 0, len) ;
            total = total-len;
        }
        
        _os.flush();
        
        if (_clen >= 0L) { _clen += count; }

        if (_clen > 0L && _clen > _thold) {
        		swap();
        }
    }
    
    private void swap() throws Exception {
	    	ByteArrayOutputStream baos= (ByteArrayOutputStream) _os;
	    	Tuple t= StreamUte.createTempFile(true);
	    	OutputStream os= (OutputStream) t.get(1);
	    	os.write(baos.toByteArray());
	    	os.flush();
	    	_os=os;
	    	_clen= -1L;
	    	_fOut= (File)t.get(0);
    }
    
    protected void doReplyError(ChannelHandlerContext ctx, MessageEvent ev, HttpResponseStatus err) throws IOException {
    		doReplyXXX(ctx,ev,err);
    }
    
    private void doReplyXXX(ChannelHandlerContext ctx, MessageEvent ev, HttpResponseStatus s) throws IOException {
    		DefaultHttpResponse res= new DefaultHttpResponse(HttpVersion.HTTP_1_1, s);
	    	Channel c= maybeGetChannel(ctx,ev);
	    	res.setChunked(false);
	    	res.setHeader("content-length", "0");
	    	c.write(res);
	    	if (! isKeepAlive()) {
	    		c.close();
	    	}    	
    }

    protected void doReqFinal(ChannelHandlerContext ctx, MessageEvent ev,StreamData inData) throws IOException {
    		doReplyXXX(ctx,ev,HttpResponseStatus.OK) ;
    }
    
    private void onMsgFinal(ChannelHandlerContext ctx, MessageEvent ev) throws IOException {
    		int dir = (Integer) _props.get("dir");
	    	StreamData out= on_msg_final(ev);
	    	if ( dir > 0) {
	    		doReqFinal(ctx,ev,out);
	    	} 
	    	else if (dir < 0) {	    		
	    		doResFinal( (Integer) _props.get("code"), (String)_props.get("reason"),out);	    		
	    	}
    }
    
    private StreamData on_msg_final(MessageEvent ev) throws IOException {        
        StreamData data= new StreamData();
        if (_fOut != null) {
            data.resetMsgContent(_fOut) ;
        }
        else
        if (_os instanceof ByteArrayOutputStream)        {            
            data.resetMsgContent(_os) ;
        }
        
        _os=StreamUte.close(_os);
        _fOut=null;
        
        return data;
    }
    
    	
	/**
	 * @param g
	 */
	public BasicChannelHandler(ChannelGroup g){
		_grp=g;
	}	
	
	/**
	 * @param t
	 * @return
	 */
	public BasicChannelHandler withThreshold(long t) {
		_thold=t; return this;
	}
	

	/**
	 * @param ctx
	 * @param ev
	 * @return
	 */
	protected Channel maybeGetChannel(ChannelHandlerContext ctx, ChannelEvent ev) {
		Channel cc= ev.getChannel();
		if (cc==null) { cc= ctx.getChannel(); }
		return cc;
	}
	
	private void msg_recv_0(HttpMessage msg) {
        String s= msg.getHeader(COOKIE);
        if ( ! isEmpty(s)) {
            Set<Cookie> cookies = new CookieDecoder().decode(s);
            if (!cookies.isEmpty()) {
                CookieEncoder enc = new CookieEncoder(true);
                for (Cookie c : cookies) {  enc.addCookie(c);  }
                _cookies= enc;
            }
        }		
	}
	
	private void onChunk(ChannelHandlerContext ctx, MessageEvent ev) throws Exception {		
		HttpChunk msg = (HttpChunk)ev.getMessage();
		sockBytes(msg.getContent());
        if (msg.isLast()) {                    
            onMsgFinal(ctx,ev);
        } 		
	}
	
	protected Map<String,StrArr> iterHeaders(HttpMessage msg) {
		Map<String,StrArr> hdrs= MP();
        List<String> lst;
        StrArr arr;
        StringBuilder dbg=null;
        
        if (tlog().isDebugEnabled()) { dbg=new StringBuilder(1024); }
        
        for (String n: msg.getHeaderNames()) {
            hdrs.put(n, arr=new StrArr());
            lst= msg.getHeaders(n);
            arr.add( lst.toArray(new String[0]));
            if (tlog().isDebugEnabled()) { dbg.append(n).append(": ").append(arr.toString()).append("\r\n"); }
        }
        
        tlog().debug("HttpResponseHdlr: headers\n{}", dbg.toString());
        
        return hdrs;
    }
	
}
