package net.sourceforge.squirrel_sql.client.session.menuattic;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import java.awt.Component;
import java.util.ArrayList;
import java.util.stream.Stream;

public class AtticHandler
{
   private static final String ATTIC_MENU_TITLE_I18N_KEY = "AtticHandler.attic.menu.title";
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(AtticHandler.class);


   public static void initAtticForMenu(JPopupMenu popupMenu, MenuOrigin menuOrigin)
   {
      if(Stream.of(popupMenu.getComponents()).anyMatch(c -> isAtticMenu(c)))
      {
         return;
      }

      JMenu mnuAttic = new JMenu(s_stringMgr.getString(ATTIC_MENU_TITLE_I18N_KEY));
      final JMenuItem mnuToAttic = new JMenuItem(s_stringMgr.getString("AtticHandler.move.to.or.from.attic"));

      final AtticToFromModel atticToFromModel = new AtticToFromModel(popupMenu);

      mnuToAttic.addActionListener(e -> new AtticToFromCtrl(atticToFromModel, menuOrigin));
      mnuAttic.add(mnuToAttic);
      popupMenu.add(mnuAttic);

      ArrayList<JMenuItem> toMoveToAttic = new ArrayList<>();

      for (AtticToFromItem atticToFromItem : atticToFromModel.getAtticToFromItems())
      {
         if(Main.getApplication().getPopupMenuAtticModel().isInAttic(menuOrigin, atticToFromItem))
         {
            toMoveToAttic.add(atticToFromItem.getMenuItem());
         }
      }
      for (JMenuItem toMove : toMoveToAttic)
      {
         popupMenu.remove(toMove);
         mnuAttic.add(toMove);
      }

      cleanDuplicateSeparators(popupMenu);
   }

   private static void cleanDuplicateSeparators(JPopupMenu popupMenu)
   {
      ArrayList<JSeparator> separatorsToRemove = new ArrayList<>();

      boolean previousWasSeparator = false;
      for (Component component : popupMenu.getComponents())
      {
         if(component instanceof JSeparator)
         {
            if(previousWasSeparator)
            {
               separatorsToRemove.add((JSeparator) component);
            }
            previousWasSeparator = true;
         }
         else
         {
            previousWasSeparator = false;
         }
      }

      separatorsToRemove.forEach(s -> popupMenu.remove(s));
   }

   private static boolean isAtticMenu(Component c)
   {
      return c instanceof JMenu && s_stringMgr.getString(ATTIC_MENU_TITLE_I18N_KEY).equals(((JMenu) c).getText());
   }
}
