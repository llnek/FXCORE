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


import static com.zotoh.core.util.LangUte.AA;
import static com.zotoh.core.util.LangUte.LT;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;


/**
 * @author kenl
 *
 */
public enum StrUte {
;

    @SuppressWarnings("unused")
    private static void main(String[] args) {
    
        try {            
            String s= stripHead( "hello joe ;", ";");
            s= stripTail(s, ";");
            s=null;
        }
        catch (Throwable t) {
            
        }
    }
    
    /**
     * @param rdr
     * @param useFileAlways
     * @return
     * @throws IOException
     */
    public static StreamData readStream(Reader rdr, boolean useFileAlways) throws IOException    {
        
        CharArrayWriter wtr= new CharArrayWriter(10000);
        char[] bits= new char[4096];
        int lmt= 1024*1024*8; // if > 8M switch to file
        int cnt=0, c;
        Writer w=wtr;
        StreamData rc=new StreamData();

        if (useFileAlways) { lmt=1; }
        if (rdr != null)
        try  {
            while ( (c=rdr.read(bits)) > 0) {
                
                w.write(bits, 0, c);
                cnt += c;
                
                if ( lmt > 0 && cnt > lmt) {
                    w=swap(wtr, rc);
                    lmt= -1;
                }
                
            }
            
            if (!rc.isDiskFile() ) {
                rc.resetMsgContent(wtr.toString()) ;
            }
                    
        }
        catch (IOException e) {            
            throw e;            
        }
        finally {
            StreamUte.close(w);
        }

        return rc;
    }
    
    
    /**
     * @param str
     * @param stripChars
     * @return
     */
    public static String stripHead(String str, String stripChars)  {

        if (isEmpty(str) || isEmpty(stripChars)) {
            return str;
        }
        
        int head=0, strLen= str.length();
        
        while ((head != strLen) && (stripChars.indexOf(str.charAt(head)) != -1)) {
            ++head;
        }
        
        return str.substring(head);
    }

    /**
     * @param strs
     * @return
     */
    public static String[] trimAll(String... strs) {
        
        String[] rc = new String[ strs.length ];
        for (int i=0; i < strs.length; ++i) {
            rc[i] = trim(strs[i]) ;
        }        
        return rc;
    }
    
    
    /**
     * @param str
     * @param stripChars
     * @return
     */
    public static String stripTail(String str, String stripChars) {
        
        if ( isEmpty(str) || isEmpty(stripChars)) {
            return str;
        }

        int tail= str.length();
        while ((tail != 0) && (stripChars.indexOf(str.charAt(tail - 1))>= 0)) {
            --tail;
        }
        
        return str.substring(0, tail);
    }
    
    /**
     * Trim head & tail of the string.
     * 
     * @param s
     * @param chars characters to be removed.
     * @return
     */
    public static String trim(String s, String chars)     {
        return stripTail( stripHead(s, chars), chars);
    }

    /**
     * Safe trim.
     * 
     * @param s
     * @return
     */
    public static String trim(String s)    {
        return s==null ? "" : s.trim();
    }
    
    /**
     * Safe return string.
     * 
     * @param s
     * @param def 
     * @return def if string is null, else the string.
     */
    public static String asString(String s, String def)     {
        return s == null ? def : s;
    }

    /**
     * Append to a string-builder, optionally inserting a delimiter if the buffer is not
     * empty.
     * 
     * @param buf
     * @param delim
     * @param item
     * @return
     */
    public static StringBuilder addAndDelim(StringBuilder buf, String delim, String item)    {
        if (item != null) {
            if (buf.length() > 0)  if (delim != null) { buf.append(delim); }
            buf.append(item);
        }
        return buf;
    }

    
    /**
     * @param ids
     * @param sep
     * @param sort
     * @return
     */
    public static String delimAsString(String[] ids, String sep, boolean sort)    {
        StringBuilder b= new StringBuilder(512);
        
        if (ids != null) { 
            
            if (sort) {   Arrays.sort(ids) ;      }
            
            for (int i =0; i  < ids.length; ++i)
            addAndDelim(b, sep, ids[i]);        
        }
        
        return b.toString();
    }
    
    /**
     * Tests if the string contains any one of the given characters.
     * 
     * @param s
     * @param chars
     * @return
     */
    public static boolean containsChar(String s, CharSequence chars)     {        
        if (s != null) for (int i = 0; i < chars.length(); ++i)        {
            char c = chars.charAt(i);
            if (s.indexOf(c) != -1) {
                return true; 
            }
        }
        return false;
    }

    /**
     * Tests if s is a substring of src.
     * 
     * @param src
     * @param s
     * @return
     */
    public static boolean hasWithin(String src, String s)    {
        return (src != null && s != null) ? src.indexOf(s) >=0 : false;
    }
    
    /**
     * Get the substring between head & end.
     * e.g.
     *     src="this is a sample message", head="is", end="message"
     *     => " a sample "
     * 
     * @param src
     * @param head
     * @param end
     * @return
     */
    public static String chomp(String src, String head, String end)    {       
        if ( !isEmpty(src) && head != null && end != null) {
            int pos= src.indexOf(head);
            String lf, rt;
            lf= src.substring(0, pos);
            pos= src.indexOf(end, pos);
            rt= src.substring(pos+ end.length());
            src= lf+rt;
        }
        return src;
    }

    /**
     * @param src
     * @param head
     * @param end
     * @return
     */
    public static String mid(String src, String head, String end)    {       
        if ( !isEmpty(src) && head != null && end != null) {
            int l= src.indexOf(head);
            int r= src.indexOf(end);
            if (l>=0) {  l += head.length(); }            
            if (l >=0 && r >= 0) {
                src= src.substring(l, r);
            }
        }
        return src;
    }
    
    /**
     * Join a list of strings together, inserting a separator in between each string.
     * 
     * @param s
     * @param sep
     * @return
     */
    public static String join(String[] s, String sep)     {
        StringBuilder bf= new StringBuilder(256);
        
        if (s != null) for (int i = 0; i < s.length; ++i) {
            addAndDelim(bf, sep, nsn(s[i]) );
        }            
                
        return bf.toString();
    }

    /**
     * @param iter
     * @param sep
     * @return
     */
    public static String join(Iterator<?> iter, String sep)     {
        StringBuilder bf= new StringBuilder(256);
        
        if (iter != null) while (iter.hasNext()) {
            addAndDelim(bf, sep, nsn(iter.next() ) );
        }            
                
        return bf.toString();
    }
    
    /**
     * @param objs
     * @param sep
     * @return
     */
    public static String join(Object[] objs, String sep)     {
        StringBuilder bf= new StringBuilder(256);
        
        if (objs != null) for (int i = 0; i < objs.length; ++i) {
            addAndDelim(bf, sep, nsn(objs[i]) );
        }            
                
        return bf.toString();
    }
    
    /**
     * @param cs
     * @param sep
     * @return
     */
    public static <T> String join(Collection<T> cs, String sep)     {
        StringBuilder bf= new StringBuilder(256);
        
        if (cs != null) for (T obj : cs) {
            addAndDelim(bf, sep, nsb(obj) );
        }            
                
        return bf.toString();
    }
    
    /**
     * Turn first character into Uppercase.
     * 
     * @param n
     * @return
     */
    public static String upcaseFirstChar(String n)    {
        if ( !isEmpty(n))        {
            char[] cs= n.toCharArray();
            cs[0]=Character.toUpperCase(cs[0]);
            n= new String(cs);
        }
        return n;
    }

    /**
     * Like the c version of strstr().  Substitute all occurrences of a substring, and replace them with a different substring.
     * 
     * @param src
     * @param delim
     * @param replaceStr
     * @return
     */
    public static String strstr(String src, String delim, String replaceStr)    {
        StringBuilder b = new StringBuilder(1024);
        int len = delim.length();
        int tail = 0;

        if (src != null) while (true)        {
            tail = src.indexOf(delim);
            if (tail < 0) { break; }                
            b.append(src.substring(0, tail)).append(replaceStr);
            src = src.substring(tail + len);
        }

        return src==null ? null : b.append(src).toString();
    }

    /**
     * Split a large string into chucks, each chunk having a specific length.
     * 
     * @param src
     * @param chunkLength
     * @return
     */
    public static String[] splitIntoChunks(String src, int chunkLength)    {
        List<String> ret = LT();

        if (src != null)        {
            while (src.length() > chunkLength)            {
                ret.add(src.substring(0, chunkLength));
                src = src.substring(chunkLength);
            }

            if (src.length() > 0) {            ret.add(src); }
        }

        return AA( String.class, ret );
    }

    /**
     * Tests String.indexOf() against a list of possible args.
     * 
     * @param src
     * @param bits
     * @return
     */
    public static boolean hasWithin(String src, String... bits)    {    	
        if (src != null) for (int i=0; i < bits.length; ++i) {
            String s= bits[i];
            if (s != null && src.indexOf(s) >= 0)
            return true;
        }        
        return false;
    }

    /**
     * Tests startWith(), looping through the list of possible prefixes.
     * 
     * @param src
     * @param prefixes
     * @return
     */
    public static boolean startsWith(String src, String... prefixes)    {
        if (src != null)  for (int i = 0; i < prefixes.length; ++i) {    
            String s1= prefixes[i];
            if (s1 != null && src.startsWith(s1))
            return true;
        }        
        return false;
    }

    /**
     * Tests String.equals() against a list of possible args. (ignoring case)
     * 
     * @param src
     * @param args
     * @return
     */
    public static boolean equalsOneOfIC(String src, String... args)    {
        if (src != null && args.length > 0)  {
            src= src.toLowerCase();
            for (int i=0; i < args.length; ++i) {
                String s= args[i];
                if (s != null && src.equals(s.toLowerCase()))
                return true;
            }
        }        
        return false;
    }

    /**
     * Tests String.equals() against a list of possible args.
     * 
     * @param src
     * @param args
     * @return
     */
    public static boolean equalsOneOf(String src, String... args)    {
        if (src != null)   for (int i=0; i < args.length; ++i) {         
            if (src.equals(args[i])) { return true; }
        }        
        return false;
    }
    
    /**
     * Tests String.indexOf() against a list of possible args. (ignoring case).
     * 
     * @param src
     * @param bits
     * @return
     */
    public static boolean hasWithinIC( String src, String... bits)     {
        if (src != null) {
            src=src.toLowerCase();
            for (int i=0; i < bits.length; ++i) {
                String s= bits[i];
                if (s != null && src.indexOf(s.toLowerCase()) >= 0)
                return true;
            }        
        }
        return false;
    }

    
    /**
     * @param str
     * @param len
     * @return
     */
    public static String right(String str, int len) {

        if (isEmpty(str) || len <= 0) {
            return "";
        }
        int delta= str.length() - len;
        return delta <= 0 ? str : str.substring(delta);
    }

    
    /**
     * @param str
     * @param len
     * @return
     */
    public static String left(String str, int len) {
        if ( isEmpty(str) || len <= 0) {
            return "";
        }
        return str.length() <= len ? str : str.substring(0,len) ;
    }
    
    
    /**
     * @param s
     * @return
     */
    public static boolean isBlank(String s)    {
        int len= s==null? 0 : s.length();
        for (int i=0; i < len; ++i) {
            if ( ! Character.isWhitespace( s.charAt(i)  )) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * @param s
     * @return
     */
    public static boolean isEmpty(String s)     {
        return s==null || s.length()==0;
    }
    
    
    /**
     * @param text
     * @param searchString
     * @param replacement
     * @param max
     * @return
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {            return text;        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {                break;            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean iceq(String a, String b)    {
        return a!=null && a.equalsIgnoreCase(b);
    }

    
    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean eq(String a, String b)     {
        return a!=null && a.equals(b);
    }
        
    /**
     * Tests startsWith (ignore-case).
     * 
     * @param src source string.
     * @param prefixes list of prefixes to test with.
     * @return
     */
    public static boolean startsWithIC(String src, String... prefixes)    {
        if (src != null)  {
            src=src.toLowerCase();
            for (int i = 0; i < prefixes.length; ++i) {    
                String s1= prefixes[i];
                if (s1 != null && src.startsWith(s1.toLowerCase()))
                return true;
            }
        }        
        return false;
    }

    
    /**
     * @param src
     * @param delim
     * @return
     */
    public static String[] split(String src, String delim) {
        List<String> rc= LT();
        if (isEmpty(src) || isEmpty(delim)) {
            rc.add(src);
        }    else {
            StringTokenizer z= new StringTokenizer(src, delim);
            while (z.hasMoreTokens()) {
                rc.add(z.nextToken());
            }
        }
        return AA(String.class, rc);
    }


    /**
     * Safely call toString().
     * 
     * @param o
     * @return "" if null.
     */
    public static String nsb(Object o)    {
        return o == null ? "" : o.toString();
    }

    
    /**
     * @param o
     * @return
     */
    public static String nsn(Object o)    {
        return o == null ? "(null)" : o.toString();
    }

    
    
    /**
     * @param sz
     * @return
     */
    public static String pad(int sz) {
        StringBuilder bd= new StringBuilder(256) ;
        while (sz > 0) {
            bd.append(" ") ;
            --sz;
        }
        return bd.toString();
    }
    
    
    /**
     * @param src
     * @param chars
     * @return
     */
    public static int indexOfAnyChar(String src, char[] chars) {
        
        if (!isEmpty(src) && (chars != null && chars.length > 0))  {
            char c;
            for(int i = 0; i < src.length(); ++i)            {
                c = src.charAt(i);
                
                for(int j = 0; j < chars.length; ++j)
                if(chars[j] == c) {
                        return i; 
                }
            }
        }
        
        return -1;
    }
    
    
    private static OutputStreamWriter swap(CharArrayWriter wtr, StreamData data)
    				throws IOException    {
        Tuple t= StreamUte.createTempFile( true);
        File fout= (File) t.get(0);
        OutputStream os= (OutputStream) t.get(0);
        data.resetMsgContent(fout) ;
        
        OutputStreamWriter w= new OutputStreamWriter(os);
        w.write( wtr.toCharArray());
        w.flush();
        
        return w;
    }
    
    
    
}
