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

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.zotoh.core.crypto.Password;
import com.zotoh.core.crypto.PwdFactory;

/**
 * @author kenl
 *
 */
public enum DataUte {
;

	/**
	 * @param obj
	 * @return
	 */
	public static boolean toBoolean(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else {
			return Boolean.parseBoolean(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static double toDouble(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Double) {
			return (Double) obj;
		} else {
			return Double.parseDouble(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static float toFloat(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Float) {
			return (Float) obj;
		} else {
			return Float.parseFloat(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static long toLong(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Long) {
			return (Long) obj;
		} else {
			return Long.parseLong(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static int toInt(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Integer) {
			return (Integer) obj;
		} else {
			return Integer.parseInt(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static Timestamp toTimestamp(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Timestamp) {
			return (Timestamp) obj;
		} else {
			return Timestamp.valueOf(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static Date toDate(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Date) {
			return (Date) obj;
		} else {
			return parseDate(obj.toString());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static byte[] toBytes(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof byte[]) {
			return (byte[]) obj;
		} else
			return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static URI toUri(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof URI) {
			return (URI) obj;
		} else if (obj instanceof String) {
			try {
				return new URI(obj.toString());
			} catch (Exception e) {
				tlog().warn("", e);
			}
		}
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static Password toPassword(Object obj) {
		if (isNull(obj))
			throw new RuntimeException("Null object conversion not allowed");
		if (obj instanceof Password) {
			return (Password) obj;
		} else if (obj instanceof String) {
			try {
				return PwdFactory.getInstance().create(obj.toString());
			} catch (Exception e) {
				tlog().warn("", e);
			}
		}
		return null;
	}

	/**
	 * @param cz
	 * @param s
	 * @return
	 */
	public static Object conv2Obj(Class<?> cz, String s) {

		Object o = null;

		if (Boolean.class.equals(cz))
			o = Boolean.parseBoolean(s);
		else if (Timestamp.class.equals(cz))
			o = CoreUte.parseTimestamp(s);
		else if (Date.class.equals(cz))
			o = CoreUte.parseDate(s);
		else if (Integer.class.equals(cz))
			o = Integer.parseInt(s);
		else if (Long.class.equals(cz))
			o = Long.parseLong(s);
		else if (Double.class.equals(cz))
			o = Double.parseDouble(s);
		else if (Float.class.equals(cz))
			o = Float.parseFloat(s);
		else if (String.class.equals(cz))
			o = s;
		else if (Password.class.equals(cz)) {
			try {
				o = PwdFactory.getInstance().create(s);
			} catch (Exception e) {
				tlog().warn("", e);
				o = null;
			}
		} else if (byte[].class.equals(cz))
			o = toBytes(s);

		return o;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String convDown(Object obj) {
		return conv2String(obj, -1);
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String convUp(Object obj) {
		return conv2String(obj, 1);
	}

	private static String conv2String(Object obj, int dir) {

		String str = "";

		if (obj instanceof Timestamp) {
			str = fmtTimestamp((Timestamp) obj);
		} else if (obj instanceof Date) {
			str = fmtDate((Date) obj);
		} else if (obj instanceof Password) {
			Password pw = (Password) obj;
			str = dir < 0 ? pw.getAsEncoded() : pw.getAsClearText();
		} else if (obj instanceof byte[]) {
			str = Base64.encodeBase64String((byte[]) obj);
		} else if (obj instanceof URI) {
			str = ((URI) obj).toString();
		} else if (obj instanceof Double || obj instanceof Float
						|| obj instanceof Integer || obj instanceof Long
						|| obj instanceof Boolean) {
			str = obj.toString();
		} else if (obj instanceof String) {
			str = (String) obj;
		} else if (obj != null) {
			str = obj.toString();
		}

		return str;
	}

}
