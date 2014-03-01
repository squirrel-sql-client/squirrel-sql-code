package net.sourceforge.squirrel_sql.plugins.dbcopy.util;
/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


/**
 * A description of this class goes here...
 */

public class MemoryDiagnostics implements Runnable {

    Thread t = null;
    
    private volatile boolean shutdown = false;
    
    private static int sleepTimeMills = 10000;
    
    public MemoryDiagnostics() {
        t = new Thread(this);
        t.setName("MemoryDiagnosticsThread");
        t.start();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (!shutdown) {
            printMemoryUsage();
            gc();
            try {
                Thread.sleep(sleepTimeMills);
            } catch (InterruptedException e) {
                // Probably shutting down.
            }
        }
        
    }

    public void printMemoryUsage() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long max = Runtime.getRuntime().maxMemory();
        System.out.println("MemoryDiagnostics.printMemoryUsage: Total="+total);
        System.out.println("MemoryDiagnostics.printMemoryUsage: Free="+free);
        System.out.println("MemoryDiagnostics.printMemoryUsage: Max="+max);
        if (total > (max/2)) {
            System.out.println("Memory allocation > 50%, running GC");
            gc();
        }
    }
    
    public void gc() {
        System.gc();
    }
    
    public void shutdown() {
        t.interrupt();
    }
    
}
