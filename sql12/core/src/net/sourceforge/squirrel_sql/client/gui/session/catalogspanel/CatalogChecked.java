package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

public class CatalogChecked
{
   private String _catalog;
   private boolean _checked;

   public CatalogChecked(String catalog, boolean checked)
   {
      _catalog = catalog;
      _checked = checked;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public boolean isChecked()
   {
      return _checked;
   }

   public void setChecked(boolean checked)
   {
      _checked = checked;
   }
}
