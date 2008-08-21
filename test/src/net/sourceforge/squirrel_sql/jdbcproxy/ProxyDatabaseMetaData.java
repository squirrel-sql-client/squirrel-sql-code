package net.sourceforge.squirrel_sql.jdbcproxy;

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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProxyDatabaseMetaData implements DatabaseMetaData {

    private DatabaseMetaData _data = null;
    
    public ProxyDatabaseMetaData(DatabaseMetaData data) {
        _data = data;
    }
    
    public int getDatabaseMajorVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDatabaseMajorVersion");
        return _data.getDatabaseMajorVersion();
    }

    public int getDatabaseMinorVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDatabaseMinorVersion");
        return _data.getDatabaseMinorVersion();
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDefaultTransactionIsolation");
        return _data.getDefaultTransactionIsolation();
    }

    public int getDriverMajorVersion() {
        return _data.getDriverMajorVersion();
    }

    public int getDriverMinorVersion() {
        return _data.getDriverMinorVersion();
    }

    public int getJDBCMajorVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getJDBCMajorVersion");
        return _data.getJDBCMajorVersion();
    }

    public int getJDBCMinorVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getJDBCMinorVersion");
        return _data.getJDBCMinorVersion();
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxBinaryLiteralLength");        
        return _data.getMaxBinaryLiteralLength();
    }

    public int getMaxCatalogNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxCatalogNameLength");
        return _data.getMaxCatalogNameLength();
    }

    public int getMaxCharLiteralLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxCharLiteralLength");        
        return _data.getMaxCharLiteralLength();
    }

    public int getMaxColumnNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnNameLength");
        return _data.getMaxColumnNameLength();
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnsInGroupBy");        
        return _data.getMaxColumnsInGroupBy();
    }

    public int getMaxColumnsInIndex() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnsInIndex");        
        return _data.getMaxColumnsInIndex();
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnsInOrderBy");        
        return _data.getMaxColumnsInOrderBy();
    }

    public int getMaxColumnsInSelect() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnsInSelect");        
        return _data.getMaxColumnsInSelect();
    }

    public int getMaxColumnsInTable() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxColumnsInTable");        
        return _data.getMaxColumnsInTable();
    }

    public int getMaxConnections() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxConnections");        
        return _data.getMaxConnections();
    }

    public int getMaxCursorNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxCursorNameLength");
        return _data.getMaxCursorNameLength();
    }

    public int getMaxIndexLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxIndexLength");        
        return _data.getMaxIndexLength();
    }

    public int getMaxProcedureNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxProcedureNameLength");        
        return _data.getMaxProcedureNameLength();
    }

    public int getMaxRowSize() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxRowSize");        
        return _data.getMaxRowSize();
    }

    public int getMaxSchemaNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxSchemaNameLength");             
        return _data.getMaxSchemaNameLength();
    }

    public int getMaxStatementLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxStatementLength");        
        return _data.getMaxStatementLength();
    }

    public int getMaxStatements() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxStatements");        
        return _data.getMaxStatements();
    }

    public int getMaxTableNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxTableNameLength");        
        return _data.getMaxTableNameLength();
    }

    public int getMaxTablesInSelect() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxTablesInSelect"); 
        return _data.getMaxTablesInSelect();
    }

    public int getMaxUserNameLength() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getMaxUserNameLength");     
        return _data.getMaxUserNameLength();
        
    }

    public int getResultSetHoldability() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getResultSetHoldability");        
        return _data.getResultSetHoldability();
    }

    public int getSQLStateType() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSQLStateType");        
        return _data.getSQLStateType();
    }

    public boolean allProceduresAreCallable() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "allProceduresAreCallable");        
        return _data.allProceduresAreCallable();
    }

    public boolean allTablesAreSelectable() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "allTablesAreSelectable");        
        return _data.allTablesAreSelectable();
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "dataDefinitionCausesTransactionCommit");        
        return _data.dataDefinitionCausesTransactionCommit();
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "dataDefinitionIgnoredInTransactions");        
        return _data.dataDefinitionIgnoredInTransactions();
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "doesMaxRowSizeIncludeBlobs");        
        return _data.doesMaxRowSizeIncludeBlobs();
    }

    public boolean isCatalogAtStart() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "isCatalogAtStart");        
        return _data.isCatalogAtStart();
    }

    public boolean isReadOnly() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "isReadOnly");        
        return _data.isReadOnly();
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "locatorsUpdateCopy");        
        return _data.locatorsUpdateCopy();
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "nullPlusNonNullIsNull");        
        return _data.nullPlusNonNullIsNull();
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "nullsAreSortedAtEnd");        
        return _data.nullsAreSortedAtEnd();
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "nullsAreSortedAtStart");        
        return _data.nullsAreSortedAtStart();
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "nullsAreSortedHigh");        
        return _data.nullsAreSortedHigh();
    }

    public boolean nullsAreSortedLow() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "nullsAreSortedLow");
        return _data.nullsAreSortedLow();
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesLowerCaseIdentifiers");        
        return _data.storesLowerCaseIdentifiers();
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesLowerCaseQuotedIdentifiers");        
        return _data.storesLowerCaseQuotedIdentifiers();
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesMixedCaseIdentifiers");        
        return _data.storesMixedCaseIdentifiers();
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesMixedCaseQuotedIdentifiers");        
        return _data.storesMixedCaseQuotedIdentifiers();
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesUpperCaseIdentifiers");        
        return _data.storesUpperCaseIdentifiers();
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "storesUpperCaseQuotedIdentifiers");        
        return _data.storesUpperCaseQuotedIdentifiers();
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsANSI92EntryLevelSQL");        
        return _data.supportsANSI92EntryLevelSQL();
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsANSI92FullSQL");        
        return _data.supportsANSI92FullSQL();
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsANSI92IntermediateSQL");        
        return _data.supportsANSI92IntermediateSQL();
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsAlterTableWithAddColumn");        
        return _data.supportsAlterTableWithAddColumn();
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsAlterTableWithDropColumn");        
        return _data.supportsAlterTableWithDropColumn();
    }

    public boolean supportsBatchUpdates() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsBatchUpdates");        
        return _data.supportsBatchUpdates();
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCatalogsInDataManipulation");        
        return _data.supportsCatalogsInDataManipulation();
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCatalogsInIndexDefinitions");        
        return _data.supportsCatalogsInIndexDefinitions();
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCatalogsInPrivilegeDefinitions");        
        return _data.supportsCatalogsInPrivilegeDefinitions();
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCatalogsInProcedureCalls");        
        return _data.supportsCatalogsInProcedureCalls();
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCatalogsInTableDefinitions");        
        return _data.supportsCatalogsInTableDefinitions();
    }

    public boolean supportsColumnAliasing() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsColumnAliasing");        
        return _data.supportsColumnAliasing();
    }

    public boolean supportsConvert() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsConvert");        
        return _data.supportsConvert();
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCoreSQLGrammar");        
        return _data.supportsCoreSQLGrammar();
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsCorrelatedSubqueries");        
        return _data.supportsCorrelatedSubqueries();
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions()
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsDataDefinitionAndDataManipulationTransactions");        
        return _data.supportsDataDefinitionAndDataManipulationTransactions();
    }

    public boolean supportsDataManipulationTransactionsOnly()
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsDataManipulationTransactionsOnly");        
        return _data.supportsDataManipulationTransactionsOnly();
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsDifferentTableCorrelationNames");        
        return _data.supportsDifferentTableCorrelationNames();
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsExpressionsInOrderBy");        
        return _data.supportsExpressionsInOrderBy();
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsExtendedSQLGrammar");        
        return _data.supportsExtendedSQLGrammar();
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsFullOuterJoins");        
        return _data.supportsFullOuterJoins();
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsGetGeneratedKeys");        
        return _data.supportsGetGeneratedKeys();
    }

    public boolean supportsGroupBy() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsGroupBy");        
        return _data.supportsGroupBy();
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsGroupByBeyondSelect");        
        return _data.supportsGroupByBeyondSelect();
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsGroupByUnrelated");        
        return _data.supportsGroupByUnrelated();
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsIntegrityEnhancementFacility");        
        return _data.supportsIntegrityEnhancementFacility();
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsLikeEscapeClause");        
        return _data.supportsLikeEscapeClause();
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsLimitedOuterJoins");        
        return _data.supportsLimitedOuterJoins();
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMinimumSQLGrammar");        
        return _data.supportsMinimumSQLGrammar();
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMixedCaseIdentifiers");        
        return _data.supportsMixedCaseIdentifiers();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMixedCaseQuotedIdentifiers");        
        return _data.supportsMixedCaseQuotedIdentifiers();
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMultipleOpenResults");        
        return _data.supportsMultipleOpenResults();
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMultipleResultSets");        
        return _data.supportsMultipleResultSets();
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsMultipleTransactions");        
        return _data.supportsMultipleTransactions();
    }

    public boolean supportsNamedParameters() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsNamedParameters");        
        return _data.supportsNamedParameters();
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsNonNullableColumns");        
        return _data.supportsNonNullableColumns();
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOpenCursorsAcrossCommit");        
        return _data.supportsOpenCursorsAcrossCommit();
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOpenCursorsAcrossRollback");        
        return _data.supportsOpenCursorsAcrossRollback();
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOpenStatementsAcrossCommit");        
        return _data.supportsOpenStatementsAcrossCommit();
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOpenStatementsAcrossRollback");        
        return _data.supportsOpenStatementsAcrossRollback();
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOrderByUnrelated");        
        return _data.supportsOrderByUnrelated();
    }

    public boolean supportsOuterJoins() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsOuterJoins");        
        return _data.supportsOuterJoins();
    }

    public boolean supportsPositionedDelete() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsPositionedDelete");        
        return _data.supportsPositionedDelete();
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsPositionedUpdate");        
        return _data.supportsPositionedUpdate();
    }

    public boolean supportsSavepoints() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSavepoints");        
        return _data.supportsSavepoints();
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSchemasInDataManipulation");        
        return _data.supportsSchemasInDataManipulation();
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSchemasInIndexDefinitions");        
        return _data.supportsSchemasInIndexDefinitions();
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSchemasInPrivilegeDefinitions");        
        return _data.supportsSchemasInPrivilegeDefinitions();
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSchemasInProcedureCalls");        
        return _data.supportsSchemasInProcedureCalls();
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSchemasInTableDefinitions");        
        return _data.supportsSchemasInTableDefinitions();
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSelectForUpdate");        
        return _data.supportsSelectForUpdate();
    }

    public boolean supportsStatementPooling() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsStatementPooling");        
        return _data.supportsStatementPooling();
    }

    public boolean supportsStoredProcedures() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsStoredProcedures");        
        return _data.supportsStoredProcedures();
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSubqueriesInComparisons");        
        return _data.supportsSubqueriesInComparisons();
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSubqueriesInExists");        
        return _data.supportsSubqueriesInExists();
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSubqueriesInIns");        
        return _data.supportsSubqueriesInIns();
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsSubqueriesInQuantifieds");        
        return _data.supportsSubqueriesInQuantifieds();
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsTableCorrelationNames");        
        return _data.supportsTableCorrelationNames();
    }

    public boolean supportsTransactions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsTransactions");        
        return _data.supportsTransactions();
    }

    public boolean supportsUnion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsUnion");        
        return _data.supportsUnion();
    }

    public boolean supportsUnionAll() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsUnionAll");        
        return _data.supportsUnionAll();
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "usesLocalFilePerTable");        
        return _data.usesLocalFilePerTable();
    }

    public boolean usesLocalFiles() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "usesLocalFiles");        
        return _data.usesLocalFiles();
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "deletesAreDetected");        
        return _data.deletesAreDetected(type);
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "insertsAreDetected");        
        return _data.insertsAreDetected(type);
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "othersDeletesAreVisible");        
        return _data.othersDeletesAreVisible(type);
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "othersInsertsAreVisible");        
        return _data.othersInsertsAreVisible(type);
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "othersUpdatesAreVisible");        
        return _data.othersUpdatesAreVisible(type);
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "ownDeletesAreVisible");        
        return _data.ownDeletesAreVisible(type);
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "ownInsertsAreVisible");        
        return _data.ownInsertsAreVisible(type);
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "ownUpdatesAreVisible");        
        return _data.ownUpdatesAreVisible(type);
    }

    public boolean supportsResultSetHoldability(int holdability)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsResultSetHoldability");        
        return _data.supportsResultSetHoldability(holdability);
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsResultSetType");        
        return _data.supportsResultSetType(type);
    }

    public boolean supportsTransactionIsolationLevel(int level)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsTransactionIsolationLevel");        
        return _data.supportsTransactionIsolationLevel(level);
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "updatesAreDetected");        
        return _data.updatesAreDetected(type);
    }

    public boolean supportsConvert(int fromType, int toType)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsConvert");        
        return _data.supportsConvert(fromType, toType);
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "supportsResultSetConcurrency");        
        return _data.supportsResultSetConcurrency(type, concurrency);
    }

    public String getCatalogSeparator() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getCatalogSeparator");        
        return _data.getCatalogSeparator();
    }

    public String getCatalogTerm() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getCatalogTerm");
        return _data.getCatalogTerm();
        
    }

    public String getDatabaseProductName() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDatabaseProductName");
        return _data.getDatabaseProductName();   
    }

    public String getDatabaseProductVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDatabaseProductVersion");
        return _data.getDatabaseProductVersion();
    }

    public String getDriverName() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDriverName");        
        return _data.getDriverName();
    }

    public String getDriverVersion() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getDriverVersion");
        return _data.getDriverVersion();
    }

    public String getExtraNameCharacters() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getExtraNameCharacters");
        return _data.getExtraNameCharacters();
    }

    public String getIdentifierQuoteString() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getIdentifierQuoteString");
        return _data.getIdentifierQuoteString();
    }

    public String getNumericFunctions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getNumericFunctions");
        return _data.getNumericFunctions();
    }

    public String getProcedureTerm() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getProcedureTerm");
        return _data.getProcedureTerm();
    }

    public String getSQLKeywords() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSQLKeywords");
        return _data.getSQLKeywords();
    }

    public String getSchemaTerm() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSchemaTerm");
        return _data.getSchemaTerm();
    }

    public String getSearchStringEscape() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSearchStringEscape");
        return _data.getSearchStringEscape();
    }

    public String getStringFunctions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getStringFunctions");
        return _data.getStringFunctions();
    }

    public String getSystemFunctions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSystemFunctions");
        return _data.getSystemFunctions();
    }

    public String getTimeDateFunctions() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getTimeDateFunctions");
        return _data.getTimeDateFunctions();
    }

    public String getURL() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getURL");
        return _data.getURL();
    }

    public String getUserName() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getUserName");
        return _data.getUserName();
    }

    public Connection getConnection() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getConnection");
        return _data.getConnection();
    }

    public ResultSet getCatalogs() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getCatalogs");
        return _data.getCatalogs();
    }

    public ResultSet getSchemas() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSchemas");
        return _data.getSchemas();
    }

    public ResultSet getTableTypes() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getTableTypes");
        return _data.getTableTypes();
    }

    public ResultSet getTypeInfo() throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getTypeInfo");
        return _data.getTypeInfo();        
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getExportedKeys");
        return _data.getExportedKeys(catalog, schema, table);
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getImportedKeys");
        return _data.getImportedKeys(catalog, schema, table);
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getPrimaryKeys");
        return _data.getPrimaryKeys(catalog, schema, table);
    }

    public ResultSet getProcedures(String catalog, String schemaPattern,
            String procedureNamePattern) throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getProcedures");
        return _data.getProcedures(catalog, schemaPattern, procedureNamePattern);
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern,
            String tableNamePattern) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSuperTables");        
        return _data.getSuperTables(catalog, schemaPattern, tableNamePattern);
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern,
            String typeNamePattern) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getSuperTypes");        
        return _data.getSuperTypes(catalog, schemaPattern, typeNamePattern);
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern,
            String tableNamePattern) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getTablePrivileges");        
        return _data.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
    }

    public ResultSet getVersionColumns(String catalog, String schema,
            String table) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getVersionColumns");        
        return _data.getVersionColumns(catalog, schema, table);
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema,
            String table, int scope, boolean nullable) throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getBestRowIdentifier");
        return _data.getBestRowIdentifier(catalog, schema, table, scope, nullable);
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table,
            boolean unique, boolean approximate) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getIndexInfo");        
        return _data.getIndexInfo(catalog, schema, table, unique, approximate);
    }

    public ResultSet getUDTs(String catalog, String schemaPattern,
            String typeNamePattern, int[] types) throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getUDTs");        
        return _data.getUDTs(catalog, schemaPattern, typeNamePattern, types);
    }

    public ResultSet getAttributes(String catalog, String schemaPattern,
            String typeNamePattern, String attributeNamePattern)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getAttributes");        
        return _data.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
    }

    public ResultSet getColumnPrivileges(String catalog, String schema,
            String table, String columnNamePattern) throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getColumnPrivileges");
        return _data.getColumnPrivileges(catalog, schema, table, columnNamePattern);
        
    }

    public ResultSet getColumns(String catalog, String schemaPattern,
            String tableNamePattern, String columnNamePattern)
            throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getColumns");
        return _data.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern,
            String procedureNamePattern, String columnNamePattern)
            throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getProcedureColumns");
        return _data.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
    }

    public ResultSet getTables(String catalog, String schemaPattern,
            String tableNamePattern, String[] types) throws SQLException {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getTables");
        return _data.getTables(catalog, schemaPattern, tableNamePattern, types);
    }

    public ResultSet getCrossReference(String primaryCatalog,
            String primarySchema, String primaryTable, String foreignCatalog,
            String foreignSchema, String foreignTable) throws SQLException 
    {
        ProxyMethodManager.check("ProxyDatabaseMetaData", "getCrossReference");
        return _data.getCrossReference(primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable);
    }

	/**
	 * @see java.sql.DatabaseMetaData#autoCommitFailureClosesAllResultSets()
	 */
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getClientInfoProperties()
	 */
	public ResultSet getClientInfoProperties() throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getFunctionColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
		String columnNamePattern) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getFunctions(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
		throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getSchemas(java.lang.String, java.lang.String)
	 */
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#supportsStoredFunctionsUsingCallSyntax()
	 */
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
