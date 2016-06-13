package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NetezzaSpecifics {
	
	private SQLDatabaseMetaData _metadata;
	private PreparedStatement _pstmtSqlSynonymReference = null;
	
	public NetezzaSpecifics(SQLDatabaseMetaData metadata) throws SQLException {
		_metadata = metadata;
		_pstmtSqlSynonymReference = NetezzaSynonym.getSqlSynonymReference(_metadata.getJDBCMetaData().getConnection());
	}

	public NetezzaSynonym returnSynonym(String catalog, String schema, String table) throws SQLException {
		ResultSet rs = null;
		try
		{
			_pstmtSqlSynonymReference.setString(1, table);
			rs = _pstmtSqlSynonymReference.executeQuery();
			if (rs.next()) {
				return new NetezzaSynonym(rs.getString(2), rs.getString(3), rs.getString(4));
			}
		}
		finally
		{
			SQLUtilities.closeResultSet(rs, false);
		}	

		return null;
	}
}
