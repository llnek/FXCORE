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
 
package com.zotoh.core.crypto;

import static com.zotoh.core.util.CoreUte.isSame;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import com.zotoh.core.util.Logger;

/**
 * A Password is a wrapper class which has the ability
 * to obfuscate a piece of clear text.
 * 
 * @author kenl
 *
 */
public final class Password implements java.io.Serializable  {
    
    private Logger ilog() {       return _log=getLogger(Password.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {         return _log==null ? ilog() : _log;    }    
    
    private static final long serialVersionUID = -6871562722221029618L;
    public static final String PWD_PFX= "CRYPT:" ;
    private static final int PWD_PFX_SZ= PWD_PFX.length();
    private String _pwd;
    
    /**
     * Constructor from encrypted text.
     * 
     * @param encoded
     * @param dummy
     * @throws Exception
     */
    protected Password(String encoded, int dummy) throws Exception      {
        _pwd= encoded;
        if ( !isEmpty(encoded)) {
            if (encoded.startsWith(PWD_PFX)) {
                encoded=encoded.substring(PWD_PFX_SZ); 
            }            
            _pwd=new JavaOfuscator().unobfuscate(encoded); 
        }
    }
    
    /**
     * Construct from clear text.
     * 
     * @param clearText
     * @throws Exception
     */
    protected Password(String clearText) throws Exception     {
        _pwd= clearText;
    }
    
    /**
     * Copy Constructor.
     * 
     * @param p
     */
    protected Password(Password p)     {
        if (p==null || p._pwd==null) {
            _pwd= null;
        } else {
            _pwd= new String(p._pwd);
        }
    }

    /**
     * 
     */
    protected Password()     {
        _pwd= null;
    }

    /**
     * Get the password as encrypted text.  The convention is to have a prefix
     * "CRYPT:" prepended to indicate that the text is ofuscated.
     * 
     * @return
     */
    public String getAsEncoded()     {
        String c= _pwd;
        
        if ( ! isEmpty(_pwd))
        try   {
            c= PWD_PFX + new JavaOfuscator().obfuscate(_pwd);
        }
        catch (Exception e) {
            tlog().warn("",e);
        }
                
        return c;
    }
    
    /**
     * Return the password as clear text.
     * 
     * @return
     */
    public String getAsClearText()     {        return _pwd;    }

    /**
     * Tests to see if password has any data.
     * 
     * @return
     */
    public boolean isNull()     {        return isEmpty(_pwd) ;    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()    {        return getAsClearText();    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)     {
        Password in = o instanceof Password ? ((Password)o) : null;
        String ins = in==null ? null : in._pwd;
        return isSame(_pwd, ins);
    }
    
    
    
    
}
