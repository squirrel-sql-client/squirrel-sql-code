package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.BaseObjectPanelTab;
/**
 * This class will display the source for a stored procedure.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProcedureSourceTab extends BaseObjectPanelTab
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Source";
		String HINT = "Show Stored Procedure Source";
	}

	/** SQL that retrieves the source of a stored procedure. */
	private static String SQL =
		"select text from sys.all_source where type = 'PROCEDURE'" +
		" and owner = ? and name = ? order by line";

	/** Component to display in tab. */
	private ProcedureSourcePanel _comp;

	/** Scrolling pane for <TT>_comp. */
	private JScrollPane _scroller;

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(ProcedureSourceTab.class);

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return i18n.HINT;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab#clear()
	 */
	public void clear()
	{
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab#getComponent()
	 */
	public Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new ProcedureSourcePanel();
			_scroller = new JScrollPane(_comp);
		}
		return _scroller;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.BaseObjectPanelTab#refreshComponent()
	 */
	protected void refreshComponent()
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		_comp.load(getSession(), getDatabaseObjectInfo());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab#setSession(ISession)
	 */
	public void setSession(ISession session)
	{
		super.setSession(session);
	}

	private static final class ProcedureSourcePanel extends JPanel
	{
		private JTextArea _ta;

		ProcedureSourcePanel()
		{
			super(new BorderLayout());
			createUserInterface();
		}

		void load(ISession session, IDatabaseObjectInfo doi)
		{
			_ta.setText("");
			try
			{
				SQLConnection conn = session.getSQLConnection();
				PreparedStatement pstmt = conn.prepareStatement(SQL);
				try
				{
					pstmt.setString(1, doi.getSchemaName());
					pstmt.setString(2, doi.getSimpleName());
					ResultSet rs = pstmt.executeQuery();
					StringBuffer buf = new StringBuffer(4096);
					while (rs.next())
					{
						buf.append(rs.getString(1));
					}
					_ta.setText(buf.toString());
				}
				finally
				{
					pstmt.close();
				}
			}
			catch (SQLException ex)
			{
				session.getMessageHandler().showMessage(ex);
			}
			
		}

		private void createUserInterface()
		{
			_ta = new JTextArea();
			_ta.setEditable(false);
			add(_ta, BorderLayout.CENTER);
		}
	}
}
