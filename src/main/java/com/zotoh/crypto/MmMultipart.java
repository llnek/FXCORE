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
 
package com.zotoh.crypto;

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.copyStream;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.zotoh.core.util.Logger;

import com.sun.mail.util.LineInputStream;
import com.zotoh.core.io.ByteFragment;
import com.zotoh.core.io.ByteFragmentInputStream;
import com.zotoh.core.io.SmartFileInputStream;

/**
 * @author kenl
 *
 */
public class MmMultipart extends MimeMultipart {
    
    private transient Logger _log=getLogger(MmMultipart.class);  
    public Logger tlog() {  return _log; }    
    private InternetHeaders _headers;

    /**
     * @param ds
     * @throws MessagingException
     */
    public MmMultipart(DataSource ds) throws MessagingException     {
        super(ds);
    }

    
    /**
     * @param content
     * @throws MessagingException
     */
    public MmMultipart(byte[] content) throws MessagingException    {
        
        tstObjArg("input-bits", content) ;
        
    	InputStream inp= new ByteFragmentInputStream(content);
    	_headers=new InternetHeaders(inp);    	
    	contentType= getHeader(_headers, "content-type");
    	ds= new MmDataSource(inp, contentType);    	
    	parsed=false;
    }

    
    /**
     * @param rawStream
     * @throws MessagingException
     * @throws IOException
     */
    public MmMultipart(InputStream rawStream) throws MessagingException, IOException     {
        
        tstObjArg("input-stream", rawStream) ;
        
        File fp= copyStream(rawStream);
        rawStream = readStream(fp);
        try            {
            _headers=new InternetHeaders(rawStream);
            contentType= getHeader(_headers, "content-type");
        }
        finally {
            close(rawStream);
        }
        ds= new MmDataSource(new SmartFileInputStream(fp), contentType);
        parsed=false;
        
    }

    
    /**
     * 
     */
    public MmMultipart()    {
        
        String uid= System.getProperty("mime.rfc2822.user","popeye");
        String ct=contentType;

        if( ! "javamail".equals(uid))        {
            try            {
                javax.mail.internet.ContentType ctt = new javax.mail.internet.ContentType(ct);
                ctt.setParameter(
                        "boundary", getRFC2822MsgID().replace('@', '.'));
                contentType = ctt.toString();
            }
            catch (Exception e) {            
                tlog().warn("", e);   
                contentType=ct;
            }
        }
    }

    
    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMultipart#getCount()
     */
    @Override
    public synchronized int getCount() throws MessagingException     {
        maybeParse();        
        return super.getCount();
    }

    
    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMultipart#getBodyPart(int)
     */
    @Override
    public synchronized BodyPart getBodyPart(int index) throws MessagingException     {
        // 0-based index
        maybeParse();        
        return super.getBodyPart(index);
    }

    
    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMultipart#getBodyPart(java.lang.String)
     */
    @Override
    public synchronized BodyPart getBodyPart(String CID) throws MessagingException    {
        maybeParse();        
        return super.getBodyPart(CID);
    }

    
    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMultipart#writeTo(java.io.OutputStream)
     */
    @Override
    public void writeTo(OutputStream os) throws IOException, MessagingException    {
        maybeParse();        
        super.writeTo(os);
    }

    private synchronized void __parse__() throws MessagingException    {
        
        if (parsed) {        return; }

        InputStream inp = null;
        try        {
            inp = ds.getInputStream();
        }
        catch (Exception e) {        
            throw new MessagingException("No inputstream from datasource");
        }

        if ( !  (inp instanceof ByteFragmentInputStream))
        return;        

        ContentType cType = new ContentType(contentType);
        String boundary = "--" + cType.getParameter("boundary");
        byte[] bndbytes = boundary.getBytes();
        int bl = bndbytes.length;

        try        {
            // Skip the preamble
            LineInputStream lin = new LineInputStream(inp);
            String line;

            while ((line = lin.readLine()) != null) {
                if (line.trim().equals(boundary))
                break;
            }

            if (line == null) {
                throw new MessagingException("no start boundary");
            }
            
            // make sure we're dealing with a ReadOnlyBAInputStream so the copy
            // will be efficient.  otherwise, just let the regular MimeMultipart
            // do its thing.

            ByteFragmentInputStream bais = (ByteFragmentInputStream) inp;
            boolean done = false;

            // Read and process body parts until we see the
            // terminating boundary line (or EOF).
            while ( ! done)            {
                InternetHeaders headers = new InternetHeaders(bais);

                if (! bais.markSupported()) {
                    throw new MessagingException("stream does not support Mark()");
                }

                int startOfBlock = bais.getPos(),                
                blockLen = 0, b, b2, i,                
                eolChars = 0;      // how many end of line characters have we read?
                boolean bol = true;    // beginning of line flag

                while (true)                {
                    if (bol) {
                        // At the beginning of a line, check whether the
                        // next line is a boundary.

                        bais.mark(bl + 4 + 1000); // bnd + "--\r\n" + lots of LWSP

                        // read bytes, matching against the boundary
                        for (i = 0; i < bl; ++i) {
                            if (bais.read() != bndbytes[i])
                            break;
                        }

                        if (i == bl) {
                            // matched the boundary, check for last boundary
                            b2 = bais.read();
                            if (b2 == '-') {
                                if (bais.read() == '-') {
                                    done = true;
                                    break;	// ignore trailing text
                                }
                            }

                            // skip linear whitespace

                            while (b2 == ' ' || b2 == '\t') {
                                b2 = bais.read();
                            }

                            // check for end of line

                            if (b2 == '\n')
                            break;

                            if (b2 == '\r') {
                                bais.mark(1);

                                if (bais.read() != '\n')
                                bais.reset();

                                break;
                            }
                        }

                        // failed to match, reset and proceed normally
                        bais.reset();

                        // if this is not the first line, write out the
                        // end of line characters from the previous line
                        if (eolChars != 0) {
                            blockLen += eolChars;
                            eolChars = 0;
                        }
                    }

                    // read the next byte
                    if ((b = bais.read()) < 0) {
                        done = true;
                        break;
                    }

                    // If we're at the end of the line, save the eol characters
                    // to be written out before the beginning of the next line.
                    if (b == '\r' || b == '\n') {
                        bol = true;
                        ++eolChars;

                        if (b == '\r') {
                            bais.mark(1);
                            if ((b = bais.read()) == '\n') ++eolChars;
                            else
                            bais.reset();
                        }
                    } 
                    else {
                        bol = false;
                        ++blockLen;
                    }
                }

                MimeBodyPart part =
                        new MmBodyPart(headers, new ByteFragment(bais.getBuf(), startOfBlock, blockLen));
                addBodyPart(part);
            }
        }
        catch (IOException e) {        
            throw new MessagingException("i/o error", e);
        }

        parsed = true;
    }

    private void maybeParse()    {
        try        {
            __parse__();
        }
        catch (Exception e) {        
            tlog().warn("", e);
        }
    }

    private static String getHeader(InternetHeaders hds, String name)    {
        String[] s= name==null ? null : hds.getHeader(name);
        return isNilArray(s) ? null : s[0];
    }

    private static String getRFC2822MsgID()    {
        String uid= System.getProperty("mime.rfc2822.user", "");
        String host= "localhost";

        if ( ! isEmpty(uid)) {        
            uid= "." + uid;
        }
        
        try        {
            InetAddress ip = InetAddress.getLocalHost();
            if (ip != null)             
            host= ip.getHostAddress();            
        }
        catch (Exception e)        
        {}
        
        host= "@" + host;

        // <hashcode>.<currentTime>.<suffix>
        StringBuilder s = new StringBuilder()
        .append( Math.abs(host.hashCode()) )
        .append(".")
        .append(System.nanoTime())
        .append(".")
//        .append("popeye")
        .append(uid)
        .append(host);

        return s.toString();
    }

}
