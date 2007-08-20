package net.sourceforge.squirrel_sql.plugins.oracle.expander;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

/**
 * This class handles the expanding of a Procedure node. 
 * 
 * This class overrides the defauls handeling of the procedure type.
 *
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class ProcedureExpander implements INodeExpander
{
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
   public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
   {
      final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
      final String schemaName = parentDbinfo.getSchemaName();
      return createProcedureNodes(session, null, schemaName);
   }

   private List<ObjectTreeNode> createProcedureNodes(ISession session, String catalogName,
                                     String schemaName)
   {
      final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
      IProcedureInfo[] procs = null;
      String objFilter = session.getProperties().getObjectFilter();
      //procs = md.getProcedures(catalogName, schemaName, objFilter != null && objFilter.length() > 0 ? objFilter :"%");
      String procedureNamePattern = objFilter != null && objFilter.length() > 0 ? objFilter : "%";
      procs = session.getSchemaInfo().getStoredProceduresInfos(catalogName, schemaName, procedureNamePattern);
      for (int i = 0; i < procs.length; ++i)
      {
                  if (procs[i].getProcedureType() == DatabaseMetaData.procedureNoResult)
         childNodes.add(new ObjectTreeNode(session, procs[i]));
      }
      return childNodes;
   }
}
