package net.sourceforge.squirrel_sql.plugins.oracle.expander;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class handles the expanding of the "Instance Parent"
 * node. It will give a list of all the instances.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class InstanceParentExpander implements INodeExpander
{
	/** SQL that retrieves the data. */
	private static String SQL =
		"select instance_number, instance_name, host_name, version,"
			+ " startup_time, status, parallel, thread#, archiver, log_switch_wait,"
			+ " logins, shutdown_pending, database_status, instance_role"
			+ " from sys.v_$instance";

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(InstanceParentExpander.class);

	/** The plugin. */
	private final OraclePlugin _plugin;

	/**
	 * Ctor.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>OraclePlugin</TT> passed.
	 */
	public InstanceParentExpander(OraclePlugin plugin)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("OraclePlugin == null");
		}

		_plugin = plugin;
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note
	 * that this method should <B>not</B> actually add the child nodes to the
	 * parent node as this is taken care of in the caller.
	 * 
	 * @param	session	Current session.
	 * @param	node	Node to be expanded.
	 * 
	 * @return	A list of <TT>ObjectTreeNode</TT> objects representing the child
	 *			nodes for the passed node.
	 */
	public List createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List childNodes = new ArrayList();
		final SQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, null,
								rs.getString(1), IObjectTypes.INSTANCE, md);
//				final Map map = new HashMap();
//				map.put("Instance Number", new Integer(rs.getInt(1)));
//				map.put("Name", rs.getString(2));
//				map.put("Host Name", rs.getString(3));
//				map.put("Version", rs.getString(4));
//				map.put("Startup Time", rs.getDate(5));
//				map.put("Instance Status", rs.getString(6));
//				map.put("Parallel", rs.getString(7));
//				map.put("Thread #", new Integer(rs.getInt(8)));
//				map.put("Archiver", rs.getString(9));
//				map.put("Log Switch Wait", rs.getString(10));
//				map.put("Logins", rs.getString(11));
//				map.put("Shutdown Pending", rs.getString(12));
//				map.put("Database Status", rs.getString(13));
//				map.put("Instance Role", rs.getString(14));
				childNodes.add(new ObjectTreeNode(session, doi));
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}
}
