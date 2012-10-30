package net.sourceforge.squirrel_sql.plugins.db2.sql;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


/**
 * There are at least three different DB2 platforms (LUW, OS/400, z/OS) that require different SQL for 
 * accessing the data dictionary.  This class provides a place to locate all of this DB2-specific SQL.
 * 
 */
public class DB2SqlImpl implements DB2Sql
{
	
	private static interface i18n
	{
		StringManager s_stringMgr = StringManagerFactory.getStringManager(DB2SqlImpl.class);

		// i18n[ProcedureSourceTab.cLanguageProcMsg=This is a C-language routine. The
		// source code is unavailable.]
		String C_LANGUAGE_PROC_MSG = s_stringMgr.getString("ProcedureSourceTab.cLanguageProcMsg");
	}
	
	private final DB2PlatformType db2Type;
	
	public DB2SqlImpl(String databaseProductName) {
		db2Type = DB2PlatformType.getDB2PlatformTypeByName(databaseProductName);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getUserDefinedFunctionSourceSql()
	 */
	@Override
	public String getUserDefinedFunctionSourceSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result =  	    
			    "select " +
			    "case " +
			    "    when body = 'SQL' and routine_definition is not null then routine_definition " +
			    "    when body = 'SQL' and routine_definition is null then 'no source available' " +
			    "    when body = 'EXTERNAL' and external_name is not null then external_name " +
			    "    when body = 'EXTERNAL' and external_name is null then 'system-generated function' " +
			    "end as definition " +
			    "from QSYS2.SYSFUNCS " +
			    "where routine_schema = ? " +
			    "and routine_name = ? ";	 
			break;
		case LUW:
			result = 
				 "SELECT " +
				    "case " +
				    "    when body is null then 'No source available' " +
				    "    else body " +
				    "end " + 	    
				    "FROM SYSIBM.SYSFUNCTIONS " +
				    "WHERE schema = ? " +
				    "AND name = ? " +
				    "AND implementation is null ";			
		case ZOS:
			result = 
				"select " +
				"case " +
				"when origin = 'E' and external_name is not null then external_name " +
				"when origin = 'S' and external_name is null then 'system-generated " +
				"function' " +
				"else 'no source available' " +
				"end as definition " +
				"from SYSIBM.SYSROUTINES " +
				"where schema = ? " +
				"and name = ? " +
				"and ROUTINETYPE = 'F' ";			
		}
		
		return result;
		
	}
	
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getUserDefinedFunctionDetailsSql()
	 */
	@Override
	public String getUserDefinedFunctionDetailsSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    "select " +
				    "routine_name as name, " +
				    "routine_schema as schema, " +
				    "routine_definer as definer, " +
				    "in_parms as parm_count, " +
				    "case external_action " +
				    "    when 'E' then 'has external side effects' " +
				    "    when 'N' then 'has no external side effects' " +
				    "end as side_effects, " +
				    "fenced, " +
				    "external_language as language, " +
				    "sql_data_access as contains_sql, " +
				    "number_of_results as result_cols, " +
				    "external_name " +
				    "from qsys2.SYSFUNCS " +
				    "where routine_schema = ? " +
				    "and routine_name = ? ";			
			break;
		case LUW:
			result = 
			    "select " +
				    "name, " +
				    "schema, " +
				    "definer, " +
				    "function_id, " +
				    "parm_count, " +
				    "side_effects, " +
				    "fenced, " +
				    "language, " +
				    "contains_sql, " +
				    "result_cols, " +
				    "class, " +
				    "jar_id " +
				    "from sysibm.SYSFUNCTIONS " +
				    "where schema = ? " +
				    "and name = ? ";			
			break;
		case ZOS:
			result = 
				"select " +
					"name, " +
					"schema, " +
					"createdby as definer, " +
					"routineid, " +
					"parm_count, " +
					"case external_action " +
					"when 'E' then 'has external side effects' " +
					"when 'N' then 'has no external side effects' " +
					"else 'ORIGIN is not E or Q' " +
					"end as side_effects, " +
					"fenced, " +
					"language, " +
					"sql_data_access as contains_sql, " +
					"result_cols, " +
					"class, " +
					"jar_id " +
					"from sysibm.SYSROUTINES " +
					"where schema = ? " +
					"and name = ? " +
					"and ROUTINETYPE = 'F' ";			
			break;
		}
		
		return result;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getViewSourceSql()
	 */
	@Override
	public String getViewSourceSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
					"select mqt_definition " + 
					"from qsys2.systables " + 
					"where table_schema = ? " + 
					"and table_name = ? ";
			break;
		case LUW:
			result = 
					"SELECT text " + 
					"FROM SYSCAT.VIEWS " + 
					"WHERE viewschema = ? " +
					"and viewname = ? ";
			break;
		case ZOS:
			result = 
					"SELECT TEXT " +
					"FROM SYSIBM.SYSVIEWS " +
					"WHERE CREATOR = ? " +
					"AND NAME = ? ";
			break;
		}
		
		return result;
	}

	@Override
	public String getTriggerDetailsSql() {
		String result = null;
		
		final String genericTriggerDetailsSql = 
			"SELECT  T1.DEFINER     AS trigger_definer, " +
	           "       T1.trigname  AS trigger_name, " +
	           "       case T1.TRIGTIME " +
	           "         when 'A' then 'AFTER' " +
	           "         when 'B' then 'BEFORE' " +
	           "         when 'I' then 'INSTEAD OF' " +
	           "       end AS trigger_time, " +
	           "       case T1.TRIGEVENT " +
	           "         when 'I' then 'INSERT' " +
	           "         when 'U' then 'UPDATE' " +
	           "         when 'D' then 'DELETE' " +
	           "         when 'S' then 'SELECT' " +
	           "         else T1.TRIGEVENT " +
	           "       end AS triggering_event, " +
	           "       T2.DEFINER     AS table_definer, " +
	           "       T2.TABNAME   AS table_name, " +
	           "       case T2.TYPE " +
	           "         when 'T' then 'TABLE' " +
	           "         when 'V' then 'VIEW' " +
	           "         else T2.TYPE " +
	           "       end AS table_type, " +
	           "       case T1.GRANULARITY " +
	           "         when 'R' then 'ROW' " +
	           "         when 'S' then 'STATEMENT' " +
	           "       else T1.GRANULARITY " +
	           "       end AS granularity, " +
	           "       case T1.VALID " +
	           "         when 'Y' THEN 'VALID' " +
	           "         when 'N' THEN 'INVALID' " +
	           "         when 'X' THEN 'INOPERATIVE' " +
	           "       end AS validity, " +
	           "       T1.REMARKS comment " +
	           "FROM    SYSCAT.TRIGGERS  AS T1, " +
	           "       SYSCAT.TABLES    AS T2 " +
	           "WHERE   T2.TABNAME = T1.TABNAME " +
	           "and T2.TABSCHEMA = T1.TABSCHEMA " +
	           "and T1.TRIGSCHEMA = ? " +
	           "and T1.trigname = ? ";
		
		switch(db2Type) {
		case OS400:
			result = genericTriggerDetailsSql;
			break;
		case LUW:
			result = genericTriggerDetailsSql;
			break;
		case ZOS:
			result =
				"SELECT T1.schema AS trigger_schema, " +
					"T1.name AS trigger_name, " +
					"case T1.TRIGTIME " +
					"when 'A' then 'AFTER' " +
					"when 'B' then 'BEFORE' " +
					"when 'I' then 'INSTEAD OF' " +
					"end AS trigger_time, " +
					"case T1.TRIGEVENT " +
					"when 'I' then 'INSERT' " +
					"when 'U' then 'UPDATE' " +
					"when 'D' then 'DELETE' " +
					"when 'S' then 'SELECT' " +
					"else T1.TRIGEVENT " +
					"end AS triggering_event, " +
					"t1.tbowner AS table_owner, " +
					"T1.TBNAME AS table_name, " +
					"case T2.TYPE " +
					"when 'T' then 'TABLE' " +
					"when 'V' then 'VIEW' " +
					"else T2.TYPE " +
					"end AS table_type, " +
					"case T1.GRANULARITY " +
					"when 'R' then 'ROW' " +
					"when 'S' then 'STATEMENT' " +
					"else T1.GRANULARITY " +
					"end AS granularity, " +
					"T1.REMARKS as comment " +
					"FROM SYSIBM.SYSTRIGGERS T1, " +
					"SYSIBM.SYSTABLES T2 " +
					"WHERE T2.NAME = T1.TBNAME " +
					"and T2.CREATOR = T1.TBOWNER " +
					"and T1.SCHEMA = ? ";
			break;
		}
		
		return result;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getSequenceDetailsSql()
	 */
	@Override
	public String getSequenceDetailsSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    "select sequence_schema, " +
				    "sequence_name, " +
				    "sequence_definer, " +
				    "data_type as type_name, " +
				    "minimum_value as min_value, " +
				    "maximum_value as max_value, " +
				    "increment as increment_by, " +
				    "case cycle_option " +
				    " when 'YES' then 'CYCLE' " +
				    " else 'NOCYCLE' " +
				    "end as cycle_flag, " +
				    "case order " +
				    " when 'YES' then 'ORDERED' " +
				    " else 'UNORDERED' " +
				    "end as order_flag, " +
				    "cache as cache_size, " +
				    "sequence_created as create_time, " +
				    "last_altered_timestamp as last_alter_time, " +
				    "long_comment as comment " +
				    "from qsys2.syssequences " +
				    "where sequence_schema = ? " +
				    "and sequence_name = ?";	    
			break;
		case LUW:
			result = 
		        "SELECT  T1.OWNER     AS sequence_owner, " +
		           "        T1.DEFINER   AS sequence_definer, " +
		           "       T1.SEQNAME   AS sequence_name, " +
		           "       T2.TYPENAME AS data_type, " +
		           "       T1.MINVALUE   AS min_value, " +
		           "       T1.MAXVALUE   AS max_value, " +
		           "       T1.INCREMENT   AS increment_by, " +
		           "       case T1.CYCLE " +
		           "         when 'Y' then 'CYCLE' " +
		           "         else 'NOCYCLE' " +
		           "       end AS cycle_flag, " +
		           "       case T1.ORDER " +
		           "         when 'Y' then 'ORDERED' " +
		           "         else 'UNORDERED' " +
		           "        end AS order_flag, " +
		           "       T1.CACHE AS cache_size, " +
		           "       T1.CREATE_TIME AS create_time, " +
		           "       T1.ALTER_TIME AS last_alter_time, " +
		           "       case T1.ORIGIN " +
		           "         when 'U' then 'User' " +
		           "         when 'S' then 'System' " +
		           "       end AS origin, " +
		           "       T1.REMARKS AS comment " +
		           "FROM    SYSCAT.SEQUENCES AS T1, " +
		           "        SYSCAT.DATATYPES AS T2 " +
		           "WHERE T1.DATATYPEID = T2.TYPEID " +
		           "and T1.SEQSCHEMA = ? " +
		           "and T1.SEQNAME = ? ";
			break;
		case ZOS:
			result = 
				"SELECT T1.schema AS sequence_owner, " +
					"T1.createdby AS sequence_definer, " +
					"T1.NAME AS sequence_name, " +
					"case t1.SOURCETYPEID " +
					"when 0 then char(t1.DATATYPEID) " +
					"else t2.name " +
					"end AS data_type, " +
					"T1.MINVALUE AS min_value, " +
					"T1.MAXVALUE AS max_value, " +
					"T1.INCREMENT AS increment_by, " +
					"case T1.CYCLE " +
					"when 'Y' then 'CYCLE' " +
					"else 'NOCYCLE' " +
					"end AS cycle_flag, " +
					"case T1.ORDER " +
					"when 'Y' then 'ORDERED' " +
					"else 'UNORDERED' " +
					"end AS order_flag, " +
					"T1.CACHE AS cache_size, " +
					"T1.CREATEDTS AS create_time, " +
					"T1.ALTEREDTS AS last_alter_time, " +
					"case T1.SEQTYPE " +
					"when 'I' then 'Identity column' " +
					"when 'S' then 'User defined' " +
					"when 'X' then 'Implicit DOCID for XML data' " +
					"end AS origin, " +
					"T1.REMARKS AS comment " +
					"FROM sysibm.SYSSEQUENCES T1 left outer join sysibm.SYSDATATYPES T2 " +
					"on T1.DATATYPEID = T2.DATATYPEID " +
					"where T1.SEQSCHEMA = ? " +
					"and T1.SEQNAME = ? ";
			break;
		}
		
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getProcedureSourceSql()
	 */
	@Override
	public String getProcedureSourceSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
				 "select routine_definition from qsys2.sysroutines " +
				 "where routine_schema = ? " +
				 "and routine_name = ? ";
			break;
		case LUW:
			result = 
				 "select " +
				 "    case " +
				 "        when language = 'C' then '" +i18n.C_LANGUAGE_PROC_MSG+"' " +
				 "        else text " +
				 "    end as text " +
				 "from SYSCAT.PROCEDURES " +
				 "where PROCSCHEMA = ? " +
				 "and PROCNAME = ? ";
			break;
		case ZOS:
			result = 
				"select text " +
				"from sysibm.SYSROUTINES " +
				"where schema = ? " +
				"and name = ? " +
				"and routinetype = 'P' ";
			break;
		}
		
		return result;
	}	
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getUserDefinedFunctionListSql()
	 */
	@Override
	public String getUserDefinedFunctionListSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    "select routine_name " +
			    "from QSYS2.SYSFUNCS " +
			    "where routine_schema = ? " +
			    "and routine_name like ? ";	
			break;
		case LUW:
			result = 
			    "SELECT name " +
			    "FROM SYSIBM.SYSFUNCTIONS " +
			    "WHERE schema = ? " +
			    "AND name like ? " +
			    "AND implementation is null";
			break;
		case ZOS:
			result = 
				"SELECT NAME " +
				"FROM SYSIBM.SYSROUTINES " +
				"WHERE ROUTINETYPE = 'F' " +
				"AND SCHEMA = ? " +
				"AND NAME like ? ";
			break;
		}
		
		return result;
	}	

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getSequenceListSql()
	 */
	@Override
	public String getSequenceListSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    "select sequence_name " +
			    "from qsys2.syssequences " +
			    "where sequence_schema = ? " +
			    "and sequence_name like ? ";
			break;
		case LUW:
			result = 
		        "select SEQNAME " +
	           "from SYSCAT.SEQUENCES " +
	           "WHERE SEQSCHEMA = ? " +
	           "AND SEQNAME like ? ";
			break;
		case ZOS:
			result = 
	        "select NAME " +
           "from sysibm.SYSSEQUENCES " +
           "WHERE SCHEMA = ? " +
           "AND NAME like ? ";
			break;
		}
		
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getTableIndexListSql()
	 */
	@Override
	public String getTableIndexListSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
		        "select " +
	           "index_name " +
	           "from qsys2.sysindexes " +
	           "where table_schema = ? " +
	           "and table_name = ? ";  
			break;
		case LUW:
			result = 
		        "select INDNAME from SYSCAT.INDEXES " +
	           "where TABSCHEMA = ? " +
	           "and TABNAME = ? ";
			break;
		case ZOS:
			result = 
				"select name " +
				"from sysibm.sysindexes T1 " +
				"where creator = ? " +
				"and TBNAME = ? ";
			break;
		}
		
		return result;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getIndexDetailsSql()
	 */
	@Override
	public String getIndexDetailsSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    	"select index_owner, " +
				    "index_name, " +
				    "index_schema, " +
				    "table_owner, " +
				    "table_name, " +
				    "table_schema, " +
				    "case is_unique " +
				    "    when 'D' then 'No (duplicates are allowed)' " +
				    "    when 'V' then 'Yes (duplicate NULL values are allowed)' " +
				    "    when 'U' then 'Yes' " +
				    "    when 'E' then 'Encoded vector index' " +
				    "end as uniqueness, " +
				    "column_count, " +
				    "system_index_name, " +
				    "system_index_schema, " +
				    "system_table_name, " +
				    "system_table_schema, " +
				    "long_comment, " +
				    "iasp_number, " +
				    "index_text, " +
				    "is_spanning_index " +
				    "from qsys2.sysindexes " +
				    "where table_schema = ? " +
				    "and index_name = ? ";	    
			break;
		case LUW:
			result = 
		        "SELECT T1.IID as index_identifier, " +
		           "       T1.DEFINER AS index_owner, " +
		           "       T1.INDNAME AS index_name, " +
		           "       T2.DEFINER AS table_owner, " +
		           "       T2.TABNAME AS table_name, " +
		           "       T3.TBSPACE AS table_space, " +
		           "       case T1.INDEXTYPE " +
		           "         when 'BLOK' then 'Block Index' " +
		           "         when 'CLUS' then 'Clustering Index' " +
		           "         when 'DIM' then 'Dimension Block Index' " +
		           "         when 'REG' then 'Regular Index' " +
		           "         when 'XPTH' then 'XML Path Index' " +
		           "         when 'XRGN' then 'XML Region Index' " +
		           "         when 'XVIL' then 'Index over XML column (Logical)' " +
		           "         when 'XVIP' then 'Index over XML column (Physical)' " +
		           "       end AS index_type, " +
		           "       case T1.UNIQUERULE " +
		           "         when 'U' then 'UNIQUE' " +
		           "         when 'D' then 'NON-UNIQUE' " +
		           "         when 'I' then 'UNIQUE (Implements PK)' " +
		           "       end AS uniqueness, " +
		           "       T1.NLEAF AS number_of_leaf_pages, " +
		           "       T1.NLEVELS AS number_of_levels, " +
		           "       T1.CREATE_TIME, " +
		           "       T1.STATS_TIME AS last_statistics_update, " +
		           "       case T1.REVERSE_SCANS " +
		           "         when 'Y' then 'Supported' " +
		           "         when 'N' then 'Not Supported' " +
		           "       end AS reverse_scans " +
		           "FROM    SYSCAT.INDEXES   AS T1, " +
		           "        SYSCAT.TABLES    AS T2, " +
		           "        SYSCAT.TABLESPACES as T3 " +
		           "WHERE  T3.TBSPACEID = T1.TBSPACEID " +
		           "and T2.TABNAME = T1.TABNAME " +
		           "and T2.TABSCHEMA = T1.TABSCHEMA " +
		           "AND     T1.TABSCHEMA = ? " +
		           "AND     T1.INDNAME = ? ";
			break;
		case ZOS:
			result = 
					"select t1.CREATOR as index_creator, " +
					"T1.NAME as index_name, " +
					"t1.TBCREATOR as table_creator, " +
					"t1.TBNAME as table_name, " +
					"t1.INDEXSPACE as index_space, " +
					"t1.COLCOUNT as index_columns, " +
					"case T1.INDEXTYPE " +
					"when '2' then 'Type 2 index' " +
					"when ' ' then 'Type 1 index' " +
					"when 'D' then 'Data-partitioned secondary index' " +
					"when 'P' then 'Partitioning index' " +
					"end AS index_type, " +
					"case t1.UNIQUERULE " +
					"when 'P' then 'UNIQUE (Implements PK)' " +
					"when 'U' then 'UNIQUE' " +
					"when 'D' then 'NON-UNIQUE' " +
					"when 'C' then 'UNIQUE (enforces unique constraint)' " +
					"when 'N' then 'UNIQUE WHERE NOT NULL' " +
					"when 'R' then 'UNIQUE (enforces uniqueness of non primary parent key)' " +
					"when 'G' then 'UNIQUE (enforces uniqueness of ROWID GENERATED BY DEFAULT " +
					"column)' " +
					"when 'X' then 'UNIQUE (enforces uniqueness of XML column)' " +
					"end as uniqueness, " +
					"t1.CLUSTERING as clustering, " +
					"t1.CLUSTERED as clustered, " +
					"case t1.CLUSTERRATIO " +
					"when 0 then 'No statistics gathered' " +
					"when -2 then 'Auxiliary table index' " +
					"else char(t1.CLUSTERRATIO) " +
					"end as cluster_ratio, " +
					"case t1.PGSIZE " +
					"when 4096 then '4K' " +
					"else strip(char(t1.PGSIZE))||'K' " +
					"end as pagesize, " +
					"case T1.NLEAF " +
					"when -1 then 'No statistics gathered' " +
					"else char(t1.nleaf) " +
					"end AS number_of_leaf_pages, " +
					"case T1.NLEVELS " +
					"when -1 then 'No statistics gathered' " +
					"else char(t1.nlevels) " +
					"end AS number_of_levels, " +
					"case t1.FULLKEYCARD " +
					"when -1 then 'No statistics gathered' " +
					"else char(t1.FULLKEYCARD) " +
					"end as full_key_cardinality, " +
					"T1.CREATEDTS as create_time, " +
					"T1.STATSTIME AS last_statistics_update, " +
					"t1.REMARKS as comment " +
					"from sysibm.sysindexes T1 " +
					"where creator = ? " +
					"and name = ? ";
			break;
		}
		
		return result;
	}	

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getTableTriggerListSql()
	 */
	@Override
	public String getTableTriggerListSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result =
			  "select trigger_name " +
			  "from qsys2.systriggers " +
			  "where trigger_schema = ? " +
			  "and event_object_table = ? ";
			break;
		case LUW:
			result = 
			  "select TRIGNAME from SYSCAT.TRIGGERS " +
			  "where TABSCHEMA = ? " +
			  "and TABNAME = ? ";
			break;
		case ZOS:
			result = 
				"select NAME " +
				"FROM SYSIBM.SYSTRIGGERS " +
				"where SCHEMA = ? " +
				"and TBOWNER = ? ";
			break;
		}
		
		return result;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql#getTriggerSourceSql()
	 */
	@Override
	public String getTriggerSourceSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = 
			    "select action_statement " +
			    "from qsys2.systriggers " +
			    "where trigger_schema = ? " +
			    "and trigger_name = ? ";
			break;
		case LUW:
			result = 
		        "select TEXT from SYSCAT.TRIGGERS " +
	           "where TABSCHEMA = ? " +
	           "and TRIGNAME = ? ";
			break;
		case ZOS:
			result = 
				"select TEXT " +
				"FROM SYSIBM.SYSTRIGGERS " +
				"where TBOWNER = ? " +
				"and TBNAME = ? ";
			break;
		}
		
		return result;
	}	
		
	public String templateSql() {
		String result = null;
		switch(db2Type) {
		case OS400:
			result = "";
			break;
		case LUW:
			result = "";
			break;
		case ZOS:
			result = "";
			break;
		}
		
		return result;
	}
	
}
