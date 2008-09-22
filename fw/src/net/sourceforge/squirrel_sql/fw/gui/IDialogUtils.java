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

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

/**
 * This "describes" the interface of Dialogs which is a static method utility class. Class should get an
 * instance of this injected into them if they want to avoid direct dependence on GUI framework objects.
 * 
 * @author manningr
 */
public interface IDialogUtils
{

	File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters);

	File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters, JComponent accessory);

	void showNotYetImplemented(Component owner);

	boolean showYesNo(Component owner, String msg);

	boolean showYesNo(Component owner, String msg, String title);

	void showOk(Component owner, String msg);

	/**
	 * From JavaDoc for JOptionPane.showInputDialog:
	 * 
	 * Prompts the user for input in a blocking dialog where the initial selection, possible selections, and
	 * all other options can be specified. The user will able to choose from <code>selectionValues</code>,
	 * where <code>null</code> implies the user can input whatever they wish, usually by means of a
	 * <code>JTextField</code>. <code>initialSelectionValue</code> is the initial value to prompt the user
	 * with. It is up to the UI to decide how best to represent the <code>selectionValues</code>, but usually a
	 * <code>JComboBox</code>, <code>JList</code>, or <code>JTextField</code> will be used.
	 * 
	 * @param parentComponent
	 *           the parent <code>Component</code> for the dialog
	 * @param message
	 *           the <code>Object</code> to display
	 * @param title
	 *           the <code>String</code> to display in the dialog title bar
	 * @param messageType
	 *           the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *           <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *           or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *           the <code>Icon</code> image to display
	 * @param selectionValues
	 *           an array of <code>Object</code>s that gives the possible selections
	 * @param initialSelectionValue
	 *           the value used to initialize the input field
	 * @return user's input, or <code>null</code> meaning the user canceled the input
	 * @exception HeadlessException
	 *               if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
	 */
	public String showInputDialog(Component parentComponent, Object message, String title, int messageType,
		Icon icon, Object[] selectionValues, Object initialSelectionValue);
}