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
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import com.mockobjects.sql.MockSingleRowResultSet;



public class MockDatabaseMetaData extends
        com.mockobjects.sql.MockDatabaseMetaData {
	
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
	
	public MockDatabaseMetaData() {
		
	}
	
	public MockDatabaseMetaData(String currentCatalog,
								String currentSchema) 
	{
		catalog = currentCatalog;
		schema = currentSchema;
	}	
	
    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#getIdentifierQuoteString()
     */
    public String getIdentifierQuoteString() throws SQLException {
        // TODO Auto-generated method stub
        return "\"";
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInDataManipulation()
     */
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInDataManipulation()
     */
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#getTableTypes()
     */
    public ResultSet getTableTypes() throws SQLException {
        MockSingleRowResultSet rs = new MockSingleRowResultSet();
        rs.addExpectedIndexedValues(new Object[] { "TABLE" });
        return rs;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#getDatabaseProductName()
     */
    public String getDatabaseProductName() throws SQLException {
        return "junitDBProductName";
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#getDatabaseProductName()
     */
    public String getDatabaseProductVersion() throws SQLException {
        return "1.0";
    }
    
    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#allProceduresAreCallable()
     */
    public boolean allProceduresAreCallable() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#allTablesAreSelectable()
     */
    public boolean allTablesAreSelectable() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#deletesAreDetected(int)
     */
    public boolean deletesAreDetected(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#doesMaxRowSizeIncludeBlobs()
     */
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#insertsAreDetected(int)
     */
    public boolean insertsAreDetected(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#isCatalogAtStart()
     */
    public boolean isCatalogAtStart() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#isReadOnly()
     */
    public boolean isReadOnly() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#locatorsUpdateCopy()
     */
    public boolean locatorsUpdateCopy() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#nullPlusNonNullIsNull()
     */
    public boolean nullPlusNonNullIsNull() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedAtEnd()
     */
    public boolean nullsAreSortedAtEnd() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedAtStart()
     */
    public boolean nullsAreSortedAtStart() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedHigh()
     */
    public boolean nullsAreSortedHigh() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#nullsAreSortedLow()
     */
    public boolean nullsAreSortedLow() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#othersDeletesAreVisible(int)
     */
    public boolean othersDeletesAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#othersInsertsAreVisible(int)
     */
    public boolean othersInsertsAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#othersUpdatesAreVisible(int)
     */
    public boolean othersUpdatesAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#ownDeletesAreVisible(int)
     */
    public boolean ownDeletesAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#ownInsertsAreVisible(int)
     */
    public boolean ownInsertsAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#ownUpdatesAreVisible(int)
     */
    public boolean ownUpdatesAreVisible(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#setupDriverName(java.lang.String)
     */
    public void setupDriverName(String arg0) {
        // TODO Auto-generated method stub
        super.setupDriverName(arg0);
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesLowerCaseIdentifiers()
     */
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesLowerCaseQuotedIdentifiers()
     */
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesMixedCaseIdentifiers()
     */
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesMixedCaseQuotedIdentifiers()
     */
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesUpperCaseIdentifiers()
     */
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#storesUpperCaseQuotedIdentifiers()
     */
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsAlterTableWithAddColumn()
     */
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsAlterTableWithDropColumn()
     */
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92EntryLevelSQL()
     */
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92FullSQL()
     */
    public boolean supportsANSI92FullSQL() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsANSI92IntermediateSQL()
     */
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsBatchUpdates()
     */
    public boolean supportsBatchUpdates() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInIndexDefinitions()
     */
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
     */
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInProcedureCalls()
     */
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCatalogsInTableDefinitions()
     */
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsColumnAliasing()
     */
    public boolean supportsColumnAliasing() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsConvert()
     */
    public boolean supportsConvert() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsConvert(int, int)
     */
    public boolean supportsConvert(int arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCoreSQLGrammar()
     */
    public boolean supportsCoreSQLGrammar() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsCorrelatedSubqueries()
     */
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
     */
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDataManipulationTransactionsOnly()
     */
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsDifferentTableCorrelationNames()
     */
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsExpressionsInOrderBy()
     */
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsExtendedSQLGrammar()
     */
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsFullOuterJoins()
     */
    public boolean supportsFullOuterJoins() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGetGeneratedKeys()
     */
    public boolean supportsGetGeneratedKeys() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupBy()
     */
    public boolean supportsGroupBy() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupByBeyondSelect()
     */
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsGroupByUnrelated()
     */
    public boolean supportsGroupByUnrelated() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsIntegrityEnhancementFacility()
     */
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsLikeEscapeClause()
     */
    public boolean supportsLikeEscapeClause() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsLimitedOuterJoins()
     */
    public boolean supportsLimitedOuterJoins() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMinimumSQLGrammar()
     */
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMixedCaseIdentifiers()
     */
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
     */
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleOpenResults()
     */
    public boolean supportsMultipleOpenResults() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleResultSets()
     */
    public boolean supportsMultipleResultSets() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsMultipleTransactions()
     */
    public boolean supportsMultipleTransactions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsNamedParameters()
     */
    public boolean supportsNamedParameters() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsNonNullableColumns()
     */
    public boolean supportsNonNullableColumns() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenCursorsAcrossCommit()
     */
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenCursorsAcrossRollback()
     */
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenStatementsAcrossCommit()
     */
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOpenStatementsAcrossRollback()
     */
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOrderByUnrelated()
     */
    public boolean supportsOrderByUnrelated() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsOuterJoins()
     */
    public boolean supportsOuterJoins() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsPositionedDelete()
     */
    public boolean supportsPositionedDelete() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsPositionedUpdate()
     */
    public boolean supportsPositionedUpdate() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetConcurrency(int, int)
     */
    public boolean supportsResultSetConcurrency(int arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetHoldability(int)
     */
    public boolean supportsResultSetHoldability(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsResultSetType(int)
     */
    public boolean supportsResultSetType(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSavepoints()
     */
    public boolean supportsSavepoints() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInIndexDefinitions()
     */
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
     */
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInProcedureCalls()
     */
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSchemasInTableDefinitions()
     */
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSelectForUpdate()
     */
    public boolean supportsSelectForUpdate() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsStatementPooling()
     */
    public boolean supportsStatementPooling() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsStoredProcedures()
     */
    public boolean supportsStoredProcedures() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInComparisons()
     */
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInExists()
     */
    public boolean supportsSubqueriesInExists() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInIns()
     */
    public boolean supportsSubqueriesInIns() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsSubqueriesInQuantifieds()
     */
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTableCorrelationNames()
     */
    public boolean supportsTableCorrelationNames() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTransactionIsolationLevel(int)
     */
    public boolean supportsTransactionIsolationLevel(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsTransactions()
     */
    public boolean supportsTransactions() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsUnion()
     */
    public boolean supportsUnion() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#supportsUnionAll()
     */
    public boolean supportsUnionAll() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#updatesAreDetected(int)
     */
    public boolean updatesAreDetected(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#usesLocalFilePerTable()
     */
    public boolean usesLocalFilePerTable() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.mockobjects.sql.MockDatabaseMetaData#usesLocalFiles()
     */
    public boolean usesLocalFiles() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

	public ResultSet getCatalogs() throws SQLException {
		return catalogs;
	}

	public void setCatalogs(String[] catalogNames, SQLDatabaseMetaData md) {
		catalogs = new MockResultSet();
		ArrayList list = new ArrayList();
		for (int i = 0; i < catalogNames.length; i++) {
			catalogs.addRow(new Object[] {catalogNames[i]});
			list.add(new TableColumnInfo(catalogNames[i], // catalog 
										 "aSchema",       // schema
										 "",              // tableName
										 "",              // columnName
										 1,               // dataType
										 "", // typeName 
										 0,  // columnSize
										 0,  // decimalDigits
										 0,  // radix
										 0,  // isNullAllowed
										 "",
										 "", // defaultValue
										 0, 
										 0, 
										 "", // isNullable 
										 md));			
		}
		TableColumnInfo[] array = 
			(TableColumnInfo[])list.toArray(new TableColumnInfo[list.size()]);
		MockResultSetMetaData rsmd = new MockResultSetMetaData(array); 
		catalogs.setTableColumnInfos(array);
	}

	public ResultSet getSchemas() throws SQLException {
		return schemas;
	}

	public void setSchemas(String[] schemaNames, SQLDatabaseMetaData md) {
		schemas = new MockResultSet(null);
		ArrayList list = new ArrayList();
		for (int i = 0; i < schemaNames.length; i++) {
			schemas.addRow(new Object[] {schemaNames[i]});
			list.add(new TableColumnInfo("aCatalog", // catalog 
					 				     "aSchema",       // schema
					 				     "",              // tableName
					 				     "",              // columnName
					 				     1,               // dataType
					 				     "", // typeName 
					 				     0,  // columnSize
					 				     0,  // decimalDigits
					 				     0,  // radix
					 				     0,  // isNullAllowed
					 				     "",
					 				     "", // defaultValue
					 				     0, 
										 0, 
										 "", // isNullable 
										 md));		
		}
		TableColumnInfo[] array = 
			(TableColumnInfo[])list.toArray(new TableColumnInfo[list.size()]);
		MockResultSetMetaData rsmd = new MockResultSetMetaData(array);
		schemas.setTableColumnInfos(array);
	}
    
	public String getSQLKeywords() throws SQLException {
		return "";
	}
	
	public void setSQLKeywords(String[] someKeywords) {
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < someKeywords.length; i++) {
			tmp.append(someKeywords[i]);
			if (i < someKeywords.length) {
				tmp.append(",");
			}
		}
		keywords = tmp.toString();
	}
	
	public ResultSet getTypeInfo() throws SQLException {
		return new MockResultSet(null);
	}
	
	public ResultSet getProcedures(String catalog, 
								   String schemaPattern, 
								   String procedureNamrPattern) 
		throws SQLException
	{
		return new MockResultSet(null);
	}
	
	public String getNumericFunctions() throws SQLException {
		return "";
	}

	public String getStringFunctions() throws SQLException {
		return "";
	}

	public ResultSet getTables(String aCatalog, 
							   String schemaPattern, 
							   String tableNamePattern, 
							   String[] types) 
		throws SQLException 
	{
		return new MockResultSet(null);
	}

	public String getTimeDateFunctions() throws SQLException {
		return "";
	}
	
	public void setCatalogTerm(String aCatalogTerm) {
		catalogTerm = aCatalogTerm;
	}
	
    public String getCatalogSeparator() {
        return ".";
    }
    
	public String getCatalogTerm() {
		return catalogTerm;
	}

	/**
	 * @param schemaTerm the schemaTerm to set
	 */
	public void setSchemaTerm(String schemaTerm) {
		this.schemaTerm = schemaTerm;
	}

	/**
	 * @return the schemaTerm
	 */
	public String getSchemaTerm() {
		return schemaTerm;
	}

	/**
	 * @param procedureTerm the procedureTerm to set
	 */
	public void setProcedureTerm(String procedureTerm) {
		this.procedureTerm = procedureTerm;
	}

	/**
	 * @return the procedureTerm
	 */
	public String getProcedureTerm() {
		return procedureTerm;
	}

	public String getDriverName() throws SQLException {
		return "MockDatabaseDriver";
	}	

	
}
