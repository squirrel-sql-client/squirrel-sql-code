package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;
import net.sourceforge.squirrel_sql.fw.props.Props;

import java.awt.event.ActionEvent;

public class CompressAliasToolbarAction  extends AliasAction  implements IToggleAction
{
   public static final String PREF_KEY_COMPRESS_ALIAS_TOOLBAR = "Squirrel.aliases.compressAliasToolbar";

   private ToggleComponentHolder _toogleComponentHolder;

   public CompressAliasToolbarAction()
   {
      super(Main.getApplication());

      _toogleComponentHolder = new ToggleComponentHolder();

   }

   @Override
   public ToggleComponentHolder getToggleComponentHolder()
   {
      return _toogleComponentHolder;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      Props.putBoolean(PREF_KEY_COMPRESS_ALIAS_TOOLBAR, _toogleComponentHolder.isSelected());
      Main.getApplication().getWindowManager().getAliasesListInternalFrame().reInitToolBar();
   }
}
