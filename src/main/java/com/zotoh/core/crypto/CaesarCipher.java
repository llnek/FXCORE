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


package com.zotoh.core.crypto;

import java.util.Arrays;

/**
 * @author kenl
 *
 */
public class CaesarCipher {
	
	private static final int ALPHA_CHS= 26;	
	private int _shift;
	
	/**
	 * Positive => right shift, Negative => left shift.
	 * 
	 * @param shiftCount
	 */
	public CaesarCipher(int shiftCount) {
		_shift= shiftCount;
//		System.out.println("shift = " + _shift) ;
	}
		
	/**
	 * @param txt
	 * @return
	 */
	public String encode(String txt) {
		if (txt==null || txt.length()==0 || _shift==0) return txt;		
		int delta= Math.abs(_shift) % ALPHA_CHS;
		char[] ca=txt.toCharArray() ,
						out = Arrays.copyOf(ca, ca.length) ;
		char ch;
		for (int i=0; i < ca.length; ++i) {
			if (ca[i] >= 'A' && ca[i] <= 'Z') {
				ch=shift_enc(delta, ca[i], 'A', 'Z') ;
			}
			else
			if (ca[i] >= 'a' && ca[i] <= 'z') {
				ch=shift_enc(delta, ca[i], 'a', 'z') ;				
			}
			else {				
				// others stay the same
				continue;
			}
			out[i] = ch;
		}
		
		return new String(out);
	}
	
	private char shift_enc(int delta, char c, char head, char tail) {
		if (_shift > 0) {
			return shiftRight(ALPHA_CHS, delta, (int) c, (int) head, (int) tail );
		} else {
			return shiftLeft(ALPHA_CHS, delta, (int) c, (int) head, (int) tail );
		}
	}
	
	private char shift_dec(int delta, char c, char head, char tail) {
		if (_shift < 0) {
			return shiftRight(ALPHA_CHS, delta, (int) c, (int) head, (int) tail );
		} else {
			return shiftLeft(ALPHA_CHS, delta, (int) c, (int) head, (int) tail );
		}
	}
	
	private char shiftRight(int width, int delta, int c, int head, int tail) {
		int ch = c + delta;
		if (ch > tail) {
			ch = ch - width ;
		}
		return (char) ch;
	}
	
	private char shiftLeft(int width, int delta, int c, int head, int tail) {		
		int ch = c - delta;
		if (ch < head) {
			ch = ch + width;
		}
		return (char) ch;
	}
	
	/**
	 * @param crypt
	 * @return
	 */
	public String decode(String crypt) {
		if (crypt==null || crypt.length()==0 || _shift==0) return crypt;		
		int delta= Math.abs(_shift) % ALPHA_CHS;
		char[] ca=crypt.toCharArray() ,
						out = Arrays.copyOf(ca, ca.length) ;
		char ch;
		for (int i=0; i < ca.length; ++i) {
			if (ca[i] >= 'A' && ca[i] <= 'Z') {
				ch= shift_dec(delta, ca[i], 'A', 'Z') ;
			}
			else
			if (ca[i] >= 'a' && ca[i] <= 'z') {
				ch= shift_dec(delta, ca[i], 'a', 'z') ;				
			}
			else {
				// others stay the same
				continue;
			}
			out[i]=ch;
		}
		
		return new String(out);
	}
	
}
