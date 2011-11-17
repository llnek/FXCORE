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

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.*;


/**
 * @author kenl
 *
 */
public class TMenuItem {

	private TMenu _sub, _parent;
	private String _desc;
	private String _id;
	private TMenuCB _cb;
	
	
	/**
	 * @param id
	 * @param desc
	 * @param m
	 */
	public TMenuItem(String id, String desc, TMenu m) {
		this(id,desc);
		tstObjArg("sub-menu", m);
		_sub=m;
	}
	
	
	/**
	 * @param id
	 * @param desc
	 * @param cb
	 */
	public TMenuItem(String id, String desc, TMenuCB cb) {
		this(id,desc);
		tstObjArg("menuitem-callback", cb);
		_cb=cb;
	}
	
	
	private TMenuItem(String id, String desc) {
		tstEStrArg("menuitem-description", desc);
		tstEStrArg("menuitem-id", id);
		_desc=trim(desc);
		_id=trim(id);		
	}
	
	
	/**
	 * @param m
	 */
	protected void setParent(TMenu m) {
		_parent=m;
	}
	
	
	/**
	 * @return
	 */
	public TMenu getParent() {
		return _parent;
	}
	
	
	/**
	 * @return
	 */
	public String getDesc() { return _desc; }

	
	/**
	 * @return
	 */
	public String getId() { return _id; }
	
	
	/**
	 * 
	 */
	public void onSelect() {
		if ( _sub != null) { _sub.show(getParent()); }
		if ( _cb != null) { _cb.command(this); }
	}
	
}
