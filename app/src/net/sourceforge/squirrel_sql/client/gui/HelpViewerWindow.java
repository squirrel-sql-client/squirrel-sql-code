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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This windows shows the SQuirreL Help files.
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
		String HELP = "Help";
		String SQUIRREL = "SQuirreL";
		String TITLE = "SQuirreL SQL Client Help";
	}

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(HelpViewerWindow.class);

	/** Application API. */
	private final IApplication _app;

	/** Panel that displays the help document. */
	private HtmlViewerPanel _detailPnl;

	/** Home URL. */
	private URL _homeURL;

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
	 * Set the Help Document displayed to that defined by the passed URL.
	 * 
	 * @param	url		URL of document to be displayed. 
	 */
	private void setSelectedHelpDocument(URL url)
	{
		try
		{
			_detailPnl.setURL(url);
		}
		catch (IOException ex)
		{
			s_log.error(ex);
			//TODO: Display in a statusbar
		}
	}

	/**
	 * Create user interface.
	 */
	private void createGUI()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
//		contentPane.add(createToolBar(), BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.add(createContentsTree(), JSplitPane.LEFT);
		splitPane.add(createDetailsPanel(), JSplitPane.RIGHT);
		contentPane.add(splitPane, BorderLayout.CENTER);

		pack();
		splitPane.setDividerLocation(200);
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				_contentsTxt.requestFocus();
//			}
//		});
	}

	/**
	 * Create a tree each node being a link to a Help document.
	 * 
	 * @return	The contents tree.
	 */
	private JScrollPane createContentsTree()
	{
		final ApplicationFiles appFiles = new ApplicationFiles();
		final FolderNode root = new FolderNode(i18n.HELP);
		final JTree tree = new JTree(new DefaultTreeModel(root));
		tree.setShowsRootHandles(true);
//		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new ObjectTreeSelectionListener());

		// Add SQuirreL help to the tree.
		File file = appFiles.getQuickStartGuideFile();
		try
		{
			DocumentNode docNode = new DocumentNode(i18n.SQUIRREL, file);
			root.add(docNode);
			_homeURL = docNode.getURL();
		}
		catch (MalformedURLException ex)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Error retrieving Help file URL for ")
				.append(file.getAbsolutePath());
			s_log.error(msg.toString(), ex);
		}

		// Add plugin help documents.
		PluginInfo[] pi = _app.getPluginManager().getPluginInformation();
		for (int i = 0; i < pi.length; ++i)
		{
			try
			{
				final File dir = pi[i].getPlugin().getPluginAppSettingsFolder();
				final String title = pi[i].getDescriptiveName();

				String fn = pi[i].getHelpFileName();
				if (fn != null && fn.length() > 0)
				{
					DocumentNode dn = new DocumentNode(title, new File(dir, fn));
					root.add(dn);
				}
			}
			catch (IOException ex)
			{
				s_log.error("Error generating Help entry for plugin"
									+ pi[i].getDescriptiveName(), ex);
			}
		}

		JScrollPane sp = new JScrollPane(tree);
		tree.setPreferredSize(new Dimension(200, 200));
		return sp;
	}

	HtmlViewerPanel createDetailsPanel()
	{
		try
		{
			_detailPnl = new HtmlViewerPanel(_app, _homeURL);
		}
		catch (IOException ex)
		{
			s_log.error(ex);
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
	
		DocumentNode(String title, File file)
				throws MalformedURLException
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
				setSelectedHelpDocument(((DocumentNode)lastComp).getURL());
			}
		}
	}
}
