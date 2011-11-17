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

import static com.zotoh.core.util.StrUte.*;

/**
 * A simple structure for a file content. 
 *
 * @author kenl
 *
 */
public class FileObject implements java.io.Serializable {
    
    private static final long serialVersionUID = 104263410375978495L;
    public String _id, _file, _cType;
    public byte[] _data;

    /**
     * @param id
     * @param filename
     * @param contentType
     * @param bits
     */
    public FileObject(String id, String filename, String contentType, byte[] bits)     {
        
        _cType= nsb(contentType);
        _file= nsb(filename);
        _data=bits;
        _id= nsb(id) ;
    }

}
