package net.sourceforge.squirrel_sql.client.session.connectionpool;

@FunctionalInterface
public interface SessionConnectionPoolChangeListener
{
   void changed();
}
