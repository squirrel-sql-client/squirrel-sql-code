package net.sourceforge.squirrel_sql.client.gui;
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This window shows the SQuirreL Help files.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HelpViewerWindow extends JFrame
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String CHANGE_LOGS = "Change Logs";
		String FAQ = "FAQ";
		String HELP = "Help";
		String LICENCES = "Licences";
		String SQUIRREL = "SQuirreL";
		String TITLE = "SQuirreL SQL Client Help";
	}

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(HelpViewerWindow.class);

	/** Application API. */
	private final IApplication _app;

	/** Tree containing a node for each help document. */
	private JTree _tree;

	/** Panel that displays the help document. */
	private HtmlViewerPanel _detailPnl;

	/** Home URL. */
	private URL _homeURL;

	/** Collection of the nodes in the tree keyed by the URL.toString(). */
	private final Map _nodes = new HashMap();

	/**
	 * Ctor.
	 * 
	 * @param	app	Application API.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public HelpViewerWindow(IApplication app)
		throws IllegalArgumentException
	{
		super(i18n.TITLE);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		createGUI();
	}

	/**
	 * Set the Document displayed to that defined by the passed URL.
	 * 
	 * @param	url		URL of document to be displayed. 
	 */
	private void setSelectedDocument(URL url)
	{
		try
		{
//			_detailPnl.setURL(url);
			_detailPnl.gotoURL(url);
		}
		catch (IOException ex)
		{
			s_log.error("Error displaying document", ex);
			//TODO: Display in a statusbar
		}
	}

	private void selectTreeNodeForURL(URL url)
	{
		// Strip local part of URL.
		String key = url.toString();
		final int idx = key.lastIndexOf('#'); 
		if ( idx > -1)
		{
			key = key.substring(0, idx);
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)_nodes.get(key);
		if (node != null)
		{
			DefaultTreeModel model = (DefaultTreeModel)_tree.getModel();
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
	private void createGUI()
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

		pack();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_detailPnl.setHomeURL(_homeURL);
				_tree.expandRow(0);
				_tree.expandRow(1);
				_tree.setSelectionRow(2);
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
			}
		});
	}

	/**
	 * Create a tree each node being a link to a document.
	 * 
	 * @return	The contents tree.
	 */
	private JScrollPane createContentsTree()
	{
		final ApplicationFiles appFiles = new ApplicationFiles();
		final FolderNode root = new FolderNode(i18n.HELP);
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

		// Add Help, Licence and Change Log nodes to the tree.
		final FolderNode helpRoot = new FolderNode(i18n.HELP);
		root.add(helpRoot);
		final FolderNode licenceRoot = new FolderNode(i18n.LICENCES);
		root.add(licenceRoot);
		final FolderNode changeLogRoot = new FolderNode(i18n.CHANGE_LOGS);
		root.add(changeLogRoot);

		// Add SQuirreL help to the Help node.
		File file = appFiles.getQuickStartGuideFile();
		try
		{
			DocumentNode dn = new DocumentNode(i18n.SQUIRREL, file);
			helpRoot.add(dn);
			_homeURL = dn.getURL();
			_nodes.put(_homeURL.toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Error retrieving Help file URL for ")
				.append(file.getAbsolutePath());
			s_log.error(msg.toString(), ex);
		}

		// Add SQuirreL Licence to the Licence node.
		file = appFiles.getLicenceFile();
		try
		{
			DocumentNode dn = new DocumentNode(i18n.SQUIRREL, file);
			licenceRoot.add(dn);
			_nodes.put(dn.getURL(), dn);
		}
		catch (MalformedURLException ex)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Error retrieving Licence file URL for ")
				.append(file.getAbsolutePath());
			s_log.error(msg.toString(), ex);
		}

		// Add SQuirreL Change Log to the Licence node.
		file = appFiles.getChangeLogFile();
		try
		{
			DocumentNode dn = new DocumentNode(i18n.SQUIRREL, file);
			changeLogRoot.add(dn);
			_nodes.put(dn.getURL(), dn);
		}
		catch (MalformedURLException ex)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Error retrieving Change Log file URL for ")
				.append(file.getAbsolutePath());
			s_log.error(msg.toString(), ex);
		}

		// Add plugin help, licence and change log documents to the tree.
		PluginInfo[] pi = _app.getPluginManager().getPluginInformation();
		for (int i = 0; i < pi.length; ++i)
		{
			try
			{
				final File dir = pi[i].getPlugin().getPluginAppSettingsFolder();
				final String title = pi[i].getDescriptiveName();

				// Help document.
				try
				{
					final String fn = pi[i].getHelpFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, new File(dir, fn));
						helpRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					s_log.error("Error generating Help entry for plugin"
										+ pi[i].getDescriptiveName(), ex);
				}
	
				// Licence document.
				try
				{
					final String fn = pi[i].getLicenceFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, new File(dir, fn));
						licenceRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					s_log.error("Error generating Licence entry for plugin"
										+ pi[i].getDescriptiveName(), ex);
				}
	
				try
				{
					// Change log.
					final String fn = pi[i].getChangeLogFileName();
					if (fn != null && fn.length() > 0)
					{
						DocumentNode dn = new DocumentNode(title, new File(dir, fn));
						changeLogRoot.add(dn);
						_nodes.put(dn.getURL().toString(), dn);
					}
				}
				catch (IOException ex)
				{
					s_log.error("Error generating Change Log entry for plugin"
										+ pi[i].getDescriptiveName(), ex);
				}
			}
			catch (IOException ex)
			{
				s_log.error("Error retrieving app settings folder for plugin"
									+ pi[i].getDescriptiveName(), ex);
			}
		}

		// FAQ.
		file = appFiles.getFAQFile();
		try
		{
			DocumentNode dn = new DocumentNode(i18n.FAQ, file);
			root.add(dn);
			_nodes.put(dn.getURL().toString(), dn);
		}
		catch (MalformedURLException ex)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Error retrieving FAQ URL for ")
				.append(file.getAbsolutePath());
			s_log.error(msg.toString(), ex);
		}


		JScrollPane sp = new JScrollPane(_tree);
		_tree.setPreferredSize(new Dimension(200, 200));

		return sp;
	}

	HtmlViewerPanel createDetailsPanel()
	{
		try
		{
			_detailPnl = new HtmlViewerPanel(_app, null);
		}
		catch (IOException ex)
		{
			s_log.error("Error creating details panel for Help display", ex);
		}
		return _detailPnl;
	}

	private class FolderNode extends DefaultMutableTreeNode
	{
		FolderNode(String title)
		{
			super(title, true);
		}
	}

	private class DocumentNode extends DefaultMutableTreeNode
	{
		private final URL _url;
	
		DocumentNode(String title, File file) throws MalformedURLException
		{
			super(title, false);
			_url = file.toURL();
		}

		URL getURL()
		{
			return _url;
		}
	}

	/**
	 * This class listens for changes in the node selected in the tree
	 * and displays the appropriate help document for the node.
	 */
	private final class ObjectTreeSelectionListener
		implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			Object lastComp = evt.getNewLeadSelectionPath().getLastPathComponent();
			if (lastComp instanceof DocumentNode)
			{
				setSelectedDocument(((DocumentNode)lastComp).getURL());
			}
		}
	}
}
