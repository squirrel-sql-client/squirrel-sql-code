package net.sourceforge.squirrel_sql.fw.util.log;

public interface ILoggerListener
{
	public void info(Class<?> source, Object message);
	public void info(Class<?> source, Object message, Throwable th);
	public void warn(Class<?> source, Object message);
	public void warn(Class<?> source, Object message, Throwable th);
	public void error(Class<?> source, Object message);
	public void error(Class<?> source, Object message, Throwable th);
}
