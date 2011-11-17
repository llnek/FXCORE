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

import static com.zotoh.core.crypto.Password.*;

/**
 * Creates passwords. 
 * 
 * @author kenl
 *
 */
public enum PwdFactory {
    
    INSTANCE;
    
    /**
     * Create a Password object from the given text.  If the text is prefixed
     * with "CRYPT:", then it is treated as encrypted content.
     * 
     * @param text
     * @return
     * @throws Exception
     */
    public Password create(String text) throws Exception     {
        if ( text != null && text.startsWith( PWD_PFX)) {
            return createFromCrypto(text) ;
        }
        else {
            return createFromText(text) ;
        }
    }
    
    /**
     * Make a clone/copy of an existing Password object.
     * 
     * @param p
     * @return
     */
    public Password copy(Password p)     {
        return new Password(p);
    }

    /**
     * Create a new password which is considered strong, i.e,
     * the password will have a mixture of lower/uppercase
     * characters, numbers, and punctuations.
     * 
     * @param length the length of the target password.
     * @return the new password in plain-text.
     * @throws Exception 
     */
    public Password createStrongPassword(int length) throws Exception     {
        return create(PwdFacImpl.createStrong(length));
    }

    /**
     * Create a text string which has random characters.
     * 
     * @param length the length of the string.
     * @return a string with random characters(letters).
     */
    public String createRandomText(int length)     {
        return PwdFacImpl.createRandom(length);
    }

    /**
     * Get the factory object for password operations.
     * 
     * @return
     */
    public static PwdFactory getInstance() {    return INSTANCE; }    

    
    private Password createFromCrypto(String encoded) throws Exception     {
        return new Password(encoded, -1);
    }

    private Password createFromText(String text) throws Exception     {
        return new Password(text);
    }

    private PwdFactory()
    {}
    
}
