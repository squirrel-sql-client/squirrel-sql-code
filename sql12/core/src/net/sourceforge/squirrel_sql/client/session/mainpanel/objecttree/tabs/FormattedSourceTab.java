package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

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
import net.sourceforge.squirrel_sql.client.util.codereformat.*;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This will provide source code formatting for object source to subclasses. The subclass only needs to call
 * setupFormatter if code reformatting is desired and whether or not to compressWhitespace, which is on by
 * default. Without calling setupFormatter, word-wrapping on word boundaries is still performed and whitespace
 * is compressed, if so configured.
 * 
 * @author manningr
 */
public abstract class FormattedSourceTab extends BaseSourceTab
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(FormattedSourceTab.class);

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FormattedSourceTab.class);

	/** does the work of formatting */
	private ICodeReformator formatter = null;

	/** whether or not to compress whitespace */
	private boolean compressWhitespace = true;

	private CommentSpec[] commentSpecs =
		new CommentSpec[] { new CommentSpec("/*", "*/"), new CommentSpec("--", "\n") };

	/** The String to use to separate statements */
	protected String statementSeparator = null;

	/** Whether or not to appendSeparator before reformatting */
	protected boolean appendSeparator = true;

	static interface i18n
	{
		// i18n[FormatterSourceTab.noSourceAvailable=No object source code is
		// available]
		String NO_SOURCE_AVAILABLE = s_stringMgr.getString("FormatterSourceTab.noSourceAvailable");
	}

	public FormattedSourceTab(String hint)
	{
		super(hint);
	}

	/**
	 * Sets up the formatter which formats the source after retrieving it from the ResultSet. If this is not
	 * setup prior to loading, then the formatter will not be used - only whitespace compressed if so enabled.
	 * 
	 * @param stmtSep
	 *           the formatter needs to know what the statement separator is.
	 * @param commentSpecs
	 *           the types of comments that can be found in the source code. This can be null, and if so, the
	 *           standard comment styles are used (i.e. -- and c-style comments)
	 */
	protected void setupFormatter(String stmtSep, CommentSpec[] commentSpecs)
	{
		if (commentSpecs != null)
		{
			this.commentSpecs = commentSpecs;
		}
		statementSeparator = stmtSep;
		formatter = new CodeReformator(CodeReformatorConfigFactory.createConfig(stmtSep, this.commentSpecs));
	}

	/**
	 * Sets up a custom formatter implementation which is used to format the source after retrieving it from 
	 * the ResultSet. If this is not setup prior to loading, then the formatter will not be used - only 
	 * whitespace compressed if so enabled.
	 * 
	 * @param codeReformator
	 * @param stmtSep
	 *           the formatter needs to know what the statement separator is.
	 * @param commentSpecs
	 *           the types of comments that can be found in the source code. This can be null, and if so, the
	 *           standard comment styles are used (i.e. -- and c-style comments)
	 */
	protected void setupFormatter(ICodeReformator codeReformator, String stmtSep, CommentSpec[] commentSpecs)
	{
		if (commentSpecs != null)
		{
			this.commentSpecs = commentSpecs;
		}
		statementSeparator = stmtSep;
		formatter = codeReformator;
	}
	
	
	/**
	 * Whether or not to convert multiple consecutive spaces into a single space.
	 * 
	 * @param compressWhitespace
	 */
	protected void setCompressWhitespace(boolean compressWhitespace)
	{
		this.compressWhitespace = compressWhitespace;
	}

	/**
	 * The panel that displays the formatted source code.
	 */
	private final class FormattedSourcePanel extends BaseSourcePanel
	{
		private static final long serialVersionUID = 1L;

		FormattedSourcePanel(ISession session){
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
					String line = rs.getString(1);
					if (line == null)
					{
						s_log.debug("load: Null object source line; skipping...");
						continue;
					}
					if (compressWhitespace)
					{
						buf.append(line.trim() + " ");
					}
					else
					{
						buf.append(line);
					}
				}
				if (appendSeparator)
				{
					buf.append("\n");
					buf.append(statementSeparator);
				}
				String processedResult = processResult(buf);
				if (formatter != null && buf.length() != 0)
				{
					if (s_log.isDebugEnabled())
					{
						s_log.debug("Object source code before formatting: " + processedResult);
					}
					getTextArea().setText(format(processedResult));
				}
				else
				{
					if (buf.length() == 0)
					{
						buf.append(i18n.NO_SOURCE_AVAILABLE);
					}
					getTextArea().setText(processedResult);
				}
				getTextArea().setCaretPosition(0);
			}
			catch (Exception ex)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Unexpected exception while formatting " + "object source code", ex);
				}
				session.showErrorMessage(ex);
			}
			finally
			{
				SQLUtilities.closeResultSet(rs);
			}
		}

	}

	/**
	 * This method can be overridden by subclasses if further processing of the result from the query needs to
	 * happen prior to formatting.  By default, no processing is done - the StringBuilder is simply converted
	 * to a string by calling it's toString method.
	 * 
	 * @param buf the StringBuilder that can be procesed.
	 * @return the processed String.
	 */
	protected String processResult(final StringBuilder buf)
	{
		return buf.toString();
	}

	/**
	 * We trap any IllegalStateException from the formatter here. If the SQL source code fails to format, log
	 * it and show the original unformatted version.
	 * 
	 * @param toFormat
	 *           the SQL to format.
	 * @return either formatted or original version of the specified SQL.
	 */
	private String format(String toFormat)
	{
		String result = toFormat;
		try
		{
			result = formatter.reformat(toFormat);
		}
		catch (IllegalStateException e)
		{
			s_log.error("format: Formatting SQL failed: " + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createStatement()
	 */
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();

		ISQLConnection conn = session.getSQLConnection();

		String sqlStatement = getSqlStatement();
		String[] bindValues = getBindValues();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Running SQL for index source tab: " + sqlStatement);
			s_log.debug("With the following bind variable values: ");
			int parameterIndex = 1;
			for (String bindValue : bindValues)
			{
				s_log.debug("[" + (parameterIndex++) + "] => '" + bindValue + "'");
			}
		}
		PreparedStatement pstmt = conn.prepareStatement(sqlStatement);

		int parameterIndex = 1;
		for (String bindValue : bindValues)
		{
			pstmt.setString(parameterIndex++, bindValue);
		}

		return pstmt;
	}

	/**
	 * Subclasses must override to provide the SQL necessary to select the source for the selected
	 * DatabaseObjectInfo. Note: the default implementation of getBindValues provides values for schema and
	 * object simple name. Therefore, it is advantageous if the where clause in the select statement returned
	 * from this method specify first the schema, and then the object name and no more bind variables. If this
	 * is possible, then it isn't necessary for subclasses to override getBindValues.
	 * 
	 * @return an SQL select statement with embedded bind variables (?'s).
	 */
	protected abstract String getSqlStatement();

	/**
	 * This method simply returns a String array containing the schema name and the selected object's simple
	 * name, in that order. If the SQL returned from getSqlStatement must specify a different order, or for
	 * example uses that catalog of the object, instead of or in addition to schema, then this method must be
	 * overridden to return the necessary bind variable values, in the order required by the SQL statement.
	 * 
	 * @return a String array of bind variable values
	 */
	protected String[] getBindValues()
	{
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		return new String[] { doi.getSchemaName(), doi.getSimpleName() };
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createSourcePanel()
	 */
	@Override
	protected BaseSourcePanel createSourcePanel() {
		return new FormattedSourcePanel(getSession());
	}
}