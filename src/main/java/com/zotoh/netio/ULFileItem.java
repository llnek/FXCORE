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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public class ULFileItem implements FileItem {
    
    private Logger ilog() {       return _log=getLogger(ULFileItem.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {         return _log==null ? ilog() : _log;    }    

    private static final long serialVersionUID = 2214937997601489203L;
    private transient OutputStream _os;
    
    private String _filename, _field, _ctype;
    private StreamData _ds;
    private byte[] _fieldBits;
    private boolean _ff;

    /**
     * @param field
     * @param contentType
     * @param isFormField
     * @param fileName
     */
    public ULFileItem(String field, String contentType, boolean isFormField, String fileName)     {
        
        tstEStrArg("file-name", fileName) ;
        tstEStrArg("field-name", field) ;
        
        _ctype= nsb(contentType);
        _field= field;
        _ff= isFormField;
        _filename= fileName;
    }
        
    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#delete()
     */
    public void delete()    {
        if (_ds != null && _ds.isDiskFile())
        _ds.getFileRef().delete();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#get()
     */
    public byte[] get()    {        return null;    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getContentType()
     */
    public String getContentType()    {               return _ctype;       }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getFieldName()
     */
    public String getFieldName()    {                return _field;       }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getInputStream()
     */
    public InputStream getInputStream() throws IOException    {
        throw new IOException("not implemented");
    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getName()
     */
    public String getName()     {                return _filename;       }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException    {
        return _os==null ? iniz() : _os;
    }

    
    /**
     * @return
     */
    public StreamData getFileData()     {        return _ds;    }
    
    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getSize()
     */
    public long getSize()    {        return 0L;    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getString()
     */
    public String getString()    {        
        String s=null;
        try         {
            s=getString("UTF-8");
        }
        catch (Exception e) {
            tlog().error("", e);
        }
        return s;
    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#getString(java.lang.String)
     */
    public String getString(String charset) throws UnsupportedEncodingException     {        
        if (maybeGetBits() != null) return new String(_fieldBits, charset);
        else
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#isFormField()
     */
    public boolean isFormField()    {        return _ff;    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#isInMemory()
     */
    public boolean isInMemory()    {        return false;    }


    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#setFieldName(java.lang.String)
     */
    public void setFieldName(String s)    {
        _field=s;
    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#setFormField(boolean)
     */
    public void setFormField(boolean b)    {
        _ff= b;
    }

    
    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.FileItem#write(java.io.File)
     */
    public void write(File fp) throws Exception    {
    }

    
    /**
     * 
     */
    public void cleanup()    {
        if (_fieldBits == null)        maybeGetBits();        
        _os= close(_os);
    }
        
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable     {
        close(_os);  super.finalize();
    }

    private byte[] maybeGetBits()    {        
        if (_os instanceof ByteArrayOutputStream) {
            _fieldBits= ((ByteArrayOutputStream)_os).toByteArray();
        }
        return _fieldBits;
    }
    
    
    private OutputStream iniz()    {
        
        if (_ff) { 
            _os= new ByteOStream(1024); 
        }
        else {
            _ds= new StreamData();
            try {
                Tuple t= createTempFile(false);
                _ds.resetMsgContent( t.get(0));
                _os = new FileOutputStream(_ds.getFileRef());
            }
            catch (Exception e) {            
                tlog().error("", e);
            }
        }      
        
        return _os;
    }
    
}
