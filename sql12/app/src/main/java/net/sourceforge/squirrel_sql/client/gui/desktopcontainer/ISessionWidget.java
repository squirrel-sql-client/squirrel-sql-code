package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ISessionWidget extends IWidget
{
   public ISession getSession();

   void closeFrame(boolean b);

   boolean hasSQLPanelAPI();
}
