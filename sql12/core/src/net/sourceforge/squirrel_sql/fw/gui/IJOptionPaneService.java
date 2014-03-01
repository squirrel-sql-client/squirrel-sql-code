package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.HeadlessException;

/**
 * Interface to allow implementations to be injected depending on runtime environment (e.g. production or
 * test).
 */
public interface IJOptionPaneService
{

	/**
	 * @see javax.swing.JOptionPane#showMessageDialog(Component, Object, String, int)
	 */
	public abstract void showMessageDialog(final Component parentComponent, final Object message,
		final String title, final int messageType);

	/**
	 * @see javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int, int) 
	 */
	public int showConfirmDialog(final Component parentComponent, final Object message, final String title,
		final int optionType, final int messageType) throws HeadlessException;

}