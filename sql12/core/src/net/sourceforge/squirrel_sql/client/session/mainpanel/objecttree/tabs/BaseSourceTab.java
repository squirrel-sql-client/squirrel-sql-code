package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public abstract class BaseSourceTab extends BaseObjectTab
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(BaseSourceTab.class);

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(BaseSourceTab.class);

	/** Hint to display for tab. */
	private final String _hint;

	/** Title of the tab */
	private String _title;

	/** Component to display in tab. */
	private BaseSourcePanel _comp;

	/** Scrolling pane for <TT>_comp. */
	private JScrollPane _scroller;

	public BaseSourceTab(String hint) {
		this(null, hint);
	}

	public BaseSourceTab(String title, String hint) {
		super();
		if (title != null)
		{
			_title = title;
		} else
		{
			// i18n[BaseSourceTab.title=Source]
			_title = s_stringMgr.getString("BaseSourceTab.title");
		}

		_hint = hint != null ? hint : _title;
	}

	/**
	 * Return the title for the tab.
	 * 
	 * @return The title for the tab.
	 */
	public String getTitle()
	{
		return _title;
	}

	/**
	 * Return the hint for the tab.
	 * 
	 * @return The hint for the tab.
	 */
	public String getHint()
	{
		return _hint;
	}

	public void clear()
	{
	}

	public Component getComponent()
	{
		
		
		if (_scroller == null)
		{
			if (_comp == null)
			{
				_comp = createSourcePanel();
			}
			_scroller = new JScrollPane(_comp);
			LineNumber lineNumber = new LineNumber(_comp);
			_scroller.setRowHeaderView(lineNumber);
			_scroller.getVerticalScrollBar().setUnitIncrement(10);
		}
		return _scroller;
	}

	/**
	 * Subclasses can use this to override the default behavior provided by the DefaultSourcePanel, with a
	 * subclass of BaseSourcePanel.
	 * 
	 * @param panel
	 * @deprecated Use {@link #createSourcePanel()} as callback method.
	 */
	public void setSourcePanel(BaseSourcePanel panel)
	{
		_comp = panel;
	}

	protected void refreshComponent()
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		
		if(_comp == null){
			_comp = createSourcePanel();
		}
		
		try
		{
			PreparedStatement pstmt = createStatement();
			try
			{
				_comp.load(getSession(), pstmt);
			} finally
			{
				SQLUtilities.closeStatement(pstmt);
			}
		} catch (SQLException ex)
		{
			s_log.error(ex);
			session.showErrorMessage(ex);
		}
	}

	/**
	 * Create a instance of {@link BaseSourcePanel}.
	 * Per default, a {@link DefaultSourcePanel} is used.
	 * Subclasses can use this to override the default behavior provided by the DefaultSourcePanel, with a
	 * subclass of BaseSourcePanel.
	 * @return The source panel to use.
	 */
	protected BaseSourcePanel createSourcePanel() {
		/*
		 * This callback method replaces the previous use of setSourcePanel, because since we use syntax highlightning, we need a session.
		 * So we need a callback for "lazy" initialization of the source pane.
		 */
		return new DefaultSourcePanel(getSession());
	}

	/**
	 * Sub-classes should override this method to return a PreparedStatement which will yield the source code
	 * of the object returned by getDatabaseObjectInfo.
	 * 
	 * @return a PreparedStatement already with bound variables, ready to be executed
	 * @throws SQLException
	 *         if any error occurs.
	 */
	protected abstract PreparedStatement createStatement() throws SQLException;

	private final class DefaultSourcePanel extends BaseSourcePanel
	{
		private static final long serialVersionUID = 1L;

		

		DefaultSourcePanel(ISession session) {
			super(session);
		}


		public void load(ISession session, PreparedStatement stmt)
		{
			getTextArea().setText("");
			ResultSet rs = null;
			try
			{
				rs = stmt.executeQuery();
				StringBuffer buf = new StringBuffer(4096);
				while (rs.next())
				{
					buf.append(rs.getString(1));
				}
				getTextArea().setText(buf.toString());
				getTextArea().setCaretPosition(0);
			} catch (SQLException ex)
			{
				session.showErrorMessage(ex);
			} finally
			{
				SQLUtilities.closeResultSet(rs);
			}

		}

	}
}
