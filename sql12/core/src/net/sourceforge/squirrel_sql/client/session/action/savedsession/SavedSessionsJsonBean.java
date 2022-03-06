package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionsJsonBean
{
   List<SavedSessionJsonBean> _savedSessionJsonBeans = new ArrayList<>();
   private boolean _showAliasChangeMsg;

   public List<SavedSessionJsonBean> getSavedSessionJsonBeans()
   {
      return _savedSessionJsonBeans;
   }

   public void setSavedSessionJsonBeans(List<SavedSessionJsonBean> savedSessionJsonBeans)
   {
      _savedSessionJsonBeans = savedSessionJsonBeans;
   }

   public boolean isShowAliasChangeMsg()
   {
      return _showAliasChangeMsg;
   }

   public void setShowAliasChangeMsg(boolean showAliasChangeMsg)
   {
      _showAliasChangeMsg = showAliasChangeMsg;
   }
}
