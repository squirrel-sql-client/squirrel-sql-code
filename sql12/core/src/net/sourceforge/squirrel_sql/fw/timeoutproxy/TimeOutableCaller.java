package net.sourceforge.squirrel_sql.fw.timeoutproxy;

@FunctionalInterface
public interface TimeOutableCaller<T>
{
   T call() throws Exception;
}
