package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

/**
 * This class provides some methods for using standard JDK dialogs.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Dialogs
{
	public static File selectFileForWriting(Frame parentFrame,
							FileExtensionFilter[] filters)
	{
		File outFile = null;
		final JFileChooser chooser = new JFileChooser();
		if (filters != null)
		{
			for (int i = 0; i < filters.length; ++i)
			{
				chooser.addChoosableFileFilter(filters[i]);
			}
		}

		for (;;)
		{
			outFile = null;
			if (chooser.showSaveDialog(parentFrame) == chooser.APPROVE_OPTION)
			{
				outFile = chooser.getSelectedFile();
				if (canSaveToFile(parentFrame, outFile))
				{
					break;
				}
			}
			else
			{
				break;
			}
		}

		return outFile;
	}

	public static void showNotYetImplemented(Component owner)
	{
		JOptionPane.showMessageDialog(owner,
			"This function has not yet been implemented", "",
			JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean showYesNo(Component owner, String msg)
	{
		return showYesNo(owner, msg, "");
	}

	public static boolean showYesNo(Component owner, String msg, String title)
	{
		int rc = JOptionPane.showConfirmDialog(owner, msg, title,
												JOptionPane.YES_NO_OPTION);
		return rc == JOptionPane.YES_OPTION;
	}

	public static void showOk(Component owner, String msg)
	{
		JOptionPane.showMessageDialog(owner, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}

	private static boolean canSaveToFile(Frame parentFrame, File outFile)
	{
		if (!outFile.exists())
		{
			return true;
		}
		String msg = outFile.getAbsolutePath() +
			"\nalready exists. Do you want to replace it?";
		if (!Dialogs.showYesNo(parentFrame, msg))
		{
			return false;
		}
		if (!outFile.canWrite())
		{
			msg = "File " + outFile.getAbsolutePath()
					+ "\ncannot be written to.";
			Dialogs.showOk(parentFrame, msg);
			return false;
		}
		outFile.delete();
		return true;
	}

}