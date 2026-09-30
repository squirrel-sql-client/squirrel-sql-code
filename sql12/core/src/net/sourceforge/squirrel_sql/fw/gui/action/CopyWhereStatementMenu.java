package net.sourceforge.squirrel_sql.fw.gui.action;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

/**
 * The "Copy as SQL WHERE statement" menu of the result table's right mouse menu.
 *
 * Clicking the menu copies the selected cells as condition starting with the chosen leading keyword.
 * Hovering the menu opens a sub menu to choose the leading keyword WHERE, AND or OR.
 * The chosen keyword is checked, remembered and shown in the menu's text.
 */
public class CopyWhereStatementMenu
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyWhereStatementMenu.class);

   private static final String PREF_KEY_LEADING_KEYWORD = "Squirrel.copywherestatementmenu.leading.keyword";

   private static final List<String> LEADING_KEYWORDS = List.of("WHERE", "AND", "OR");

   private final JMenu _menu = new JMenu();
   private final HashMap<String, JCheckBoxMenuItem> _itemByKeyword = new HashMap<>();
   private final Runnable _copy;

   /**
    * @param copy Copies the selected cells using {@link #getLeadingKeyword()}.
    */
   public CopyWhereStatementMenu(Runnable copy)
   {
      _copy = copy;

      _menu.setToolTipText(s_stringMgr.getString("CopyWhereStatementMenu.tooltip"));

      ButtonGroup buttonGroup = new ButtonGroup();
      for (String keyword : LEADING_KEYWORDS)
      {
         JCheckBoxMenuItem item = new JCheckBoxMenuItem(keyword);
         item.addActionListener(e -> onLeadingKeywordSelected(keyword));
         buttonGroup.add(item);
         _itemByKeyword.put(keyword, item);
         _menu.add(item);
      }

      // A JMenu does not execute on click by itself. Clicking it copies using the current leading keyword.
      _menu.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            if(SwingUtilities.isLeftMouseButton(e) && _menu.contains(e.getPoint()))
            {
               MenuSelectionManager.defaultManager().clearSelectedPath();
               _copy.run();
            }
         }
      });

      updateFromPreferences();
   }

   public JMenu getMenu()
   {
      return _menu;
   }

   /**
    * Needs to be called before the menu is shown because the leading keyword may have been changed in another result table's menu.
    */
   public void updateFromPreferences()
   {
      String keyword = getLeadingKeyword();
      _menu.setText(s_stringMgr.getString("CopyWhereStatementMenu.copy.as", keyword));
      _itemByKeyword.get(keyword).setSelected(true);
   }

   public static String getLeadingKeyword()
   {
      String keyword = Props.getString(PREF_KEY_LEADING_KEYWORD, LEADING_KEYWORDS.get(0));

      if(false == LEADING_KEYWORDS.contains(keyword))
      {
         return LEADING_KEYWORDS.get(0);
      }

      return keyword;
   }

   private void onLeadingKeywordSelected(String keyword)
   {
      Props.putString(PREF_KEY_LEADING_KEYWORD, keyword);
      updateFromPreferences();
      _copy.run();
   }
}
