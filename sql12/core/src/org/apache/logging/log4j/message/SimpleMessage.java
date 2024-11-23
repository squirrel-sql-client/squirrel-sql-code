package org.apache.logging.log4j.message;

public class SimpleMessage extends Message
{
   private String _s;

   public SimpleMessage()
   {
      this(null);
   }

   public SimpleMessage(String s)
   {
      _s = s;
   }

   @Override
   public String toString()
   {
      return "" + _s;
   }
}
