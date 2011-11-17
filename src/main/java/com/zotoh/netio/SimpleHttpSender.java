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

import static com.zotoh.core.io.StreamUte.readBytes;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;

/**
 * @author kenl
 *
 */
public class SimpleHttpSender  {
    
    private transient Logger _log=getLogger(SimpleHttpSender.class);  
    public Logger tlog() { return _log;  }    
    
    private HttpClientr _client;
    private String _doc, _url; 
    
    
    /**
     * @param args
     */
    public static void main(String[] args)    {       
        try {
            new SimpleHttpSender().start(args);
        }
        catch (Throwable t) {        
            t.printStackTrace();
        }
    }

    private void start(String[] args) throws Exception { 
        
        if ( !parseArgs(args)) {		usage();		return;      }
        
        	_client= send(new BasicHttpMsgIO() {
        		
                public void onOK(int code, String reason, StreamData res) {
                    try 
                    {
                        System.out.println("Response Status Code: " +  code);
                        System.out.println("Response Data: " + 
                            ((res!=null && res.getSize() > 0L) 
                                    ? CoreUte.asString(res.getBytes() ) : ""));
                    } 
                    catch (Exception e) {}
                }
                
                public void onError(int code, String reason) {
                    System.out.println("Error: code =" + code + ", reason=" + reason);
                }        	    
        	});
        	_client.block();
    }
    
    private HttpClientr send(HttpMsgIO cb) throws Exception     {
        
        byte[] bits= null; 
        HttpClientr t;

        if ( !isEmpty(_doc)) {
            bits= readBytes( new File(_doc) );
        }
                
        if ( CoreUte.isNil(bits)) {
        		t=HttpUte.simpleGET( new URI(_url), cb);
        }
        else {
            t= HttpUte.simplePOST( new URI(_url), new StreamData( bits), cb) ;        	
        }
        
        return t;
    }

    private void usage()    {
        
        System.out.println("HttpSender  <URL> [ <docfile> ]");
        System.out.println("e.g.");
        System.out.println("HttpSender http://localhost:8080/SomeUri?x=y ");
        System.out.println("");
        System.out.println("");
        System.out.println("");

    }

    
    private boolean parseArgs(String[] args) throws URISyntaxException, 
    		MalformedURLException     {
        
        int pos=0;
        
		if (args.length < 1) { return false; }    		
        _url= args[0];
        ++pos;
        
        if (args.length > pos) {
        		_doc=args[pos];
        }
        
        return true;
    }

}
