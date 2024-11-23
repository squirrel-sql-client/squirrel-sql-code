package org.apache.logging.log4j.util;

public class Unbox
{
   public static StringBuilder box(final float value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final double value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final short value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final int value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final char value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final long value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final byte value)
   {
      return getSB().append(value);
   }

   public static StringBuilder box(final boolean value)
   {
      return getSB().append(value);
   }

   private static StringBuilder getSB()
   {
      return new StringBuilder();
   }
}
