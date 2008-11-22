/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.mo.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import com.mockobjects.sql.MockSingleRowResultSet;

@SuppressWarnings("unused")
public class MockDatabaseMetaData extends com.mockobjects.sql.MockDatabaseMetaData
{

	private MockResultSet catalogs = null;

	private MockResultSet schemas = null;

	private String keywords = null;

	private MockResultSet typeInfo = null;

	private MockResultSet procedures = null;

	private String catalog = "aCatalog";

	private String schema = "aSchema";

	private String catalogTerm = "CATALOG";

	private String schemaTerm = "SCHEMA";

	private String procedureTerm = "PROCEDURE";

	public MockDatabaseMetaData()
	{

	}

	public MockDatabaseMetaData(String currentCatalog, String currentSchema)
	{
		catalog = currentCatalog;
		schema = currentSchema;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#getIdentifierQuoteString()
	 */
	public String getIdentifierQuoteString() throws SQLException
	{

		return "\"";
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInDataManipulation()
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInDataManipulation()
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#getTableTypes()
	 */
	public ResultSet getTableTypes() throws SQLException
	{
		MockSingleRowResultSet rs = new MockSingleRowResultSet();
		rs.addExpectedIndexedValues(new Object[] { "TABLE" });
		return rs;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#getDatabaseProductName()
	 */
	public String getDatabaseProductName() throws SQLException
	{
		return "junitDBProductName";
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#getDatabaseProductName()
	 */
	public String getDatabaseProductVersion() throws SQLException
	{
		return "1.0";
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#allProceduresAreCallable()
	 */
	public boolean allProceduresAreCallable() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#allTablesAreSelectable()
	 */
	public boolean allTablesAreSelectable() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#deletesAreDetected(int)
	 */
	public boolean deletesAreDetected(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#doesMaxRowSizeIncludeBlobs()
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#insertsAreDetected(int)
	 */
	public boolean insertsAreDetected(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#isCatalogAtStart()
	 */
	public boolean isCatalogAtStart() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#locatorsUpdateCopy()
	 */
	public boolean locatorsUpdateCopy() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#nullPlusNonNullIsNull()
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedAtEnd()
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedAtStart()
	 */
	public boolean nullsAreSortedAtStart() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedHigh()
	 */
	public boolean nullsAreSortedHigh() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedLow()
	 */
	public boolean nullsAreSortedLow() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#othersDeletesAreVisible(int)
	 */
	public boolean othersDeletesAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#othersInsertsAreVisible(int)
	 */
	public boolean othersInsertsAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#othersUpdatesAreVisible(int)
	 */
	public boolean othersUpdatesAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#ownDeletesAreVisible(int)
	 */
	public boolean ownDeletesAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#ownInsertsAreVisible(int)
	 */
	public boolean ownInsertsAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#ownUpdatesAreVisible(int)
	 */
	public boolean ownUpdatesAreVisible(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#setupDriverName(java.lang.String)
	 */
	public void setupDriverName(String arg0)
	{

		super.setupDriverName(arg0);
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesLowerCaseIdentifiers()
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesLowerCaseQuotedIdentifiers()
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesMixedCaseIdentifiers()
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesMixedCaseQuotedIdentifiers()
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesUpperCaseIdentifiers()
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#storesUpperCaseQuotedIdentifiers()
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsAlterTableWithAddColumn()
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsAlterTableWithDropColumn()
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92EntryLevelSQL()
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92FullSQL()
	 */
	public boolean supportsANSI92FullSQL() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92IntermediateSQL()
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsBatchUpdates()
	 */
	public boolean supportsBatchUpdates() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInIndexDefinitions()
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInProcedureCalls()
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInTableDefinitions()
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsColumnAliasing()
	 */
	public boolean supportsColumnAliasing() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsConvert()
	 */
	public boolean supportsConvert() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsConvert(int, int)
	 */
	public boolean supportsConvert(int arg0, int arg1) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCoreSQLGrammar()
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCorrelatedSubqueries()
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDataManipulationTransactionsOnly()
	 */
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDifferentTableCorrelationNames()
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsExpressionsInOrderBy()
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsExtendedSQLGrammar()
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsFullOuterJoins()
	 */
	public boolean supportsFullOuterJoins() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGetGeneratedKeys()
	 */
	public boolean supportsGetGeneratedKeys() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupBy()
	 */
	public boolean supportsGroupBy() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupByBeyondSelect()
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupByUnrelated()
	 */
	public boolean supportsGroupByUnrelated() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsIntegrityEnhancementFacility()
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsLikeEscapeClause()
	 */
	public boolean supportsLikeEscapeClause() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsLimitedOuterJoins()
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMinimumSQLGrammar()
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMixedCaseIdentifiers()
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleTransactions()
	 */
	public boolean supportsMultipleTransactions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsNonNullableColumns()
	 */
	public boolean supportsNonNullableColumns() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenCursorsAcrossCommit()
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenCursorsAcrossRollback()
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenStatementsAcrossCommit()
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenStatementsAcrossRollback()
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOrderByUnrelated()
	 */
	public boolean supportsOrderByUnrelated() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOuterJoins()
	 */
	public boolean supportsOuterJoins() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsPositionedDelete()
	 */
	public boolean supportsPositionedDelete() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsPositionedUpdate()
	 */
	public boolean supportsPositionedUpdate() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetConcurrency(int, int)
	 */
	public boolean supportsResultSetConcurrency(int arg0, int arg1) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetHoldability(int)
	 */
	public boolean supportsResultSetHoldability(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetType(int)
	 */
	public boolean supportsResultSetType(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSavepoints()
	 */
	public boolean supportsSavepoints() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInIndexDefinitions()
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInProcedureCalls()
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInTableDefinitions()
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSelectForUpdate()
	 */
	public boolean supportsSelectForUpdate() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsStatementPooling()
	 */
	public boolean supportsStatementPooling() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsStoredProcedures()
	 */
	public boolean supportsStoredProcedures() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInComparisons()
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInExists()
	 */
	public boolean supportsSubqueriesInExists() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInIns()
	 */
	public boolean supportsSubqueriesInIns() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInQuantifieds()
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTableCorrelationNames()
	 */
	public boolean supportsTableCorrelationNames() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTransactionIsolationLevel(int)
	 */
	public boolean supportsTransactionIsolationLevel(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTransactions()
	 */
	public boolean supportsTransactions() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsUnion()
	 */
	public boolean supportsUnion() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#supportsUnionAll()
	 */
	public boolean supportsUnionAll() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#updatesAreDetected(int)
	 */
	public boolean updatesAreDetected(int arg0) throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#usesLocalFilePerTable()
	 */
	public boolean usesLocalFilePerTable() throws SQLException
	{

		return false;
	}

	/* (non-Javadoc)
	 * @see com.mockobjects.sql.MockDatabaseMetaData#usesLocalFiles()
	 */
	public boolean usesLocalFiles() throws SQLException
	{

		return false;
	}

	public ResultSet getCatalogs() throws SQLException
	{
		return catalogs;
	}

	public void setCatalogs(String[] catalogNames, SQLDatabaseMetaData md)
	{
		catalogs = new MockResultSet();
		TableColumnInfo[] cols = new TableColumnInfo[] { new TableColumnInfo("aCatalog", // catalog
			"aSchema", // schema
			"", // tableName
			"", // columnName
			1, // dataType; 1 == CHAR
			"", // typeName
			0, // columnSize
			0, // decimalDigits
			0, // radix
			0, // isNullAllowed
			"", "", // defaultValue
			0, 0, "", // isNullable
			md) };
		catalogs.setTableColumnInfos(cols);
		for (int i = 0; i < catalogNames.length; i++)
		{
			catalogs.addRow(new Object[] { catalogNames[i] });
		}
	}

	public ResultSet getSchemas() throws SQLException
	{
		return schemas;
	}

	public void setSchemas(String[] schemaNames, SQLDatabaseMetaData md)
	{
		schemas = new MockResultSet(null);
		TableColumnInfo[] cols = new TableColumnInfo[] { new TableColumnInfo("aCatalog", // catalog
			"aSchema", // schema
			"", // tableName
			"", // columnName
			1, // dataType; 1 == CHAR
			"", // typeName
			0, // columnSize
			0, // decimalDigits
			0, // radix
			0, // isNullAllowed
			"", "", // defaultValue
			0, 0, "", // isNullable
			md) };
		schemas.setTableColumnInfos(cols);
		for (int i = 0; i < schemaNames.length; i++)
		{
			schemas.addRow(new Object[] { schemaNames[i] });
		}

	}

	public String getSQLKeywords() throws SQLException
	{
		return "";
	}

	public void setSQLKeywords(String[] someKeywords)
	{
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < someKeywords.length; i++)
		{
			tmp.append(someKeywords[i]);
			if (i < someKeywords.length)
			{
				tmp.append(",");
			}
		}
		keywords = tmp.toString();
	}

	public ResultSet getTypeInfo() throws SQLException
	{
		return new MockResultSet(null);
	}

	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamrPattern)
		throws SQLException
	{
		return new MockResultSet(null);
	}

	public String getNumericFunctions() throws SQLException
	{
		return "";
	}

	public String getStringFunctions() throws SQLException
	{
		return "";
	}

	public ResultSet getTables(String aCatalog, String schemaPattern, String tableNamePattern, String[] types)
		throws SQLException
	{
		return new MockResultSet(null);
	}

	public String getTimeDateFunctions() throws SQLException
	{
		return "";
	}

	public void setCatalogTerm(String aCatalogTerm)
	{
		catalogTerm = aCatalogTerm;
	}

	public String getCatalogSeparator()
	{
		return ".";
	}

	public String getCatalogTerm()
	{
		return catalogTerm;
	}

	/**
	 * @param schemaTerm
	 *           the schemaTerm to set
	 */
	public void setSchemaTerm(String schemaTerm)
	{
		this.schemaTerm = schemaTerm;
	}

	/**
	 * @return the schemaTerm
	 */
	public String getSchemaTerm()
	{
		return schemaTerm;
	}

	/**
	 * @param procedureTerm
	 *           the procedureTerm to set
	 */
	public void setProcedureTerm(String procedureTerm)
	{
		this.procedureTerm = procedureTerm;
	}

	/**
	 * @return the procedureTerm
	 */
	public String getProcedureTerm()
	{
		return procedureTerm;
	}

	public String getDriverName() throws SQLException
	{
		return "MockDatabaseDriver";
	}

	/**
	 * @see java.sql.DatabaseMetaData#autoCommitFailureClosesAllResultSets()
	 */
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{

		return false;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getClientInfoProperties()
	 */
	public ResultSet getClientInfoProperties() throws SQLException
	{

		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getFunctionColumns(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
		String columnNamePattern) throws SQLException
	{

		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getFunctions(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
		throws SQLException
	{

		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getSchemas(java.lang.String, java.lang.String)
	 */
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
	{

		return null;
	}

}
