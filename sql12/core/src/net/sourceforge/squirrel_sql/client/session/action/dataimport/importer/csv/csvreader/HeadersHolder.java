package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.csv.csvreader;

import java.util.HashMap;

class HeadersHolder
{
   public String[] headers;

   public int length;

   public HashMap<String, Integer> indexByName;

   public HeadersHolder()
   {
      headers = null;
      length = 0;
      indexByName = new HashMap<String, Integer>();
   }
}
