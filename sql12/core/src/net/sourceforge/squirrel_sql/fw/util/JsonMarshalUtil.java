package net.sourceforge.squirrel_sql.fw.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.SimpleType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.*;

public class JsonMarshalUtil
{
   public final static ILogger s_log = LoggerController.createLogger(JsonMarshalUtil.class);

   public static void writeObjectToFile(File file, Object jsonBean)
   {
      try
      {
         FileOutputStream fos = new FileOutputStream(file);

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         // This version of objectWriter.writeValue() ensures,
         // that objects are written in JsonEncoding.UTF8
         // and thus that there won't be encoding problems
         // that makes the loadObjects methods crash.
         objectWriter.writeValue(fos, jsonBean);

         fos.close();

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T> T readObjectFromFileSave(File file, Class<T> clazz, T errorFallBackReturnValue)
   {
      try
      {
         return readObjectFromFile(file, clazz);
      }
      catch (Throwable e)
      {
         s_log.error("Failed to read Json file " + file + " for to load object of class " + clazz + ". Will return errorFallBackReturnValue.", e);
         return errorFallBackReturnValue;
      }
   }

   public static <T> T readObjectFromFile(File file, Class<T> clazz)
   {
      try(FileInputStream is = new FileInputStream(file);
          InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName()))
      {
         ObjectMapper mapper = new ObjectMapper();
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         return mapper.readValue(isr, SimpleType.construct(clazz));
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
