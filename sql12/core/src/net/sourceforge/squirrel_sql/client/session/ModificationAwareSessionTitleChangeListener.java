package net.sourceforge.squirrel_sql.client.session;

@FunctionalInterface
public interface ModificationAwareSessionTitleChangeListener
{
   void titleChanged(String oldTitle, String newTitle);
}
