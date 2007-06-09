package net.sourceforge.squirrel_sql.fw.util;


/**
 * This message handler just swallows messages sent to it.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class NullMessageHandler implements IMessageHandler
{
	private static NullMessageHandler s_handler = new NullMessageHandler();

	/**
	 * Ctor private becuase this is a singleton.
	 */
	private NullMessageHandler()
	{
		super();
	}

	/**
	 * Return the only instance of this class.
	 *
	 * @return	the only instance of this class.
	 */
	public static NullMessageHandler getInstance()
	{
		return s_handler;
	}

	/**
	 * Swallow this msg.
	 */
	public void showMessage(Throwable th, ExceptionFormatter formatter)
	{
		// Empty.
	}

	/**
	 * Swallow this msg.
	 */
	public void showMessage(String msg)
	{
		// Empty.
	}

	/**
	 * Swallow this msg.
	 */
	public void showErrorMessage(Throwable th, ExceptionFormatter formatter)
	{
		// Empty.
	}

	/**
	 * Swallow this msg.
	 */
	public void showErrorMessage(String msg)
	{
		// Empty.
	}

   public void showWarningMessage(String msg)
   {
      // Empty.
   }
      
}
