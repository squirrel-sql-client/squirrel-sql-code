package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This sheet shows the SQuirreL log files.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewLogsSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "SQuirreL Logs";
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ViewLogsSheet.class);

	/** Singleton instance of this class. */
	private static ViewLogsSheet s_instance;

	/** Application API. */
	private IApplication _app;

	/** Combo box containing all the log files. */
	private DirectoryListComboBox _logDirCmb = new DirectoryListComboBox();
	
	/** Text area containing the log contents. */
	private JTextArea _logContentsTxt = new JTextArea(20, 50);

	/** Directory containing the log files. */
	private File _logDir;

	/**
	 * Ctor specifying the application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication passed.
	 */
	private ViewLogsSheet(IApplication app)
	{
		super(i18n.TITLE);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_logDir = new ApplicationFiles().getExecutionLogFile().getParentFile();
		createUserInterface();
	}

	/**
	 * Show this window
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
	 */
	public static synchronized void showSheet(IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new ViewLogsSheet(app);
			app.getMainFrame().addInternalFrame(s_instance, true, null);
			GUIUtils.centerWithinDesktop(s_instance);
		}
		s_instance.setVisible(true);
	}

	public void dispose()
	{
		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
	}

	/**
	 * Close this sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * Refresh the log.
	 */
	synchronized private void refreshLog()
	{
		CursorChanger cursorChg = new CursorChanger(this);
		cursorChg.show();
		try {
			_logContentsTxt.setText("");
			String log = (String)_logDirCmb.getSelectedItem();
			if (log != null)
			{
				try
				{
					File logFile = new File(_logDir, log);
					if (logFile.exists() && logFile.canRead())
					{
						FileReader rdr = new FileReader(logFile);
						try
						{
							_logContentsTxt.read(rdr, logFile.toURL());
						}
						finally
						{
							rdr.close();
						}
					}
				}
				catch (IOException ex)
				{
					final String msg = "Error occured reading log file";
					s_log.error(msg, ex);
					_app.showErrorDialog(msg, ex);
				}
			}
			else
			{
				s_log.debug("Null log file name");
			}
			
			// Position to the start of the last line in log.
			try
			{
				int pos = _logContentsTxt.getText().length() - 1;
				int line = _logContentsTxt.getLineOfOffset(pos);
				pos = _logContentsTxt.getLineStartOffset(line);
				_logContentsTxt.setCaretPosition(pos);
			}
			catch (BadLocationException ex)
			{
				s_log.error("Error positioning caret in log text component", ex);
			}
		} finally {
			cursorChg.restore();
		}
	}

	/**
	 * Create user interface.
	 */
	private void createUserInterface()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GUIUtils.makeToolWindow(this, true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		contentPane.add(createMainPanel(), BorderLayout.CENTER);
		contentPane.add(createButtonsPanel(), BorderLayout.SOUTH);
		pack();
	}

	private ToolBar createToolBar()
	{
		final ToolBar tb = new ToolBar();
		tb.setBorder(BorderFactory.createEtchedBorder());
		tb.setUseRolloverButtons(true);
		tb.setFloatable(false);

		final JLabel lbl = new JLabel(getTitle() + " are stored in ", SwingConstants.CENTER);
		lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		tb.add(lbl);

		tb.add(new OutputLabel(_logDir.getAbsolutePath()));

		return tb;
	}

	/**
	 * Create the main panel containing the log details and selector.
	 */
	private JPanel createMainPanel()
	{
		_logContentsTxt.setEditable(false);
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_logContentsTxt);
		_logContentsTxt.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		File appLogFile = new ApplicationFiles().getExecutionLogFile();
		_logDirCmb.load(appLogFile.getParentFile());

		_logDirCmb.addActionListener(new ChangeLogListener());
		if (_logDirCmb.getModel().getSize() > 0)
		{
			_logDirCmb.setSelectedItem(appLogFile.getName());
		}

		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(_logDirCmb, BorderLayout.NORTH);
		_logContentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		pnl.add(new JScrollPane(_logContentsTxt), BorderLayout.CENTER);

		return pnl;
	}

	/**
	 * Create panel at bottom containing the buttons.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton refreshBtn = new JButton("Refresh");
		pnl.add(refreshBtn);
		refreshBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshLog();
			}
		});

		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] {closeBtn, refreshBtn});
		getRootPane().setDefaultButton(closeBtn);

		return pnl;
	}

	private final class ChangeLogListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			ViewLogsSheet.this.refreshLog();
		}
	}
}