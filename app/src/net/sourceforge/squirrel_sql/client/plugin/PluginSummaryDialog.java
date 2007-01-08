package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

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
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginSummaryDialog extends JDialog
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginSummaryDialog.class);

	private final IApplication _app;

	private PluginSummaryTable _pluginPnl;

	public PluginSummaryDialog(IApplication app, Frame owner)
		throws DataSetException
	{
		super(owner, s_stringMgr.getString("PluginSummaryDialog.title"));
		_app = app;
		createGUI();
	}

	private void saveSettings()
	{
		_app.getPluginManager().setPluginStatuses(_pluginPnl.getPluginStatus());
	}

	private void createGUI() throws DataSetException
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.PLUGINS);
//		if (icon != null)
//		{
//			setIconImage(icon.getImage());
//		}

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Label containing the location of the plugins at top of dialog.
		final JLabel pluginLoc = new JLabel(s_stringMgr.getString("PluginSummaryDialog.pluginloc",
					new ApplicationFiles().getPluginsDirectory().getAbsolutePath()));
		pluginLoc.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		contentPane.add(pluginLoc, BorderLayout.NORTH);

		// Table of loaded plugins in centre of dialog.
		final PluginManager pmgr = _app.getPluginManager();
		final PluginInfo[] pluginInfo = pmgr.getPluginInformation();
		final PluginStatus[] pluginStatus = pmgr.getPluginStatuses();
		_pluginPnl = new PluginSummaryTable(pluginInfo, pluginStatus);
		contentPane.add(new JScrollPane(_pluginPnl), BorderLayout.CENTER);

		final JPanel btnsPnl = new JPanel();
//TODO: Enable once load works for plugins.
//		final JButton okBtn = new JButton(s_stringMgr.getString("PluginSummaryDialog.ok"));
//		okBtn.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				saveSettings();
//				dispose();
//			}
//		});
//		btnsPnl.add(okBtn);
		final JButton closeBtn = new JButton(s_stringMgr.getString("PluginSummaryDialog.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				dispose();
			}
		});
		btnsPnl.add(closeBtn);
		contentPane.add(btnsPnl, BorderLayout.SOUTH);


      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);



      pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}
}
