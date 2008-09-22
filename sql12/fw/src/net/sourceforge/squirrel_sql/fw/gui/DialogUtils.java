/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

/**
 * A Utility class that is instantiatable and delegates all calls to the static methods of Dialogs. This
 * allows IDialogUtils to be injected so that a direct reference to Dialogs is unnecessary. This is important
 * in unit tests since it is not desirable to instantiate windowing toolkit components.
 * 
 * @author manningr
 */
public class DialogUtils implements IDialogUtils
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#selectFileForWriting(java.awt.Frame,
	 *      net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter[])
	 */
	public File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters)
	{
		return Dialogs.selectFileForWriting(parentFrame, filters);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#selectFileForWriting(java.awt.Frame,
	 *      net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter[], javax.swing.JComponent)
	 */
	public File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters, JComponent accessory)
	{
		return Dialogs.selectFileForWriting(parentFrame, filters, accessory);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#showNotYetImplemented(java.awt.Component)
	 */
	public void showNotYetImplemented(Component owner)
	{
		Dialogs.showNotYetImplemented(owner);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#showOk(java.awt.Component, java.lang.String)
	 */
	public void showOk(Component owner, String msg)
	{
		Dialogs.showOk(owner, msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#showYesNo(java.awt.Component, java.lang.String)
	 */
	public boolean showYesNo(Component owner, String msg)
	{
		return Dialogs.showYesNo(owner, msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#showYesNo(java.awt.Component, java.lang.String,
	 *      java.lang.String)
	 */
	public boolean showYesNo(Component owner, String msg, String title)
	{
		return Dialogs.showYesNo(owner, msg, title);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.IDialogUtils#showInputDialog(java.awt.Component, 
	 * java.lang.Object, java.lang.String, int, javax.swing.Icon, java.lang.Object[], java.lang.Object)
	 */
	public String showInputDialog(Component parentComponent, Object message, String title, int messageType,
		Icon icon, Object[] selectionValues, Object initialSelectionValue)
	{
		String dbName =
			(String) JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon,
				selectionValues, initialSelectionValue);
		return dbName;
	}
}
