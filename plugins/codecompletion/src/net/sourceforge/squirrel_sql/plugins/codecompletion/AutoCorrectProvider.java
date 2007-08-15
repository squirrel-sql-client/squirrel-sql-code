package net.sourceforge.squirrel_sql.plugins.codecompletion;

import java.util.Hashtable;

public interface AutoCorrectProvider
{
   Hashtable<String, String> getAutoCorrects();
}
