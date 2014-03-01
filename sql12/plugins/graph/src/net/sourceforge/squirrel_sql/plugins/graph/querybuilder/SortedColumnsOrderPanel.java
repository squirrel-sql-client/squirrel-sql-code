package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;

import javax.swing.*;
import java.awt.*;

public class SortedColumnsOrderPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SortedColumnsOrderPanel.class);

  JTable tblOrder;
  JButton btnUp;
  JButton btnDown;

   public SortedColumnsOrderPanel(HideDockButtonHandler hideDockButtonHandler, String labelText)
   {
      setLayout(new BorderLayout());
      add(createButtonPanel(hideDockButtonHandler, labelText), BorderLayout.NORTH);

      tblOrder = new JTable();
      tblOrder.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      tblOrder.getTableHeader().setResizingAllowed(true);
      tblOrder.getTableHeader().setReorderingAllowed(false);
      tblOrder.setAutoCreateColumnsFromModel(false);
      tblOrder.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(new JScrollPane(tblOrder), BorderLayout.CENTER);
   }

   private JPanel createButtonPanel(HideDockButtonHandler hideDockButtonHandler, String labelText)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(hideDockButtonHandler.getHideButton(),gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(labelText),gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      btnUp = new JButton(s_stringMgr.getString("graph.GraphQueryOrderPanel.moveUp"));
      ret.add(btnUp,gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      btnDown = new JButton(s_stringMgr.getString("graph.GraphQueryOrderPanel.moveDown"));
      ret.add(btnDown,gbc);

      gbc = new GridBagConstraints(4,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }
}
