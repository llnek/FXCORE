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


import static com.zotoh.core.util.CoreUte.getCZldr;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LangUte.MP;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;


/**
 * Utility functions for class related or reflection related operations. 
 *
 * @author kenl
 *
 */
public enum MetaUte {
;

    /**
     * @param z
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> forName( String z) throws ClassNotFoundException {        
        return Class.forName(z) ;
    }
    
    /**
     * 
     * @param clazz
     * @param ld
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClass(String clazz, ClassLoader... ldr) 
    				throws ClassNotFoundException     {
        return getCZldr(ldr).loadClass(clazz); 
    }

    /**
     * Create an object of this class, calling the default constructor.
     * 
     * @param clazz
     * @param ldr optional.
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static Object create(String clazz, ClassLoader... ldr)  throws NoSuchMethodException
		    , InvocationTargetException
		    , IllegalAccessException
		    , InstantiationException
		    , ClassNotFoundException    {
//        tstEStrArg("class-name", clazz) ;
        return  create( loadClass(clazz, ldr)) ;
    }

    
    /**
     * Create an object of this class, calling the default constructor.
     * 
     * @param c
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object create(Class<?> c) throws NoSuchMethodException
		    , InvocationTargetException
		    , IllegalAccessException
		    , InstantiationException     {
        tstObjArg("class-obj", c) ;
        return c.getDeclaredConstructor((Class[])null).newInstance((Object[])null);
    }
    
    /**
     * @param c
     * @return
     */
    public static Class<?>[] getAllParents(Class<?> c) {        
        List< Class<?> > bin= LT();
        collPars(c, bin);
        if (bin.size() > 0) {
        	// since we always add the original class
        		bin.remove(0);
        }
        return bin.toArray( new Class<?>[0] ) ; 
    }
    
    
    /**
     * @param c
     * @return
     */
    public static Method[] getAllMethods(Class<?> c) {        
        Map<String,Method> bin= MP();
        collMtds(c, 0, bin);
        return bin.values().toArray(new Method[0] ) ; 
    }
    
    
    /**
     * @param c
     * @return
     */
    public static Field[] getAllFields(Class<?> c) {        
        Map<String, Field> bin= MP();
        collFlds(c, 0, bin);
        return bin.values().toArray(new Field[0] ) ; 
    }
    
    private static void collPars(Class<?> c, List<Class<?> > bin) {
        Class<?> par = c.getSuperclass();
        if (par != null) {
            collPars(par, bin);
        }
        bin.add(0, c);
    }
    
    private static void collFlds(Class<?> c, int level, Map<String,Field> bin) {
        
        Field[] flds= c.getDeclaredFields();
        int x;
        Class<?> par = c.getSuperclass();
        
        if (par != null) {
            collFlds(par, level +1, bin);
        }

        for (int i=0;  flds != null && i < flds.length; ++i) {           

            if (level > 0) {
                x= flds[i].getModifiers();
                // we only want the inherited fields from parents
                if ( Modifier.isStatic(x)  || Modifier.isPrivate(x))
                continue;
            }
            
            bin.put(flds[i].getName(), flds[i]);
        }
        
    }
    
    private static void collMtds(Class<?> c, int level, Map<String,Method> bin) {
        
        Method[] mtds= c.getDeclaredMethods();
        int x;
        Class<?> par = c.getSuperclass();
        
        if (par != null) {
            collMtds(par, level +1, bin);
        }
        
        for (int i=0;  mtds != null && i < mtds.length; ++i) {
            
            if (level > 0) {
                x= mtds[i].getModifiers();
                // we only want the inherited methods from parents
                if ( Modifier.isStatic(x)  || Modifier.isPrivate(x))
                continue;
            }
            
            bin.put(mtds[i].getName(), mtds[i]);
        }
        
    }
    
}
