package org.squirrelsql.session;

public interface SessionManagerListener
{
   void contextActiveOrActivating(SessionTabContext sessionTabContext);
   void contextClosing(SessionTabContext sessionTabContext);
}
