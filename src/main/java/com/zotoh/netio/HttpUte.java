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

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.mail.Header;
import javax.net.ssl.SSLContext;

import org.jboss.netty.handler.codec.http.HttpMessage;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.StrArr;

/**
 * @author kenl
 *
 */
public enum HttpUte {
;

    private static Logger _log=getLogger(HttpUte.class); 
    public static Logger tlog() {  return _log;    }        
    private static long s_td= 8 * 1024 * 1024; // 8 Meg
    private static SSLContext _client;
    
    static {
    		iniz();
    }
    
    /**
     * @return
     */
    public static SSLContext getClientSSL() {    	return _client;    }

    /**
     * @return
     */
    public static long getDefThreshold() {       return s_td;    }
    
    /**
     * @param url
     * @param in
     * @param cb
     * @return
     * @throws Exception
     */
    public static HttpClientr simplePOST(URI url, StreamData in, HttpMsgIO cb) 
                throws Exception    {
        
        tstObjArg("post-url", url) ;
        tstObjArg("data", in) ;
        
        HttpClientr cr= new HttpClientr();
        cr.connect(url);
        cr.post(cb,in);
        return cr;
    }

    /**
     * @param ssl
     * @param host
     * @param port
     * @param uriPart
     * @param in
     * @param cb
     * @return
     * @throws Exception
     */
    public static HttpClientr simplePOST(boolean ssl, String host, int port, 
            String uriPart, StreamData in, HttpMsgIO cb) throws Exception    {
        
        URI u= null;
        
        try  {
            u =new URI(ssl? "https":"http", null, host, port, uriPart, null, null);
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }

        return simplePOST(u, in, cb);
    }

    
    /**
     * @param url
     * @param cb
     * @return
     * @throws Exception
     */
    public static HttpClientr simpleGET(URI url, HttpMsgIO cb)
    throws Exception    {
        
        tstObjArg("get-url", url);
        
        HttpClientr cr= new HttpClientr();
        cr.connect(url);
        cr.get(cb);
        
        return cr;
    }
    
    /**
     * @param ssl
     * @param host
     * @param port
     * @param uriPart
     * @param query
     * @param cb
     * @return
     * @throws Exception
     */
    public static HttpClientr simpleGET(boolean ssl, String host, int port, 
            String uriPart, String query, HttpMsgIO cb)
    throws Exception     {
        
        URI u= null;        
        try        {
            u=new URI(ssl?"https":"http", null, host, port, uriPart, query, null);
        }
        catch (URISyntaxException e) {
            throw new IOException (e);
        }
        
        return simpleGET(u, cb) ;
    }

    /**
     * @param hds
     * @return
     */
    public static String writeHeaders(Map<String,String> hds)    {
        StringBuilder bd= new StringBuilder(512);
        for (Map.Entry<String, String> en : hds.entrySet()) {
            bd.append(en.getKey()).append(": ")
            .append(en.getValue())
            .append("\r\n");
        }
        return bd.toString();
    }

    
    /**
     * @param hds
     * @return
     */
    public static String writeHeaders(Header[] hds)    {
        StringBuilder bd= new StringBuilder(512);
        for (int i=0; i < hds.length; ++i) {
            bd.append(hds[i].getName())
            .append(": ")
            .append(hds[i].getValue())
            .append("\r\n");
        }
        return bd.toString();
    }

    public static void wrHds(HttpMessage m, Map<String,StrArr> hds)    {
        String[] arr;
        String k;
        for (Map.Entry<String,StrArr> en : hds.entrySet()) {
            arr= en.getValue().toArray();
            k=en.getKey();
            if (arr != null) for (int i=0; i < arr.length; ++i) {
                m.addHeader(k, nsb(arr[i])) ;                
            }
        }
    }

    @SuppressWarnings("unused")
    private static int tstConnectRefuse(ConnectException e) {
        String s= e.getMessage().toLowerCase();
        int rc= -1;

        if (s.indexOf("connection") >= 0 && s.indexOf("refused") >= 0 &&
                s.indexOf("connect") >= 0) {
            rc= 0;
        }

        return rc;
    }
    

    private static void iniz() {
        try {
            _client= SSLContext.getInstance("TLS");
            _client.init(null, SSLTrustMgrFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the client-side SSLContext", e);
        }       
    }

    
}




