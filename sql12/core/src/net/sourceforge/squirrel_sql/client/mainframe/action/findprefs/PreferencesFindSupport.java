package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

@FunctionalInterface
public interface PreferencesFindSupport<T>
{
   T createFindInfo(boolean ofOpenDialog);
}
