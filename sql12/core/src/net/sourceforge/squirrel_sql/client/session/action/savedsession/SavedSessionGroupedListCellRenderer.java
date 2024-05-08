package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
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

      JTextField vanillaTextField = new JTextField();

      JTextArea textArea = new JTextArea(SavedSessionUtil.getDisplayString(value));
      textArea.setEditable(false);
      textArea.setBackground(isSelected ? vanillaTextField.getSelectionColor() : vanillaTextField.getBackground());

      JComponent ret = textArea;

      if(value.isGroup())
      {
         ret = new JPanel(new BorderLayout());
         ret.add(textArea, BorderLayout.CENTER);
         JLabel lblGroupIcon = new JLabel(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GROUP_SAVE));
         lblGroupIcon.setBackground(isSelected ? vanillaTextField.getSelectionColor() : vanillaTextField.getBackground());
         ret.setBackground(isSelected ? vanillaTextField.getSelectionColor() : vanillaTextField.getBackground());
         ret.add(lblGroupIcon, BorderLayout.WEST);
      }

      if(cellHasFocus)
      {
         ret.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      return ret;
   }

}
