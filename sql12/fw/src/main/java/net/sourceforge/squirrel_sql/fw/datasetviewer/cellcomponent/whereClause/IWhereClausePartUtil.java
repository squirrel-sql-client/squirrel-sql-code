package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides some utility methods for handling parts of an where-clause.
 * @author Stefan Willinger
 *
 */
public interface IWhereClausePartUtil {

	/**
	 * Creates an where clause from a list of {@link IWhereClausePart}
	 * @param whereClauseParts list of where clause parts
	 * @return the where clause, or <code>null</code>, if no suitable part of an where clause is avaiable
	 */
	public abstract String createWhereClause(
			List<IWhereClausePart> whereClauseParts);

	/**
	 * Sets the parameter values into the PreparedStatements, if any exists.
	 * Setting the parameter value will start at the given position.
	 * @param pstmt PreparedStatement, which contains the where clause.
	 * @param whereClauseParts parts of the where clause from this prepared statement.
	 * @param firstPosition The position of the first parameter, which should be set.
	 * @return The next index-position after setting the parameters
	 * @throws SQLException If an exception occurs while setting the parameters
	 */
	public abstract int setParameters(PreparedStatement pstmt,
			List<IWhereClausePart> whereClauseParts, int firstPosition)
			throws SQLException;

	/**
	 * Checks, if the list of where-clause parts contains at least one usable part.
	 * An {@link IWhereClausePart} is usable, if {@link IWhereClausePart#shouldBeUsed()} returns true.
	 * @param whereClauseParts list of where clause parts, which should be checked
	 * @return true, if at least one where clause part is usable.
	 * @see IWhereClausePart#shouldBeUsed()
	 */
	public abstract boolean hasUsableWhereClause(
			List<IWhereClausePart> whereClauseParts);

}