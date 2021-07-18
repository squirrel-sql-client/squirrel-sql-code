package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import java.io.Serializable;
import java.util.Comparator;

final class DatabaseObjectTypeComparator implements Comparator<DatabaseObjectType>, Serializable
{
   public int compare(DatabaseObjectType o1, DatabaseObjectType o2)
   {
      return o1.getName().compareToIgnoreCase(o2.getName());
   }
}
