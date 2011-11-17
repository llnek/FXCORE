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

import static com.zotoh.core.util.CoreUte.tstNonNegIntArg;
import static com.zotoh.core.util.StrUte.nsb;

/**
 * @author kenl
 *
 */
public enum HTTPStatus {
    
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    UNORDERED_COLLECTION(425, "Unordered Collection"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    NOT_EXTENDED(510, "Not Extended"),
    OTHER(0,"");

    private String _reasonStr;
    private int _code;

    /**
     * @param code
     * @return
     */
    public static boolean isServerError(String code)    {
        return code==null ? false : code.startsWith("50");
    }

    
    /**
     * @param code
     * @return
     */
    public static boolean isClientError(String code) {
        return code==null ? false 
                : (code.startsWith("40")||code.startsWith("41")||
                        code.startsWith("42")|| code.startsWith("44"));
    }

    
    /**
     * @param code
     * @return
     */
    public static boolean isRedirected(String code) {
        return code==null ? false : code.startsWith("30");
    }

    
    /**
     * @param code
     * @return
     */
    public static boolean isSuccess(String code) {
        return code==null ? false : code.startsWith("20");
    }
    

    /**
     * @param code
     * @return
     */
    public static HTTPStatus xref(int code) {
        
        tstNonNegIntArg("status-code", code) ;
        String str;

        switch(code) {
			case 100: return CONTINUE;
			case 101: return SWITCHING_PROTOCOLS;
			case 102: return PROCESSING;
			case 200: return OK;
			case 201: return CREATED;
			case 202: return ACCEPTED;
			case 203: return NON_AUTHORITATIVE_INFORMATION;
			case 204: return NO_CONTENT;
			case 205: return RESET_CONTENT;
			case 206: return PARTIAL_CONTENT;
			case 207: return MULTI_STATUS;
			case 300: return MULTIPLE_CHOICES;
			case 301: return MOVED_PERMANENTLY;
			case 302: return FOUND;
			case 303: return SEE_OTHER;
			case 304: return NOT_MODIFIED;
			case 305: return USE_PROXY;
			case 307: return TEMPORARY_REDIRECT;
			case 400: return BAD_REQUEST;
			case 401: return UNAUTHORIZED;
			case 402: return PAYMENT_REQUIRED;
			case 403: return FORBIDDEN;
			case 404: return NOT_FOUND;
			case 405: return METHOD_NOT_ALLOWED;
			case 406: return NOT_ACCEPTABLE;
			case 407: return PROXY_AUTHENTICATION_REQUIRED;
			case 408: return REQUEST_TIMEOUT;
			case 409: return CONFLICT;
			case 410: return GONE;
			case 411: return LENGTH_REQUIRED;
			case 412: return PRECONDITION_FAILED;
			case 413: return REQUEST_ENTITY_TOO_LARGE;
			case 414: return REQUEST_URI_TOO_LONG;
			case 415: return UNSUPPORTED_MEDIA_TYPE;
			case 416: return REQUESTED_RANGE_NOT_SATISFIABLE;
			case 417: return EXPECTATION_FAILED;
			case 422: return UNPROCESSABLE_ENTITY;
			case 423: return LOCKED;
			case 424: return FAILED_DEPENDENCY;
			case 425: return UNORDERED_COLLECTION;
			case 426: return UPGRADE_REQUIRED;
			case 500: return INTERNAL_SERVER_ERROR;
			case 501: return NOT_IMPLEMENTED;
			case 502: return BAD_GATEWAY;
			case 503: return SERVICE_UNAVAILABLE;
			case 504: return GATEWAY_TIMEOUT;
			case 505: return HTTP_VERSION_NOT_SUPPORTED;
			case 506: return VARIANT_ALSO_NEGOTIATES;
			case 507: return INSUFFICIENT_STORAGE;
			case 510: return NOT_EXTENDED;
        }

        if(code < 100)
            str = "Unknown Status";
        else
        if(code < 200)
            str = "Informational";
        else
        if(code < 300)
            str = "Successful";
        else
        if(code < 400)
            str = "Redirection";
        else
        if(code < 500)
            str = "Client Error";
        else
        if(code < 600)
            str = "Server Error";
        else
            str = "Unknown Status";

        OTHER._reasonStr= str;
        OTHER._code=code;
        
        return OTHER;
    }

	
    /**
     * @param code
     * @param reason
     */
    private HTTPStatus(int code, String reason) {
		tstNonNegIntArg("status-code", code);
		//tstEStrArg("status-text", reason);
        _code = code;
        _reasonStr = nsb(reason);
    }

	
    /**
     * @return
     */
    public int getCode() {        return _code;    }

	
    /**
     * @return
     */
    public String getReasonPhrase() {
        return _reasonStr;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString() {
		return "" + _code + " " + _reasonStr;
    }

    
}

