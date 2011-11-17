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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

/**
 * Splits an xml file into fragment files based on inputs.  The inputs
 * are simple xpaths.  For example, give this xpath "/PO/Order/OrderLine", all the
 * OrderLines will be extracted out.  Each OrderLine element will be splitted out into a separate file.
 * A manifest will be generated in the output directory to provide cross references for each OrderLine
 * and the file that contains that element. For example, the inside of the manifest will look like
 * "/PO/Order/OrderLine[1]  -> /somefolder/outputdir/file1.xml" 
 * "/PO/Order/OrderLine[2]  -> /somefolder/outputdir/file2.xml"... etc 
 *
 * @author kenl
 *
 */
public class XmlSplit extends XmlScanner {
    
    private final List<String> _xpaths= LT();
    private String _filepath, _manifestDir, 
    _manifestFp,  _dir;
    
    // out
    private OutputStream _manifest;

    // keeps track of the elements as we push & pop while walking the tree
    private final List<ElementInfo> _trail= LT();
    
    // keeps track of the sequence/order/index of each element
    private final Map<String,Long> _seq= MP();
    
    // the set of active callbacks
    private final Map<String,ElementCB> _cbs= MP();
    
    // the root element
    // the current path
    private String _pfx="", _xpath="";    

    /**
     * 
     * @param pathToFile
     * @param listOfXPaths
     * @param outputDir
     * @return
     * @throws Exception
     */    
    public static String split(File pathToFile, List<String> listOfXPaths, File outputDir) 
            throws Exception    {
        tstObjArg("list-xpaths", listOfXPaths) ;
        tstObjArg("file-path", pathToFile) ;
        tstObjArg("output-dir", outputDir) ;
        
        return new XmlSplit().start(pathToFile, listOfXPaths, outputDir);
    }

    
    /**
     * @return
     */
    public String getFilePath()    {         return _filepath;    }
    
    /* (non-Javadoc)
     * @see com.zotoh.core.xml.SaxHandler#onStartElement(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    protected void onStartElement(String uri, String lname, String qname, Map<String,String> a) 
            throws SAXException    {
        String cdata= getLastElementCDATA();
        ElementInfo prev, child;
        int sz;

        if (isEmpty(_pfx)) {             _pfx= lname;        }
        _xpath +=  "/" + lname;

        child= new ElementInfo(lname,qname,a);
        prev=null;
        sz= _trail.size();        
        
        if (sz > 0) { prev= _trail.get(sz-1); }
        if (prev != null) {
            prev.addOneChild(child);
        }
        
        _trail.add(child);

        for (String regexp : _xpaths)  if (_xpath.matches(regexp)) {            
            try             {
                _cbs.put(_xpath, 
                        new ElementCB(this, _dir, _pfx, _xpath));
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
                
        for (ElementCB callback : _cbs.values())        
        try         {
                callback.onStart(_trail, _seq, _xpath, 
                        uri, lname, qname, cdata, a);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        
    }

    
    /* (non-Javadoc)
     * @see com.zotoh.core.xml.SaxHandler#onEndElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onEndElement(String uri, String lname, String qname, String cdata)  
                throws SAXException    {
        List<String> lst= LT();
        ElementCB callback;
        boolean done;
        
        for (String key : _cbs.keySet()) {
            callback= _cbs.get(key);
            try             {
                done= callback.onEnd(_trail, _xpath, uri, lname, qname, cdata);
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
            if (done) lst.add(key);
        }

        for (String s : lst) {
            _cbs.remove(s);
        }

        _xpath= _xpath.substring(0, _xpath.lastIndexOf("/"));
        _trail.remove(_trail.size()-1);
        
    }

    
    /**
     * @param xpath
     * @param filename
     * @throws IOException
     */
    protected void registerOneFile(String xpath, String filename) throws IOException    {
        StringBuilder bf= new StringBuilder(1024)
        .append(xpath)
        .append(": ")
        .append(filename)
        .append("\n");
        
        _manifest.write( asBytes(bf.toString()));
        _manifest.flush();        
    }

    private String start(File pathToFile, List<String> listOfXPaths, File out) 
            throws Exception    {        
        InputStream inp= null;
        try        {
            inp= new FileInputStream(pathToFile);

            _manifestDir= niceFPath(out.getCanonicalPath() + "/META-INF");
            _manifestFp= _manifestDir + "/MANIFEST.MF";

            _filepath= niceFPath( pathToFile);
            _dir= niceFPath( out);

            new File(_manifestDir).mkdirs();
            out.mkdirs();
            
            _manifest= new FileOutputStream(_manifestFp);
            for (String s : listOfXPaths)
            _xpaths.add(s);

            scan(inp);
        }
        finally {        
            close( _manifest);
            close(inp);
        }

        return _manifestFp;
    }

    
    private XmlSplit()
    {}
    
}
