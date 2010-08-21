package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ISession;


public class LeftJoin extends AbstractJoin
{

   public LeftJoin(ISession session)
   {
      super(session);
   }

   public String getCompareString()
   {
      return "#l";
   }

   public String getCompletionString()
   {
      return "#l,<table1>,<table2>,...<tableN>,";
   }

   public String toString()
   {
      return getCompletionString() + " left join";
   }

   @Override
   protected String getJoinClause(String fkName, 
                                  String table1, 
                                  String table2, 
                                  Hashtable<String, Vector<ColBuffer>> colBuffersByFkName)
   {
      return "LEFT JOIN ";
   }
}
