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

/**
 * @author kenl
 *
 */
public interface MimeConsts {
    
    public static final String CTE_QUOTED= "quoted-printable";
    public static final String CTE_7BIT= "7bit";
    public static final String CTE_8BIT= "8bit";
    public static final String CTE_BINARY= "binary";
    public static final String CTE_BASE64= "base64";

    
    public static String  MIME_USER_PROP    = "mime.rfc2822.user";
    public static String  MIME_USER_JAVAMAIL   = "javamail";
    public static String  DEF_USER    = "popeye";
    public static String  MIME_USER_PREFIX   = "zotoh";
    public static String  DEF_HOST    = "localhost";
    public static String   MIME_HEADER_MSGID  = "Message-ID";
    public static String MIME_MULTIPART_BOUNDARY  = "boundary";
    public static String  DOT   = ".";
    public static String  AT  = "@";
    public static char CH_DOT   = '.';
    public static char CH_AT  = '@';
    public static String  STR_LT   = "<";
    public static String STR_GT    = ">";
    public static final int ALL   = -1;
    public static final int  ALL_ASCII   = 1;
    public static final int MOSTLY_ASCII   = 2;
    public static final int MOSTLY_NONASCII   = 3;

    // Capitalized MIME constants to use when generating MIME headers
    // for messages to be transmitted.
    public static final String AS2_VER_ID      = "1.1";
    public static final String UA    = "user-agent";
    public static final String TO     = "to";
    public static final String FROM    = "from";
    public static final String AS2_VERSION      = "as2-version";
    public static final String AS2_TO     = "as2-to";
    public static final String AS2_FROM    = "as2-from";
    public static final String SUBJECT      = "subject";
    public static final String CONTENT_TYPE    = "content-type";
    public static final String CONTENT       = "content";
    public static final String CONTENT_NAME     = "content-name";
    public static final String CONTENT_LENGTH    = "content-length";
    public static final String CONTENT_LOC  = "content-Location";
    public static final String CONTENT_ID      = "content-id";
    public static final String CONTENT_TRANSFER_ENCODING  = "content-transfer-encoding";
    public static final String CONTENT_DISPOSITION   = "content-disposition";
    public static final String DISPOSITION_NOTIFICATION_TO    = "disposition-notification-to";
    public static final String DISPOSITION_NOTIFICATION_OPTIONS  = "disposition-notification-options";
    public static final String SIGNED_REC_MICALG= "signed-receipt-micalg";
    public static final String MESSAGE_ID     = "message-id";
    public static final String ORIGINAL_MESSAGE_ID   = "original-message-id";
    public static final String RECEIPT_DELIVERY_OPTION   = "receipt-delivery-option";
    public static final String DISPOSITION  = "disposition";
    public static final String DATE      = "date";
    public static final String MIME_VERSION     = "mime-version";
    public static final String FINAL_RECIPIENT     = "final-recipient";
    public static final String ORIGINAL_RECIPIENT     = "original-recipient";
    public static final String RECV_CONTENT_MIC     = "received-content-mic";

    public static final String RFC822= "rfc822";
    public static final String RFC822_PFX= RFC822 + "; ";
   
    public static final String APP_XML= "application/xml";
    public static final String TEXT_PLAIN= "text/plain";
    public static final String APP_OCTET= "application/octet-stream";
    public static final String PKCS7SIG= "pkcs7-signature";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_XML = "text/xml";
    public static final String MSG_DISP = "message/disposition-notification";

    public static final String ERROR   = "error";
    public static final String FAILURE = "failure";
    public static final String WARNING  = "warning";
    public static final String HEADERS  = "headers";

    public static final String ISO_8859_1 = "iso-8859-1";
    public static final String US_ASCII = "us-ascii";

    public static final String CRLF= "\r\n";
}
