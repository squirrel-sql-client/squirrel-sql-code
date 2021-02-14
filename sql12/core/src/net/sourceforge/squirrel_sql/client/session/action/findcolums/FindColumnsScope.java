package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class FindColumnsScope
{
   private IObjectTreeAPI _objectTreeAPI;
   private ISession _session;

   public FindColumnsScope(IObjectTreeAPI objectTreeAPI, ISession session)
   {
      _objectTreeAPI = objectTreeAPI;
      _session = session;

      if(null != _objectTreeAPI)
      {
         _session = _objectTreeAPI.getSession();
      }
   }

   public ISession getSession()
   {
      return _session;
   }
}
