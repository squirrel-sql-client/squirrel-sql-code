package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.apache.commons.lang.StringUtils;

/**
 * Basic implementation of an WhereClausePart.
 * 
 * @author Stefan Willinger
 *
 */
public abstract class AbstractWhereClausePart implements IWhereClausePart {

	/**
	 * The part of the whereClause.
	 * This could, but must not contain an parameter.
	 */
	private String whereClause;

	/**
	 * Column, which this WhereClausePart refers.
	 */
	private String column;
	
	
	/**
	 * Creates an part of the where clause with an custom specified whereClausePart.
	 * @param columnDef Column, which is part of this where clause
	 * @param whereClausePart Custom part of the where clause.
	 */
	protected AbstractWhereClausePart(ColumnDisplayDefinition columnDef, String whereClausePart ){
		setColumn(columnDef.getColumnName());
		setWhereClause(whereClausePart);
	}
	
	/**
	 * Creates an part of the where clause only with an column name. The subclass hast to ensure, that an where-clause will be set.
	 * @param columnDef Column, which is part of this where clause
	 */
	protected AbstractWhereClausePart(ColumnDisplayDefinition columnDef ){
		if(columnDef == null){
			throw new IllegalArgumentException("columnDef must not be null");
		}
		setColumn(columnDef.getColumnName());
	}

	

	private void setColumn(String column) {
		if (StringUtils.isEmpty(column)) {
			throw new IllegalArgumentException("column must be not empty");
		}
		this.column = column;
	}

	

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#getWhereClause()
	 */
	@Override
	public String getWhereClause() {
		if(whereClause == null){
			throw new IllegalStateException("It's not intended, that an part of an where-clause is null!");
		}
		return whereClause;
	}

	

	
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#appendToClause(java.lang.StringBuilder)
	 */
	@Override
	public void appendToClause(StringBuilder whereClause){
		if(shouldBeUsed() == false){
			throw new IllegalStateException("Should append to a whereClause, but this one should not be used!");
		}
		String clause = getWhereClause();

		if (whereClause.length() == 0)
		{
			whereClause.append(" WHERE ");
			whereClause.append(clause);
		}
		else
		{
			whereClause.append(" AND ");
			whereClause.append(clause);
		}
	}
	
	
	
	/**
	 * Sets the where clause.
	 * @throws IllegalArgumentException if the clause is blank
	 * @param clause
	 */
	protected void setWhereClause(String clause){
		if(StringUtils.isBlank(clause)){
			throw new IllegalArgumentException("clause must not be blank.");
		}
		this.whereClause = clause;
	}

	

	/**
	 * Normally each column can be used within an where clause.
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart#shouldBeUsed()
	 */
	@Override
	public boolean shouldBeUsed() {
		return true;
	}

	/**
	 * The name of the related column
	 * @return the name of the related column
	 */
	public String getColumn() {
		return column;
	}
	
	
	
	

}
