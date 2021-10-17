package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

@FunctionalInterface
public interface MessageHandlerReader
{
   IMessageHandler getMessageHandler();
}
