package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001 - 2002 Colin Bell
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

/**
 * About box dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutBoxDialog extends JDialog
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AboutBoxDialog.class);

	/** Singleton instance of this class. */
	private static AboutBoxDialog s_instance;

	/** The tabbed panel. */
	private SquirrelTabbedPane _tabPnl;

	/** System panel. */
	private SystemPanel _systemPnl;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String ABOUT = "About";
	}

	private AboutBoxDialog(IApplication app)
	{
		super(app.getMainFrame(), i18n.ABOUT, true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		createGUI(app);
	}

	/**
	 * Show the About Box.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
	 */
	public static synchronized void showAboutBox(IApplication app)
		throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (s_instance == null)
		{
			s_instance = new AboutBoxDialog(app);
		}
		s_instance.show();
	}

	private void createGUI(IApplication app)
	{
		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;

		_tabPnl = new SquirrelTabbedPane(app.getSquirrelPreferences());

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_tabPnl.add("About", new AboutPanel(app)); // i18n
		if (isDebug)
		{
			s_log.debug("AboutPanel created in "
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_tabPnl.add("Credits", new CreditsPanel(app)); // i18n
		if (isDebug)
		{
			s_log.debug("CreditsPanel created in "
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_systemPnl = new SystemPanel(app);
		_tabPnl.add("System", _systemPnl); // i18n
		if (isDebug)
		{
			s_log.debug(
				"SystemPanel created in "
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}

		_tabPnl.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals("System"))
				{
					_systemPnl._memoryPnl.startTimer();
				}
				else
				{
					_systemPnl._memoryPnl.stopTimer();
				}
			}
		});

		contentPane.add(_tabPnl, BorderLayout.CENTER);

		// Ok button at bottom of dialog.
		JPanel btnsPnl = new JPanel();
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setVisible(false);
			}
		});
		btnsPnl.add(okBtn);
		contentPane.add(btnsPnl, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okBtn);

		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals("System"))
				{
					_systemPnl._memoryPnl.startTimer();
				}
			}
			public void windowDeactivated(WindowEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals("System"))
				{
					_systemPnl._memoryPnl.stopTimer();
				}
			}
		});

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private static final class CreditsPanel extends JScrollPane
	{
		CreditsPanel(IApplication app)
		{
			super();

			setBorder(BorderFactory.createEmptyBorder());

			final JEditorPane credits = new JEditorPane();
			credits.setEditable(false);
			credits.setContentType("text/html");

			// Required with the first beta of JDK1.4.1 to stop
			// this scrollpane from being too tall.
			credits.setPreferredSize(new Dimension(200, 200));

			final StringBuffer creditsData = readCreditsFile(app);

			// Get list of contributors names.
			final Map map = new TreeMap();
			readContributorsFile(app, map);

			// Get list of all plugin developers names. Allow for multiple
			// developers for a plugin in the form "John Smith, James Brown".
			PluginInfo[] pi = app.getPluginManager().getPluginInformation();
			for (int i = 0; i < pi.length; ++i)
			{
				String authors = pi[i].getAuthor();
				StringTokenizer strok = new StringTokenizer(authors, ",");
				while (strok.hasMoreTokens())
				{
					String tok = strok.nextToken().trim();
					map.put(tok, tok);
				}
				authors = pi[i].getAuthor();
				strok = new StringTokenizer(authors, ",");
				while (strok.hasMoreTokens())
				{
					String tok = strok.nextToken().trim();
					map.put(tok, tok);
				}
			}

			// Put some HTML formatting around each developers name.
			final StringBuffer devNamesBuf = new StringBuffer();
			devNamesBuf.append("<CENTER>");
			for (Iterator it = map.keySet().iterator(); it.hasNext();)
			{
				String theName = (String) it.next();
				devNamesBuf.append("<EM>").append(theName).append("</EM><BR>");
			}
			devNamesBuf.append("</CENTER>");
			final String names = devNamesBuf.toString();

			int pos = creditsData.toString().indexOf("%0");
			if (pos > -1)
			{
				creditsData.replace(pos, pos + 2, names);
				credits.setText(creditsData.toString());
			}
			else
			{
				s_log.error("Unable to find Plugin Developers replacement token %0 in credits.html");
			}

			setViewportView(credits);
			credits.setCaretPosition(0);
		}

		private void readContributorsFile(IApplication app, Map map)
		{
			final URL url = app.getResources().getContributorsURL();
			if (url != null)
			{
				try
				{
					BufferedReader rdr = new BufferedReader(new InputStreamReader(url.openStream()));
					try
					{
						String line = null;
						while ((line = rdr.readLine()) != null)
						{
							map.put(line, line);
						}
					}
					finally
					{
						rdr.close();
					}
				}
				catch (IOException ex)
				{
					s_log.error("Error reading contributors file", ex);
				}
			}
			else
			{
				s_log.error("Couldn't retrieve contributors File URL");
			}
		}

		private StringBuffer readCreditsFile(IApplication app)
		{
			final URL url = app.getResources().getCreditsURL();
			StringBuffer buf = new StringBuffer(2048);

			if (url != null)
			{
				try
				{
					BufferedReader rdr = new BufferedReader(new InputStreamReader(url.openStream()));
					try
					{
						String line = null;
						while ((line = rdr.readLine()) != null)
						{
							buf.append(line);
						}
					}
					finally
					{
						rdr.close();
					}
				}
				catch (IOException ex)
				{
					s_log.error("Error reading credits file", ex);
					buf.append("Error reading credits file: " + ex.toString());
				}
			}
			else
			{
				s_log.error("Couldn't retrieve Credits File URL");
				buf.append("Couldn't retrieve Credits File URL");
			}
			return buf;
		}
	}

	private static final class AboutPanel extends JPanel
	{
		AboutPanel(IApplication app)
		{
			super();
			final SquirrelResources rsrc = app.getResources();
			setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			setLayout(new BorderLayout());
			setBackground(new Color(rsrc.S_SPLASH_IMAGE_BACKGROUND));
			Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);
			add(BorderLayout.CENTER, new JLabel(icon));
			JTextArea ta = new JTextArea();
			ta.setEditable(false);
			ta.setOpaque(false);
			ta.append(Version.getVersion());
			ta.append("\n");
			ta.append(Version.getCopyrightStatement());
			ta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(BorderLayout.SOUTH, ta);
		}
	}

	private static final class SystemPanel extends JPanel
	{
		private MemoryPanel _memoryPnl;

		SystemPanel(IApplication app)
		{
			super();
			setLayout(new BorderLayout());
			DataSetViewerTablePanel propsPnl = new DataSetViewerTablePanel();
			try
			{
				propsPnl.show(new HashtableDataSet(System.getProperties()));
			}
			catch (DataSetException ex)
			{
				s_log.error("Error occured displaying System Properties", ex);
			}

			_memoryPnl = new MemoryPanel();
			add(new JScrollPane(propsPnl.getComponent()), BorderLayout.CENTER);
			add(_memoryPnl, BorderLayout.SOUTH);

			//setPreferredSize(new Dimension(400, 400));
		}
	}

	private static class MemoryPanel
		extends PropertyPanel
		implements ActionListener
	{
		private final JLabel _totalMemoryLbl = new JLabel();
		private final JLabel _usedMemoryLbl = new JLabel();
		private final JLabel _freeMemoryLbl = new JLabel();
		private Timer _timer;

		MemoryPanel()
		{
			super();
			add(new JLabel("Java heap size:"), _totalMemoryLbl); // i18n
			add(new JLabel("Used heap:"), _usedMemoryLbl); // i18n
			add(new JLabel("Free heap:"), _freeMemoryLbl); // i18n

			JButton gcBtn = new JButton("Garbage Collect"); //i18n
			gcBtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					System.gc();
				}
			});
			add(gcBtn, new JLabel(""));
		}

		public void removeNotify()
		{
			stopTimer();
			super.removeNotify();
		}

		/**
		 * Update component with the current memory status.
		 * 
		 * @param	evt		The current event.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			updateMemoryStatus();
		}

		synchronized void startTimer()
		{
			if (_timer == null)
			{
				s_log.debug("Starting memory timer (AboutBox)");
				//_thread = new Thread(new MemoryTimer());
				//_thread.start();
				updateMemoryStatus();
				_timer = new Timer(2000, this);
				_timer.start();
			}
		}

		synchronized void stopTimer()
		{
			if (_timer != null)
			{
				s_log.debug("Ending memory timer (AboutBox)");
				_timer.stop();
				_timer = null;
			}
		}

		private void updateMemoryStatus()
		{
			Runtime rt = Runtime.getRuntime();
			final long totalMemory = rt.totalMemory();
			final long freeMemory = rt.freeMemory();
			final long usedMemory = totalMemory - freeMemory;
			_totalMemoryLbl.setText(Utilities.formatSize(totalMemory, 1));
			_usedMemoryLbl.setText(Utilities.formatSize(usedMemory, 1));
			_freeMemoryLbl.setText(Utilities.formatSize(freeMemory, 1));
		}
	}
}
