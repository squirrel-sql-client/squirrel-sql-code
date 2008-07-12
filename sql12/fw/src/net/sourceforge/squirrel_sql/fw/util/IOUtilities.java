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
package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.CRC32;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilities {

   /** Logger for this class. */
   private final static ILogger s_log = LoggerController.createLogger(IOUtilities.class);

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

   public static void closeOutputStream(OutputStream os) {
      if (os != null) {
         try {
            os.close();
         } catch (Exception e) {
            s_log.error("closeOutpuStream: Unable to close OutputStream - "
                  + e.getMessage(), e);
         }
      }
   }

   /**
    * Closes the specified Reader which can be null.  Logs an error if 
    * an exception occurs while closing.
    * 
    * @param reader the Reader to close.
    */
   public static void closeReader(Reader reader) {
      if (reader != null) {
         try {
            reader.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileReader - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   public static void closeWriter(Writer writer) {
      if (writer != null) {
         try {
            writer.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileWriter - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   /**
    * Reads from the specified InputStream and copies bytes read to the
    * specified OuputStream.
    * 
    * @param is
    *           the InputStream to read from
    * @param os
    *           the OutputStream to write to
    * @throws IOException
    *            in an exception occurs while reading/writing
    */
   public static void copyBytes(InputStream is, OutputStream os) 
      throws IOException
   {
      byte[] buffer = new byte[8192];
      int length;
      while ((length = is.read(buffer)) > 0) {
         os.write(buffer, 0, length);
      }
   }
   
   /**
    * Computes the CRC32 checksum for the specified file.  This doesn't appear
    * to be compatible with cksum.
    * 
    * @param f the file to compute a checksum for.
    * 
    * @return the checksum value for the file specified
    */
   public static long getCheckSum(File f) throws IOException {
       CRC32 result = new CRC32();  
       FileInputStream fis = null;
       try {
           fis = new FileInputStream(f);
           int b = 0;
           while ((b = fis.read()) != -1) {
               result.update(b);
           }
       } finally {
           IOUtilities.closeInputStream(fis);
       }
       return result.getValue();
   }
   
}
