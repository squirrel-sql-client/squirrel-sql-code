package net.sourceforge.squirrel_sql.client.gui;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.MXBean;
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
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import com.jgoodies.forms.builder.ButtonBarBuilder;
/**
 * About box dialog.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutBoxDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    /** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AboutBoxDialog.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AboutBoxDialog.class);

	/** Singleton instance of this class. */
	private static AboutBoxDialog s_instance;

	/** The tabbed panel. */
	private JTabbedPane _tabPnl;

	/** System panel. */
	private SystemPanel _systemPnl;
	
	private ThreadPanel _threadPnl;

	/** Close button for dialog. */
	private final JButton _closeBtn = new JButton(s_stringMgr.getString("AboutBoxDialog.close"));


	private AboutBoxDialog(IApplication app)
	{
		super(app.getMainFrame(), s_stringMgr.getString("AboutBoxDialog.about"), true);
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
		s_instance.setVisible(true);
	}

	private void createGUI(IApplication app)
	{
		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;

		_tabPnl = UIFactory.getInstance().createTabbedPane();

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_tabPnl.add(s_stringMgr.getString("AboutBoxDialog.about"), new AboutPanel(app));
		if (isDebug)
		{
            // i18n[AboutBoxDialog.aboutpanelcreatetime=AboutPanel created in ]
			s_log.debug(s_stringMgr.getString("AboutBoxDialog.aboutpanelcreatetime")
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_tabPnl.add(s_stringMgr.getString("AboutBoxDialog.credits"), new CreditsPanel(app)); // i18n
		if (isDebug)
		{
            // i18n[AboutBoxDialog.creditspanelcreatetime=CreditsPanel created in ]
			s_log.debug(s_stringMgr.getString("AboutBoxDialog.creditspanelcreatetime")
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}

		if (isDebug)
		{
			start = System.currentTimeMillis();
		}
		_systemPnl = new SystemPanel();
		_tabPnl.add(s_stringMgr.getString("AboutBoxDialog.system"), _systemPnl);
		if (isDebug)
		{
            // i18n[AboutBoxDialog.systempanelcreatetime=SystemPanel created in ]
			s_log.debug(s_stringMgr.getString("AboutBoxDialog.systempanelcreatetime")
					+ (System.currentTimeMillis() - start)
					+ "ms");
		}
		
		_threadPnl = new ThreadPanel();
		_tabPnl.add(s_stringMgr.getString("AboutBoxDialog.threads"), _threadPnl);

		_tabPnl.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
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
//		JPanel btnsPnl = new JPanel();
//		JButton okBtn = new JButton("OK");

//		_closeBtn.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				setVisible(false);
//			}
//		});
//		btnsPnl.add(okBtn);
//		contentPane.add(btnsPnl, BorderLayout.SOUTH);
		contentPane.add(createButtonBar(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(_closeBtn);

		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
				{
					_systemPnl._memoryPnl.startTimer();
				}
			}
			public void windowDeactivated(WindowEvent evt)
			{
				String title = _tabPnl.getTitleAt(_tabPnl.getSelectedIndex());
				if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
				{
					_systemPnl._memoryPnl.stopTimer();
				}
			}
		});

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private JPanel createButtonBar()
	{
		_closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setVisible(false);
			}
		});

		final ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
//		builder.addGridded(new JButton("Alter"));
//		builder.addRelatedGap();
		builder.addGridded(_closeBtn);

		return builder.getPanel();
	}

	private static final class CreditsPanel extends JScrollPane
	{
        private static final long serialVersionUID = 1L;

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

			String creditsHtml = readCreditsHtml(app);

			StringBuffer pluginHtml = new StringBuffer();
			// Get list of all plugin developers names. Allow for multiple
			// developers for a plugin in the form "John Smith, James Brown".
			PluginInfo[] pi = app.getPluginManager().getPluginInformation();
			for (int i = 0; i < pi.length; ++i)
			{
				pluginHtml.append("<br><b>").append(pi[i].getDescriptiveName()).append(":</b>");

				String authors = pi[i].getAuthor();
				StringTokenizer strok = new StringTokenizer(authors, ",");
				while (strok.hasMoreTokens())
				{
					pluginHtml.append("<br>").append(strok.nextToken().trim());
				}
				String contribs = pi[i].getContributors();
				strok = new StringTokenizer(contribs, ",");
				while (strok.hasMoreTokens())
				{
					pluginHtml.append("<br>").append(strok.nextToken().trim());
				}


				pluginHtml.append("<br>");
			}

			creditsHtml = creditsHtml.replaceAll("@@replace", pluginHtml.toString());
			credits.setText(creditsHtml);

			setViewportView(credits);
			credits.setCaretPosition(0);
		}

		private String readCreditsHtml(IApplication app)
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
							String internationalizedLine = 
								Utilities.replaceI18NSpanLine(line, s_stringMgr);
							buf.append(internationalizedLine);
						}
					}
					finally
					{
						rdr.close();
					}
				}
				catch (IOException ex)
				{
						  // i18n[AboutBoxDialog.error.creditsfile=Error reading credits file]
						  String errorMsg =
								s_stringMgr.getString("AboutBoxDialog.error.creditsfile");
					s_log.error(errorMsg, ex);
					buf.append(errorMsg + ": " + ex.toString());
				}
			}
			else
			{
					 // i18n[AboutBoxDialog.error.creditsfileurl=Couldn't retrieve Credits File URL]
					 String errorMsg =
						  s_stringMgr.getString("AboutBoxDialog.error.creditsfileurl");
				s_log.error(errorMsg);
				buf.append(errorMsg);
			}
			return buf.toString();
		}
				
	}

	private static final class AboutPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;

        AboutPanel(IApplication app)
		{
			super();
			final SquirrelResources rsrc = app.getResources();
			setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			setLayout(new BorderLayout());
			setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
			Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);
			add(BorderLayout.CENTER, new JLabel(icon));
            
            VersionPane versionPane = new VersionPane(true);
            versionPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(BorderLayout.SOUTH, versionPane);
		}
	}

	private static final class SystemPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;
        private MemoryPanel _memoryPnl;

		SystemPanel()
		{
			super();
			setLayout(new BorderLayout());
			DataSetViewerTablePanel propsPnl = new DataSetViewerTablePanel();
			propsPnl.init(null);
			try
			{
				propsPnl.show(new HashtableDataSet(System.getProperties()));
			}
			catch (DataSetException ex)
			{
                // i18n[AboutBoxDialog.error.systemprops=Error occured displaying System Properties]
				s_log.error(s_stringMgr.getString("AboutBoxDialog.error.systemprops"), ex);
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
        private static final long serialVersionUID = 1L;
        private final JLabel _totalMemoryLbl = new JLabel();
		private final JLabel _usedMemoryLbl = new JLabel();
		private final JLabel _freeMemoryLbl = new JLabel();
		private Timer _timer;

		MemoryPanel()
		{
			super();
			add(new JLabel(s_stringMgr.getString("AboutBoxDialog.heapsize")), _totalMemoryLbl);
			add(new JLabel(s_stringMgr.getString("AboutBoxDialog.usedheap")), _usedMemoryLbl);
			add(new JLabel(s_stringMgr.getString("AboutBoxDialog.freeheap")), _freeMemoryLbl);

			JButton gcBtn = new JButton(s_stringMgr.getString("AboutBoxDialog.gc"));
			gcBtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					Utilities.garbageCollect();
				}
			});
			add(gcBtn, new JLabel(""));
		}

		public void removeNotify()
		{
			super.removeNotify();
			stopTimer();
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
                // i18n[AboutBoxDialog.info.startmemtime=Starting memory timer (AboutBox)]
				s_log.debug(s_stringMgr.getString("AboutBoxDialog.info.startmemtime"));
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
                // i18n[AboutBoxDialog.info.endmemtimer=Ending memory timer (AboutBox)]
				s_log.debug(s_stringMgr.getString("AboutBoxDialog.info.endmemtimer"));
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
	
	private static final class ThreadPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;
        
        private JTextArea content;

        ThreadPanel()
		{
			super();
			setLayout(new BorderLayout());
			
			content = new JTextArea(5,20);
			content.setEditable(false);
			content.setLineWrap(false);
			doThreadDump();
			add(new JScrollPane(content), BorderLayout.CENTER);
			add(createButtons(), BorderLayout.SOUTH);
		}

		/**
		 * @return
		 */
		private JPanel createButtons() {
			JPanel buttonPanel = new JPanel(new BorderLayout());
			JButton refreshButton = new JButton(s_stringMgr.getString("ThreadPanel.refresh"));
			buttonPanel.add(refreshButton, BorderLayout.WEST);
			
			refreshButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doThreadDump();
				}
			});
			
			return buttonPanel;
		}

		private void doThreadDump() {
			StringBuilder sb = new StringBuilder(1000);

			ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
			for (ThreadInfo threadInfo : threadInfos) {
				sb.append(threadInfo.toString());
				sb.append(StringUtilities.getEolStr());
			}

			content.setText(sb.toString());
		}
	}
}
