package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.util.Calendar;
import java.util.Date;
/**
 * Information about an executed query.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLExecutionInfo
{
	/** Query index. */
	private int _idx;

	/** Execution start time. */
	private Date _sqlExecutionStart;

	/** Results processing start time. */
	private Date _resultsProcessingStart;

	/** Results processing end time. */
	private Date _resultsProcessingEnd;

	/** SQL script executed. */
	private String _sql;

	/** Number of rows query limited to. */
	private final int _maxRows;
   private Integer _numberResultRowsRead;

   /**
	 * Default ctor. Defaults SQL execution start time to the current time.
	 */
//	public SQLExecutionInfo()
//	{
//		this(1, "");
//	}

	/**
	 * ctor specifying the query index. Defaults SQL execution start time to
	 * the current time.
	 *
	 * @param	idx		Query index.
	 */
//	public SQLExecutionInfo(int idx)
//	{
//		this(idx, "");
//	}

	/**
	 * ctor specifying the SQL being executed. Defaults SQL execution start time
	 * to the current time.
	 *
	 * @param	sql		SQL being executed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> sql passed.
	 */
//	public SQLExecutionInfo(String sql)
//	{
//		super();
//		if (sql == null)
//		{
//			throw new IllegalArgumentException("SQL script == null");
//		}
//		_sql = sql;
//		_sqlExecutionStart = Calendar.getInstance().getTime();
//	}

	/**
	 * ctor specifying the SQL being executed and the query index. Defaults SQL
	 * execution start time to the current time.
	 *
	 * @param	idx		Query index.
	 * @param	sql		SQL being executed.
	 * @param	maxRows	Number of rows query is limited to.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> sql passed.
	 */
	public SQLExecutionInfo(int idx, String sql, int maxRows)
	{
		super();
		if (sql == null)
		{
			throw new IllegalArgumentException("SQL script == null");
		}
		_idx = idx;
		_sql = sql;
		_maxRows = maxRows;
		_sqlExecutionStart = Calendar.getInstance().getTime();
	}

	/**
	 * Flag that the SQL execution is complete.
	 */
	public void sqlExecutionComplete()
	{
		_resultsProcessingStart = Calendar.getInstance().getTime();
	}

	/**
	 * Flag that the results processing is complete.
	 */
	public void resultsProcessingComplete()
	{
		_resultsProcessingEnd = Calendar.getInstance().getTime();
	}

	/**
	 * Retrieve the query index.
	 *
	 * @return	Query index.
	 */
	public int getQueryIndex()
	{
		return _idx;
	}

	/**
	 * Retrieve the SQL script executed.
	 *
	 * @return	SQL script executed.
	 */
	public String getSQL()
	{
		return _sql;
	}

	/**
	 * Retrieve the SQL Execution start time.
	 *
	 * @return	SQL execution start time.
	 */
	public Date getSQLExecutionStartTime()
	{
		return _sqlExecutionStart;
	}

	/**
	 * Set the SQL Execution start time.
	 *
	 * @param	value	SQL execution start time.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Date</TT> passed.
	 */
	public void setSQLExecutionStartTime(Date value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("SQL Execution start time == null");
		}
		_sqlExecutionStart = value;
	}

	/**
	 * Retrieve the elapsed time time in milliseconds for the SQL execution.
	 *
	 * @return		SQL execution elapsed time in millis.
	 */
	public long getSQLExecutionElapsedMillis()
	{
		long results = 0;
		if (_resultsProcessingStart != null)
		{
			results = _resultsProcessingStart.getTime() - _sqlExecutionStart.getTime();
		}
		return results;
	}

	/**
	 * Retrieve the elapsed time time in milliseconds for the results processing.
	 *
	 * @return		Results processing elapsed time in millis.
	 */
	public long getResultsProcessingElapsedMillis()
	{
		long results = 0;
		if (_resultsProcessingEnd != null && _resultsProcessingStart != null)
		{
			results = (_resultsProcessingEnd.getTime() - _resultsProcessingStart.getTime());
		}
		return results;
	}

	/**
	 * Retrieve the total elapsed time time in milliseconds.
	 *
	 * @return	Total elapsed time in millis.
	 */
	public long getTotalElapsedMillis()
	{
		return getSQLExecutionElapsedMillis() + getResultsProcessingElapsedMillis();
	}

	/**
	 * Retrieve number of rows query limited to. Zero means unlimited.
	 *
	 * @return	number of rows query limited to.
	 */
	public int getMaxRows()
	{
		return _maxRows;
	}

   public void setNumberResultRowsRead(int numberResultRowsRead)
   {
      _numberResultRowsRead = numberResultRowsRead;
   }

   public Integer getNumberResultRowsRead()
   {
      return _numberResultRowsRead;
   }
}
