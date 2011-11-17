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
 
package com.zotoh.core.mime;

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.zotoh.core.util.Logger;

/**
 * This is a utility class that provides various MIME related functionality.
 *
 * @author kenl
 *
 */
public enum MimeUte implements MimeConsts {    
;
    
    private static Logger _log=getLogger(MimeUte.class); 
    public static Logger tlog() {  return _log;   }    

	
    /**
     * @param cType
     * @return
     */
    public static boolean isSigned(String cType)    {    		
        cType= nsb(cType).toLowerCase();
        //tlog().debug("MimeUte:isSigned: ctype={}", cType);
        return ( cType.indexOf("multipart/signed") >=0 ) ||                    
                    (isPKCS7mime(cType) && (cType.indexOf("signed-data") >=0) );            
    }

    
    /**
     * @param cType
     * @return
     */
    public static boolean isEncrypted(String cType)    {
        cType= nsb(cType).toLowerCase();        
        //tlog().debug("MimeUte:isEncrypted: ctype={}", cType);
        return ( isPKCS7mime(cType)  &&  (cType.indexOf("enveloped-data") >= 0) );
    }

    
    /**
     * @param cType
     * @return
     */
    public static boolean isCompressed(String cType)    {
        cType= nsb(cType).toLowerCase();
        //tlog().debug("MimeUte:isCompressed: ctype={}", cType);
        return ( cType.indexOf("application/pkcs7-mime") >= 0 ) &&            
                (cType.indexOf("compressed-data") >= 0 );
    }

    
    /**
     * @param cType
     * @return
     */
    public static boolean isMDN(String cType)    {
        cType= nsb(cType).toLowerCase();
        //tlog().debug("MimeUte:isMDN: ctype={}", cType);
        return (cType.indexOf("multipart/report") >=0) &&            
                (cType.indexOf("disposition-notification") >= 0);
    }

	
    /**
     * @param obj
     * @return
     * @throws Exception
     */
    public static InputStream maybeAsStream(Object obj) 
            throws Exception    {
        InputStream inp = null;
        
        if (obj instanceof byte[]) { inp= asStream((byte[]) obj); }
        else
        if (obj instanceof InputStream) { inp = (InputStream) obj; }
        else
        if (obj instanceof String)        {
            inp = asStream(asBytes((String) obj));
        }
        
        return inp;
    }

    
    /**
     * @param u
     * @return
     */
    public static String urlDecode(String u) {
        if (u != null)
        try {
            u= URLDecoder.decode(u, "UTF-8");
        }
        catch (Exception e) {
            u=null;
        }
        return u;
    }

    
    /**
     * @param u
     * @return
     */
    public static String urlEncode(String u) {
        if (u != null)
        try {
            u= URLEncoder.encode(u, "UTF-8");
        }
        catch (Exception e) {
            u= null;
        }
        return u;
    }

    private static boolean isPKCS7mime(String s)    {
        return (s.indexOf("application/pkcs7-mime") >=0) || 
        	(s.indexOf("application/x-pkcs7-mime") >=0) ;
    }
 
}
