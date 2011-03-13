package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * An part of an where-clause, which doesn't use an parameter.
 * The where clause could be anything.
 * @author Stefan Willinger
 *
 */
public class NoParameterWhereClausePart extends AbstractWhereClausePart {

	/**
	 * Constructs an part of an where-clause with an given clause without any parameter.
	 * @param columnDef An column which the clause refers
	 * @param clause The part of the where clause to use.
	 */
	public NoParameterWhereClausePart(ColumnDisplayDefinition columnDef, String clause) {
		super(columnDef, clause);
	}

	/**
	 * An {@link NoParameterWhereClausePart} doesn't use any SQL-Parameter
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#isParameterUsed()
	 */
	@Override
	public boolean isParameterUsed() {
		return false;
	}
	
	/**
	 * For an {@link NoParameterWhereClausePart}, it is not allowed to set an
	 * parameter into an {@link PreparedStatement}
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.AbstractWhereClausePart#setParameter(java.sql.PreparedStatement,
	 *      int)
	 * @throws IllegalStateException
	 *             whenever this method is called, it will throw an
	 *             {@link IllegalStateException}
	 */
	@Override
	public void setParameter(PreparedStatement pstmt, int position)
			throws SQLException {
		throw new IllegalStateException(
				"An " +getClass().getSimpleName() + "cannot set an parameter into an PreparedStatement!");
	}

}