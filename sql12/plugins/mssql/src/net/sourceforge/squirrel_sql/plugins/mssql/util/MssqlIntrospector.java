package net.sourceforge.squirrel_sql.plugins.mssql.util;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import static net.sourceforge.squirrel_sql.fw.sql.SQLUtilities.closeResultSet;
import static net.sourceforge.squirrel_sql.fw.sql.SQLUtilities.closeStatement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.CheckConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.DefaultConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.ForeignKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.PrimaryKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.TableConstraints;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFile;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFileInfo;

public class MssqlIntrospector
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(MssqlIntrospector.class);

	public final static int MSSQL_TABLE = 1;

	public final static int MSSQL_VIEW = 2;

	public final static int MSSQL_STOREDPROCEDURE = 3;

	public final static int MSSQL_UDF = 4;

	public final static int MSSQL_UDT = 5;

	public final static int MSSQL_RULE = 6;

	public final static int MSSQL_DEFAULT = 7;

	public final static int MSSQL_UNKNOWN = -1;

	public static TableConstraints getTableConstraints(IDatabaseObjectInfo oi, ISQLConnection conn)
		throws java.sql.SQLException
	{
		TableConstraints constraints = new TableConstraints();

		Connection c = conn.getConnection();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helpconstraint ?, ? }");
			stmt.setString(1, oi.getSimpleName());
			stmt.setString(2, "nomsg");

			rs = stmt.executeQuery();

			while (rs.next())
			{
				String constraintType = rs.getString(1);
				String constraintName = rs.getString(2);
				// String deleteAction = rs.getString(3);
				// String updateAction = rs.getString(4);
				// String statusEnabled = rs.getString(5);
				// String statusForReplication = rs.getString(6);
				String constraintKeys = rs.getString(7);

				if (constraintType.startsWith("DEFAULT"))
				{
					DefaultConstraint def = new DefaultConstraint();
					String col = constraintType.substring(18).trim(); // chop off "DEFAULT on column ";

					def.setConstraintName(constraintName);
					def.addConstraintColumn(col);
					def.setDefaultExpression(constraintKeys);

					constraints.addConstraint(def);
				}
				else if (constraintType.startsWith("CHECK"))
				{
					CheckConstraint check = new CheckConstraint();
					String col = constraintType.substring(16).trim(); // chop off "CHECK on column ";

					check.setConstraintName(constraintName);
					check.addConstraintColumn(col);
					check.setCheckExpression(constraintKeys);

					constraints.addConstraint(check);
				}
				else if (constraintType.startsWith("FOREIGN KEY"))
				{
					/* NOTE: there are two rows.
					 * NOTE: MssqlConstraint holds the columns in the table participating in the key.
					 * NOTE: ForeignKeyConstraint holds the columns in the referenced table IN THE SAME ORDER.
					 */
					ForeignKeyConstraint fk = new ForeignKeyConstraint();

					fk.setConstraintName(constraintName);

					String foreignColumns[] = constraintKeys.split(", ");
					for (int i = 0; i < foreignColumns.length; i++)
						fk.addConstraintColumn(foreignColumns[i]);

					rs.next();

					constraintKeys = rs.getString(7);
					// constraintKeys looks like this --> `REFERENCES pubs.dbo.foo (fooid, quuxid)'
					constraintKeys = constraintKeys.substring(11); // chop off "REFERENCES "
					String[] tableAndColumns = constraintKeys.split(" ", 2);
					// now tableAndColumns[0] contains the table name and tableAndColumns[1] contains
					// the bracketed list of columns.
					fk.setReferencedTable(tableAndColumns[0]);
					String primaryColumns[] =
						tableAndColumns[1].substring(1, tableAndColumns[1].length() - 2).split(",");
					for (int i = 0; i < primaryColumns.length; i++)
						fk.addPrimaryColumn(primaryColumns[i]);

					constraints.addConstraint(fk);
				}
				else if (constraintType.startsWith("PRIMARY KEY"))
				{
					PrimaryKeyConstraint pk = new PrimaryKeyConstraint();

					pk.setConstraintName(constraintName);
					pk.setClustered(constraintType.endsWith("(clustered)"));

					String cols[] = constraintKeys.split(", ");
					for (int i = 0; i < cols.length; i++)
						pk.addConstraintColumn(cols[i]);

					constraints.addConstraint(pk);
				}
			}

		}
		catch (java.sql.SQLException ex)
		{
			s_log.error("getTableConstraints: Unexpected exception - " + ex.getMessage(), ex);
			// probably just no results -- return it empty.
			return constraints;
		}
		finally
		{
			SQLUtilities.closeStatement(stmt);
			SQLUtilities.closeResultSet(rs);
		}

		return constraints;
	}

	public static DatabaseFileInfo getDatabaseFileInfo(String catalogName, ISQLConnection conn)
		throws java.sql.SQLException
	{
		DatabaseFileInfo dbInfo = new DatabaseFileInfo();

		Connection c = conn.getConnection();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helpdb ? }");
			stmt.setString(1, catalogName);

			if (!stmt.execute()) return null;
			rs = stmt.getResultSet();
			rs.next();

			dbInfo.setDatabaseName(rs.getString(1));
			dbInfo.setDatabaseSize(rs.getString(2));
			dbInfo.setOwner(rs.getString(3));
			dbInfo.setCreatedDate(rs.getString(5));
			String[] options = rs.getString(6).split(", ");
			dbInfo.setCompatibilityLevel(rs.getShort(7));

			// dbStatus -> `Status=ONLINE, Updateability=READ_WRITE, UserAccess=MULTI_USER, Recovery=SIMPLE,
			// Version=539, Collation=SQL_Latin1_General_CP1_CI_AS, SQLSortOrder=52, IsTornPageDetectionEnabled,
			// IsAutoCreateStatistic'
			for (int i = 0; i < options.length; i++)
			{
				if (options[i].indexOf('=') != -1)
				{
					String parts[] = options[i].split("=");
					dbInfo.setOption(parts[0], parts[1]);
				}
				else dbInfo.setOption(options[i], "1");
			}

			if (!stmt.getMoreResults()) return dbInfo;
		}
		catch (Exception e)
		{
			s_log.error("getDatabaseFileInfo(1): Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			closeResultSet(rs);
			closeStatement(stmt);
		}

		try
		{
			rs = stmt.getResultSet();

			while (rs.next())
			{
				String name = rs.getString(1).trim();
				short id = rs.getShort(2);
				String filename = rs.getString(3).trim();
				String filegroup = rs.getString(4);
				String size = rs.getString(5);
				String maxSize = rs.getString(6);
				String growth = rs.getString(7);
				String usage = rs.getString(8);

				DatabaseFile file = new DatabaseFile();
				file.setName(name);
				file.setId(id);
				file.setFilename(filename);
				file.setFilegroup(filegroup);
				file.setSize(size);
				file.setMaxSize(maxSize);
				file.setGrowth(growth);
				file.setUsage(usage);

				if (filegroup == null) dbInfo.addLogFile(file);
				else dbInfo.addDataFile(file);
			}
		}
		catch (Exception e)
		{
			s_log.error("getDatabaseFileInfo(2): Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			closeResultSet(rs);
		}

		return dbInfo;
	}

	public static int getObjectInfoType(IDatabaseObjectInfo oi)
	{
		if (oi instanceof ITableInfo)
		{
			String tableType = ((ITableInfo) oi).getType();
			if (tableType.equals("TABLE")) return MSSQL_TABLE;
			else if (tableType.equals("VIEW")) return MSSQL_VIEW;
			else return MSSQL_UNKNOWN;
		}
		else if (oi instanceof IProcedureInfo)
		{
			/* i do believe the getSimpleName() will end in ;1 if it's a procedure 
			 * and ;0 if it's a UDF. */
			String simpleName = oi.getSimpleName();
			if (simpleName.endsWith(";0")) return MSSQL_UDF;
			else if (simpleName.endsWith(";1")) return MSSQL_STOREDPROCEDURE;
			else return MSSQL_UNKNOWN;
		}
		else if (oi instanceof IUDTInfo)
		{
			return MSSQL_UDT;
		}
		else return MSSQL_UNKNOWN;
	}

	public static String generateCreateScript(IDatabaseObjectInfo oi, ISQLConnection conn,
		boolean withConstraints) throws java.sql.SQLException
	{
		StringBuilder buf = new StringBuilder();

		if (getObjectInfoType(oi) == MSSQL_TABLE) buf.append(MssqlIntrospector.generateCreateTableScript(oi,
			conn, withConstraints));
		else
		{
			Connection c = conn.getConnection();
			buf.append(getHelpTextForObject(MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()), c));
		}

		buf.append("GO\n\n");
		return buf.toString();
	}

	public static String getHelpTextForObject(String objectName, Connection c) throws java.sql.SQLException
	{
		StringBuilder buf = new StringBuilder();

		CallableStatement stmt = null;
		ResultSet helpText = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helptext (?) }");
			stmt.setString(1, objectName);
			helpText = stmt.executeQuery();
			while (helpText.next())
			{
				buf.append(helpText.getString(1));
			}
		}
		catch (Exception e)
		{
			s_log.error("getHelpTextForObject: Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			closeResultSet(helpText);
			closeStatement(stmt);
		}

		return buf.toString();
	}

	public static String generateCreateDatabaseScript(String catalogName, ISQLConnection conn)
		throws java.sql.SQLException
	{
		StringBuilder buf = new StringBuilder();

		DatabaseFileInfo dbInfo = MssqlIntrospector.getDatabaseFileInfo(catalogName, conn);
		Object[] dataFiles = dbInfo.getDataFiles();
		Object[] logFiles = dbInfo.getLogFiles();

		buf.append("CREATE DATABASE [");
		buf.append(dbInfo.getDatabaseName());
		buf.append("]\nON ");

		String lastFilegroup = "";
		for (int i = 0; i < dataFiles.length; i++)
		{
			DatabaseFile file = (DatabaseFile) dataFiles[i];

			String thisFilegroup = file.getFilegroup();
			if (!thisFilegroup.equals(lastFilegroup))
			{
				// if it's PRIMARY, just write it without the FILEGROUP prefix.
				if (thisFilegroup.equals("PRIMARY")) buf.append("PRIMARY");
				else
				{
					buf.append("FILEGROUP ");
					buf.append(thisFilegroup);
				}
				buf.append("\n");
				lastFilegroup = thisFilegroup;
			}

			buf.append("( NAME = ");
			buf.append(file.getName());
			buf.append(",\n\tFILENAME = '");
			buf.append(file.getFilename());
			buf.append("',\n\tSIZE = ");
			buf.append(file.getSize());
			if (!file.getMaxSize().equals("Unlimited"))
			{
				buf.append(",\n\tMAXSIZE = ");
				buf.append(file.getMaxSize());
			}
			buf.append(",\n\tFILEGROWTH = ");
			buf.append(file.getGrowth());
			buf.append(" )");

			if (i < dataFiles.length - 1) buf.append(",");
			buf.append("\n");
		}

		buf.append("LOG ON\n");
		for (int i = 0; i < logFiles.length; i++)
		{
			DatabaseFile file = (DatabaseFile) logFiles[i];

			buf.append("( NAME = ");
			buf.append(file.getName());
			buf.append(",\n\tFILENAME = '");
			buf.append(file.getFilename());
			buf.append("',\n\tSIZE = ");
			buf.append(file.getSize());
			if (!file.getMaxSize().equals("Unlimited"))
			{
				buf.append(",\n\tMAXSIZE = ");
				buf.append(file.getMaxSize());
			}
			buf.append(",\n\tFILEGROWTH = ");
			buf.append(file.getGrowth());
			buf.append(" )");

			if (i < logFiles.length - 1) buf.append(",");

			buf.append("\n");
		}

		buf.append("GO\n\n");

		return buf.toString();
	}

	public static String generateCreateIndexesScript(IDatabaseObjectInfo oi, ISQLConnection conn)
		throws java.sql.SQLException
	{
		Connection c = conn.getConnection();

		StringBuilder buf = new StringBuilder();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helpindex ? }");
			stmt.setString(1, oi.getSimpleName());
			rs = stmt.executeQuery();
		}
		catch (java.sql.SQLException e)
		{
			s_log.error("getTableConstraints: Unexpected exception - " + e.getMessage(), e);
			// no indexes, i guess.
			return "";
		}
		finally
		{
			closeResultSet(rs);
			closeStatement(stmt);
		}

		while (rs.next())
		{
			String indexName = rs.getString(1);
			// `clustered, unique, primary key located on PRIMARY'
			String[] info = rs.getString(2).split(" located on ");
			String[] keys = rs.getString(3).split(", ");
			String[] attribs = info[0].split(", ");
			boolean isUnique = false;
			boolean isClustered = false;
			for (int i = 0; i < attribs.length; i++)
			{
				if (attribs[i].equals("clustered")) isClustered = true;
				else if (attribs[i].equals("unique")) isUnique = true;
			}

			buf.append("CREATE ");
			if (isUnique) buf.append("UNIQUE ");
			buf.append(isClustered ? "CLUSTERED " : "NONCLUSTERED ");
			buf.append("INDEX [");
			buf.append(indexName);
			buf.append("]\n\tON [");
			buf.append(oi.getSimpleName());
			buf.append("] (");
			for (int i = 0; i < keys.length; i++)
			{
				boolean isDesc = false;
				String keyName = keys[i];
				if (keyName.endsWith("(-)"))
				{
					isDesc = true;
					keyName = keyName.substring(0, keyName.length() - 3);
				}
				buf.append(keyName);
				if (isDesc) buf.append(" DESC");
				if (i < keys.length - 1) buf.append(", ");
			}
			buf.append(")\n\tON [");
			buf.append(info[1]);
			buf.append("]\nGO\n\n");
		}

		return buf.toString();
	}

	public static String generateCreateTriggersScript(IDatabaseObjectInfo oi, ISQLConnection conn)
		throws java.sql.SQLException
	{
		Connection c = conn.getConnection();

		StringBuilder buf = new StringBuilder();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helptrigger ? }");
			stmt.setString(1, oi.getSimpleName());
			rs = stmt.executeQuery();
		}
		catch (java.sql.SQLException e)
		{
			s_log.error("generateCreateTriggersScript: Unexpected exception - " + e.getMessage(), e);
			// no triggers, i guess.
			return "";
		}
		finally
		{
			closeResultSet(rs);
			closeStatement(stmt);
		}

		while (rs.next())
		{
			String triggerName = rs.getString(1);
			buf.append(MssqlIntrospector.getHelpTextForObject(triggerName, c));
			buf.append("\nGO\n\n");
		}

		return buf.toString();
	}

	public static String generatePermissionsScript(IDatabaseObjectInfo oi, ISQLConnection conn)
		throws java.sql.SQLException
	{
		Connection c = conn.getConnection();

		StringBuilder buf = new StringBuilder();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helprotect ? }");
			stmt.setString(1, MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()));
			rs = stmt.executeQuery();
		}
		catch (java.sql.SQLException e)
		{
			s_log.error("generatePermissionsScript: Unexpected exception - " + e.getMessage(), e);
			// no permissions, i guess.
			return "";
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}

		while (rs.next())
		{
			/*
			Owner  Object                 Grantee            Grantor ProtectType Action Column             
			------ ---------------------- ------------------ ------- ----------- ------ ------------------ 
			dbo    billing_bak            public             dbo     Grant       Delete .
			dbo    billing_bak            public             dbo     Grant       Insert .
			dbo    billing_bak            public             dbo     Grant       Select (All+New)
			dbo    billing_bak            public             dbo     Grant       Update (All+New)
			dbo    billing_bak            usbilling          dbo     Deny        Insert .
			*/
			// String owner = rs.getString(1);
			String grantee = rs.getString(3);
			// String grantor = rs.getString(4);
			String protectType = rs.getString(5).trim();
			String action = rs.getString(6);
			// String column = rs.getString(7);

			/*
			GRANT 
			    { ALL [ PRIVILEGES ] | permission [ ,...n ] } 
			    { 
			        [ ( column [ ,...n ] ) ] ON { table | view } 
			        | ON { table | view } [ ( column [ ,...n ] ) ] 
			        | ON { stored_procedure | extended_procedure } 
			        | ON { user_defined_function }
			    } 
			TO security_account [ ,...n ] 
			[ WITH GRANT OPTION ] 
			[ AS { group | role } ] 
			
			REVOKE [ GRANT OPTION FOR ] 
			    { ALL [ PRIVILEGES ] | permission [ ,...n ] } 
			    { 
			        [ ( column [ ,...n ] ) ] ON { table | view } 
			        | ON { table | view } [ ( column [ ,...n ] ) ] 
			        | ON { stored_procedure | extended_procedure } 
			        | ON { user_defined_function } 
			    } 
			{ TO | FROM } 
			    security_account [ ,...n ] 
			[ CASCADE ] 
			[ AS { group | role } ] 
			 */

			if (protectType.equals("Grant")) buf.append("GRANT ");
			else if (protectType.equals("Deny")) buf.append("REVOKE ");
			buf.append(action.toUpperCase());
			buf.append(" ON [");
			buf.append(MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()));
			buf.append("] ");
			if (protectType.equals("Grant")) buf.append("TO ");
			else if (protectType.equals("Deny")) buf.append("FROM ");
			buf.append(grantee);

			buf.append("\nGO\n\n");
		}

		return buf.toString();
	}

	protected static String generateCreateTableScript(IDatabaseObjectInfo oi, ISQLConnection conn,
		boolean withConstraints) throws java.sql.SQLException
	{
		Connection c = conn.getConnection();

		StringBuilder buf = new StringBuilder();

		TableConstraints constraints = MssqlIntrospector.getTableConstraints(oi, conn);

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_help ? }");
			stmt.setString(1, oi.getSimpleName());

			if (!stmt.execute()) return null;

			/* since .execute() returned true, the first result is a ResultSet. */
			rs = stmt.getResultSet();
			/* Name     Owner       Type        Created_datetime                                       
			 * ---------------------------------------------------------
			 * billing  dbo         user table  2004-03-08 10:41:05.030
			 */
			if (!rs.next()) return null;
			buf.append("CREATE TABLE [");
			buf.append(rs.getString(2));
			buf.append("].[");
			buf.append(rs.getString(1));
			buf.append("] (");
			buf.append("\n");

			if (!stmt.getMoreResults()) return null;
			rs = stmt.getResultSet();
			/* Column_name          Type    Computed    Length  Prec    Scale   Nullable    TrimTrailingBlanks  FixedLenNullInSource    Collation
			 * -------------------------------------------------------------------------------------------------------------------------------------------------------
			 * Location             char    no          2                       yes         no                  yes                     Latin1_General_CI_AS
			 * TotalBilledAmnt      money   no          8       19      4       yes         (n/a)               (n/a)                   NULL
			 */
			while (rs.next())
			{
				String colName = rs.getString(1);
				String colType = rs.getString(2);
				buf.append("\t[");
				buf.append(colName);
				buf.append("] [");
				buf.append(colType);
				buf.append("] ");
				if (colType.equals("char") || colType.equals("varchar"))
				{
					buf.append("(");
					buf.append(rs.getInt(4)); // length
					buf.append(") COLLATE ");
					buf.append(rs.getString(10)); // collation
					buf.append(" ");
				}
				if (rs.getString(7).equals("yes")) buf.append("NULL ");
				else buf.append("NOT NULL ");

				if (withConstraints)
				{
					List<DefaultConstraint> defs = constraints.getDefaultsForColumn(colName);
					/* there can be only one default in truth, but the model allows more than one. */

					if (defs != null && defs.size() == 1)
					{
						DefaultConstraint def = defs.get(0);
						buf.append("CONSTRAINT [");
						buf.append(def.getConstraintName());
						buf.append("] DEFAULT ");
						buf.append(def.getDefaultExpression());
						buf.append(" ");
					}
				}

				buf.append(",\n");
			}
		}
		catch (SQLException e)
		{
			s_log.error("generateCreateTableScript: Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			closeResultSet(rs);
			closeStatement(stmt);
		}
		if (withConstraints)
		{
			/* there can be only one PK in truth, but the model allows more than one. */
			List<PrimaryKeyConstraint> pks = constraints.getPrimaryKeyConstraints();
			if (pks != null && pks.size() == 1)
			{
				PrimaryKeyConstraint pk = pks.get(0);
				buf.append("\tCONSTRAINT [");
				buf.append(pk.getConstraintName());
				buf.append("] PRIMARY KEY ");
				buf.append(pk.isClustered() ? "CLUSTERED" : "NONCLUSTERED");
				buf.append("\n\t(\n\t\t");
				Object[] cols = pk.getConstraintColumns();
				for (int i = 0; i < cols.length; i++)
				{
					buf.append("[");
					buf.append((String) cols[i]);
					buf.append("]");
					if (i < cols.length - 1) buf.append(", ");
				}
				buf.append("\n\t)\n");
				/* TODO: FILLFACTOR, ON [PRIMARY], etc. */
			}

			List<ForeignKeyConstraint> fks = constraints.getForeignKeyConstraints();
			for (int i = 0; i < fks.size(); i++)
			{
				ForeignKeyConstraint fk = fks.get(i);
				buf.append("\tFOREIGN KEY\n\t(\n\t\t");
				Object[] foreignColumns = fk.getConstraintColumns();
				for (int j = 0; j < foreignColumns.length; j++)
				{
					buf.append("[");
					buf.append((String) foreignColumns[j]);
					buf.append("]");
					if (j < foreignColumns.length - 1) buf.append(", ");
				}
				buf.append("\n\t) REFERENCES [");
				buf.append(fk.getReferencedTable());
				buf.append("] (\n\t\t");
				Object[] primaryColumns = fk.getPrimaryColumns();
				for (int j = 0; j < primaryColumns.length; j++)
				{
					buf.append("[");
					buf.append((String) primaryColumns[j]);
					buf.append("]");
					if (j < primaryColumns.length - 1) buf.append(",\n");
				}
				buf.append("\n\t),");
			}

			for (CheckConstraint check : constraints.getCheckConstraints())
			{
				buf.append("\tCONSTRAINT [");
				buf.append(check.getConstraintName());
				buf.append("] CHECK ");
				buf.append(check.getCheckExpression());
				buf.append(",\n");
			}
		}

		buf.append(")\n");
		/* TODO: ON [PRIMARY] */

		return buf.toString();
	}

	public static String generateUsersAndRolesScript(String catalogName, ISQLConnection conn)
		throws java.sql.SQLException
	{
		StringBuilder buf = new StringBuilder();

		Connection c = conn.getConnection();

		CallableStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helpuser }");
			rs = stmt.executeQuery();
			while (rs.next())
			{
				String userName = rs.getString(1);
				String loginName = rs.getString(3);

				if (userName.equals("dbo")) continue;

				buf.append("if not exists (select * from dbo.sysusers where name = N'");
				buf.append(userName);
				buf.append("' and uid < 16382)\n\tEXEC sp_grantdbaccess N'");
				buf.append(loginName);
				buf.append("', N'");
				buf.append(userName);
				buf.append("'\nGO\n\n");
			}

		}
		catch (SQLException e)
		{
			s_log.error("generateUsersAndRolesScript(1): Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}

		CallableStatement userStmt = null;
		ResultSet userRs = null;

		try
		{
			stmt = c.prepareCall("{ call sp_helprole }");
			rs = stmt.executeQuery();

			while (rs.next())
			{
				String roleName = rs.getString(1);
				short roleId = rs.getShort(2);

				if (roleId < 16400) continue;

				buf.append("if not exists (select * from dbo.sysusers where name = N'");
				buf.append(roleName);
				buf.append("' and uid > 16399)\n\tEXEC sp_addrole N'");
				buf.append(roleName);
				buf.append("'\nGO\n\n");

				/* add users to the role. */
				userStmt = c.prepareCall("{ call sp_helprolemember ? }");
				userStmt.setString(1, roleName);
				userRs = userStmt.executeQuery();

				while (userRs.next())
				{
					String userInRole = userRs.getString(2);
					buf.append("exec sp_addrolemember N'");
					buf.append(roleName);
					buf.append("', N'");
					buf.append(userInRole);
					buf.append("'\nGO\n\n");
				}
			}
		}
		catch (SQLException e)
		{
			s_log.error("generateUsersAndRolesScript(2): Unexpected exception - " + e.getMessage(), e);
		}
		finally
		{
			closeResultSet(rs);
			closeResultSet(userRs);
			closeStatement(stmt);
			closeStatement(userStmt);
		}

		return buf.toString();
	}

	public static String generateDropScript(IDatabaseObjectInfo oi)
	{
		StringBuilder buf = new StringBuilder();
		String useThisName;
		int objectType = MssqlIntrospector.getObjectInfoType(oi);
		// stored procedures and functions have that dangling ;version thing.
		if (objectType == MSSQL_STOREDPROCEDURE || objectType == MSSQL_UDF) useThisName =
			oi.getSimpleName().split(";")[0];
		else useThisName = oi.getSimpleName();

		buf.append("IF EXISTS ( SELECT * FROM sysobjects WHERE id = OBJECT_ID('");
		buf.append(oi.getSchemaName());
		buf.append(".");
		buf.append(useThisName);
		buf.append("') )\n\tDROP ");
		switch (objectType)
		{
		case MssqlIntrospector.MSSQL_TABLE:
			buf.append("TABLE");
			break;
		case MssqlIntrospector.MSSQL_VIEW:
			buf.append("VIEW");
			break;
		case MssqlIntrospector.MSSQL_UDF:
			buf.append("FUNCTION");
			break;
		case MssqlIntrospector.MSSQL_STOREDPROCEDURE:
			buf.append("PROCEDURE");
			break;
		}
		buf.append(" ");
		buf.append(useThisName);
		buf.append("\nGO\n\n");

		return buf.toString();
	}

	public static String getFixedVersionedObjectName(String objectName)
	{
		String[] parts = objectName.split(";");
		return parts[0];
	}

	public static String formatDataType(String dataType, short dataLength, int dataPrec, int dataScale)
	{
		StringBuilder buf = new StringBuilder();

		if (dataType.endsWith("char"))
		{
			buf.append(dataType);
			buf.append("(");
			buf.append(dataLength);
			buf.append(")");
		}
		else if (dataType.equals("float"))
		{
			buf.append(dataType);
			buf.append("(");
			buf.append(dataPrec);
			buf.append(")");
		}
		else if (dataType.equals("decimal") || dataType.equals("numeric"))
		{
			buf.append(dataType);
			buf.append("(");
			buf.append(dataPrec);
			buf.append(",");
			buf.append(dataScale);
			buf.append(")");
		}
		else buf.append(dataType);

		return buf.toString();
	}

}
