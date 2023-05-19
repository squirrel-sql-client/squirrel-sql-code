package net.sourceforge.squirrel_sql.client.session.menuattic;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PopupMenuAtticModel
{
   private PopupMenuAtticJsonBean _popupMenuAtticJsonBean = null;

   public boolean isInAttic(MenuOrigin menuOrigin, AtticToFromItem item)
   {
      init();

      final String menuText = MenuAtticUtil.getMenuText(item.getMenuItem());
      return getOrCreateMenuAtticItemJsonBean(menuOrigin).getMenuTexts().contains(menuText);
   }

   private PopupMenuAtticItemJsonBean getOrCreateMenuAtticItemJsonBean(MenuOrigin menuOrigin)
   {
      for (PopupMenuAtticItemJsonBean popupMenuAtticItemJsonBean : _popupMenuAtticJsonBean.getPopupMenuAtticItemJsonBeans())
      {
         if(popupMenuAtticItemJsonBean.getMenuOrigin() == menuOrigin)
         {
            return popupMenuAtticItemJsonBean;
         }
      }

      final PopupMenuAtticItemJsonBean ret = new PopupMenuAtticItemJsonBean();
      ret.setMenuOrigin(menuOrigin);
      _popupMenuAtticJsonBean.getPopupMenuAtticItemJsonBeans().add(ret);
      return ret;
   }


   public void setAttic(MenuOrigin menuOrigin, List<AtticToFromItem> inAtticList)
   {
      init();
      final ArrayList<String> texts = new ArrayList<>(inAtticList.stream().map(i -> i.getMenuText()).collect(Collectors.toList()));
      getOrCreateMenuAtticItemJsonBean(menuOrigin).setMenuTexts(texts);

      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getPopupMenuAtticFile(), _popupMenuAtticJsonBean);
   }

   private void init()
   {
      if(null != _popupMenuAtticJsonBean)
      {
         return;
      }

      _popupMenuAtticJsonBean = new PopupMenuAtticJsonBean();
      if(new ApplicationFiles().getPopupMenuAtticFile().exists())
      {
         _popupMenuAtticJsonBean = JsonMarshalUtil.readObjectFromFileSave(new ApplicationFiles().getPopupMenuAtticFile(), PopupMenuAtticJsonBean.class, _popupMenuAtticJsonBean);
      }
   }
}
