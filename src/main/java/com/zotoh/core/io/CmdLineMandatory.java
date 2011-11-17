/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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


/**
 * @author kenl
 *
 */
public abstract class CmdLineMandatory  extends CmdLineQuestion {
    

    /**
     * @param id
     * @param question
     * @param choices
     * @param defAnswer
     */
    public CmdLineMandatory(String id, String question, String choices, String defAnswer) {
        super(id,question,choices,defAnswer);
        _mandatory=true;
    }
    
    
    /**
     * @param id
     * @param question
     */
    public CmdLineMandatory(String id, String question) {
        this(id, question, "", "");
    }
    
    
    
    
}



