package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ISession;


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

   @Override
   protected String getJoinClause(String fkName, 
                                  String table1, 
                                  String table2, 
                                  Hashtable<String, Vector<ColBuffer>> colBuffersByFkName)
   {
      return "RIGHT JOIN ";
   }
}
