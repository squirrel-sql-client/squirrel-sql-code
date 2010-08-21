package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;


public interface NamespaceCtrlListener
{
   String nameSpaceSelected(ISession session, String nameSpace, String aliasNameTemplate);
}
