package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import java.util.Date;

/**
 * A single cell value. Exactly one of the typed value fields is populated
 * (the rest are {@code null}, and are omitted from JSON by the
 * {@code NON_NULL} serialization setting); all {@code null} represents SQL
 * {@code NULL}.
 *
 * @param primitive   {@code true} when the cell holds a primitive value
 *                    ({@code int}/{@code long}/{@code boolean}); {@code false}
 *                    for a textual value
 * @param stringValue the value when textual, else {@code null}
 * @param intValue    the value when an {@code int}, else {@code null}
 * @param longValue   the value when a {@code long}, else {@code null}
 * @param boolValue   the value when a {@code boolean}, else {@code null}
 */
public record McpResultCell(boolean primitive, String stringValue, Integer intValue, Long longValue, Boolean boolValue, Date dateValue, Double doubleValue)
{
   public static McpResultCell ofString(String value)
   {
      return new McpResultCell(false, value, null, null, null, null, null);
   }

   public static McpResultCell ofInt(Integer value)
   {
      return new McpResultCell(true, null, value, null, null, null, null);
   }

   public static McpResultCell ofLong(Long value)
   {
      return new McpResultCell(true, null, null, value, null, null, null);
   }

   public static McpResultCell ofBool(Object value)
   {
      if(null == value)
      {
         return new McpResultCell(true, null, null, null, null, null, null);
      }
      else if(value instanceof Boolean boolValue)
      {
         return new McpResultCell(true, null, null, null, boolValue, null, null);
      }
      else if(value instanceof Number numberValue)
      {
         return new McpResultCell(true, null, null, null, 0 == numberValue.intValue(), null, null);
      }
      return McpResultCell.ofString("" + value);
   }

   public static McpResultCell ofDate(Date value)
   {
      return new McpResultCell(false, null, null, null != value ? value.getTime() : null, null, value, null);
   }

   public static McpResultCell ofDouble(Double value)
   {
      return new McpResultCell(false, null, null, null, null, null, value);
   }
}
