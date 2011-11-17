package com.zotoh.netio;

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zotoh.core.util.StrArr;

/**
 * @author kenl
 *
 */
public class UriMatrixDecoder {

    private Map<String, StrArr> _params;
    private final String _charset;
    private final String _uri;
    private String _path;


    /**
     * @param uri
     */
    public UriMatrixDecoder(String uri) {
        this(uri, "utf-8");
    }

    
    /**
     * @param uri
     * @param charset
     */
    public UriMatrixDecoder(String uri, String charset) {
        tstEStrArg("charset", charset);
        tstEStrArg("uri", uri);
        this._uri = uri;
        this._charset = charset;
    }

    
    /**
     * @param uri
     */
    public UriMatrixDecoder(URI uri) {
        this(uri, "utf-8");
    }

    
    /**
     * @param uri
     * @param charset
     */
    public UriMatrixDecoder(URI uri, String charset) {
        tstEStrArg("charset", charset);
        tstObjArg("uri", uri);
        this._uri = uri.toASCIIString();
        this._charset = charset;
    }

    /**
     * Returns the decoded path string of the URI.
     * 
     * @return
     */
    public String getPath() {
        if (_path == null) {
            int pos = _uri.indexOf(';');
            _path=_uri;
            if (pos >= 0) {
                _path = _uri.substring(0, pos);
            }
        }
        return _path;
    }

    /**
     * Returns the decoded key-value parameter pairs of the URI.
     * 
     * @return
     */
    public Map<String, StrArr> getParameters() {
        if (_params == null) {
            int pathLength = getPath().length();
            if (_uri.length() == pathLength) {
                return Collections.emptyMap();
            }
            _params = decodeParams(_uri.substring(pathLength + 1));
        }
        return _params;
    }

    private Map<String, StrArr> decodeParams(String s) {
        Map<String, StrArr> params = new LinkedHashMap<String, StrArr>();
        String name = null;
        int pos = 0; // Beginning of the unprocessed region
        int i;       // End of the unprocessed region
        char c = 0;  // Current character
        for (i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == '=' && name == null) {
                if (pos != i) {
                    name = decodeComponent(s.substring(pos, i), _charset);
                }
                pos = i + 1;
            } else if (c == ';') {
                if (name == null && pos != i) {
                    // We haven't seen an `=' so far but moved forward.
                    // Must be a param of the form ';a;' so add it with
                    // an empty value.
                    addParam(params, decodeComponent(s.substring(pos, i), _charset), "");
                } else if (name != null) {
                    addParam(params, name, decodeComponent(s.substring(pos, i), _charset));
                    name = null;
                }
                pos = i + 1;
            }
        }

        if (pos != i) {  // Are there characters we haven't dealt with?
            if (name == null) {     // Yes and we haven't seen any `='.
                addParam(params, decodeComponent(s.substring(pos, i), _charset), "");
            } else {                // Yes and this must be the last value.
                addParam(params, name, decodeComponent(s.substring(pos, i), _charset));
            }
        } else if (name != null) {  // Have we seen a name without value?
            addParam(params, name, "");
        }

        return params;
    }

    private static String decodeComponent(String s, String charset) {
        if (s == null) {
            return "";
        }

        try {
            return URLDecoder.decode(s, charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(charset);
        }
    }

    private static void addParam(Map<String, StrArr> params, String name, String value) {
        StrArr values = params.get(name);
        if (values == null) {
            values = new StrArr();
            params.put(name, values);
        }
        values.add(value);
    }
    
    
    
    
}
