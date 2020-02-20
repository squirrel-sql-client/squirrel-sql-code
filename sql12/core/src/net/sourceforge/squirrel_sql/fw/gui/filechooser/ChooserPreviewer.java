package net.sourceforge.squirrel_sql.fw.gui.filechooser;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileManagementUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * This is a decorator class that can be used in a <TT>JFileChooser</TT>
 * to preview the contents of a text or image file. If the file name
 * has a suffix of jpg, jpeg, gif or png this class will attempt to render
 * it as an image, else it will render it as text.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ChooserPreviewer extends JComponent
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChooserPreviewer.class);

	private static final ILogger s_log = LoggerController.createLogger(ChooserPreviewer.class);

	/**
	 * Empty panel. Used when cannot display anything in the
	 * preview panel.
	 */
	private final JPanel _emptyPnl = new JPanel();

	/** Text area to display the contents of a text file in. */
	private final JTextArea _textComponent = new JTextArea();

	/** Label to display the contents of an image file in. */
	private final JLabel _imageComponent = new JLabel();

	/** Component currently being displayed in the preview area. */
	private Component _currentComponent;

	/** Scrollpane for <TT>_currentComponent</TT>. */
	private JScrollPane _currentComponentSp;

	/** <TT>JFileChooser</TT> that this accessory belongs to. */
	private JFileChooser _fileChooser;

	private PropertyChangeListener _propChangeListener;

	/**
	 * Default ctor.
	 * @param fileChooser
	 */
	public ChooserPreviewer(JFileChooser fileChooser)
	{
		_fileChooser = fileChooser;
		createUserInterface();
		_propChangeListener = e -> onPropertyChanged(e);
		_fileChooser.addPropertyChangeListener(_propChangeListener);
	}

	/**
	 * Remove listener from the chooser.
	 */
	void cleanup()
	{
		_fileChooser.removePropertyChangeListener(_propChangeListener);
	}

	/**
	 * The file selected in the <TT>FileChooser</TT> has changed so display
	 * its contents in this previewer.
	 */
	private void fileChanged()
	{
		Component componentToUse = _emptyPnl;

		File file = _fileChooser.getSelectedFile();
		if (file != null && file.isFile() && file.canRead())
		{
			String suffix = Utilities.getFileNameSuffix(file.getPath()).toLowerCase();
			if (suffix.equals("gif") || suffix.equals("jpg")
				|| suffix.equals("jpeg") || suffix.equals("png"))
			{
				componentToUse = readImageFile(file);
			}
			else
			{
				componentToUse = readTextFile(file);
			}
		}

		if (componentToUse != _currentComponent)
		{
			_currentComponentSp.setViewportView(componentToUse);
			_currentComponent = componentToUse;
		}
	}

	/**
	 * Read the image from the passed file and return it within
	 * a component.
	 *
	 * @param	file	The file to be read.
	 *
	 * @return	The image component.
	 */
	protected Component readImageFile(File file)
	{
		final ImageIcon icon = new ImageIcon(file.getPath());
		_imageComponent.setIcon(icon);
		return _imageComponent;
	}

	/**
	 * Read the first portion of the passed file
	 * and place them in the text component. If cannot
	 * read it then clear the text component.
	 *
	 * @param	file	The file to be read.
	 *
	 * @return	The text component
	 */
	protected Component readTextFile(File file)
	{
		String text = "";
		if (file != null && file.isFile() && file.canRead())
		{
			try
			{
				text = FileManagementUtil.readFileAsString(file, 15000);
			}
			catch (Exception e)
			{
				String errMsg = s_stringMgr.getString("ChooserPreviewer.errorReadingFile", file.getAbsolutePath(), e.toString());
				Main.getApplication().getMessageHandler().showErrorMessage(errMsg);
				text = errMsg;
				s_log.error(errMsg, e);
			}
		}
		_textComponent.setText(text);
		_textComponent.setCaretPosition(0);
		return _textComponent;
	}

	/**
	 * Create the User Interface.
	 */
	private void createUserInterface()
	{
		_textComponent.setEditable(false);
		setLayout(new BorderLayout());
		_currentComponentSp = new JScrollPane(_textComponent);
		add(_currentComponentSp, BorderLayout.CENTER);
		//setPreferredSize(new Dimension(400, 0));
	}

	private void onPropertyChanged(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
		{
			fileChanged();
		}
	}
}
