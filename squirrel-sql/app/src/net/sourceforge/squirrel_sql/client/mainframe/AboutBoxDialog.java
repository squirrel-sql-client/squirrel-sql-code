package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
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
public class AboutBoxDialog extends JDialog {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(AboutBoxDialog.class);

	/** Singleton instance of this class. */
	private static AboutBoxDialog s_instance;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String ABOUT = "About";
	}

	private AboutBoxDialog(IApplication app) {
		super(app.getMainFrame(), i18n.ABOUT, true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		createUserInterface(app);
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
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (s_instance == null) {
			s_instance = new AboutBoxDialog(app);
		}
		s_instance.show();
	}

	private void createUserInterface(IApplication app) {
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent evt) {
				s_log.debug("windowActivated");
			}
			public void windowDeactivated(WindowEvent evt) {
				s_log.debug("windowdeActivated");
			}
		});

		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;

		JTabbedPane tabPnl = new JTabbedPane();

		if (isDebug) {
			start = System.currentTimeMillis();
		}
		tabPnl.add("About", new AboutPanel(app));  // i18n
		if (isDebug) {
			s_log.debug("AboutPanel created in "
						+ (System.currentTimeMillis() - start) + "ms");
		}

		if (isDebug) {
			start = System.currentTimeMillis();
		}
		tabPnl.add("Credits", new CreditsPanel(app));  // i18n
		if (isDebug) {
			s_log.debug("CreditsPanel created in "
						+ (System.currentTimeMillis() - start) + "ms");
		}

		if (isDebug) {
			start = System.currentTimeMillis();
		}
		tabPnl.add("System", new SystemPanel(app)); // i18n
		if (isDebug) {
			s_log.debug("SystemPanel created in "
						+ (System.currentTimeMillis() - start) + "ms");
		}

		contentPane.add(tabPnl, BorderLayout.CENTER);

		// Ok button at bottom of dialog.
		JPanel btnsPnl = new JPanel();
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});
		btnsPnl.add(okBtn);
		contentPane.add(btnsPnl, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okBtn);

		Dimension ps = contentPane.getPreferredSize();
		ps.width = 400;
		contentPane.setPreferredSize(ps);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}

	private static final class CreditsPanel extends JScrollPane {
		CreditsPanel(IApplication app) {
			super();
			final JEditorPane credits = new JEditorPane();
			credits.setEditable(false);
			credits.setContentType("text/html");
			final URL url = app.getResources().getCreditsURL();
			StringBuffer buf = new StringBuffer(1024);

			try {
				BufferedReader rdr = new BufferedReader(new InputStreamReader(url.openStream()));
				try {
					String line = null;
					while ((line = rdr.readLine()) != null) {
						buf.append(line);
					}
					credits.setText(buf.toString());
				} finally {
					rdr.close();
				}
			} catch (IOException ex) {
				s_log.error("Error reading credits file", ex);
			}

			// Get list of all plugin developers names and place in credits. Make sure that
			// we only display each developer once even if they have developed more than
			// one plugin. Also allow for multiple developers for a plugin in the
			// form "John Smith, James Brown". Sort names.
			Map map = new TreeMap();
			PluginInfo[] pi = app.getPluginManager().getPluginInformation();
			for (int i = 0; i < pi.length; ++i) {
				StringTokenizer strok = new StringTokenizer(pi[i].getPlugin().getAuthor(), ",");
				while (strok.hasMoreTokens()) {
					String tok = strok.nextToken().trim();
					map.put(tok, tok);
				}
			}
			StringBuffer devNamesBuf = new StringBuffer();
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String theName = (String)it.next();
				devNamesBuf.append("<EM>").append(theName).append("</EM>, ");
			}

			// Remove trailing ", ".
			String names = null;
			if (devNamesBuf.length() > 2) {
				names = devNamesBuf.substring(0, devNamesBuf.length() - 2);
			} else {
				names = devNamesBuf.toString();
			}

			String data = buf.toString();
			int pos = data.indexOf("%0");
			if (pos > -1) {
				buf.replace(pos, pos + 2, names);
				credits.setText(buf.toString());
			} else {
				s_log.error("Unable to find Plugin Developers replacement token %0 in credits.html");
			}

			setViewportView(credits);
		}

	}

	private static final class AboutPanel extends JPanel {
		AboutPanel(IApplication app) {
			super();
			setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			setLayout(new BorderLayout());
			setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
			Icon icon = app.getResources().getIcon(SquirrelResources.ImageNames.SPLASH_SCREEN);
			JLabel iconLbl = new JLabel(icon);
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

	private static final class SystemPanel extends JPanel {
		SystemPanel(IApplication app) {
			super();
			setLayout(new BorderLayout());
			DataSetViewerTablePanel propsPnl = new DataSetViewerTablePanel();
			DataSetViewer viewer = new DataSetViewer(propsPnl);
			try {
				viewer.show(new HashtableDataSet(System.getProperties()));
			} catch (DataSetException ex) {
				s_log.error("Error occured displaying System Properties", ex);
			}

			add(new JScrollPane(propsPnl.getComponent()), BorderLayout.CENTER);
			add(new MemoryPanel(), BorderLayout.SOUTH);

			setPreferredSize(new Dimension(400, 400));
		}
	}

	private static class MemoryPanel extends PropertyPanel {
		private JLabel _totalMemoryLbl = new JLabel();
		private JLabel _usedMemoryLbl = new JLabel();
		private JLabel _freeMemoryLbl = new JLabel();
		private boolean _killThread = false;
		private Thread _thread = null;

		MemoryPanel() {
			super();
			add(new JLabel("Java heap size:"), _totalMemoryLbl); // i18n
			add(new JLabel("Used heap:"), _usedMemoryLbl); // i18n
			add(new JLabel("Free heap:"), _freeMemoryLbl); // i18n

			JButton gcBtn = new JButton("Garbage Collect"); //i18n
			gcBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					System.gc();
				}
			});
			add(gcBtn, new JLabel(""));

			_thread = new Thread(new MemoryTimer());
			_thread.start();
		}

		public void removeNotify() {
			if (_thread != null) {
				_killThread = true;
				try {
					_thread.join();
				} catch (InterruptedException ignore) {
				}
				_thread = null;
				_killThread = false;
			}
			super.removeNotify();
		}

		private final class MemoryTimer implements Runnable {
			private static final long MB_VALUE = 1048576;
			private static final long KB_VALUE = 1024;

			private static final String MB = " MB";
			private static final String KB = " KB";
			private static final String BYTES = " bytes";

			private DecimalFormat _fmt = new DecimalFormat("#,##0.0#");


			public void run() {
				Thread.currentThread().setName("Memory Timer");
				for (;;) {
					Runtime rt = Runtime.getRuntime();
					final long totalMemory = rt.totalMemory();
					final long freeMemory = rt.freeMemory();
					final long usedMemory = totalMemory - freeMemory;
					_totalMemoryLbl.setText(formatSize(totalMemory));
					_usedMemoryLbl.setText(formatSize(usedMemory));
					_freeMemoryLbl.setText(formatSize(freeMemory));
					s_log.debug("Memory timer thread executed");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
					if (_killThread) {
						break;
					}
				}
			}

			// i18n
			private String formatSize(long nbrBytes) {
				StringBuffer buf = new StringBuffer();
				double size = nbrBytes;
				double val = size / MB_VALUE;
				if (val > 1) {
					return _fmt.format(val).concat(MB);
				}
				val = size / KB_VALUE;
				if (val > 10) {
					return _fmt.format(val).concat(KB);
				}
				return _fmt.format(val).concat(BYTES);
			}
		}
	}
}
