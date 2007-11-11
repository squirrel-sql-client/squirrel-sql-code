/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.util;

import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilities {
        
    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(IOUtilities.class);
                
    public static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                s_log.error("closeInputStream: Unable to close InputStream - "
                        + e.getMessage(), e);
            }
        }
    }    
    
    public static void closeOutpuStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
                s_log.error("closeOutpuStream: Unable to close OutputStream - "
                        + e.getMessage(), e);
            }
        }
    }
    
   
}
