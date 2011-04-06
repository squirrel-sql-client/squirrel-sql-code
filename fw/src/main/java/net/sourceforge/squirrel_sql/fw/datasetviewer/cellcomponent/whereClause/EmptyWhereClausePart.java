package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A special Implementation of IWhereClausePart, which indicates, that the column represented by this should not be used in a Where Clause.
 * @author Stefan Willinger
 *
 */
public class EmptyWhereClausePart implements IWhereClausePart {

	public EmptyWhereClausePart() {
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#setParameter(java.sql.PreparedStatement, int)
	 */
	@Override
	public void setParameter(PreparedStatement pstmt, int position)
			throws SQLException {
		throw new IllegalStateException(
				"A NopWhereClausePart can't set an parameter");
	}

	/**
	 * This class represents a column, that could not be used within an where
	 * clause.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.AbstractWhereClausePart#shouldBeUsed()
	 */
	@Override
	public boolean shouldBeUsed() {
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#getWhereClause()
	 */
	@Override
	public String getWhereClause() {
		throw new IllegalStateException(
				"A NopWhereClauseParte does not support a whereClause");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#appendToClause(java.lang.StringBuilder)
	 */
	@Override
	public void appendToClause(StringBuilder whereClause) {
		throw new IllegalStateException(
				"A NopWhereClausePart can't append to a whereClause");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#isParameterUsed()
	 */
	@Override
	public boolean isParameterUsed() {
		throw new IllegalStateException(
				"A NopWhereClauseParte does not support a whereClause");
	}
}