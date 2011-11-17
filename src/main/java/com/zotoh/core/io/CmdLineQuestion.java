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

import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;

import java.util.Properties;


/**
 * @author kenl
 *
 */
public abstract class CmdLineQuestion {
    
    protected boolean _mandatory=false;
    private String _defaultAnswer="",
    _id,
    _choices,
    _answer,
    _question;
    private Properties _props;
    
    
    /**
     * @param id
     * @param question
     * @param choices
     * @param defAnswer
     */
    public CmdLineQuestion(String id, String question, String choices, String defAnswer) {
        _id=nsb(id);
        _defaultAnswer=nsb(defAnswer);
        _question=nsb(question);
        _choices= nsb(choices);
    }
    
    
    /**
     * @param id
     * @param question
     */
    public CmdLineQuestion(String id, String question) {
        this(id, question, "", "");
    }
    
    /**
     * @return
     */
    public boolean isMandatory() { return _mandatory; }
    
    /**
     * @param b
     */
    public void setMandatory(boolean b) { _mandatory=b;} 
    
    
    /**
     * @return
     */
    public String getId() { return _id; }
    
    
    /**
     * @return
     */
    public String getQuestion() { return _question; }
    
    
    /**
     * @return
     */
    public String getChoices() { return _choices; }
    
    
    /**
     * @return
     */
    public String getAnswer() { return _answer; }

    
    /**
     * @return
     */
    public String getDefaultAnswer() { return _defaultAnswer; }
    
    /**
     * @param a
     */
    public void setDefaultAnswer(String a) { _defaultAnswer=nsb(a); }
    
    
    /**
     * @param c
     */
    public void setChoices(String c) { _choices=nsb(c); }
        
    /**
     * @param props
     */
    protected void setOutputProperties(Properties props) {
        _props=props;
    }
    
    
    /**
     * @param a
     * @return
     */
    protected String setAnswer(String a) {
        if (isEmpty(a)) {
            a= _defaultAnswer;
        }
        _answer= nsb(a);
        return onAnswerSetOutput(_answer, _props);
    }
    
    /**
     * @param answer
     * @param props
     * @return
     */
    protected abstract String onAnswerSetOutput(String answer, Properties props);
    
}



