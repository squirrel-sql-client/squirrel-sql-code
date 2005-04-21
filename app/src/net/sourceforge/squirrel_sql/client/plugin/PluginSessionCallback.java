package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Any plugin is required to provide an implementation of this callback interface.
 * The callback implementation has to be returned by IPlugin.sesssionStarted. 
 *
 */
public interface PluginSessionCallback
{
   //void sessionInternalFrameOpened(SessionInternalFrame sessionInternalFrame, ISession sess);

   void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess);
   void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess);
}
