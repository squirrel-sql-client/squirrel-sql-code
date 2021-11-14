package net.sourceforge.squirrel_sql.client.preferences.themes;

import java.util.Hashtable;

public interface SyntaxExternalService
{
   Hashtable<String, String> getAutoCorrects();

   void applyDarkTheme();

   void applyDefaultTheme();
}
