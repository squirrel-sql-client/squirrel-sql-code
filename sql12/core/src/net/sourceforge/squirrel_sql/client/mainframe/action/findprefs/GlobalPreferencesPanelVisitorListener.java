package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

@FunctionalInterface
public interface GlobalPreferencesPanelVisitorListener
{
   void visitFindableComponent(PrefComponentInfo prefComponentInfo);
}
