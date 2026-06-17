package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

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
public record ResultCell(boolean primitive, String stringValue, Integer intValue, Long longValue, Boolean boolValue)
{
   public static ResultCell ofString(String value)
   {
      return new ResultCell(false, value, null, null, null);
   }

   public static ResultCell ofInt(Integer value)
   {
      return new ResultCell(true, null, value, null, null);
   }

   public static ResultCell ofLong(Long value)
   {
      return new ResultCell(true, null, null, value, null);
   }

   public static ResultCell ofBool(Boolean value)
   {
      return new ResultCell(true, null, null, null, value);
   }
}
