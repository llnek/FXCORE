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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.WWID.generate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMessage;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public class FileUploader {
    
    private transient Logger _log=getLogger(FileUploader.class); 
    public Logger tlog() {         return _log;    }
    
    private final Map<File,String> _clientFnames= MP();
    private final Map<String,String> _fields= MP();
    private final List<File> _files= LT(),
    _atts= LT();    
    private String _url;
    
    /**
     * @param args
     * @throws Exception
     */
    public void start(String[] args) throws Exception    {
        if ( parseArgs(args)) {        	
	        upload(new BasicHttpMsgIO() {
	            public void onOK(int code, String reason, StreamData res) {
	                System.out.println("Done: status=" + code);
	            }
	        });
        }
    }
    
    /**
     * 
     */
    public FileUploader()
    {}
    
    /**
     * @param name
     * @param value
     */
    public void addField(String name, String value)    {
        if ( !isEmpty(name) && !isEmpty(value))
        _fields.put(name, value);
    }

    /**
     * @param path
     * @param clientFname
     * @throws IOException
     */
    public void addAtt(File path, String clientFname) throws IOException    {
        addOneAtt(  path, nsb(clientFname));
    }

    
    /**
     * @param path
     * @throws IOException
     */
    public void addAtt(File path) throws IOException    {
        addAtt(path, "");
    }

    
    /**
     * @param path
     * @param clientFname
     * @throws IOException
     */
    public void addFile(File path, String clientFname) throws IOException    {
        addOneFile( path, nsb(clientFname));
    }

    
    /**
     * @param path
     * @throws IOException
     */
    public void addFile(File path) throws IOException    {
        addFile(path, "");
    }

    
    /**
     * @param url
     */
    public void setUrl(String url)    {
        _url= nsb(url);
    }
    
    
    /**
     * @throws IOException
     */
    public void send(HttpMsgIO cb) throws IOException    {
        try         {
            upload(cb);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    private void upload(final HttpMsgIO cb) throws Exception   {
        
        Tuple t= preload();
        
        final String ctype= (String) t.get(1);        
        File fo= (File) t.get(0);
        StreamData in= new StreamData(fo);
        
        tlog().debug("FileUploader: posting to url: {}" , _url);
        
        HttpUte.simplePOST(new URI(_url), in, new WrappedHttpMsgIO(cb) {
        	public void configMsg(HttpMessage m) {
        		super.configMsg(m);
        		m.setHeader("content-type", ctype);
        	}        	
        });
    }

    private Tuple preload() throws IOException     {
        Tuple t=createTempFile(true) ;
        OutputStream out= null;
        try  {
            return new Tuple( t.get(0), fmt( out=(OutputStream) t.get(1) ) );
        }
        finally         {
            close(out);
        }
    }
    
    private String fmt(OutputStream out) throws UnsupportedEncodingException, IOException    {
        String boundary = generate();
        int n;
        
        // fields
        for (String f : _fields.keySet()) {
            writeOneField(boundary, f, _fields.get(f), out);
        }
        
        // files
        n=1;
        for (File path: _files) {
            writeOneFile(boundary, "file."+n, path, "binary", out);
            ++n;
        }
        
        // atts
        n=1;
        for (File path: _atts) {
            writeOneFile(boundary, "att."+n, path, "binary", out);
            ++n;
        }
    
        StringBuilder bf= new StringBuilder(256)
        .append("--").append(boundary)
        .append("--\r\n");
        
        out.write(bf.toString().getBytes("UTF-8"));
        out.flush();
        
        return "multipart/form-data; boundary=" + boundary;
    }
    
    private void writeOneField(String boundary, String field, String value, OutputStream out) throws UnsupportedEncodingException, IOException    {
        StringBuilder bf= new StringBuilder(1024);

        bf
        .append("--").append(boundary).append("\r\n")        
        .append("Content-Disposition: form-data; ")
        .append("name=\"").append(field)
        .append("\"\r\n")
        .append("\r\n")               
        .append(value).append("\r\n");
        
        out.write(bf.toString().getBytes("UTF-8"));
        out.flush();
    }
    
    private void writeOneFile(String boundary, String field, 
            File path, String cte,  OutputStream out) 
        throws UnsupportedEncodingException, IOException     {
        
        StringBuilder bf= new StringBuilder(512);
        String fname=path.getName();
        long clen= path.length();
        String cfn= _clientFnames.get(path);
        
        if (!isEmpty(cfn)) {    fname=cfn;        }
        
        bf
        .append("--").append(boundary).append("\r\n")        
        .append("Content-Disposition: form-data; ")
        .append("name=\"").append(field).append("\"; filename=\"")
        .append(fname).append("\"\r\n")
        .append("Content-Type: application/octet-stream\r\n")
        .append("Content-Transfer-Encoding: " + cte + "\r\n")
        .append("Content-Length: ").append(Long.toString(clen)).append("\r\n")
        .append("\r\n");
        
        out.write(bf.toString().getBytes("UTF-8"));
        out.flush();

        InputStream inp=null;
        try  {
            inp= new FileInputStream(path);
            streamToStream(inp, out, clen);        
        }
        finally {
            close(inp);
        }
        
        out.write("\r\n".getBytes("UTF-8"));
        out.flush();
                
    }
    
    private boolean usage()    {
        System.out.println("FileUpload url -p:a=b -p:c=d -f:f1 -f:f2 -a:a1 -a:a2 ...");
        System.out.println("e.g.");
        System.out.println("FileUpload http://localhost:8003/HelloWorld -p:a=b -f:/temp/a.txt -a:/temp/b.att");
        System.out.println("");
        return false;
    }

    private boolean parseArgs(String[] av) throws IOException    {
        
        if (av.length < 2)        {   return usage();        }
        _url=av[0];

        String[] ss;
        String s;
        for (int i=1; i < av.length; ++i)        {
            s= av[i];            
            if (s.startsWith("-p:"))            {
                ss=s.substring(3).split("=");
                addField(ss[0],ss[1]);
            }
            else
            if (s.startsWith("-f:"))            {
                addFile(new File(s.substring(3)));
            }
            else
            if (s.startsWith("-a:"))            {
                addAtt( new File(s.substring(3)));                
            }
        }
                
        return true;
    }

    private void addOneFile(File path, String clientFname)    {
        tstObjArg("file-url", path);
        _clientFnames.put(path, clientFname);
        _files.add(path);
    }

    private void addOneAtt(File path, String clientFname)    {
        tstObjArg("file-url", path);
        _clientFnames.put(path, clientFname);
        _atts.add(path);
    }
    
}
