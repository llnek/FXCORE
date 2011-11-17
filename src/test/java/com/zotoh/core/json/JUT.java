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
 
package com.zotoh.core.json;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.util.JSONUte;

public final class JUT {
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception    {
    }

    @AfterClass
    public static void finz()    {
    }

    @Before
    public void open() throws Exception    {
    }

    @After
    public void close() throws Exception    {
    }
    
    @Test
    public void testFromString() throws Exception    {
        JSONObject top= JSONUte.read( jsonStr());
        JSONArray a;
        JSONObject obj;
        
        assertTrue(top != null);
        
        assertTrue("hello".equals(top.getString("a")));
        assertTrue("world".equals(top.getString("b")));
        a= top.getJSONArray("c");
        assertTrue(a != null);
        assertTrue(a.length()==2);
        assertTrue(a.get(0).equals(true));
        assertTrue(a.get(1).equals(false));
        obj= top.getJSONObject("d");
        assertTrue(obj != null);        
    }
    
    @Test
    public void testToString() throws Exception    {
        JSONObject top= JSONUte.read( jsonStr());        
        String s= JSONUte.asString(top);
        assertTrue(s != null);        
    }

    private String jsonStr()    {
        return "{" +
        "a : \"hello\"," +
        "b : \"world\"," +
        "c : [true,false]," +
        "d : {} " +
        "}";        
    }
}

