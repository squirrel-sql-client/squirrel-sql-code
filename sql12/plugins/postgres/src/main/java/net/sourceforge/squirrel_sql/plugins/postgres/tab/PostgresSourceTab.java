package net.sourceforge.squirrel_sql.plugins.postgres.tab;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Base source tab providing common functionality for PostgreSQL source tabs.
 * 
 * @author manningr
 */
public abstract class PostgresSourceTab extends BaseSourceTab
{

	public static final int VIEW_TYPE = 0;

	public static final int STORED_PROC_TYPE = 1;

	public static final int TRIGGER_TYPE = 2;

	protected int sourceType = VIEW_TYPE;

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(PostgresSourceTab.class);

	private static CommentSpec[] commentSpecs = new CommentSpec[]
		{ new CommentSpec("/*", "*/"), new CommentSpec("--", "\n") };

	private static CodeReformator formatter = new CodeReformator(CodeReformatorConfigFactory.createConfig(";", commentSpecs));

	public PostgresSourceTab(String hint) {
		super(hint);
	}

	private final class PostgresSourcePanel extends BaseSourcePanel
	{
		private static final long serialVersionUID = 1L;

		PostgresSourcePanel(ISession session) {
			super(session);
		}

		public void load(ISession session, PreparedStatement stmt)
		{
			getTextArea().setText("");
			ResultSet rs = null;
			try
			{
				rs = stmt.executeQuery();
				StringBuilder buf = new StringBuilder(4096);
				while (rs.next())
				{
					String nextString = rs.getString(1);
					if (sourceType == STORED_PROC_TYPE)
					{
						buf.append(nextString);
					}
					if (sourceType == TRIGGER_TYPE)
					{
						buf.append(nextString.trim());
						buf.append(" ");
					}
					if (sourceType == VIEW_TYPE)
					{
						buf.append(nextString.trim());
						buf.append(" ");
					}
				}
				// Stored Procedures can have comments embedded in them, so
				// don't line-wrap them.
				if (sourceType == VIEW_TYPE || sourceType == TRIGGER_TYPE)
				{
					if (s_log.isDebugEnabled())
					{
						s_log.debug("View source before formatting: " + buf.toString());
					}
					getTextArea().setText(formatter.reformat(buf.toString()));
				} else
				{
					getTextArea().setText(buf.toString());
				}
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
	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createSourcePanel()
	 */
	@Override
	protected BaseSourcePanel createSourcePanel() {
		return new PostgresSourcePanel(getSession());
	}
}
