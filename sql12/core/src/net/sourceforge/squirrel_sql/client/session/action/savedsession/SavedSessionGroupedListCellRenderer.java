package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SavedSessionGroupedListCellRenderer implements ListCellRenderer<SavedSessionGrouped>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionGroupedListCellRenderer.class);

   @Override
   public Component getListCellRendererComponent(JList<? extends SavedSessionGrouped> list, SavedSessionGrouped value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      JTextArea comp = new JTextArea(SavedSessionUtil.getDisplayString(value));
      comp.setEditable(false);

      if(isSelected)
      {
         comp.setBackground(new JTextField().getSelectionColor());
      }

      if(cellHasFocus)
      {
         comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      return comp;

   }

}
