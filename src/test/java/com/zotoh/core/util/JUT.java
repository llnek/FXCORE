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

import static com.zotoh.core.util.ByteUte.readAsBytes;
import static com.zotoh.core.util.ByteUte.readAsInt;
import static com.zotoh.core.util.ByteUte.readAsLong;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.core.util.StrUte.chomp;
import static com.zotoh.core.util.StrUte.containsChar;
import static com.zotoh.core.util.StrUte.eq;
import static com.zotoh.core.util.StrUte.equalsOneOf;
import static com.zotoh.core.util.StrUte.equalsOneOfIC;
import static com.zotoh.core.util.StrUte.hasWithin;
import static com.zotoh.core.util.StrUte.join;
import static com.zotoh.core.util.StrUte.splitIntoChunks;
import static com.zotoh.core.util.StrUte.startsWith;
import static com.zotoh.core.util.StrUte.startsWithIC;
import static com.zotoh.core.util.StrUte.strstr;
import static com.zotoh.core.util.StrUte.trim;
import static com.zotoh.core.util.StrUte.upcaseFirstChar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JUT implements CoreVars {

    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUT.class);
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
    public void testByteUte() throws Exception {          
        assertEquals(911L,readAsLong( readAsBytes(911L)));
        assertEquals(911,readAsInt( readAsBytes(911)));
    }
    
    @Test
    public void testDateUte() throws Exception {
        GregorianCalendar g, gc= new GregorianCalendar(2050, 5, 20);
        Date dt, base= gc.getTime();        
        dt= DateUte.addYears(base, -5) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.YEAR)== 2045) ;
        dt= DateUte.addYears(base, 5) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.YEAR)== 2055) ;
        dt= DateUte.addMonths(base, -2) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.MONTH)== 3) ;
        dt= DateUte.addMonths(base, 2) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.MONTH)== 7) ;
        dt= DateUte.addDays(base, -10) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.DAY_OF_MONTH)== 10) ;
        dt= DateUte.addDays(base, 10) ;
        g= new GregorianCalendar(); g.setTime(dt);
        assertTrue(g.get(Calendar.DAY_OF_MONTH)== 30) ;
    }
    
    @Test
    public void testCardinality() throws Exception {
        Cardinality c= new Cardinality(null);
        assertTrue(c.getMaxOccurs()== c.getMinOccurs());
        assertTrue(c.getMaxOccurs()==0);
        assertFalse(c.isRequired());
        c= new Cardinality("");
        assertTrue(c.getMaxOccurs()== c.getMinOccurs());
        assertTrue(c.getMaxOccurs()==0);
        assertFalse(c.isRequired());
        c= new Cardinality("1,10");
        assertTrue(c.getMaxOccurs() ==10);
        assertTrue(c.getMinOccurs()==1);
        assertTrue(c.isRequired());
        c= new Cardinality("0,n");
        assertTrue(c.getMaxOccurs() ==Integer.MAX_VALUE);
        assertTrue(c.getMinOccurs()==0);
        assertFalse(c.isRequired());
        c= new Cardinality("-9,20");
        assertTrue(c.getMaxOccurs() ==20);
        assertTrue(c.getMinOccurs()==0);
        assertFalse(c.isRequired());
    }
        
    @Test
    public void testMiscStr() throws Exception    {
        String s= CoreUte.normalize("hello$");
        assertTrue(eq("hello_0x24_",s));
        assertTrue(eq("hello", asString(asBytes("hello"))));
    }
    
    @Test
    public void testZip() throws Exception    {
        byte[] sa= "hello".getBytes("utf-8");
        sa=CoreUte.deflate(sa);
        sa=CoreUte.inflate(sa);
        assertTrue(eq("hello", new String(sa)));
    }
    
    @Test
    public void testTrim() throws Exception    {
        String s=trim("<hello>", "<>");
        assertTrue(eq("hello",s));
        
        s= trim("     hello       ");
        assertTrue(eq("hello",s));
    }
    
    @Test
    public void testContainsChar() throws Exception    {
        boolean ok= containsChar("this is amazing !!!", "^%!$*");
        assertTrue(ok);
        ok= containsChar("this is amazing !!!", "^%$*");
        assertFalse(ok);
    }
    
    @Test
    public void testSplitChunks() throws Exception    {
        String[] ss= splitIntoChunks("1234567890", 5);
        assertTrue(ss != null && ss.length==2);
        assertTrue(eq("12345", ss[0]));
        assertTrue(eq("67890", ss[1]));
    }

    @Test
    public void testStrstr() throws Exception    {
        String s= strstr("this is a message to joe : hello joe", "joe", "bobby");
        assertTrue(eq("this is a message to bobby : hello bobby", s));
    }
        
    @Test
    public void testFmtDate() throws Exception    {
        GregorianCalendar now= new GregorianCalendar(2000, 9, 2, 12, 13, 14);
        Date n= now.getTime();
        String s= CoreUte.fmtDate(n, DT_FMT);
        assertTrue(s != null && s.length() > 0);
        Date a= CoreUte.parseDate(s, DT_FMT);
        assertTrue(a.equals(n));
    }

       
    @Test
    public void testFmtTS() throws Exception    {
        GregorianCalendar now= new GregorianCalendar(2000, 9, 2, 12, 13, 14);
        Timestamp n= new Timestamp(now.getTime().getTime());
        String s= n.toString();
        assertTrue(s != null && s.length() > 0);
        Timestamp a= CoreUte.parseTimestamp(s);
        assertTrue(a.equals(n));
    }

       
    @Test
    public void testParseDate() throws Exception    {
        Date d;

        d= CoreUte.parseDate("8764395345");
        assertNull(d);
        
        d= CoreUte.parseDate("2000-03-04 16:17:18");
        assertNotNull(d);
    }
    
       
    @Test
    public void testParseTS() throws Exception    {
        Timestamp ts= CoreUte.parseTimestamp("43654kjljlfk");
        assertNull(ts);
        
        ts= CoreUte.parseTimestamp("2010-09-02 13:14:15");
        assertNotNull(ts);
    }
    
       
    @Test
    public void testUpcaseFirstChar() throws Exception    {
        String s= upcaseFirstChar("joe");
        assertTrue(eq("Joe",s));
    }
    
       
    @Test
    public void testArrToStr() throws Exception    {
        String s= join(new String[]{"hello", "joe"}, ",");
        assertTrue(eq("hello,joe",s));
        s= join(new String[]{"hello", "joe"}, null);
        assertTrue(eq("hellojoe",s));
    }
    
       
    @Test
    public void testChomp() throws Exception    {
        String s= chomp("this is a long string, to be chomped by joe", 
                " a long string", "to be chomped by");
        assertTrue(eq("this is joe", s));
    }
    
       
    @Test
    public void testHasWithin() throws Exception    {
        boolean ok;
        
        ok=hasWithin("hello joe, how are you?", "are");
        assertTrue(ok);

        ok=hasWithin("hello joe, how are you?", "hello");
        assertTrue(ok);

        ok=hasWithin("hello joe, how are you?", "jack");
        assertFalse(ok);
    }
    
       
    @Test
    public void testAddAndDelim() throws Exception    {
        StringBuilder bf= new StringBuilder(256);
        addAndDelim(bf, ";", "hello");
        assertTrue(eq("hello", bf.toString()));
        addAndDelim(bf, ";", "joe");
        assertTrue(eq("hello;joe", bf.toString()));
    }
    
       
    @Test
    public void testEqualsOneOf() throws Exception    {
        boolean ok;
        
        ok=equalsOneOf("jim", new String[]{"Jack", "joe", "jim"});
        assertTrue(ok);
        
        ok=equalsOneOf("Jim", new String[]{"Jack", "joe", "jim"});
        assertFalse(ok);
        
        ok=equalsOneOfIC("Jim", new String[]{"Jack", "joe", "jim"} );
        assertTrue(ok);
    }
    
       
    @Test
    public void testStartsWith() throws Exception    {
        boolean ok;
        
        ok=startsWith("hello joe", new String[]{"joe", "hell", });
        assertTrue(ok);

        ok=startsWith("hello joe", new String[]{"joe", "HeLlo", });
        assertFalse(ok);

        ok=startsWithIC("hello joe", new String[]{"joe", "HeLlo", });
        assertTrue(ok);

    }
    
       
    @Test
    public void testZeroInteger() throws Exception    {
        int m, n= 0;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testOneInteger() throws Exception    {
        int m, n= 1;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testSmallInteger() throws Exception    {
        int m, n= 100;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testLargeInteger() throws Exception    {
        int m, n= Integer.MAX_VALUE;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }
    
   
    @Test
    public void testNegOneInteger() throws Exception    {
        int m, n= -1;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testNegSmallInteger() throws Exception    {
        int m, n= -100;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testNegLargeInteger() throws Exception    {
        int m, n= Integer.MIN_VALUE;        
        m= ByteUte.readAsInt( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }
    
   
    @Test
    public void testZeroLong() throws Exception    {
        long m, n= 0L;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testOneLong() throws Exception    {
        long m, n= 1L;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testSmallLong() throws Exception    {
        long m, n= 100L;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testLargeLong() throws Exception    {
        long m, n= Long.MAX_VALUE;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }
    
   
    @Test
    public void testNegOneLong() throws Exception    {
        long m, n= -1L;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testNegSmallLong() throws Exception    {
        long m, n= -100L;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }

   
    @Test
    public void testNegLargeLong() throws Exception    {
        long m, n= Long.MIN_VALUE;        
        m= ByteUte.readAsLong( ByteUte.readAsBytes(n));
        assertTrue(m==n);
    }
    
    //@Test
    public void testMenu() throws Exception {
    	TMenu m1= new TMenu("M1");
    	TMenu m2= new TMenu("M2");
    	TMenuCB cb=new TMenuCB() {
			public void command(TMenuItem i) {
			}};
    	m1.add(new TMenuItem("m.i1", "New", cb));
    	m1.add(new TMenuItem("m.i2", "Open", m2));
    	m1.add(new TMenuItem("m.i3", "Close", cb));
    	m1.show(null);    	
    	m1=null;
    }
    
    
}
