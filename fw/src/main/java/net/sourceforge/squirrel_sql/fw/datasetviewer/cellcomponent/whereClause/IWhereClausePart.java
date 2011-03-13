package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Part of an where-clause of an SQL-Update statement when updating
 * data with an editable result. Each part of an where clause is intended to
 * to be an conjunction (AND operator within the SQL).
 * A specific Implementation could provide functionality to handle NULL values, parameter values and so on.
 * Using WhereClausePart's remove the needs of the {@link DatabaseSpecificEscape} wich was present
 * for PostgreSQL and MckoiSQL, because it can use parameter values.
 * Some databases needs to escape some special characters. e.g. MySQL (see http://dev.mysql.com/doc/refman/5.1/en/string-syntax.html)
 * Now, Squirrel can use parameter values for the where clause. So the JDBC-driver is responsible for handling this special characters.
 * 
 * @author Stefan Willinger
 *
 */
public interface IWhereClausePart{

	/**
	 * The part of the where-clause.
	 * When using parameters something like <code>columnName = ?</code>
	 * @return The part of an where-clause.
	 */
	public String getWhereClause();

	/**
	 * Appends this part of the where-clause to the where-clause as an cunjunction.
	 * If the where-clause is empty, then this method will insert the where key-word.
	 * If this part uses an parameter, then something like <code>columnName = ?</code> is used as where clause part.
	 * @param whereClause where clause, to which this part will be added as conjunction
	 * @see #getWhereClause()
	 */
	public void appendToClause(StringBuilder whereClause);
	
	/**
	 * Sets the appropriate parameter value into the given position of the PreparedStatement.
	 * Its only allowed to call this method, if {@link #isParameterUsed()} returns true. Otherwise an {@link IllegalStateException} will be thrown.
	 * @param pstmt statement, where the parameter should be set.
	 * @param position Position of the parameter
	 * @throws SQLException Exception when something goes wrong while setting the parameter value.
	 * @throws IllegalStateException when this method is called, even this where clause part doesn't use an parameter value.
	 * @see #isParameterUsed()
	 */
	public void setParameter(PreparedStatement pstmt, int position) throws SQLException;
	
	/**
	 * Check if this part of an where clause uses SQL-Parameters.
	 * If this method returns false, then a call of {@link #setParameter(PreparedStatement, int)} will throw an Exception.
	 * @return true, if this use an SQL-Parameter. Otherwise false.
	 */
	public boolean isParameterUsed();
	
	
	/**
	 * Indicates, that this part of an where-clause should be used in an where-clause.
	 * Sometimes, it is not possible to create an where-clause for an column. Then this
	 * method will return false.
	 * @return true, if this part can be used in an where-clause. Otherwise false.
	 */
	public boolean shouldBeUsed();
	
   }