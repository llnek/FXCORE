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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zotoh.core.util.Logger;

import com.zotoh.netio.NetUte;

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.*;
import static com.zotoh.core.util.LoggerFactory.getLogger;

/**
 * @author kenl
 *
 */
public class SimpleFileServer {
    
    private transient Logger _log=getLogger(SimpleFileServer.class); 
    public Logger tlog() {   return _log;   }    
    
    private InetAddress _ip;
    private int _port;
    private Td _engine;
    
    /**
     * @param ipAddr
     * @param ipv6
     * @param port
     * @throws UnknownHostException
     * @throws IOException
     */
    public SimpleFileServer(String ipAddr, boolean ipv6, int port) 
    throws UnknownHostException, IOException    {
        
        tstEStrArg("ip-address", ipAddr);
        tstPosIntArg("port", port) ;
        
        _ip= isEmpty(ipAddr) ? InetAddress.getLocalHost() : 
                InetAddress.getByAddress(NetUte.ipv4ToBytes(ipAddr));
        _port=port;
    }
    
    
    /**
     * @param host
     * @param port
     * @throws UnknownHostException
     */
    public SimpleFileServer(String host, int port) throws UnknownHostException    {
        
        tstPosIntArg("port", port) ;
        //tstEStrArg("host", host);
        
        _ip= NetUte.getNetAddr(host);
        _port=port;
    }
    
    
    /**
     * @param port
     * @throws UnknownHostException
     */
    public SimpleFileServer(int port) throws UnknownHostException    {
        this("", port);
    }

    
    /**
     * @throws IOException
     */
    public void start() throws IOException    {
        _engine= new Td(new ServerSocket(_port, 0, _ip) ) ;
        _engine.start();
    }


    /**
     * 
     */
    public void stop() {
    	_engine.halt();
    }
    
    
    
}

/**
 * @author kenl
 *
 */
class Td extends Thread  implements RejectedExecutionHandler {
    
    private ServerSocket _ssoc;
    
    /* (non-Javadoc)
     * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)        {
        //TODO: better print out
        tlog().warn("Threadpool: rejectedExecution!");
    }
    
    /**
     * 
     */
    protected void halt() {
    	_ssoc= NetUte.close(_ssoc);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()        {
        ThreadPoolExecutor pe= new ThreadPoolExecutor(4, 8, 5000, TimeUnit.MILLISECONDS, 
                new LinkedBlockingQueue<Runnable>(), 
                this);
        boolean stopped=false;
        
        while ( ! stopped) {
            try {
                pe.execute( new FileServerHandler( _ssoc, _ssoc.accept() ));
            }
            catch (Exception e) {                
                NetUte.close(_ssoc);
                //_lg.warn("", e);
                stopped=true;
            }
        }
        
        tlog().debug("FileServer: halted") ;
        return;
    }
    
    /**
     * @param ssoc
     */
    protected Td(ServerSocket ssoc)        {
        _ssoc=ssoc;
        setDaemon(true);
    }
    
}


/**
 * @author kenl
 *
 */
class FileServerHandler implements Runnable {
    
	private ServerSocket _ssoc;
    private Socket _soc;


    /**
     * @param ssoc
     * @param soc
     */
    public FileServerHandler(ServerSocket ssoc, Socket soc )    {
        _soc=soc;
        _ssoc=ssoc;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()    {
    		boolean halt=false;
        try         {
            BufferedReader rdr  = new BufferedReader(new InputStreamReader(_soc.getInputStream()));
            boolean stopped= false;            
            tlog().debug("FileServerHandler: run started");                    
            while ( ! stopped)  {
                String s, cmd=null, 
                p1=null;

                if ((s=rdr.readLine()) != null) {
                    int pos= s==null ? -1 : indexOfAnyChar(s, " \t".toCharArray());
                    cmd = pos >= 0 ? s.substring(0,pos) : "";
                    p1= pos >=0 ? s.substring(pos+1) : null;
                    if (cmd != null) cmd=cmd.trim();
                    if (p1 != null) p1=p1.trim();
                }
                
                tlog().debug("FileServer: cmd= {}, fp={}", cmd, p1) ;
                
                if ("rcp".equalsIgnoreCase(cmd)) {
                    // command expected: rcp <SPACE> filepath
                    File fp = new File(p1);
                    long len=0L;
                    
                    if ( fp.exists() && fp.canRead()) {
                        len= fp.length();
                    }
                    
                    DataOutputStream out = new DataOutputStream(_soc.getOutputStream());
                    FileInputStream fin=new FileInputStream(fp);
                    
                    tlog().debug("FileServer: clen= {}", len) ;
                    try {
                        out.writeLong(len);
                        out.flush();                    
                        streamToStream(fin, out);
                    }
                    finally {                    
                        close(fin);
                    }
                    tlog().debug("FileServer: file served") ;
                }
                else    
                if ("rrm".equalsIgnoreCase(cmd))  {
                    File fp= new File(p1);
                    boolean ok=false;
                    
                    if ( ! fp.exists()) {
                        tlog().debug("File doesn't exist: {}", fp.getCanonicalPath());
                    }
                    else  {
                        ok= fp.delete();
                        tlog().debug("File: {} {}", fp.getCanonicalPath() , (ok ? "deleted" : "not deleted"));
                    }
                }
                else
            	if ("stop".equalsIgnoreCase(cmd)) {
            		halt=stopped=true;
            	}
                else {
                    tlog().debug("FileServerHandler: stopped");                    
                    stopped=true;
                }                
            }
        } 
        catch (Exception e) {         
            //tlog().warn("", e);
        } 
        finally {        
            NetUte.close(_soc);
        }
        
        if (halt) NetUte.close(_ssoc);
    }
    
    
    
    
}
