package net.sourceforge.squirrel_sql.fw.util.log;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class SquirrelLogger implements ILogger
{
	private ILoggerListener _listener;
	private Class<?> _clazz;

	SquirrelLogger(Class<?> clazz, ILoggerListener listener)
	{
		Utilities.checkNull("SquirrelLogger.init","clazz", clazz, "listener", listener);
		_listener = listener;
		_clazz = clazz;
	}

	/**
	 * @see ILogger#debug(Object)
	 */
	public void debug(Object message)
	{
		SQLoggerKernel.debug(_clazz, message);
	}

	/**
	 * @see ILogger#debug(Object, Throwable)
	 */
	public void debug(Object message, Throwable th)
	{
		SQLoggerKernel.debug(_clazz, message, th);
	}

	/**
	 * @see ILogger#info(Object)
	 */
	public void info(Object message)
	{
		if(message instanceof Throwable)
		{
			SQLoggerKernel.info(_clazz, null , (Throwable) message);
		}
		else
		{
			SQLoggerKernel.info(_clazz, message);
		}
		_listener.info(_clazz, message);
	}

	/**
	 * @see ILogger#info(Object, Throwable)
	 */
	public void info(Object message, Throwable th)
	{
		SQLoggerKernel.info(_clazz, message, th);
		_listener.info(_clazz, message, th);
	}

	/**
	 * @see ILogger#warn(Object)
	 */
	public void warn(Object message)
	{
		if(message instanceof Throwable)
		{
			SQLoggerKernel.warn(null , (Throwable) message);
		}
		else
		{
			SQLoggerKernel.warn(_clazz, message);
		}
		_listener.warn(_clazz, message);
	}

	/**
	 * @see ILogger#warn(Object, Throwable)
	 */
	public void warn(Object message, Throwable th)
	{
		SQLoggerKernel.warn(_clazz, message, th);
		_listener.warn(_clazz, message, th);
	}

	/**
	 * @see ILogger#error(Object)
	 */
	public void error(Object message)
	{
		if(message instanceof Throwable)
		{
			SQLoggerKernel.error(null , (Throwable) message);
		}
		else
		{
			SQLoggerKernel.error(_clazz, message);
		}
		_listener.error(_clazz, message);
	}

	/**
	 * @see ILogger#error(Object, Throwable)
	 */
	public void error(Object message, Throwable th)
	{
		SQLoggerKernel.error(_clazz, message, th);
		_listener.error(_clazz, message, th);
	}

	/**
	 * @see ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return SQLoggerKernel.isDebugEnabled();
	}

	/**
	 * @see ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return SQLoggerKernel.isInfoEnabled();
	}
}
