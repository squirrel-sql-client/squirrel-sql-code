package net.sourceforge.squirrel_sql.client.session.action.savedsession;

@FunctionalInterface
public interface SavedSessionMoreCtrlClosingListener
{
   void closed(SavedSessionJsonBean savedSessionJsonBean, boolean openInNewSession);
}
