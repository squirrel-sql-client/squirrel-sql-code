package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

/**
 * A Service implementation for JOptionPane static methods that allow guarantees they will be invoked on the
 * event dispatch thread. The interface also allows classes that depend on this implementation in production
 * to use a mock in unit tests.
 */
public class JOptionPaneService implements IJOptionPaneService
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IJOptionPaneService#showMessageDialog(java.awt.Component,
	 *      java.lang.Object, java.lang.String, int)
	 */
	@Override
	public void showMessageDialog(final Component parentComponent, final Object message, final String title,
		final int messageType)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
			}
		});
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IJOptionPaneService#showConfirmDialog(java.awt.Component,
	 *      java.lang.Object, java.lang.String, int, int)
	 */
	public int showConfirmDialog(final Component parentComponent, final Object message, final String title,
		final int optionType, final int messageType) throws HeadlessException
	{
		final RunnableWithIntResult inputRunner = new RunnableWithIntResult()
		{
			private int result = -1;

			@Override
			public void run()
			{
				result = JOptionPane.showConfirmDialog(parentComponent, message, title, optionType, messageType);

			}

			@Override
			public int getResult()
			{
				return result;
			}
		};

		GUIUtils.processOnSwingEventThread(inputRunner, true);
		return inputRunner.getResult();
	}

	private interface RunnableWithIntResult extends Runnable
	{
		public int getResult();
	}
}
