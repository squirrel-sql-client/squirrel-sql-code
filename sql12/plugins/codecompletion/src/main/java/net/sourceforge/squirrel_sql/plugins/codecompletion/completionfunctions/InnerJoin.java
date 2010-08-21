package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ISession;


public class InnerJoin extends AbstractJoin
{

   public InnerJoin(ISession session)
   {
      super(session);
   }

   public String getCompareString()
   {
      return "#i";
   }

   public String getCompletionString()
   {
      return "#i,<table1>,<table2>,...<tableN>,";
   }

   public String toString()
   {
      return getCompletionString() + " inner join";
   }

   @Override
   protected String getJoinClause(String fkName, 
                                  String table1, 
                                  String table2, 
                                  Hashtable<String, Vector<ColBuffer>> colBuffersByFkName)
   {
      return "INNER JOIN ";
   }
}
