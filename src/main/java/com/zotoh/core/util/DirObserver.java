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

import static com.zotoh.core.util.CoreUte.tstObjArg;

import java.io.File;
import java.io.IOException;

/**
 * @author kenl
 *
 */
public final class DirObserver {    
    
    private File _root;
    
    /**
     * @param args
     */
    @SuppressWarnings("unused")
	private static void main(String[] args)     {        
        try  {
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * @param rootDir
     */
    public DirObserver(File rootDir )    {
        tstObjArg("root-dir", rootDir) ;
        _root=rootDir;
    }
    
    /**
     * @throws IOException
     */
    public void process( DirWatcher w) throws IOException    {
        walk(_root, w);
    }

    private void walk(File root, DirWatcher ww ) throws IOException     {
    	
        File[] files= root.listFiles() ;
        File f;        
        File[] ff= new File[1];
        
        for (int i=0 ; i < files.length; ++i ) {
        	f = files[i] ;
        	if ( f.isDirectory()) {
        		walk( f, ww) ;
        	} else {
        		ff[0]=f;
        		ww.onDirWithFiles( root , ff ) ;
        	}        	
        }
        
        ww.onDirEnd(root) ;
        
    }
    


}
