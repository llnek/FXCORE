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
 
package com.zotoh.core.io;

import static com.zotoh.core.io.StreamUte.asStream;
import static com.zotoh.core.io.StreamUte.bytesFromFile;
import static com.zotoh.core.io.StreamUte.gunzip;
import static com.zotoh.core.io.StreamUte.gzip;
import static com.zotoh.core.util.CoreUte.getFilePath;
import static com.zotoh.core.util.CoreUte.getTmpDir;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.FileUte.delete;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.StrUte;

/**
 * Wrapper structure to abstract a piece of data which can be a file
 * or a memory byte[].  If the data is byte[], it will also be
 * compressed if a certain threshold is exceeded. 
 *
 * @author kenl
 *
 */
public class StreamData implements java.io.Serializable {
    
    private Logger ilog() {       return _log=getLogger(StreamData.class);    }    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    private transient Logger _log= ilog();

    private static final long serialVersionUID = -8637175588593032279L;
    private static final int CMPZ_THREADHOLD=1024*1024*4; // 4meg
    private static File _wd;
    
    private boolean _cmpz=false, _cls=true;
    private String  _encoding="UTF-8", _fp;  
    private byte[] _bin;    
    private long _binSize= 0L;
    
    private List<InputStream> _res= LT();
    
    
    /**
     * Get the current shared working directory.
     * 
     * @return working dir.
     */
    public static File getWDir()    { 
        return (_wd==null)  ? (_wd= getTmpDir()) : _wd;
    }

    /**
     * Working directory is where all the temporary/sharable files are created.
     * 
     * @param fpDir a directory.
     */
    public static void setWDir(File fpDir)    {
        
        if (fpDir != null)
        try {
            fpDir.mkdirs();
            if (fpDir.isDirectory() && 
               fpDir.canRead() && 
               fpDir.canWrite())         {
                _wd=fpDir; 
            }
        }
        catch (Throwable t) {}
        
    }

    /**
     * Create from a data object.
     * 
     * @param obj
     * @throws IOException 
     */
    public StreamData(Object obj) throws IOException    {
        resetMsgContent(obj);        
    }

    /**
     * 
     */
    public StreamData()
    {}

    /**
     * @param enc
     */
    public void setEncoding(String enc) { _encoding=StrUte.nsb(enc); }
    
    
    /**
     * @return
     */
    public String getEncoding() { return _encoding; }
    
    /**/
    public boolean isZiped() {        return _cmpz;    }
    
    /**
     * Control the internal file.
     * 
     * @param del true to delete, false ignore.
     */
    public void setDeleteFile(boolean del)    {        _cls= del;    }
    
    /**
     * Tests if the file is to be deleted.
     * 
     * @return
     */
    public boolean isDeleteFile()    {        return _cls;    }
    
    /**
     * Clean up.
     */
    public void destroy()    {
        
        for (int i=0; i < _res.size(); ++i) {
            StreamUte.close( _res.get(i) );
        }
        _res.clear();
        
        if (! isEmpty(_fp)) {
            if (isDeleteFile()) {
            delete(_fp); }
        }
        
        reset();
    }

    /**
     * Tests if the internal data is a file.
     * 
     * @return
     */
    public boolean isDiskFile()     {        return ! isEmpty( _fp);    }

    
    /**
     * @param obj
     * @throws IOException
     */
    public void resetMsgContent(Object obj) throws IOException    {
        resetMsgContent(obj, true);
    }
    
    
    /**
     * @param obj
     * @param delIfFile
     * @throws IOException
     */
    public void resetMsgContent(Object obj, boolean delIfFile) throws IOException    {

        destroy();

        byte[] bits= null;
        File f= null;
        
        if (obj instanceof ByteArrayOutputStream)         {
            ByteArrayOutputStream baos=(ByteArrayOutputStream) obj;
            maybeCmpz( bits= baos.toByteArray());
        }
        else 
        if (obj instanceof byte[])        {
            maybeCmpz( bits = (byte[]) obj);
        }
        else 
        if (obj instanceof File)        {
            _fp= getFilePath( f= (File) obj );
        }
        else 
        if (obj instanceof File[])        {
            File[] fs=(File[]) obj;
            if (fs.length > 0)
            _fp= getFilePath( f= fs[0]);
        }
        else
        if (obj != null)         {
            maybeCmpz( bits = obj.toString().getBytes(_encoding));
        }
        
        if (! isEmpty(_fp)) {            
            _fp= niceFPath(_fp);
            setDeleteFile(delIfFile);
        }
        
        // debug stuff
        if (this==null) {           
            if (f != null) {
                tlog().debug("StreamData: disk file: " + f + "\nsize = " + f.length()) ;                
            }
            else {
                tlog().debug("StreamData: bytes to be stored = " +
                        (bits== null ? 0 :
                        bits.length)) ;                
            }
        }
        
    }

    /**
     * Get the internal data.
     * 
     * @return
     * @throws IOException
     */
    public Object getMsgContent() throws IOException    {
        if (isDiskFile()) return new File( _fp);
        if ( _bin==null) return null;
        if (! _cmpz) return _bin;
        return gunzip( _bin);
    }

    
    /**
     * @return
     */
    public Boolean hasContent()     {
        if (isDiskFile()) return true;
        if ( _bin !=null) return true;
        return false;
    }
    
    /**
     * Get the data as a stream.
     * 
     * @return
     * @throws Exception
     */
    public InputStream getStream() throws IOException    {
        return getStream(false);
    }

    /**
     * Get the data as bytes.  If it's a file-ref, the entire file content will be read
     * in as byte[].
     * 
     * @return
     * @throws IOException
     */
    public byte[] getBytes() throws IOException    {
        return isDiskFile() ? bytesFromFile(new File( _fp)) : getBin();
    }

    /**
     * Get the file path if it is a file-ref.
     * 
     * @return the file-path, or null.
     */
    public File getFileRef()     { 
        return ! isEmpty(_fp) ? new File( _fp) : null; 
    }

    /**
     * Get the file path if it is a file-ref.
     * 
     * @return the file-path, or null.
     */
    public String getFp()     { 
        return !  isEmpty(_fp) ? _fp : null; 
    }

    /**
     * Get the internal data if it is in memory byte[].
     * 
     * @return 
     * @throws IOException 
     */
    public byte[] getBin() throws IOException    {
        if ( _bin==null) return null;
        if (!  _cmpz) return _bin;
        return gunzip( _bin);
    }

    /**
     * Get the size of the internal data (no. of bytes).
     * 
     * @return
     * @throws IOException
     */
    public long getSize() throws IOException    {
        return isDiskFile() ? getFileRef().length() : _binSize;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable    {
        destroy();
    }
        
    private InputStream getStream(boolean deleteFile) throws IOException    {
        
        if (isDiskFile()) {
            InputStream inp= new SmartFileInputStream(new File( _fp)); //, deleteFile);
            _res.add(inp);
            return inp;
        }
        
        if ( _bin==null) return null;
        if (! _cmpz) return asStream( _bin);
        return asStream( gunzip( _bin));
    }
    
    private void maybeCmpz(byte[] bits) throws IOException    {
        
        _binSize= bits.length;
        
        if (bits == null || bits.length < CMPZ_THREADHOLD)        {
            _cmpz=false;
            _bin=bits;
        }   else    {
            _cmpz=true;
            _bin= gzip(bits); 
        }
        
    }

    private void reset()    {        
        _cmpz=false;
        _res.clear();
        _cls=true;
        _bin=null;
        _fp=null;
        _binSize=0L;
    }

}
