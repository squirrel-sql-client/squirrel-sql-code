package net.sourceforge.squirrel_sql.fw.timeoutproxy;

@FunctionalInterface
public interface TimeOutableInvoker
{
   void invoke() throws Exception;
}
