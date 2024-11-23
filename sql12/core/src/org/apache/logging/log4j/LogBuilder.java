package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;

public interface LogBuilder
{
   void log(CharSequence message);
   void log(String message);
   void log(String message, Object... params);
   void log(String message, org.apache.logging.log4j.util.Supplier<?>... params);
   void log(Message message);
   void log(org.apache.logging.log4j.util.Supplier<Message> messageSupplier);
   Message logAndGet(final org.apache.logging.log4j.util.Supplier<Message> messageSupplier);
   void log(Object message);
   void log(String message, Object p0);
   void log(String message, Object p0, Object p1);
   void log(String message, Object p0, Object p1, Object p2);
   void log(String message, Object p0, Object p1, Object p2, Object p3);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8);
   void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);
   void log();

}
