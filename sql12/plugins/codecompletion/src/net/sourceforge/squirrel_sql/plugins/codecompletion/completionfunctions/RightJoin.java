package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;

import java.util.Hashtable;
import java.util.Vector;


public class RightJoin extends AbstractJoin
{

   public RightJoin(ISession session)
   {
      super(session);
   }

   public String getCompareString()
   {
      return "#r";
   }

   public String getCompletionString()
   {
      return "#r,<table1>,<table2>,...<tableN>,";
   }

   public String toString()
   {
      return getCompletionString() + " right join";
   }

   protected String getJoinClause(String fkName, String table1, String table2, Hashtable colBuffersByFkName)
   {
      return "RIGHT JOIN ";
   }
}
