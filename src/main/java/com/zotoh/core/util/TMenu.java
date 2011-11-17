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

import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.errBadArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.trim;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * @author kenl
 *
 */
public class TMenu {
	
	protected final List<TMenuItem> _choices= LT();
    protected final Set<String> _ids= ST();
	protected final String _title;

    private Logger ilog() { return _log= getLogger(TMenu.class); }
    private transient Logger _log= ilog();
    public Logger tlog() { return _log==null ? ilog() : _log ;   }
    
	protected TMenu _prev;
	
	
	/**
	 * @param title
	 */
	public TMenu(String title) {
		_title=trim(title);
	}
	
	
	/**
	 * 
	 */
	public void pop() {
		if (_prev != null) { _prev.display(); }
	}

	
	/**
	 * @param upper
	 */
	public void show(TMenu upper) {
		if (upper != null) { _prev=upper; }
		display();
	}

	
	/**
	 * 
	 */
	protected void display() {
		Console c= System.console();
//		clsConsole();
		dispTitle(c);
		int pos=0;
		for (TMenuItem i : _choices) {
			dispMItem(c, ++pos, i.getDesc());
		}		
        dispMItem(c, 99, ((_prev==null) ? "Quit" : "^Back"));
		
		String s=getInput(c);
		int ptr= asInt(trim(s), 0);
		if (ptr==99) {
			pop();
		}
		else	if (ptr >=1 && ptr <= _choices.size()) {		    
			_choices.get(ptr-1).onSelect();
		}
		else {
			display();
		}
	}
	
	
	/**
	 * @param i
	 */
	public void add(TMenuItem i) {
		tstObjArg("menu-item", i);
		if (_ids.contains(i.getId())) {
			errBadArg("Item with same id exists already");
		}
		i.setParent(this);
        _ids.add(i.getId());
		_choices.add(i) ;
	}
	
	
	/**
	 * @param i
	 */
	public void remove(TMenuItem i) {
		if (i != null) {
			_ids.remove(i.getId()) ;
            _choices.remove(i) ;
		}
	}

	
	/**
	 * @return
	 */
	public Collection<TMenuItem> getItems() {
		return Collections.unmodifiableList(_choices);
	}
	
	
	private void dispTitle(Console c) {
	    if (c != null) {
	        c.printf("****************************************\n");
	        c.printf("%s %s", "Menu:",_title);
	        c.printf("****************************************\n");	        
	    }
	    else {
            System.out.print("****************************************\n");
            System.out.format("%s %s\n", "Menu:",_title);
            System.out.print("****************************************\n");         	        
	    }
	}
	
    
    private void dispMItem(Console c, int pos, String desc) {
        if (c != null) {
            c.printf("%2d)  %s\n", pos, desc);             
        }
        else {
            System.out.format("%2d)  %s\n", pos, desc);                         
        }
    }	
    
    
    private String getInput(Console c) {
        String rc="";
        if (c != null) {
            rc= c.readLine();
        }
        else {        
            try {
                rc= new BufferedReader(new InputStreamReader(System.in)).readLine();                
            }
            catch (IOException e) {
                tlog().warn("",e);
            }
        }       
        return rc;
    }
	
	
}
