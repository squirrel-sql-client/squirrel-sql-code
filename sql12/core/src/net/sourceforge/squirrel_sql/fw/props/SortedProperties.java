package net.sourceforge.squirrel_sql.fw.props;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

class SortedProperties extends Properties
{
   public Enumeration keys()
   {
      Enumeration keysEnum = super.keys();
      Vector<String> keyList = new Vector<>();
      while (keysEnum.hasMoreElements())
      {
         keyList.add((String) keysEnum.nextElement());
      }
      Collections.sort(keyList);
      return keyList.elements();
   }

}


