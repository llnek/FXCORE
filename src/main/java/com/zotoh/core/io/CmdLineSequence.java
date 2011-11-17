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
import static com.zotoh.core.util.StrUte.trim;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import static com.zotoh.core.util.LangUte.*;


/**
 * @author kenl
 *
 */
public abstract class CmdLineSequence {
    
    private Map<String,CmdLineQuestion> _Qs;
    private CmdLineSequence _par;
    private Reader _in;
    private Writer _out;
    private boolean _canceled;
    
    
    /**
     * @param props
     * @throws IOException
     */
    public void start(Properties props) throws IOException {
        
        CmdLineQuestion c;
        
        if (_par == null) {
            System.out.println(">>> Press <Ctrl-D><Enter> to cancel...");                    
        }  else {
            _par.start(props);
            if (_par.isCanceled()) { 
                _canceled=true;
                end();
                return;
            }
        }
                
        c= _Qs.get(onStart());
        
        if ( c== null) { 
            end();  
        }    else {
            cycle(c, props);
        }
        
        
    }
    
    
    /**
     * @return
     */
    public boolean isCanceled() { return _canceled; }
    
    
    /**
     * @param id
     */
    public void remove(String id) {        _Qs.remove(id);    }
    
    
    private void cycle(CmdLineQuestion c, Properties props) throws IOException {
        while (c != null) {
            c.setOutputProperties(props);
            c= _Qs.get( popQuestion(c));
        }        
        end();
    }
    
    
    /**
     * @param c
     * @return
     * @throws IOException
     */
    protected String popQuestion(CmdLineQuestion c) 
                throws IOException {
        String d= c.getDefaultAnswer();
        String q= c.getQuestion();
        String ch= c.getChoices();
        String s= "";
        
        _out.write(q);
        
        if (c.isMandatory()) { _out.write('*'); }
        _out.write(" ? ");
        
        if (! isEmpty(ch)) {
        	
        	if (ch.indexOf('\n')>= 0) {
                _out.write(
                				( ch.startsWith("\n") ? "[" : "[\n")
                				+ ch 
                				+ ( ch.endsWith("\n") ? "]" : "\n]") );        		        		        		
        	} else {
                _out.write("[" + ch + "]");        		
        	}
        	
            s= " ";
        }
        
        if (! isEmpty(d)) {
            _out.write("(" + d + ")");
            s= " ";
        }
        
        _out.write(s);
        _out.flush();
        
        // get the input from user
        s= readData();
        
        if (isCanceled()) {
            System.out.println("");
            s="";
        }   else {
            s= c.setAnswer(s);
        }
        
        return s;
    }
        
    private String readData() throws IOException {
        StringBuilder b=new StringBuilder();
    	boolean esc=false;
    	int c;
    	
        // windows has '\r\n'
        // linux has '\n'
    	while(true) {
    		c=  _in.read();
    		if (c== -1 || c==4) { esc=true; break; }
    		if (c=='\r' || c== '\b'|| c==27 /*esc*/) { continue; }
    		if (c== '\n') { break; }
//    		if (c=='\r' || c== '\n') { break; }
    		b.append( (char) c) ;
    	}
    	
    	if (esc) {
    		_canceled=true;
    		b.setLength(0);
    	}

    	return trim( b.toString());
    }
    
    private void end() {
        /*
        try { _out.close(); } catch (Throwable e) {}
        try { _in.close(); } catch (Throwable e) {}
        */
        onEnd();
    }
    

    /**
     * @return
     */
    protected abstract String onStart();

    
    /**
     * 
     */
    protected void onEnd()
    {}
    
    private CmdLineSequence()   {
        _out= new OutputStreamWriter( new BufferedOutputStream(System.out));
        _in = new InputStreamReader(System.in);
        _Qs= MP();
    }
    

    /**
     * @param par
     * @param qs
     */
    public CmdLineSequence(CmdLineSequence par, CmdLineQuestion...qs)   {
        this(qs);
        _par=par;
    }
    

    /**
     * @param qs
     */
    public CmdLineSequence(CmdLineQuestion...qs)   {
        this();
        for (int i=0; i < qs.length; ++i) {
        _Qs.put( qs[i].getId(), qs[i]);
        }
    }
    
    
    
}



