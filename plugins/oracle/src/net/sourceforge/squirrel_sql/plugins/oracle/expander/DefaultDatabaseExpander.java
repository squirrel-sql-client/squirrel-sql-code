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

import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
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


   public DefaultDatabaseExpander(ISession session)
   {
      super(session);
   }

   public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
   {
      try
      {
         final List<ObjectTreeNode> childNodes = super.createChildren(session, parentNode);

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
