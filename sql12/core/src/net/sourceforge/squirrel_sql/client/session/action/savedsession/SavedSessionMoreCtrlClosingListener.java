package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;

@FunctionalInterface
public interface SavedSessionMoreCtrlClosingListener
{
   void closed(SavedSessionGrouped savedSessionGrouped, boolean openInNewSession);
}
