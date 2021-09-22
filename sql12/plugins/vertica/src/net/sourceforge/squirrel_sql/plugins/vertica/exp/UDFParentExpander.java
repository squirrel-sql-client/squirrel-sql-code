/*
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

package net.sourceforge.squirrel_sql.plugins.vertica.exp;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.timeoutproxy.StatementExecutionTimeOutHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the expanding of the "SYSTEM TABLE" node.
 * It will give a list of all the system tables available in the schema.
 */


public class UDFParentExpander implements INodeExpander
{


   /** SQL used to load system table names v1  */


   //	"SELECT FUNCTION_NAME FROM V_CATALOG.USER_FUNCTIONS" +
   //" WHERE SCHEMA_NAME = ? AND FUNCTION_NAME ILIKE ?" +
   // " ORDER BY FUNCTION_NAME";//


   /**
    * SQL used to load system table names v2
    */
   private String SQL =
         "SELECT FUNCTION_NAME FROM V_CATALOG.USER_FUNCTIONS" +
               " WHERE UPPER(PROCEDURE_TYPE) = 'USER DEFINED FUNCTION' and SCHEMA_NAME = ? AND FUNCTION_NAME ILIKE ?" +
               " ORDER BY FUNCTION_NAME";

   /**
    * Default ctor.
    */
   public UDFParentExpander()
   {
      super();
   }

   /**
    * Create the child nodes for the passed parent node and return them. Note
    * that this method should <B>not</B> actually add the child nodes to the
    * parent node as this is taken care of in the caller.
    *
    * @param   session   Current session.
    * @param   node   Node to be expanded.
    * @return A list of <TT>ObjectTreeNode</TT> objects representing the child
    * nodes for the passed node.
    */
   public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
         throws SQLException
   {

      int dbmajorname = session.getSQLConnection().getConnection().getMetaData().getDatabaseMajorVersion();

      if (dbmajorname < 6)
      {
         SQL =
               "SELECT FUNCTION_NAME FROM V_CATALOG.USER_FUNCTIONS" +
                     " WHERE SCHEMA_NAME = ? AND FUNCTION_NAME ILIKE ?" +
                     " ORDER BY FUNCTION_NAME";

      }

      final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();

      final String catalogName = parentNode.getDatabaseObjectInfo().getCatalogName();
      final String schemaName = parentNode.getDatabaseObjectInfo().getSchemaName();

      final ISQLConnection conn = session.getSQLConnection();
      final SQLDatabaseMetaData md = conn.getSQLMetaData();
      //final PreparedStatement pstmt = conn.prepareStatement(SQL);
      final PreparedStatement pstmt = StatementExecutionTimeOutHandler.prepareStatement(conn, SQL);
      final ObjFilterMatcher filterMatcher = new ObjFilterMatcher(session.getProperties());

      ResultSet rs = null;
      try
      {
         pstmt.setString(1, schemaName);
         pstmt.setString(2, filterMatcher.getSqlLikeMatchString());

         rs = pstmt.executeQuery();
         while (rs.next())
         {
            IDatabaseObjectInfo si = new DatabaseObjectInfo(catalogName, schemaName,
                                                            rs.getString(1), DatabaseObjectType.UDF, md);

            childNodes.add(new ObjectTreeNode(session, si));
         }
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
         SQLUtilities.closeStatement(pstmt);
      }
      return childNodes;
   }

}
