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

import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.safeGetClzname;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.mime.MimeConsts;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public enum MICUte {
;

    private static final String CRLF= "\r\n";
    private static final byte[] CRLFBITS= asBytes(CRLF);
    
    /**
     * Calculates a Message Integrity Check (Message Disposition)
     *
     * @param content The content across which the MIC is calculated. This can
     *                   be of type InputStream, String, byte[], or a Multipart (multipart/signed)
     * @param algorithm  The algorithm to use to calculate the MIC (e.g. sha1, md5)
     * @param addcrlfToString If true, adds a CRLF on the String data
     *                   since the receiving MTA uses canonicalized data to calculate MIC
     * @param addcrlfTobyte If true, adds a CRLF on the byte data
     *                   since the receiving MTA uses canonicalized data to calculate MIC
     * @return The base 64 encoded MIC value
     * @exception Exception
     */
    public static String calc(Object content, Certificate[] certs, String algo, 
            boolean addcrlfToString, 
            boolean addcrlfToBytes) throws Exception    {
        InputStream micStream= null;
        byte micBits[] = null;
        boolean addcrlf= false;
        
        if (content instanceof byte[]) {
            micBits= fromBytes( (byte[]) content, addcrlfToBytes);
        }
        else 
        if (content instanceof String) {
            micBits= fromString( (String) content, addcrlfToString);
        }
        else 
        if (content instanceof InputStream) {
            if ( addcrlfToBytes || addcrlfToString )
            addcrlf= true;
            micStream = (InputStream) content;
        }
        else 
        if (CryptoUte.isSigned(content)) {
            return fromSMP(certs, content);
        }
        else 
        if (content instanceof Multipart) {
            Object rc= fromMP( (Multipart) content);
            if (rc instanceof InputStream) micStream= (InputStream) rc;
            else
            if (rc instanceof byte[]) micBits= (byte[]) rc;
        }
        else {
            error(content);
        }

        MessageDigest messageDigest = MessageDigest.getInstance(algo);
        byte[] mic;
        
        if (micBits != null) {         
            mic= messageDigest.digest(micBits);
        }
        else {
            byte[] buf= new byte[4096];
            int c;
            while ((c= micStream.read(buf)) > 0) {
                messageDigest.update(buf, 0, c);
            }

            if ( addcrlf ) {
               messageDigest.update(CRLFBITS);
            }

            if ( micStream.markSupported())
            micStream.reset();

            mic= messageDigest.digest();
        }

        return Base64.encodeBase64String(mic);
    }

    
    /**
     * @param content
     * @param certs
     * @param algo
     * @return
     * @throws Exception
     */
    public static String calc(Object content, Certificate[] certs, String algo) 
    throws Exception    {
        return calc(content, certs, algo, false, false);
    }
    
    private static Object fromMP(Multipart mp) throws Exception    {
        ContentType ct = new ContentType(mp.getContentType());  
        BodyPart bp;
        Object contents;
        Object rc= null;
        int count = mp.getCount();
        
        if ( "multipart/mixed".equalsIgnoreCase(ct.getBaseType()))        {
            if ( count > 0 ) {
                bp = mp.getBodyPart(0);
                contents = bp.getContent();
                
                // check for EDI payload sent as attachment
                String ctype = bp.getContentType();
                boolean getNextPart = false;
                
                if (ctype.indexOf("text/plain") >= 0) {
                    if (contents instanceof String) {
                        String bodyText = "This is a generated cryptographic message in MIME format";
                        if (((String)contents).startsWith(bodyText)) {
                            getNextPart = true;
                        }
                    }
                    
                    if ( ! getNextPart) {
                        // check for a content disposition
                        // if disposition type is attachment, then this is a doc
                        getNextPart = true;
                        String disp = bp.getDisposition();
                        if (disp != null && disp.toLowerCase().equals("attachment"))
                            getNextPart = false;
                    }
                }
                
                if ( (count >= 2) && getNextPart ) {
                    bp = mp.getBodyPart(1);
                    contents = bp.getContent();
                }

                if ( contents instanceof String ) {
                    rc= asBytes( (String) contents);
                } 
                else 
                if ( contents instanceof byte[] ) {
                    rc= contents;
                } 
                else 
                if (contents instanceof InputStream) {
                    rc= contents;
                } 
                else {
                    String cn= contents==null ? "null" : contents.getClass().getName();
                    throw new Exception("Unsupport MIC object: " + cn);
                }
            }
        }
        else
        if ( count > 0 ) {
            bp = mp.getBodyPart(0);
            contents = bp.getContent();
            
            if ( contents instanceof String ) {
                rc= asBytes( (String) contents);
            } 
            else 
            if ( contents instanceof byte[] ) {
                rc= contents;
            } 
            else 
            if ( contents instanceof InputStream) {
                rc= contents;
            } 
            else  {
                String cn= contents==null ? "null" : contents.getClass().getName();
                throw new Exception("Unsupport MIC object: " + cn);
            }
        }

        return rc;
    }
    
    private static String fromSMP(Certificate[] certs, Object micContent) throws Exception    {
        
        if ( ! (micContent instanceof MimeMultipart) ) {
            error(micContent);
        }

        MimeMultipart mp= (MimeMultipart) micContent;
        Tuple rc= CryptoUte.verifySmimeDigSig(mp, certs, MimeConsts.CTE_BINARY);
        
        return Base64.encodeBase64String( (byte[]) rc.get(1));
    }
    
    private static byte[] fromString(String str, boolean wantcrlf) throws Exception    {
        if (wantcrlf) str= str + CRLF;
        return asBytes(str);
    }

    private static byte[] fromBytes(byte[] bits, boolean wantcrlf) throws Exception    {
        ByteOStream baos = new ByteOStream();        
        baos.write( bits);
                
        if (wantcrlf) {
            baos.write(CRLFBITS);
        }
        
        return baos.asBytes();        
    }
    
    private static void error(Object c) throws Exception    {
        throw new Exception("Unsupported MIC content object : " + safeGetClzname(c));
    }

    
    
}
