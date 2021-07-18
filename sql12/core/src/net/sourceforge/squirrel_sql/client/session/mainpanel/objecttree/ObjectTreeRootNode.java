package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

final class ObjectTreeRootNode extends ObjectTreeNode
{
   ObjectTreeRootNode(ISession session)
   {
      super(session, createDbo(session));
   }

   private static final IDatabaseObjectInfo createDbo(ISession session)
   {
      return new DatabaseObjectInfo(null, null, session.getAlias().getName(), DatabaseObjectType.SESSION, session.getMetaData());
   }
}
