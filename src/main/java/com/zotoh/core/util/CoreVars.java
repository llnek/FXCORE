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

/**
 * General constants. 
 *
 * @author kenl
 *
 */
public interface CoreVars {
    
    public static final String[] MONTHS = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", 
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

    public static final String TS_FMT_NANO="yyyy-MM-dd HH:mm:ss.fffffffff";
    public static final String TS_FMT="yyyy-MM-dd HH:mm:ss";
    
    public static final String DT_FMT_MICRO= "yyyy-MM-dd' 'HH:mm:ss.SSS";
    public static final String DT_FMT= "yyyy-MM-dd' 'HH:mm:ss";
    public static final String DATE_FMT= "yyyy-MM-dd";

    public static final String USASCII= "ISO-8859-1";
    public static final String UTF16="UTF-16";
    public static final String UTF8="UTF-8";
    public static final String SLASH = "/";
    public static final String PATHSEP = SLASH;


}
