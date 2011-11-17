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
 
package com.zotoh.core.xml;

import static com.zotoh.core.util.CoreUte.rc2Stream;
import static com.zotoh.core.util.CoreUte.rc2Url;
import static com.zotoh.core.util.CoreUte.rc2bytes;
import static com.zotoh.core.util.FileUte.writeFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;


public final class JUT implements XmlVars  {
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUT.class);
    }

    @BeforeClass
    public static void iniz() throws Exception     {
    }

    @AfterClass
    public static void finz()    {
    }

    @Before
    public void open() throws Exception     {
    }

    @After
    public void close() throws Exception    {
    }

    @Test
    public void testToDOM() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/core/xml/simple.xml") ;
        try {            
            assertTrue(XmlUte.toDOM(inp) != null);
        }
        finally {
            StreamUte.close(inp);
        }
    }
    
    @Test
    public void testWriteDOM() throws Exception {
        InputStream inp= rc2Stream("com/zotoh/core/xml/simple.xml") ;
        try {            
            Document doc= XmlUte.toDOM(inp);
            String s= DOMWriter.writeOneDoc(doc) ;
            assertTrue(s != null && s.length() > 0);
        }
        finally {
            StreamUte.close(inp);
        }
    }
    
    @Test
    public void testXmlScanner() throws Exception     {
        URL doc= rc2Url("com/zotoh/core/xml/malformed.xml");
        assertFalse(new XmlScanner().scan(doc));
        
        doc= rc2Url("com/zotoh/core/xml/simple.xml");
        assertTrue(new XmlScanner().scan(doc));        
    }

    @Test
    public void testDTDValidator() throws Exception     {
        
        URL dtd= rc2Url("com/zotoh/core/xml/3a4.dtd", this.getClass().getClassLoader());
        InputStream inp= null;        
        try        {
            inp= rc2Stream("com/zotoh/core/xml/bad.dtd.xml");
            assertTrue(new DTDValidator().scanForErrors(inp, dtd));
        }
        finally {
            StreamUte.close(inp);
        }
        
        try        {
            inp= rc2Stream("com/zotoh/core/xml/good.dtd.xml");
            assertFalse(new DTDValidator().scanForErrors(inp, dtd));
        }
        finally {
            StreamUte.close(inp);
        }
        
    }
        
    @Test
    public void testXSDValidator() throws Exception     {
        URL dtd= rc2Url("com/zotoh/core/xml/3a4.xsd");
        InputStream inp= null;        
        try        {
            inp= rc2Stream("com/zotoh/core/xml/bad.xsd.xml");
            assertFalse(new XSDValidator().scanForErrors(inp, dtd));
        }
        finally {
            StreamUte.close(inp);
        }
        
        try        {
            inp= rc2Stream("com/zotoh/core/xml/good.xsd.xml");
            assertTrue(new XSDValidator().scanForErrors(inp, dtd));
        }
        finally {
            StreamUte.close(inp);
        }
        
    }
    
    @Test
    public void testSplitter() throws Exception     {
        byte[] bits= rc2bytes("com/zotoh/core/xml/split.xml");
        File dir= CoreUte.genTmpDir();
        dir.mkdirs();
        String path= dir.getCanonicalPath();        
        File src= new File(path+"/src.xml");
        writeFile(src, bits);
        
        XmlSplit.split(src, Arrays.asList("/XSIGroup/XSI"), dir);
        
        File[] c= dir.listFiles(new FF());
        
        int len= c== null ? 0 : c.length;
        // generated 26 xml files + the src file == 27        
        assertTrue(len==(26+1));
    }

    
    private class FF implements FilenameFilter {
        public boolean accept(File dir, String fname)            {
            return fname.endsWith(".xml") ;
        }                    
    }
    
    
    
}

