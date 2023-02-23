package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import java.util.List;
import java.util.Map;

public enum PreferencesAddressBook
{
   CHANGE_TRACKING_PREFS,
   QUERY_CONNECTION_POOL_PREFS;

   public void jumpTo()
   {
      PrefsFindInfo prefsFindInfo = ComponentInfoByPathUtil.createPrefsFindInfo();

      for( Map.Entry<List<String>, List<PrefComponentInfo>> entry : prefsFindInfo.getComponentInfoByPath().entrySet() )
      {
         for( PrefComponentInfo prefComponentInfo : entry.getValue() )
         {
            if(prefComponentInfo.getComponent() instanceof AddressablePrefComponent )
            {
               if( ((AddressablePrefComponent)prefComponentInfo.getComponent()).getAddress() == this)
               {
                  new GotoHandler().gotoPath(entry.getKey(), false);
                  return;
               }
            }
         }
      }

      throw new IllegalStateException("Failed to find component for address " + this.name());
   }

}
