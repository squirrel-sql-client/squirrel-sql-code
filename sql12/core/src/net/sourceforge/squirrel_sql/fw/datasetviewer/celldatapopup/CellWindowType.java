package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import javax.swing.Icon;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum CellWindowType
{
   DIALOG,
   FRAME;


   private static final String PREF_KEY_CELL_WINDOW_TYPE = "Squirrel.cell.popup.window.type";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellWindowType.class);


   public static CellWindowType getSelected()
   {
      return CellWindowType.valueOf(Props.getString(PREF_KEY_CELL_WINDOW_TYPE, DIALOG.name()));
   }

   public static void setSelected(CellWindowType newType)
   {
      Props.putString(PREF_KEY_CELL_WINDOW_TYPE, newType.name());
   }

   public Icon getIcon()
   {
      return
            switch(this)
            {
               case FRAME -> Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.RED_FRAME_WITH_DIALOG);
               case DIALOG -> Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FRAME_WITH_RED_DIALOG);
            };
   }

   public String getToggleToToolTipText()
   {
      return
            switch(this)
            {
               case FRAME -> s_stringMgr.getString("CellWindowType.switch.to.dialog.display");
               case DIALOG -> s_stringMgr.getString("CellWindowType.switch.to.frame.display");
            };
   }
}
