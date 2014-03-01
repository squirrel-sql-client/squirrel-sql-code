package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;

public class DefaultDataModelImplementationDetails implements IDataModelImplementationDetails
{
   private ISession _session;

   public DefaultDataModelImplementationDetails()
   {
   }

   public DefaultDataModelImplementationDetails(ISession session)
   {
      _session = session;
   }



   @Override
   public String getStatementSeparator()
   {
      if (null == _session)
      {
         return ";";
      }
      else
      {
         return _session.getProperties().getSQLStatementSeparator();
      }
   }
}
