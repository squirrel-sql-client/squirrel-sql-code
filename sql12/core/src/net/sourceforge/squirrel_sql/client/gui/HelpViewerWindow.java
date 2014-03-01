package net.sourceforge.squirrel_sql.client.gui;

/*
 * Copyright (C) 2003-2004 Colin Bell
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
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappersImpl;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.commons.lang.StringUtils;

/**
 * This window shows the SQuirreL Help files.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HelpViewerWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(HelpViewerWindow.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(HelpViewerWindow.class);

	/** Application API. */
	private final IApplication _app;

	/** Tree containing a node for each help document. */
	private JTree _tree;

	/** Panel that displays the help document. */
	private HtmlViewerPanel _detailPnl;

	/** Statusbar at bottom of window. */
	private StatusBar _statusBar = new StatusBar();

	/** Home URL. */
	private URL _homeURL;

	/** Collection of the nodes in the tree keyed by the URL.toString(). */
	private final Map<String, DefaultMutableTreeNode> _nodes = new HashMap<String, DefaultMutableTreeNode>();

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/** A FileWrapper-enhanced version of ApplicationFiles that removes direct references to File */
	private ApplicationFileWrappers applicationFiles = new ApplicationFileWrappersImpl();

	/**
	 * Ctor.
	 * 
	 * @param app
	 *           Application API.
	 * @throws IllegalArgumentException
	 *            Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public HelpViewerWindow(IApplication app) throws IllegalArgumentException, BaseException
	{
		// i18n[HelpViewerWindow.title=SQuirreL SQL Client Help]
		super(s_stringMgr.getString("HelpViewerWindow.title"));
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		try
		{
			createGUI();
		}
		catch (IOException ex)
		{
			throw new BaseException(ex);
		}
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this.fileWrapperFactory = fileWrapperFactory;
	}

	/**
	 * @param applicationFiles the applicationFiles to set
	 */
	public void setApplicationFiles(ApplicationFileWrappers applicationFiles)
	{
		Utilities.checkNull("setApplicationFiles", "applicationFiles", applicationFiles);
		this.applicationFiles = applicationFiles;
	}
	
	
	/**
	 * Set the Document displayed to that defined by the passed URL.
	 * 
	 * @param url
	 *           URL of document to be displayed.
	 */
	private void setSelectedDocument(URL url)
	{
		try
		{
			_detailPnl.gotoURL(url);
			// i18n[HelpViewerWindow.pageloaded=Page loaded.]
			_statusBar.setText(s_stringMgr.getString("HelpViewerWindow.pageloaded"));
		}
		catch (IOException ex)
		{
			// i18n[HelpViewerWindow.error.displaydocument=Error displaying document]
			s_log.error(s_stringMgr.getString("HelpViewerWindow.error.displaydocument"), ex);
			_statusBar.setText(ex.toString());
		}
	}

	private void selectTreeNodeForURL(URL url)
	{
		// Strip local part of URL.
		String key = url.toString();
		final int idx = key.lastIndexOf('#');
		if (idx > -1)
		{
			key = key.substring(0, idx);
		}
		DefaultMutableTreeNode node = _nodes.get(key);
		if (node != null) // && node != _tree.getLastSelectedPathComponent())
		{
			DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
			TreePath path = new TreePath(model.getPathToRoot(node));
			if (path != null)
			{
				_tree.expandPath(path);
				_tree.scrollPathToVisible(path);
				_tree.setSelectionPath(path);
			}
		}
	}

	/**
	 * Create user interface.
	 */
	private void createGUI() throws IOException
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.VIEW);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.add(createContentsTree(), JSplitPane.LEFT);
		splitPane.add(createDetailsPanel(), JSplitPane.RIGHT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(200);

		contentPane.add(new HtmlViewerPanelToolBar(_app, _detailPnl), BorderLayout.NORTH);

		Font fn = _app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		contentPane.add(_statusBar, BorderLayout.SOUTH);

		pack();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_detailPnl.setHomeURL(_homeURL);
				_tree.expandRow(0);
				_tree.expandRow(2);
				if (_app.getSquirrelPreferences().isFirstRun())
				{
					_tree.setSelectionRow(1);
				}
				else
				{
					_tree.setSelectionRow(3);
				}
				_tree.setRootVisible(false);
			}
		});

		_detailPnl.addListener(new IHtmlViewerPanelListener()
		{
			public void currentURLHasChanged(HtmlViewerPanelListenerEvent evt)
			{
				selectTreeNodeForURL(evt.getHtmlViewerPanel().getURL());
			}

			public void homeURLHasChanged(HtmlViewerPanelListenerEvent evt)
			{
				// Nothing to do.
			}
		});
	}

	/**
	 * Create a tree each node being a link to a document.
	 * 
	 * @return The contents tree.
	 */
	private JScrollPane createContentsTree() throws IOException
	{
		
		// i18n[HelpViewerWindow.help=Help]
		final FolderNode root = new FolderNode(s_stringMgr.getString("HelpViewerWindow.help"));
		_tree = new JTree(new DefaultTreeModel(root));
		_tree.setShowsRootHandles(true);
		_tree.addTreeSelectionListener(new ObjectTreeSelectionListener());

		// Renderer for tree.
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		SquirrelResources rsrc = _app.getResources();
		renderer.setLeafIcon(rsrc.getIcon(SquirrelResources.IImageNames.HELP_TOPIC));
		renderer.setOpenIcon(rsrc.getIcon(SquirrelResources.IImageNames.HELP_TOC_OPEN));
		renderer.setClosedIcon(rsrc.getIcon(SquirrelResources.IImageNames.HELP_TOC_CLOSED));
		_tree.setCellRenderer(renderer);

		// First put the Welcome to SQuirreL node.
		FileWrapper file = applicationFiles.getWelcomeFile();
		try
		{
			// i18n[HelpViewerWindow.welcome=Welcome]
			DocumentNode dn = new DocumentNode(s_stringMgr.getString("HelpViewerWindow.welcome"), file);
			root.add(dn);
			_nodes.put(dn.getURL().toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			// i18n[HelpViewerWindow.error.loadwelcomefile=Error retrieving Welcome file URL for {0}]
			String msg = s_stringMgr.getString("HelpViewerWindow.error.loadwelcomefile", file.getAbsolutePath());
			s_log.error(msg, ex);
		}

		// Add Help, Licence and Change Log nodes to the tree.
		// i18n[HelpViewerWindow.help=Help]
		final FolderNode helpRoot = new FolderNode(s_stringMgr.getString("HelpViewerWindow.help"));
		root.add(helpRoot);
		_nodes.put(helpRoot.getURL().toString(), helpRoot);
		// i18n[HelpViewerWindow.licences=Licences]
		final FolderNode licenceRoot = new FolderNode(s_stringMgr.getString("HelpViewerWindow.licences"));
		root.add(licenceRoot);
		_nodes.put(licenceRoot.getURL().toString(), licenceRoot);
		// i18n[HelpViewerWindow.changelogs=Change Logs]
		final FolderNode changeLogRoot = new FolderNode(s_stringMgr.getString("HelpViewerWindow.changelogs"));
		root.add(changeLogRoot);
		_nodes.put(changeLogRoot.getURL().toString(), changeLogRoot);

		// Add SQuirreL help to the Help node.
		file = applicationFiles.getQuickStartGuideFile();
		try
		{
			// i18n[HelpViewerWindow.squirrel=SQuirreL]
			DocumentNode dn = new DocumentNode(s_stringMgr.getString("HelpViewerWindow.squirrel"), file);
			helpRoot.add(dn);
			_homeURL = dn.getURL();
			_nodes.put(_homeURL.toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			// i18n[HelpViewerWindow.error.loadwelcomefile=Error retrieving Help file URL for {0}]
			String msg = s_stringMgr.getString("HelpViewerWindow.error.loadhelpfile", file.getAbsolutePath());
			s_log.error(msg, ex);
		}

		// Add SQuirreL Licence to the Licence node.
		file = applicationFiles.getLicenceFile();
		try
		{
			// i18n[HelpViewerWindow.squirrel=SQuirreL]
			DocumentNode dn = new DocumentNode(s_stringMgr.getString("HelpViewerWindow.squirrel"), file);
			licenceRoot.add(dn);
			_nodes.put(dn.getURL().toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			// i18n[HelpViewerWindow.error.loadlicencefile=Error retrieving Licence file URL for {0}]
			String msg = s_stringMgr.getString("HelpViewerWindow.error.loadlicencefile", file.getAbsolutePath());
			s_log.error(msg, ex);
		}

		// Add SQuirreL Change Log to the Licence node.
		file = applicationFiles.getChangeLogFile();
		try
		{
			// i18n[HelpViewerWindow.squirrel=SQuirreL]
			DocumentNode dn = new DocumentNode(s_stringMgr.getString("HelpViewerWindow.squirrel"), file);
			changeLogRoot.add(dn);
			_nodes.put(dn.getURL().toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			// i18n[HelpViewerWindow.error.loadchangelogfile=Error retrieving Change Log file URL for {0}]
			String msg =
				s_stringMgr.getString("HelpViewerWindow.error.loadchangelogfile", file.getAbsolutePath());
			s_log.error(msg, ex);
		}

		// Add plugin help, licence and change log documents to the tree.
		PluginInfo[] pi = _app.getPluginManager().getPluginInformation();
		for (int i = 0; i < pi.length; ++i)
		{
			try
			{
				final FileWrapper dir = pi[i].getPlugin().getPluginAppSettingsFolder();
				final String title = pi[i].getDescriptiveName();

				// Help document.
				try
				{
					final String fn = pi[i].getHelpFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, fileWrapperFactory.create(dir, fn));
						helpRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					// i18n[HelpViewerWindow.error.loadpluginhelp=Error generating Help entry for plugin {0}]
					String msg =
						s_stringMgr.getString("HelpViewerWindow.error.loadpluginhelp", pi[i].getDescriptiveName());
					s_log.error(msg, ex);
				}

				// Licence document.
				try
				{
					final String fn = pi[i].getLicenceFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, fileWrapperFactory.create(dir, fn));
						licenceRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					// i18n[HelpViewerWindow.error.loadpluginlicence=Error generating Licence entry for plugin {0}]
					String msg =
						s_stringMgr.getString("HelpViewerWindow.error.loadpluginlicence",
							pi[i].getDescriptiveName());
					s_log.error(msg, ex);
				}

				try
				{
					// Change log.
					final String fn = pi[i].getChangeLogFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, fileWrapperFactory.create(dir, fn));
						changeLogRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					// i18n[HelpViewerWindow.error.loadchangelog=Error generating Change Log entry for plugin {0}]
					String msg =
						s_stringMgr.getString("HelpViewerWindow.error.loadchangelog", pi[i].getDescriptiveName());
					s_log.error(msg, ex);
				}
			}
			catch (IOException ex)
			{
				// i18n[HelpViewerWindow.error.loadpluginsettings=Error retrieving app settings folder for plugin
				// {0}]
				String msg =
					s_stringMgr.getString("HelpViewerWindow.error.loadpluginsettings", pi[i].getDescriptiveName());
				s_log.error(msg, ex);
			}
		}

		// FAQ.
		file = applicationFiles.getFAQFile();
		try
		{
			// i18n[HelpViewerWindow.faq=FAQ]
			DocumentNode dn = new DocumentNode(s_stringMgr.getString("HelpViewerWindow.faq"), file);
			root.add(dn);
			_nodes.put(dn.getURL().toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			// i18n[HelpViewerWindow.error.loadfaqfile=Error retrieving FAQ from URL = {0}]
			String msg = s_stringMgr.getString("HelpViewerWindow.error.loadfaqfile", file.getAbsolutePath());
			s_log.error(msg, ex);
		}

		// generate contents file.
		helpRoot.generateContentsFile();
		licenceRoot.generateContentsFile();
		changeLogRoot.generateContentsFile();

		JScrollPane sp = new JScrollPane(_tree);
		sp.setPreferredSize(new Dimension(200, 200));

		return sp;
	}

	HtmlViewerPanel createDetailsPanel()
	{
		_detailPnl = new HtmlViewerPanel(null);
		return _detailPnl;
	}

	private class DocumentNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;

		private URL _url;

		DocumentNode(String title, FileWrapper file) throws MalformedURLException
		{
			super(title, false);
			setFile(file);
		}

		DocumentNode(String title, boolean allowsChildren)
		{
			super(title, allowsChildren);
		}

		URL getURL()
		{
			return _url;
		}

		void setFile(FileWrapper file) throws MalformedURLException
		{
			_url = file.toURI().toURL();
		}
	}

	private class FolderNode extends DocumentNode
	{
		private static final long serialVersionUID = 1L;

		private final List<String> _docTitles = new ArrayList<String>();

		private final List<URL> _docURLs = new ArrayList<URL>();

		private final FileWrapper _contentsFile;

		FolderNode(String title) throws IOException
		{
			super(title, true);
			_contentsFile = fileWrapperFactory.createTempFile("sqschelp", "html");
			_contentsFile.deleteOnExit();
			setFile(_contentsFile);
		}

		public void add(MutableTreeNode node)
		{
			super.add(node);
			if (node instanceof DocumentNode)
			{
				final DocumentNode dn = (DocumentNode) node;
				final URL docURL = dn.getURL();
				if (docURL != null)
				{
					String docTitle = dn.toString();
					if (StringUtils.isEmpty(docTitle))
					{
						docTitle = docURL.toExternalForm();
					}
					_docTitles.add(docTitle);
					_docURLs.add(docURL);
				}
			}
		}

		synchronized void generateContentsFile()
		{
			try
			{
				final PrintWriter pw = new PrintWriter(_contentsFile.getFileWriter());
				try
				{
					StringBuffer buf = new StringBuffer(50);
					buf.append("<HTML><BODY><H1>").append(toString()).append("</H1>");
					pw.println(buf.toString());
					for (int i = 0, limit = _docTitles.size(); i < limit; ++i)
					{
						// final String docTitle = (String)_docTitles.get(i);
						final URL docUrl = _docURLs.get(i);
						buf = new StringBuffer(50);
						buf.append("<A HREF=\"").append(docUrl).append("\">").append(_docTitles.get(i)).append(
							"</A><BR>");
						pw.println(buf.toString());
					}
					pw.println("</BODY></HTML");
				}
				finally
				{
					pw.close();
				}
			}
			catch (IOException ex)
			{
				// i18n[HelpViewerWindow.error.congen=Error generating Contents file]
				String msg = s_stringMgr.getString("HelpViewerWindow.error.congen");
				s_log.error(msg, ex);
				_statusBar.setText(msg);
			}
		}
	}

	/**
	 * This class listens for changes in the node selected in the tree and displays the appropriate help
	 * document for the node.
	 */
	private final class ObjectTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			final TreePath path = evt.getNewLeadSelectionPath();
			if (path != null)
			{
				Object lastComp = path.getLastPathComponent();
				if (lastComp instanceof DocumentNode)
				{
					setSelectedDocument(((DocumentNode) lastComp).getURL());
				}
			}
		}
	}
}
