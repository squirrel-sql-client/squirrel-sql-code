package org.squirrelsql.services;

import java.lang.reflect.Field;

public class JDBCTypeMapper
{
      public static String getJdbcTypeName(int jdbcType)
      {
         try
         {
            String result = "UNKNOWN";
            Field[] fields = java.sql.Types.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
               Field field = fields[i];
               if (field.getInt(null) == jdbcType)
               {
                  result = field.getName();
                  break;
               }
            }
            return result;
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
