package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;

import java.util.Hashtable;

public interface AutoCorrectProvider
{
   Hashtable getAutoCorrects();
}
