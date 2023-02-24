package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Container;

public class SavedSessionMoreDialogFindInfo implements DialogFindInfo
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionMoreDialogFindInfo.class);


   private String _title;
   private Container _contentPane;

   @Override
   public String getDialogTitle()
   {
      return s_stringMgr.getString("GlobalPreferencesDialogFindInfo.dialog.title", _title);
   }

   public Container getContentPane()
   {
      return _contentPane;
   }

   public void setTitle(String title)
   {
      _title = title;
   }

   public void setContentPane(Container contentPane)
   {
      _contentPane = contentPane;
   }
}
