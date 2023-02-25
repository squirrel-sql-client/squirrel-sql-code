package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

@FunctionalInterface
public interface PreferencesFindSupport<T extends DialogFindInfo>
{
   T createFindInfo(boolean ofOpenDialog);
}
