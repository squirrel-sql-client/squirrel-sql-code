package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ISQLEntryPanelFactoryExt extends ISQLEntryPanelFactory
{
   void sessionEnding(ISession session);
}
