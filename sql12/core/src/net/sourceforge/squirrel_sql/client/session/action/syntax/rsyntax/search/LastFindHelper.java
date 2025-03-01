package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.search;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.fife.rsta.ui.search.SearchComboBox;

public class LastFindHelper
{
   private static final int MAX_LAST_FIND_COUNT = 5;
   private static final String PREF_KEY_PREFIX_LAST_FIND = "Squirrel.syntax.last.find_";

   public static void storeLastFinds(SearchComboBox findTextCombo)
   {
      for(int i=0; i < Math.min(MAX_LAST_FIND_COUNT, findTextCombo.getItemCount()); ++i)
      {
         Props.putString(PREF_KEY_PREFIX_LAST_FIND + i, findTextCombo.getItemAt(i).toString());
      }
   }

   public static void loadLastFinds(SearchComboBox findTextCombo)
   {
      for(int i = MAX_LAST_FIND_COUNT - 1; i >= 0; --i)
      {
         String lastFind = Props.getString(PREF_KEY_PREFIX_LAST_FIND + i, null);

         if(false == StringUtilities.isEmpty(lastFind))
         {
            findTextCombo.addItem(lastFind);
         }
      }
      findTextCombo.getModel().setSelectedItem(null);
   }
}
