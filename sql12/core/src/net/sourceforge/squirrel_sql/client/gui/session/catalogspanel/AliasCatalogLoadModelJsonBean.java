package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import java.util.ArrayList;
import java.util.List;

public class AliasCatalogLoadModelJsonBean
{
   private List<String> _additionalUserChosenCatalogs = new ArrayList<>();

   public List<String> getAdditionalUserChosenCatalogs()
   {
      return _additionalUserChosenCatalogs;
   }

   public void setAdditionalUserChosenCatalogs(List<String> additionalUserChosenCatalogs)
   {
      _additionalUserChosenCatalogs = additionalUserChosenCatalogs;
   }
}
