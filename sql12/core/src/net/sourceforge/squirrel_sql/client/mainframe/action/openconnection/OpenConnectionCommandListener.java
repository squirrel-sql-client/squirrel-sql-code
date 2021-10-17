package net.sourceforge.squirrel_sql.client.mainframe.action.openconnection;

@FunctionalInterface
public interface OpenConnectionCommandListener
{
   void openConnectionFinished(Throwable t);
}
