package net.sourceforge.squirrel_sql.plugins.exportconfig.action;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.ChooserPreviewer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
/**
 * Base class for all the "save" commands.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
abstract class AbstractSaveCommand implements ICommand
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AbstractSaveCommand.class);

	/** The last directory saved to. */
	private static File s_lastDir;

	/** Parent frame. */
	private final Frame _frame;

	/** Current plugin. */
	private ExportConfigPlugin _plugin;

	/**
	 * Ctor.
	 *
	 * @param	frame	Parent frame for dialog.
	 * @param	plugin	The current plugin.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ISession</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 */
	AbstractSaveCommand(Frame frame, ExportConfigPlugin plugin)
	{
		super();
		if (frame == null)
		{
			throw new IllegalArgumentException("Frame == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("ExportConfigPlugin == null");
		}
		_frame = frame;
		_plugin = plugin;
	}

	public void execute() throws BaseException
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileExtensionFilter("XML files",
												new String[] { ".xml" }));
		File file = null;
		if (s_lastDir != null)
		{
			file = new File(s_lastDir, getDefaultFilename());
		}
		else
		{
			file = new File((File)null, getDefaultFilename());
		}
		chooser.setSelectedFile(file);
		chooser.setAccessory(new ChooserPreviewer());
		chooser.setDialogTitle("Save " + getSaveDescription());

		for (;;)
		{
			if (chooser.showSaveDialog(_frame) == JFileChooser.CANCEL_OPTION)
			{
				break;
			}
			if (saveFile(chooser.getSelectedFile()))
			{
				break;
			}
		}
	}

	private boolean saveFile(File file)
	{
		if (file.exists())
		{
			if (!Dialogs.showYesNo(_frame, file.getAbsolutePath()
						+ "\nalready exists. Do you want to replace it?"))
			{
				return false;
			}
			if (!file.canWrite())
			{
				Dialogs.showOk(_frame, file.getAbsolutePath()
						+ "\nexists and you cannot write to it.\nPlease use a different name.");
				return false;
			}
			file.delete();
		}

		s_lastDir = file.getParentFile();

		final IApplication app = _plugin.getApplication();
		try
		{
			writeToFile(file);
			app.showErrorDialog(getSaveDescription() + " saved to " +
						file.getAbsolutePath());
		}
		catch (IOException ex)
		{
			_plugin.getApplication().showErrorDialog("Error writing to\n" +
										file.getAbsolutePath(), ex);
		}
		catch (XMLException ex)
		{
			_plugin.getApplication().showErrorDialog("Error writing to\n" +
										file.getAbsolutePath(), ex);
		}
		return true;
	}

	/**
	 * Override this to supply the description of the objects being saved.
	 *
	 * @return	description of the objects being saved.
	 */
	protected abstract String getSaveDescription();

	/**
	 * Override this to supply the default file name to be used.
	 *
	 * @return	The default file name.
	 */
	protected abstract String getDefaultFilename();

	/**
	 * Override this m,ethod to do the actual writing out to the file system.
	 *
	 * @param	file	The <TT>File</TT> to be written to.
	 *
	 * @throws	IOException		Thrown if an IO error occurs.
	 * @throws	XMLException	Thrown if an XML error occurs.
	 */
	protected abstract void writeToFile(File file)
					throws IOException, XMLException;
}
