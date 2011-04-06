package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;

/**
 * The part of an where-clause, which uses an parameter.
 * @author Stefan Willinger
 *
 */
public class ParameterWhereClausePart extends AbstractWhereClausePart {

	/**
	 * The DataTypeComponent, which had this WhereClausePart created and could
	 * be used for setting the parameter values into the PreparedStatement
	 */
	private IDataTypeComponent dataTypeComponent;
	/**
	 * Value of the parameter, if any is used by the whereClause.
	 */
	private Object parameterValue;

	
	/**
	 * Creates an {@link IWhereClausePart} with the given column and parameter.
	 * The generated part of the where-clause will use an parameter value like
	 * <code>column = ?</code>
	 * If the value of the parameter is null, then you should use {@link IsNullWhereClausePart} insteed this.
	 * @param column
	 *            Column, which is part of the where clause (must be not null)
	 * @param parameterValue
	 *            Value, which is used within the where-clause (must not be
	 *            null)
	 * @param dataTypeComponent
	 *            DataTypeComponent, which is responsible for setting the
	 *            parameter into the PreparedStatement. (must be not null)
	 * @see IsNullWhereClausePart
	 */
	public ParameterWhereClausePart(ColumnDisplayDefinition columnDef,
			Object parameterValue, IDataTypeComponent dataTypeComponent) {
		super(columnDef);
		setDataTypeComponent(dataTypeComponent);
		setParameterValue(parameterValue);
		setWhereClause(getColumn() + " = ?");
	}

	/**
	 * Sets the parameter value, which should be used within this where clause
	 * part.
	 * 
	 * @param parameterValue
	 */
	protected void setParameterValue(Object parameterValue) {
		if (parameterValue == null) {
			throw new IllegalArgumentException(
					"The parameterValue must not be null");
		}
		this.parameterValue = parameterValue;
	}

	/**
	 * Set's the apropriate  {@link IDataTypeComponent} which should be used for setting the value into the {@link PreparedStatement}
	 * @param dataTypeComponent for setting the value into the PreparedStatement
	 */
	private void setDataTypeComponent(IDataTypeComponent dataTypeComponent) {

		if (dataTypeComponent == null) {
			throw new IllegalArgumentException("column must be not empty");
		}
		this.dataTypeComponent = dataTypeComponent;
	}

	/**
	 * 
	 * @return the value of the parameter of this where clause part, if any
	 *         exists.
	 */
	public Object getParameterValue() {
		return parameterValue;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause
	 * .IWhereClausePart#setParameter(java.sql.PreparedStatement, int)
	 */
	@Override
	public void setParameter(PreparedStatement pstmt, int position)
			throws SQLException {
		this.dataTypeComponent.setPreparedStatementValue(pstmt,
				this.parameterValue, position);
	}

	/**
	 * This clause uses parameter
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#isParameterUsed()
	 */
	@Override
	public boolean isParameterUsed() {
		return true;
	}
}