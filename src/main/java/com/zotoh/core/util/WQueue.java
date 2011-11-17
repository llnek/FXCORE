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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.zotoh.core.util.Logger;

import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.rmi.server.UID;

/**
 * A basic Threadpool that schedules <i>Unit of Work</i>. 
 *
 * @author kenl
 *
 */
public class WQueue implements RejectedExecutionHandler {
    
    private transient Logger _log=getLogger(WQueue.class);   
    public Logger tlog() {   return _log;   }
    
    private ThreadPoolExecutor _sc;
    private String _id;
    
    /**
     * 
     */
    public WQueue() {
        this( new UID().toString() );
    }

    
    /**
     * @param min
     * @param max
     */
    public void start(int min, int max)    {
        iniz(min, max);
    }

    
    /**
     * 
     */
    public void start()    {
        start(1,1);
    }
    
    /**
     * Get the name of this queue.
     * 
     * @return the queue name.
     */
    public String getId()    {        return _id;    }

    /**
     * Place this <i>Unit of Work</i> onto the queue, ready for execution.
     * 
     * @param work some work to be done.
     */
    public void schedule(Runnable work)    {
        _sc.execute(work);
    }
    
    /**
     * Handle the case when there's too much work for the queue to handle.
     * Subclass should override and implement code.
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)    {
        tlog().warn("WQueue rejected work - threads are max'ed out : " + _id);
    }
    
    private void iniz(int min, int max)    {
        _sc= new ThreadPoolExecutor(min, Math.max(1, max), 5000, TimeUnit.MILLISECONDS, 
                new LinkedBlockingQueue<Runnable>(), this);  
        _sc.setThreadFactory(new TFac(_id));
    }

    private WQueue(String id)    {
        _id= id;
    }
    
    /**
     * The default thread factory - from javasoft code.  The reason why
     * we cloned this is so that we can control how the thread-id is
     * traced out. (we want some meaninful thread name).
     */
    private static class TFac implements ThreadFactory {
        private final AtomicInteger tn = new AtomicInteger(1);
        private final ThreadGroup grp;
        private final String pfx;
        public TFac(String id) {
            SecurityManager s = System.getSecurityManager();
            grp = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();            
            pfx = "tpool(" + id + ")";
        }
        public Thread newThread(Runnable r) {
            Thread t = new Thread(grp, r,  pfx + tn.getAndIncrement(),  0);
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
