package net.sourceforge.squirrel_sql.plugins.mysql.gui;
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
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.Borders;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
//import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
//import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
/**
 * Dialog that allows user to alter the dtructure of a table
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AlterTableDialog extends JDialog
{
	/** Logger for this class. */
//	private final static ILogger s_log =
//		LoggerController.createLogger(AlterTableDialog.class);

    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTableDialog.class);

	/**
	 * Ctor.
	 *
	 * @param	app		Appliocation API.
	 * @param	plugin	This plugin.
	 * @param	ti		Points to table to be modified.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT>,
	 *			<TT>MysqlPlugin</TT>, or <TT>ITableInfo</TT> passed.
	 */
	public AlterTableDialog(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
		throws SQLException
	{
		super(ctorHelper(session, plugin, ti), true);

		createGUI(session, plugin, ti);
	}

	private void createGUI(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
		throws SQLException
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(s_stringMgr.getString("AlterTableDialog.title",
										ti.getQualifiedName()));
		setContentPane(buildContentPane(session, plugin, ti));
	}
	@SuppressWarnings("unused")
	private JComponent buildContentPane(ISession session, MysqlPlugin plugin,
											ITableInfo ti)
		throws SQLException
	{
		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(buildMainPanel(session, ti), BorderLayout.CENTER);
		pnl.add(buildToolBar(), BorderLayout.SOUTH);
		pnl.setBorder(Borders.TABBED_DIALOG_BORDER);

		return pnl;

//		final FormLayout layout = new FormLayout(
//				"3dlu, 75dlu:grow(1.0), 3dlu",
//				"center:pref:grow(1.0), 8dlu, bottom:pref");
//		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
//		builder.setDefaultDialogBorder();
//		builder.setLeadingColumnOffset(1);
//
//		builder.append(buildMainPanel(session, ti));
//		builder.nextLine();
//		builder.appendSeparator();
//		builder.nextLine();
//		builder.append(buildToolBar());
//
//		return builder.getPanel();
}

	private JTabbedPane buildMainPanel(ISession session, ITableInfo ti)
		throws SQLException
	{
		final JTabbedPane tabPnl = UIFactory.getInstance().createTabbedPane();
		final JPanel pnl = new AlterColumnsPanelBuilder().buildPanel(session, ti);
		tabPnl.addTab(getString("AlterTableDialog.columns"), null, pnl,
						getString("AlterTableDialog.columnshint"));
		return tabPnl;
	}

	private JPanel buildToolBar()
	{
		final ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
		// i18n[mysql.alterDlgAlter=Alter]
		builder.addGridded(new JButton(s_stringMgr.getString("mysql.alterDlgAlter")));
		builder.addRelatedGap();
		// i18n[mysql.alterDlgClose=Close]
		builder.addGridded(new JButton(s_stringMgr.getString("mysql.alterDlgClose")));

		return builder.getPanel();
	}

	private static String getString(String stringMgrKey)
	{
		return s_stringMgr.getString(stringMgrKey);
	}

	private static Frame ctorHelper(ISession session, MysqlPlugin plugin,
										ITableInfo ti)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}
		return session.getApplication().getMainFrame();
	}
}
