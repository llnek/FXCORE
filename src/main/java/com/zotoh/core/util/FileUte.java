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

import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.zotoh.core.io.StreamUte;


/**
 * @author kenl
 *
 */
public enum FileUte {
;

    /**
     * @param fp
     * @return
     */
    public static boolean isFileKosher(File fp) {
        return fp != null && fp.exists()
                && fp.canRead() && fp.canWrite() ;
    }
    
    /**
     * @param dir
     * @return
     */
    public static boolean isDirKosher(File dir) {
        return dir != null && dir.exists() && dir.canExecute()
                && dir.canRead() && dir.canWrite() ;
    }
    
    
    /**
     * @param srcFile
     * @param destDir
     * @param createDestDir
     * @return
     * @throws IOException
     */
    public static File moveFileToDir(File srcFile, File destDir, boolean createDestDir) 
    				throws IOException     {
    	
        tstObjArg("source-file", srcFile) ;
        tstObjArg("dest-dir", destDir) ;
        
        if ( !destDir.exists() && createDestDir) {
            destDir.mkdirs();
        }
        
        if ( ! destDir.exists() || ! destDir.isDirectory() ) {            
            throw new IOException("\"" + destDir + "\" does not exist, or not a directory");
        }
                
        return moveFile(srcFile, new File(destDir, srcFile.getName()) );
    }

    
    /**
     * @param srcFile
     * @param destFile
     * @return
     * @throws IOException
     */
    public static File moveFile(File srcFile, File destFile) 
    				throws IOException     {        
    	
        tstObjArg("source-file", srcFile) ;
        tstObjArg("dest-file", destFile) ;
        
        if ( ! srcFile.exists() || ! srcFile.isFile()) {
            throw new IOException("\"" + srcFile + "\" does not exist or not a valid file");
        }
        
        if ( destFile.exists() ) {
            throw new IOException("\"" + destFile + "\" already exists");
        }
        
        if ( ! srcFile.renameTo(destFile)) {
            copyFile( srcFile, destFile );
            if (!srcFile.delete()) {
                FileUte.delete(destFile);
                throw new IOException("Failed to delete original file \"" + srcFile + "\"");
            }
        }
        
        return destFile;
    }

    
    /**
     * @param fp
     * @param s
     * @throws IOException
     */
    public static void writeFile(File fp, String s) throws IOException    {
        if (s != null) { 
            writeFile(fp, asBytes(s));
        }
    }
       
    /**
     * @param fp
     * @param bits
     * @throws IOException
     */
    public static void writeFile(File fp, byte[] bits) throws IOException    {
        OutputStream out= null;
        
        if (fp != null && bits != null)
        try  {
            out= new FileOutputStream(fp);
            out.write(bits);
        }
        finally {        
            StreamUte.close(out);
        }
    }    
    

    /**
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile) 
    				throws IOException    {
        tstObjArg("source-file", srcFile) ;
        tstObjArg("dest-file", destFile) ;
        
        if (srcFile == destFile ||
                srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            return;
        }
        
        if  ( !srcFile.exists() || ! srcFile.isFile()) {
            throw new IOException("\"" + srcFile + "\" does not exist or not a valid file");
        }

        if ( ! new File( destFile.getParent()).mkdirs() ) {
            throw new IOException("Failed to create directory for \"" + destFile + "\"");            
        }
        
        copyOneFile(srcFile, destFile);
    }
    
    /**
     * @param dir
     * @throws IOException
     */
    public static void purgeDir(File dir) throws IOException {
        
    	if ( dir != null) {
    		FileUtils.deleteDirectory(dir) ;
    	}
    }

    /**
     * @param dir
     * @return
     * @throws IOException
     */
    public static void purgeDirFiles(File dir) throws IOException {
        if ( dir != null) { 
        	FileUtils.cleanDirectory(dir) ;
        }
    }
    
    /**
     * @param path
     * @return
     */
    public static String getParentPath(String path) {
        if ( ! isEmpty(path)) {
            path= new File(path).getParent();
        }
        return path;
    }
    
    /**
     * @param path
     * @return
     */
    public static String getBaseName(String path) {        
        if ( ! isEmpty(path)) {
            path= trimExtension( new File(path).getName()); 
        }
        return path;
    }
    
    /**
     * @param path
     * @return
     */
    public static String getFileName(String path) {        
        if ( ! isEmpty(path)) {
            path= new File(path).getName(); 
        }
        return path;
    }
    
    private static String trimExtension(String path) {
        
        if ( ! isEmpty(path)) {
            int pos = posOfSuffix(path);
            if (pos >= 0) {
                path=path.substring(0, pos);
            }
        }
        
        return path;
    }

    /**
     * @param path
     */
    public static void delete(String path)     {
    	delete(  new File(path) );    	
    }

    /**
     * @param f
     */
    public static void delete(File f)     {
    	try {
	    	if (f.isDirectory()) { purgeDir(f) ; }
	    	else {
	    		f.delete();
	    	}
    	}
    	catch (Exception e) {}        
    }

    private static int maybeLastDirSep(String path) {
        int pos= -1;
        if (path != null) {
            pos= Math.max( path.lastIndexOf("/"), path.lastIndexOf("\\"));
        }
        return pos;
    }

    private static int posOfSuffix(String file) {
        int p1,p2, pos= -1;
        if (file != null) {
            p1 = file.lastIndexOf(".");
            p2 = maybeLastDirSep(file);
            pos= (p2 > p1 ? -1 : p1);
        }
        return pos;
    }
    
    private static void copyOneFile(File srcFile, File destFile) 
    				throws IOException {        
        if (destFile.exists()) {
            
            if (!destFile.isFile()) {
                throw new IOException("\"" + destFile + "\" exists but is not a valid file");                                
            }
            
            if (!destFile.canWrite()) {
                throw new IOException("Cannot overwrite \"" + destFile + "\"");                
            }
            
        }
        
        InputStream src = new FileInputStream(srcFile);
        OutputStream out = null;
        
        try {
            streamToStream(src, out = new FileOutputStream(destFile));
        } 
        finally {
            StreamUte.close(out);
            StreamUte.close(src);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        
        // preserve the file datetime
        destFile.setLastModified(srcFile.lastModified());
    }

}
