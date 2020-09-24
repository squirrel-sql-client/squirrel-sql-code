package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.csvreader;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteObjectMarkerUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ByteObjectMarkerUtil.class);

   private final static ILogger s_log = LoggerController.createLogger(ByteObjectMarkerUtil.class);

   public static int getBomLength(File importFile, Charset charset)
   {
      try
      {
         if(null == charset)
         {
            return 0;
         }

         try(FileInputStream bomCheckFis = new FileInputStream(importFile))
         {
            final byte[] firstFourBytes = new byte[4];
            bomCheckFis.read(firstFourBytes);
            return getLengthOfBom(firstFourBytes, charset);
         }
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   /**
    * See https://en.wikipedia.org/wiki/Byte_order_mark#Byte_order_marks_by_encoding
    * and for & 255: https://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java
    */
   private static int getLengthOfBom(byte[] buffer, Charset charset)
   {
      if(null != charset)
      {
         if(StandardCharsets.UTF_8.equals(charset))
         {
            if((buffer[0] & 255) == 239 && (buffer[1]  & 255) == 187 && (buffer[2] & 255) == 191 )
            {
               return logBomRemove(charset,3);
            }
         }
         else if(StandardCharsets.UTF_16BE.equals(charset))
         {
            if((buffer[0] & 255) == 254 && (buffer[1] & 255) == 255)
            {
               return logBomRemove(charset,2);
            }
         }
         else if(StandardCharsets.UTF_16LE.equals(charset))
         {
            if((buffer[0] & 255) == 255 && (buffer[1] & 255) == 254)
            {
               return logBomRemove(charset,2);
            }
         }
         else if(charset.name().equals("UTF-32BE"))
         {
            if((buffer[0] & 255) == 0 && (buffer[1] & 255) == 0 && (buffer[2] & 255) == 254 && (buffer[3] & 255) == 255)
            {
               return logBomRemove(charset,4);
            }
         }
         else if(charset.name().equals("UTF-32LE"))
         {
            if((buffer[0] & 255) == 255 && (buffer[1] & 255) == 254 && (buffer[2] & 255) == 0 && (buffer[3] & 255) == 0)
            {
               return logBomRemove(charset,4);
            }
         }

      }

      return 0;
   }

   private static int logBomRemove(Charset charset, int bomLength)
   {
      final String msg = s_stringMgr.getString("ByteObjectMarkerUtil.bomRemovedMessage", charset.name(), bomLength);
      Main.getApplication().getMessageHandler().showMessage(msg);
      s_log.info(msg);

      return bomLength;
   }

}
