package net.sourceforge.squirrel_sql.plugins.codecompletion;

import java.util.Hashtable;

public interface SyntaxExternalService
{
   Hashtable<String, String> getAutoCorrects();

   void applyDarkTheme();

   void applyDefaultTheme();

}
