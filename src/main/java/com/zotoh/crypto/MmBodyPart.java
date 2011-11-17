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
 
package com.zotoh.crypto;

import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

import com.zotoh.core.io.ByteFragment;
import com.zotoh.core.io.ByteFragmentInputStream;

/**
 * @author kenl
 *
 */
public class MmBodyPart extends MimeBodyPart {
    
    protected ByteFragment _ct;

    /**
     * @param hdrs
     * @param ct
     * @throws MessagingException
     */
    public MmBodyPart(InternetHeaders hdrs, ByteFragment ct) throws MessagingException     {
        headers= hdrs;
        _ct= ct;
    }

    
    /**
     * @param hdrs
     * @param ct
     * @throws MessagingException
     */
    public MmBodyPart(InternetHeaders hdrs, byte[] ct) throws MessagingException     {
        _ct= new ByteFragment(ct);
        headers= hdrs;
    }

    
    /**
     * @param inp
     * @throws MessagingException
     */
    public MmBodyPart(InputStream inp) throws MessagingException    {
        super(inp);
        _ct = new ByteFragment(super.content);
    }

    
    /**
     * 
     */
    public MmBodyPart()
    {}

    
    /**
     * @return
     * @throws MessagingException
     */
    public ByteFragment getContentBP() throws MessagingException     {
        return _ct;
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeBodyPart#getContentStream()
     */
    @Override
    protected InputStream getContentStream() throws MessagingException     {
        return (_ct == null) ? super.getContentStream() : new ByteFragmentInputStream(_ct);
    }

    
}

