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
 
package com.zotoh.core.util;

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.readStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Utility functions related to JSON objects/strings.  The JSONObject source code is from json.org 
 *
 */
public enum JSONUte {
;

    /**
     * @param root
     * @return
     * @throws JSONException
     */
    public static String asString(JSONObject root) throws JSONException     {
        return root==null ? "" : root.toString(4);
    }
    
    /**
     * @param obj
     * @param key
     * @return
     */
    public static JSONObject getObject(JSONObject obj, String key)     {
        return obj==null ? null : obj.optJSONObject(key);
    }

    /**
     * @param obj
     * @param key
     * @return
     */
    public static JSONArray getArray(JSONObject obj, String key)     {
        return obj==null ? null : obj.optJSONArray(key);
    }

    /**
     * @param arr
     * @param pos
     * @return
     */
    public static JSONObject getObject(JSONArray arr, Integer pos)     {
        return arr==null ? null : arr.optJSONObject(pos);
    }

    /**
     * @param obj
     * @param key
     * @return
     */
    public static String getString(JSONObject obj, String key)     {
        return obj==null ? null : obj.optString(key); 
    }

    
    /**
     * @param obj
     * @param key
     * @return
     */
    public static boolean getBoolean(JSONObject obj, String key)     {
        return obj==null ? false : obj.optBoolean(key);
    }

    
    /**
     * @param obj
     * @param key
     * @return
     */
    public static int getInt(JSONObject obj, String key)     {
        return obj==null ? 0 : obj.optInt(key);
    }

    
    /**
     * @param j
     * @param fld
     * @param value
     * @throws JSONException
     */
    public static void addString(JSONObject j, String fld, String value) throws JSONException     {        
        if (j != null && fld != null && value != null)         {
            j.put(fld, value);
        }
    }

    
    /**
     * @param j
     * @param fld
     * @param b
     * @throws JSONException
     */
    public static void addString(JSONObject j, String fld, Boolean b) throws JSONException     {        
        if (j != null && fld != null)         {
            j.put(fld, Boolean.toString(b));
        }
    }

    
    /**
     * @param j
     * @param fld
     * @param n
     * @throws JSONException
     */
    public static void addString(JSONObject j, String fld, Integer n) throws JSONException     {        
        if (j != null && fld != null)        {
            j.put(fld, Integer.toString(n));            
        }
    }

    
    /**
     * @param j
     * @param fld
     * @return
     * @throws JSONException
     */
    public static JSONArray getAndSetArray(JSONObject j, String fld) throws JSONException    {
        JSONArray r= null;
        Object o;
        
        if (j != null && fld != null)         {            
            o= j.opt(fld);
            if ( o instanceof JSONArray)            {
                r= (JSONArray) o;
            }
            else
            if (o != null)            {
                j.remove(fld);
            }

            if (r==null) {
                r= new JSONArray();
                j.put(fld, r);
            }
        }
        
        return r;
    }
    
    
    /**
     * @param j
     * @param fld
     * @return
     * @throws JSONException
     */
    public static JSONObject getAndSetObject(JSONObject j, String fld) throws JSONException    {
        JSONObject r= null;
        Object o;

        if (j != null && fld != null)        {
            o= j.opt(fld);
            if ( o instanceof JSONObject)            {
                r= (JSONObject) o;
            }
            else
            if (o != null)            {
                j.remove(fld);
            }

            if (r==null) {
                r= new JSONObject();
                j.put(fld, r);
            }
        }
        
        return r;
    }

    
    /**
     * @param j
     * @param fld
     * @param obj
     * @throws JSONException
     */
    public static void addObject(JSONObject j, String fld, JSONObject obj) throws JSONException    {
        if (j != null && fld != null  && obj != null)
        j.put(fld, obj);
    }

    
    /**
     * @param r
     * @param obj
     * @throws JSONException
     */
    public static void addItem(JSONArray r, JSONObject obj) throws JSONException    {
        if (r != null  && obj != null)
        r.put(obj);        
    }

    
    /**
     * @param json
     * @return
     * @throws JSONException
     */
    public static JSONObject read(InputStream json) throws JSONException    {        
        return new JSONObject( new JSONTokener(json));
    }

    /**
     * @param json
     * @return
     * @throws JSONException
     */
    public static JSONObject read(File json) throws JSONException    {
        InputStream inp= null;
        try {
            inp= readStream(json);
            return new JSONObject( new JSONTokener(inp));
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
        finally {
            close(inp);
        }
    }
    
    /**
     * @param json
     * @return
     * @throws JSONException
     */
    public static JSONObject read(String json) throws JSONException    {
        return new JSONObject( new JSONTokener(json));
    }

    
    /**
     * @return
     */
    public static JSONObject newJSON() {
        return new JSONObject();
    }
    
    
}
