package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import java.util.ArrayList;
import java.util.List;

public class CatalogsToLoad
{
   public boolean isSpecifyCatalogs()
   {
      return false;
   }

   public List<String> getCatalogs()
   {
      return new ArrayList<>();
   }

   public String[] getCatalogsArr()
   {
      return new String[0];
   }
}
