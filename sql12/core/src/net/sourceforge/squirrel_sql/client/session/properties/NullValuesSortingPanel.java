package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class NullValuesSortingPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(NullValuesSortingPanel.class);

   private JRadioButton radSortNullHighest = new JRadioButton(s_stringMgr.getString("NullValuesSortingPanel.sort.null.highest"));
   private JRadioButton radSortNullLowest = new JRadioButton(s_stringMgr.getString("NullValuesSortingPanel.sort.null.lowest"));

   public NullValuesSortingPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      add(new MultipleLineLabel(s_stringMgr.getString("NullValuesSortingPanel.sort.title")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      add(radSortNullHighest, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      add(radSortNullLowest, gbc);

      ButtonGroup bg = new ButtonGroup();

      bg.add(radSortNullHighest);
      bg.add(radSortNullLowest);

      setBorder(BorderFactory.createTitledBorder(""));
   }

   public void setNullHighest(boolean sortNullsAsHighestValue)
   {
      radSortNullHighest.setSelected(sortNullsAsHighestValue);
      radSortNullLowest.setSelected(!sortNullsAsHighestValue);
   }

   public boolean isNullHighest()
   {
      return radSortNullHighest.isSelected();
   }
}
