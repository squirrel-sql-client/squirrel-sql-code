package net.sourceforge.squirrel_sql.client.plugin;
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This dialog displays a summary of all plugins.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginSummaryDialog extends JFrame
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginSummaryDialog.class);

	public PluginSummaryDialog(IApplication app, Frame owner)
		throws DataSetException
	{
		super(s_stringMgr.getString("PluginSummaryDialog.title"));
		createGUI(app);
	}

	private void createGUI(IApplication app) throws DataSetException
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final SquirrelResources rsrc = app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.PLUGINS);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Label containing the location of the plugins at top of dialog.
		final JLabel pluginLoc = new JLabel(s_stringMgr.getString("PluginSummaryDialog.pluginloc",
					new ApplicationFiles().getPluginsDirectory().getAbsolutePath()));
		pluginLoc.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		contentPane.add(pluginLoc, BorderLayout.NORTH);
		
		// Table of loaded plugins in centre of dialog.
		final PluginInfo[] pluginInfo = app.getPluginManager().getPluginInformation();
		final Component pluginPnl = new PluginSummaryPanel(pluginInfo).getComponent();
		contentPane.add(new JScrollPane(pluginPnl), BorderLayout.CENTER);

		// Ok button at bottom of dialog.
		JPanel btnsPnl = new JPanel();
		JButton closeBtn = new JButton(s_stringMgr.getString("PluginSummaryDialog.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				dispose();
			}
		});
		btnsPnl.add(closeBtn);
		contentPane.add(btnsPnl, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(closeBtn);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}
}
