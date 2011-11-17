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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.LoggerFactory.getLogger;


/**
 * @author kenl
 *
 */
public enum VMUte {
;

    private static Logger _log= getLogger(VMUte.class);
    public static Logger tlog() { return _log; }    


    /**
     * @param runBeforeRestart
     * @throws IOException
     */
    public static void relaunchJVM(Runnable runBeforeRestart) throws IOException {
    	
        try {
            List<String> vmargs = ManagementFactory.getRuntimeMXBean().getInputArguments();
            String[] cmdargs = System.getProperty("sun.java.command").split(" ");
            if (cmdargs==null || cmdargs.length == 0) {
                throw new IOException("Not supported on this JVM");
            }
            
            StringBuilder cmd = new StringBuilder();
            cmd.append( niceFPath(System.getProperty("java.home")))
            .append("/bin/")
            .append(isWindows() ? "java.exe" : "java");
            cmd.append(" ");
            for (String arg : vmargs) {
                if (arg.indexOf("-agentlib") >=0) {}
                else {
                    cmd.append(arg).append(" ");
                }
            }

            // add classpath ref
            cmd.append("-cp \"").append( System.getProperty("java.class.path")).append("\"");
            cmd.append(" ");
            
            // add target
            if (cmdargs[0].endsWith(".jar")) {
                cmd.append("-jar " + new File(cmdargs[0]).getAbsolutePath());
            } 
            else {
                cmd.append(cmdargs[0]);
            }

            // add rest of application args
            
            for (int i = 1; i < cmdargs.length; i++) {
                cmd.append(" ").append(cmdargs[i]);
            }
            
            final String execStr= cmd.toString();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Runtime.getRuntime().exec( execStr);
                    } catch (IOException e) {
                        tlog().warn("",e);
                    }
                }
            });

            if (runBeforeRestart!= null) {
                runBeforeRestart.run();
            }

            System.exit(0);
        } 
        catch (Exception e) {
            throw new IOException("Error while trying to relaunch jvm", e);
        }
    }
    
    @SuppressWarnings("unused")
    private static void main(String[] args) {
        try {
            relaunchJVM(new Runnable() {
                public void run() {}
            });
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
