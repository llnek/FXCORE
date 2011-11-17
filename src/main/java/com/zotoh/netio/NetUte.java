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

import static com.zotoh.core.util.ByteUte.readAsBytes;
import static com.zotoh.core.util.CoreUte.getEnvVar;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.split;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.RTE;


/**
 * @author kenl
 *
 */
public enum NetUte {
;

    private static Logger _log=getLogger(NetUte.class); 
    public static Logger tlog() {  return _log;    }    

    /**
     * @param host
     * @return
     * @throws UnknownHostException
     */
    public static String getHostAddress(String host) throws UnknownHostException    {        
        try {
            if ( isEmpty(host) )
            return InetAddress.getLocalHost().getHostAddress();            
            else
            return InetAddress.getByName(host).getHostAddress();            
        }
        catch (UnknownHostException e) { throw e; }
        catch (Exception e) {
            throw new UnknownHostException("unknown host: " + host);
        }
    }
    
    /**
     * @return
     */
    public static String getLocalHost()  {
    	try {
    		return InetAddress.getLocalHost().getHostName();
    	}
    	catch (UnknownHostException e) {
    		// we really don't want to have a checked exception for something
    		// so simple.!
    		// this really shouldnt happen, right?
    		throw new RTE(e);
    	}
    }
    
    /**
     * @param s
     * @return
     */
    public static boolean isLHost(String s)    {
        return isEmpty(s) ;//|| "localhost".equals(s) || "127.0.0.1".equals(s);
    }
    
    /**
     * @param host
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getNetAddr(String host) throws UnknownHostException    {
        return isLHost(host) ?  InetAddress.getLocalHost()
        :
        InetAddress.getByName(host);
    }
    
    /**
     * @param ip
     * @return
     * @throws IOException
     */
    public static byte[] ipv4ToBytes(String ip) throws IOException     {
        String[] s= split( nsb(ip),".");
        byte[] bits= null;
        int n;
                
        if (s !=null && s.length==4)         {
            bits= new byte[4];
            for (int i=0; i < 4; ++i)            {
                try { n= Integer.parseInt(s[i]); } catch (Exception e) {n=255;}
                if ( n < 0 || n > 254) {
                    bits=null;
                }
                if (bits != null)
                try {
                    bits[i] = readAsBytes(n)[3];
                }
                catch (IOException e) {
                    bits=null;
                }
                
                if (bits==null) 
                break;                
            }
        }
        
        if (bits==null) throw new IOException("Malformed IPv4 address: " + ip);        
        return bits;
    }

    
    /**
     * @param soc
     * @return null
     */
    public static ServerSocket close(ServerSocket soc)    {
        try { if (soc != null) soc.close(); } catch (Exception e) {}
        return null;
    }

    
    /**
     * @param soc
     * @return null
     */
    public static Socket close(Socket soc)    {
        try { if (soc != null) soc.close(); } catch (Exception e) {}
        return null;
    }
    
    
    /**
     * @param url
     * @return
     */
    public static String resolveAndExpandFileUrl(String url)     {
        return url==null ? null : url.startsWith("file:") ? expandW32Url(expandUNXUrl(url)) : url;
    }

    /**
     * Make the domain name lowercase, keeping the name-id part
     * case sensitive.
     *
     * @param email
     * @return
     */
    public static String canonicalizeEmailAddress(String email)    {
        StringBuilder buf=new StringBuilder(256);
        String l,r;
        int pos=nsb(email).indexOf("@");
        if (pos > 0) {
            l=email.substring(0,pos);
            r=email.substring(pos);
            buf.append(l).append(r.toLowerCase());
            email=buf.toString();
        }
        return email;
    }
    
    
    /**
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static int getPort(String url) throws URISyntaxException    {
        return url==null ? -1 : new URI(url).getPort();
    }

    
    /**
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static String getHostPartUri(String uri) throws URISyntaxException    {
        return uri==null ? null : new URI(uri).getHost();
    }

    
    /**
     * @param soc
     * @return
     * @throws IOException
     */
    public static byte[] sockItAsBits(InputStream soc) throws IOException    {
        
        BufferedInputStream bf= new BufferedInputStream( soc);
        ByteOStream baos= new ByteOStream(10000);
        byte[] buf= new byte[4096];
        int c;
        
        while ( (c=bf.read(buf)) != -1) {
            baos.write(buf,0,c);
        }
        
        return baos.asBytes();
    }
    
    private static String expandW32Url(String url)    {
        
        int last = url.length() - 1,
        head= -1,
        tail = -1;
        String var, env, lf, rt;

        if ((head = url.indexOf('%')) >= 0 && head < last) {
            tail = url.indexOf('%', head + 1);
        }

        if (head >= 0 && tail > head) {
            
            rt = url.substring(tail + 1);
            lf = url.substring(0, head);

            var = url.substring(head + 1, tail);
            env = System.getProperty(var);
            if (env == null) {
                env = getEnvVar(var);
            }

            url= expandW32Url( lf + env + rt );
        }

        return url;
    }

    private static String expandUNXUrl(String url)    {
        
        int last = url.length() - 1,
        head = -1,
        tail = -1;        
        String var, env, lf, rt;

        if ((head = url.indexOf('$')) >= 0 && head < last)        {
            tail = url.indexOf('}', head + 1);
        }

        if (head >= 0 && tail > head)        {
            
            var = url.substring(head + 1, tail);
            rt = url.substring(tail + 1);
            lf = url.substring(0, head);
            
            if ('{' == var.charAt(0)) {
                var = var.substring(1);
                env = System.getProperty(var);
                if (env == null) {
                    env = getEnvVar(var);
                }
                url= expandUNXUrl(  lf + env + rt);
            }
        }

        return url;
    }

}
