package net.sourceforge.squirrel_sql.client.session.menuattic;

import java.util.ArrayList;
import java.util.List;

public class PopupMenuAtticJsonBean
{
   private List<PopupMenuAtticItemJsonBean> _popupMenuAtticItemJsonBeans = new ArrayList<>();

   public List<PopupMenuAtticItemJsonBean> getPopupMenuAtticItemJsonBeans()
   {
      return _popupMenuAtticItemJsonBeans;
   }

   public void setPopupMenuAtticItemJsonBeans(List<PopupMenuAtticItemJsonBean> popupMenuAtticItemJsonBeans)
   {
      _popupMenuAtticItemJsonBeans = popupMenuAtticItemJsonBeans;
   }
}
