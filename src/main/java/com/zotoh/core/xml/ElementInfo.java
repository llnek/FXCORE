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
 
package com.zotoh.core.xml;

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.MP;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;

/**
 * Simple data structure to keep track of information belonging to an xml element.
 *
 * @author kenl
 *
 */
public class ElementInfo implements java.io.Serializable {
    
	private static final long serialVersionUID= -832892378327L;
    private Map<String, String> _atts;    
    private Map<String, Long> _children;
    private String _qname, _lname;
    private ElementInfo _parent;

	/**
	 * Constructor.
	 * 
	 * @param ln local name.
	 * @param qn qualified name.
	 * @param atts set of xml attributes.
	 */	
    public ElementInfo(String ln, String qn, Map<String,String> atts)     {
    	
        tstEStrArg("local-name", ln) ;
        tstEStrArg("q-name", qn) ;
        tstObjArg("attributes", atts) ;
        
        _children= MP();
        _qname= qn;
        _lname= ln;
        _atts= atts;
    }


    /**
     * @param c
     */
    public void addOneChild(ElementInfo c)     {
    	
        Long idx= _children.get(c.getLname());
        
        // this long value is used to keep track of the n# of repeated elements (children) with this local name.
        if (idx==null) { idx= 0L; }
        idx = idx+1;

        c._parent=this;
        _children.put(c.getLname(), idx);
    }

    /**
     * Get the total number of immediate child elements with this same local name.  That is, how many repeated elements are there
     * with this local name.
     * 
     * @param lname local name of element.
     * @return the total count.
     */    
    public long getCountOfChildren(String lname)    {
        Long idx= _children.get(lname);
        
        if (idx==null)         {
        	idx=1L;
            _children.put(lname, idx);        	
        }
        
        return idx;
    }

    
    /**
     * @return
     */
    public String getQname()    {             return _qname;       }

    
    /**
     * @return
     */
    public String getLname()        {                return _lname;       }
    
    
    /**
     * @return
     */
    public Map<String,String> getAtts()        {       
        return 
        unmodifiableMap(_atts);    
    }

    
    /**
     * @return
     */
    public ElementInfo getParent()        {                return _parent;       }
    
    

}
