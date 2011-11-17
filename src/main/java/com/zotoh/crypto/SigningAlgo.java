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
 
package com.zotoh.crypto;

/**
 * Constants for Signing Algorithms. 
 *
 * @author kenl
 *
 */
public enum SigningAlgo {
    
    SHA512("SHA512withRSA"),
    SHA256("SHA256withRSA"),
    SHA1("SHA1withRSA"),
        SHA_512("SHA-512"),
        SHA_1("SHA-1"),
        SHA_256("SHA-256"),
        MD_5("MD5"),
    MD5("MD5withRSA");
    
    public String toString() { return _algo; }
    private String _algo;
    private SigningAlgo(String s) {
        _algo=s;
    }
    
}
