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

import static com.zotoh.core.util.LangUte.*;


import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.trimLastPathSep;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.eq;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.xml.XmlUte.escape;
import static com.zotoh.core.xml.XmlVars.XMLHDLINE;
import static java.util.Collections.unmodifiableMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.SeqNumGen;

/**
 * During the traversal of a XML tree, upon each element, this callback will be
 * triggered.  Used in conjunction with <i>Xmlsplit</i> to split up a xml file. 
 *
 *@see com.zotoh.core.xml.XmlSplit
 *
 * @author kenl
 *
 */
public class ElementCB {
    
    private transient Logger _log=getLogger(ElementCB.class); 
    public Logger tlog() {         return _log; }    
	
    private String _targetMatchedXPath, _fp;
    private OutputStream _out;
    private XmlSplit _app;

    /**
     * Constructor.
     * 
     * @param app function that splits an XML fragment.
     * @param dir output.
     * @param pfx prefix
     * @param xpath 
     */    
    public ElementCB(XmlSplit app, String dir, String pfx, String xpath) throws IOException     {
    	
        tstObjArg("xml-split", app) ;
        tstEStrArg("output-dir", dir);
        tstEStrArg("prefix", pfx);            
        tstEStrArg("xpath", xpath);
        
        _fp= niceFPath(trimLastPathSep(dir) + "/" + createFile(pfx));        
        _out= new FileOutputStream(_fp);
        _out.write(asBytes(XMLHDLINE));
        _out.flush();        
        _targetMatchedXPath= xpath;
        _app= app;        
    }

    /**
     * 
     * @param currentStack
     * @param seq
     * @param currentXPath
     * @param uri
     * @param lname
     * @param qname
     * @param cdata
     * @param atts
     */    
    public void onStart(List<ElementInfo> currentStack, Map<String,Long> seq,
            String currentXPath, String uri, String lname, String qname, 
            String cdata, Map<String,String> atts)
    throws IOException     {
        
        boolean isTargetNode= eq( _targetMatchedXPath, currentXPath);
        write( _out, escape(cdata));

        StringBuilder buf= new StringBuilder(1024);
        String attributes= asString(atts);

        if (isTargetNode)         {
            StringBuilder trail=new StringBuilder(512);
            StringBuilder bf=new StringBuilder(4096);
            String t, ln;
            ElementInfo prev, elem;
            long n;
            int end, cnt= currentStack.size();
            end=cnt-1;

            for (int i=0; i < cnt; ++i) {            
                elem= currentStack.get(i);
                prev= elem.getParent();
                ln= elem.getLname();
                
                if (prev != null) { n= prev.getCountOfChildren(ln); }
                else
                { n=1; }
                
                if (i < end) {                 
                    bf.append("<")
                    .append(elem.getQname())
                    .append(asString(elem.getAtts()))
                    .append(">\n");
                }
                
                trail.append("/")
                .append(ln)
                .append("[")
                .append(n)
                .append("]");
            }
            
            _app.registerOneFile( t= trail.toString(), _fp);
            write(_out, "<!-- " + t + " -->\n");

            if (bf.length() > 0) write(_out, bf.toString());
        }

        buf.append("<")
        .append(qname)
        .append(attributes)
        .append(">");
        
        write(_out, buf.toString());
    }

    /**
     * 
     * @param currentStack
     * @param currentXPath
     * @param uri
     * @param lname
     * @param qname
     * @param cdata
     * @return
     */    
    public boolean onEnd(List<ElementInfo> currentStack, String currentXPath, 
            String uri, String lname, String qname, String cdata) 
    throws IOException    {
        
        boolean isTargetNode= eq(_targetMatchedXPath, currentXPath);
        boolean done= false;

        if ( ! isEmpty(cdata))         {             write(_out, escape(cdata));        }
        write(_out, "</" + qname + ">");

        if (isTargetNode) {        
            StringBuilder bf=new StringBuilder(4096);
            ElementInfo elem;
            done=true;
            for (int i= currentStack.size()-2; i >= 0; --i) {            
                elem= currentStack.get(i);
                bf.append("</").append(elem.getQname()).append(">");
            }

            if (bf.length() > 0) 	write(_out, bf.toString());
            closeOut();
        }

        return done;
    }

    /**
     * Write out the set of xml element attributes as a string.
     * 
     * @param atts the attributes.
     * @return string.
     */    
    public String asString(Map<String,String> atts)    {
    	
        StringBuilder b3=new StringBuilder(256);
        StringBuilder b2=new StringBuilder(256);
        StringBuilder b1=new StringBuilder(256);
        StringBuilder p;
        String v;
        
        for (String s : atts.keySet()) {        
            v= atts.get(s);
            if (s.startsWith("xmlns:")) p=b2;
            else
            if (eq("xmlns",s)) p=b1;
            else
            p=b3;

            p.append(" ")
            .append(s)
            .append("=\"")
            .append(v)
            .append("\"");
        }

        return b1.append(b2).append(b3).toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable     {
        closeOut();
        super.finalize();
    }

    /**
     * Scan and grab all the namespace attributes.
     * 
     * @param atts the set of xml element attributes.
     * @return set of namespaces.
     */    
    protected Map<String,String> findAllNSPs(Map<String,String> atts)     {
        Map<String, String> ret= MP();
        
        for (String s : atts.keySet())        {
            if (s.startsWith("xmlns:") || "xmlns".equals(s)) {
                ret.put(s, atts.get(s));            	
            }
        }
        return unmodifiableMap(ret);
    }

    private void closeOut()     {
        _out= StreamUte.close(_out);
    }

    private void write(OutputStream out, String s, String enc) throws IOException    {
        if (out != null && s != null && s.length() > 0) {                    
            out.write( s.getBytes(enc));
            out.flush();
        }
    }

    private void write(OutputStream out, String s) throws IOException    {
        write(out,s, "utf-8");
    }

    private String createFile(String pfx)    {
        return pfx + "." 
        + Long.toString(System.currentTimeMillis()) 
        + Long.toString( SeqNumGen.getInstance().next() ) 
        + ".xml";
    }

    
    
    
}
