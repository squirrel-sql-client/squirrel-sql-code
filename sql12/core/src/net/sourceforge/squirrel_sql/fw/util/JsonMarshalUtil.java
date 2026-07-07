package net.sourceforge.squirrel_sql.fw.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.SimpleType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class JsonMarshalUtil
{
   public final static ILogger s_log = LoggerController.createLogger(JsonMarshalUtil.class);

   public static void writeObjectToFile(File file, Object jsonBean)
   {
      try
      {
         FileOutputStream fos = new FileOutputStream(file);
         writeObjectToBuffer(jsonBean, fos, true);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static String toJsonString(Object obj)
   {
      return toJsonString(obj, true);
   }

   public static String toJsonString(Object obj, boolean prettyPrinted)
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      writeObjectToBuffer(obj, bos, prettyPrinted);
      return bos.toString(StandardCharsets.UTF_8);
   }

   public static <T> T fromJsonString(String jsonString, Class<T> clazz)
   {
      return readObjectFromStream(new ByteArrayInputStream(jsonString.getBytes()), clazz);
   }

   private static void writeObjectToBuffer(Object jsonBean, OutputStream os, boolean prettyPrinted)
   {
      try
      {
         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter;
         if(prettyPrinted)
         {
            objectWriter = mapper.writerWithDefaultPrettyPrinter();
         }
         else
         {
            // supposed to be single lined.
            objectWriter = mapper.writer();
         }

         // This version of objectWriter.writeValue() ensures,
         // that objects are written in JsonEncoding.UTF8
         // and thus that there won't be encoding problems
         // that makes the loadObjects methods crash.
         objectWriter.writeValue(os, jsonBean);

         os.close();
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static <T> T readObjectFromFileSave(File file, Class<T> clazz, T fallBackReturnValue)
   {
      try
      {
         if (false == file.exists())
         {
            s_log.info("Json file " + file + " to load object of class " + clazz + ". Does not exist will return fallBackReturnValue.");
            return fallBackReturnValue;
         }

         return readObjectFromFile(file, clazz);
      }
      catch (Throwable e)
      {
         s_log.warn("Could not read Json file " + file + " to load object of class " + clazz + ". Will return fallBackReturnValue.", e);
         return fallBackReturnValue;
      }
   }

   public static <T> T readObjectFromFile(File file, Class<T> clazz)
   {
      try (FileInputStream is = new FileInputStream(file))
      {
         return readObjectFromStream(is, clazz);
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static <T> T readObjectFromStream(InputStream is, Class<T> clazz)
   {
      try (InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName()))
      {
         ObjectMapper mapper = new ObjectMapper();
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         return mapper.readValue(isr, SimpleType.construct(clazz));
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
