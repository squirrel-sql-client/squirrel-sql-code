/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionDetailsTab;

/**
 * This database expander limits the schemas that are displayed in the object
 * tree to only those that the user has privileges to access.  In the event that
 * the session account has the DBA privilege, all schemas in the database will
 * be shown (DBA privilege connotes access to all database schemas).
 */
public class DefaultDatabaseExpander extends DatabaseExpander
{
   private OraclePlugin _oraclePlugin;


   public DefaultDatabaseExpander(ISession session, OraclePlugin oraclePlugin)
   {
      super(session);
      _oraclePlugin = oraclePlugin;
   }

   protected List createSchemaNodes(ISession session,
                                    SQLDatabaseMetaData md,
                                    String catalogName)
      throws SQLException
   {
      final List childNodes = new ArrayList();
      if (session.getProperties().getLoadSchemasCatalogs())
      {
         final String[] schemas = _oraclePlugin.getAccessibleSchemas(session);
         final String[] schemaPrefixArray =
            session.getProperties().getSchemaPrefixArray();
         for (int i = 0; i < schemas.length; ++i)
         {
            boolean found = (schemaPrefixArray.length > 0) ? false : true;
            for (int j = 0; j < schemaPrefixArray.length; ++j)
            {
               if (schemas[i].startsWith(schemaPrefixArray[j]))
               {
                  found = true;
                  break;
               }
            }
            if (found)
            {
               IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName, null,
                  schemas[i],
                  DatabaseObjectType.SCHEMA, md);
               childNodes.add(new ObjectTreeNode(session, dbo));
            }
         }
      }
      return childNodes;
   }

   public List createChildren(ISession session, ObjectTreeNode parentNode)
   {
      try
      {
         final List childNodes = super.createChildren(session, parentNode);

         final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

         // Users.
         DatabaseObjectInfo dboInfo = new DatabaseObjectInfo(null, null, "USERS",
            IObjectTypes.USER_PARENT, md);
         ObjectTreeNode node = new ObjectTreeNode(session, dboInfo);
         childNodes.add(node);

         if (InstanceDetailsTab.isAccessible(session))
         {
            // Instances.
            dboInfo = new DatabaseObjectInfo(null, null, "INSTANCES",
               IObjectTypes.INSTANCE_PARENT, md);
            node = new ObjectTreeNode(session, dboInfo);
            childNodes.add(node);
         }

         if (SessionDetailsTab.isAccessible(session))
         {
            // Sessions.
            dboInfo = new DatabaseObjectInfo(null, null, "SESSIONS",
               IObjectTypes.SESSION_PARENT, md);
            node = new ObjectTreeNode(session, dboInfo);
            childNodes.add(node);
         }

         return childNodes;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


}
