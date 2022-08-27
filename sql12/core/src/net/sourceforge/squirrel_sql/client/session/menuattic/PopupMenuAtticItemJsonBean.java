package net.sourceforge.squirrel_sql.client.session.menuattic;

import java.util.ArrayList;
import java.util.List;

public class PopupMenuAtticItemJsonBean
{
   private MenuOrigin _menuOrigin;
   private List<String> _menuTexts = new ArrayList<>();

   public MenuOrigin getMenuOrigin()
   {
      return _menuOrigin;
   }

   public void setMenuOrigin(MenuOrigin menuOrigin)
   {
      _menuOrigin = menuOrigin;
   }

   public List<String> getMenuTexts()
   {
      return _menuTexts;
   }

   public void setMenuTexts(List<String> menuTexts)
   {
      _menuTexts = menuTexts;
   }
}
