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

	@Override
	public String getWhereClause() {
		throw new IllegalStateException(
				"A NopWhereClauseParte does not support a whereClause");
	}

	@Override
	public void appendToClause(StringBuilder whereClause) {
		throw new IllegalStateException(
				"A NopWhereClausePart can't append to a whereClause");
	}

	@Override
	public boolean isParameterUsed() {
		throw new IllegalStateException(
				"A NopWhereClauseParte does not support a whereClause");
	}
}