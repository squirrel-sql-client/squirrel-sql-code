package net.sourceforge.squirrel_sql.plugins.syntax;

import java.util.Enumeration;
import java.util.Hashtable;


public class AutoCorrectData
{
   private Hashtable<String, String> autoCorrects;
   private boolean enableAutoCorrects = true;

   public AutoCorrectData(Hashtable<String, String> newAutoCorrects, 
                          boolean enableAutoCorrects)
   {
      autoCorrects = newAutoCorrects;
      this.enableAutoCorrects = enableAutoCorrects;
   }


   /**
    * Just for xml serialization
    */
   public AutoCorrectData()
   {
   }

   public AutoCorrectDataItem[] getAutoCorrects()
   {
      AutoCorrectDataItem[] ret = new AutoCorrectDataItem[autoCorrects.size()];

      int i=0;
      for(Enumeration<String> e=autoCorrects.keys(); e.hasMoreElements();)
      {
         String err = e.nextElement();
         ret[i] = new AutoCorrectDataItem(err, autoCorrects.get(err));
         ++i;
      }

      return ret;
   }

   public void setAutoCorrects(AutoCorrectDataItem[] dataItems)
   {
      autoCorrects = new Hashtable<String, String>();
      for (int i = 0; i < dataItems.length; i++)
      {
         autoCorrects.put(dataItems[i].getErr(), dataItems[i].getCorr());
      }
   }

   public boolean isEnableAutoCorrects()
   {
      return enableAutoCorrects;
   }

   public void setEnableAutoCorrects(boolean enableAutoCorrects)
   {
      this.enableAutoCorrects = enableAutoCorrects;
   }

   public Hashtable<String, String> getAutoCorrectsHash()
   {
      return autoCorrects;
   }
}
