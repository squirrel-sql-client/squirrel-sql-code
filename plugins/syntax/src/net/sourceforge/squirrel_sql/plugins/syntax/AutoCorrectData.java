package net.sourceforge.squirrel_sql.plugins.syntax;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;


public class AutoCorrectData
{
   private Hashtable autoCorrects;
   private boolean enableAutoCorrects = true;

   public AutoCorrectData(Hashtable newAutoCorrects, boolean enableAutoCorrects)
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
      for(Enumeration e=autoCorrects.keys(); e.hasMoreElements();)
      {
         String err = (String) e.nextElement();
         ret[i] = new AutoCorrectDataItem(err, (String) autoCorrects.get(err));
         ++i;
      }

      return ret;
   }

   public void setAutoCorrects(AutoCorrectDataItem[] dataItems)
   {
      autoCorrects = new Hashtable();
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

   public Hashtable getAutoCorrectsHash()
   {
      return autoCorrects;
   }
}
