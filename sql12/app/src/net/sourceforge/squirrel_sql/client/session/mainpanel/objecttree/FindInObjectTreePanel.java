package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class FindInObjectTreePanel extends JPanel
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(FindInObjectTreePanel.class);


   JButton _btnFind;
   JToggleButton _btnApplyAsFilter;

   public FindInObjectTreePanel(JTextComponent textComponent, SquirrelResources resources)
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2,2,2,0), 0,0);
      add(textComponent, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,0,2,0), 0,0);
      _btnFind = new JButton(resources.getIcon(SquirrelResources.IImageNames.FIND));
      _btnFind.setBorder(BorderFactory.createEtchedBorder());
      _btnFind.setToolTipText(s_stringMgr.getString("FindInObjectTreePanel.find"));
      add(_btnFind, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,0,2,2), 0,0);
      _btnApplyAsFilter = new JToggleButton(resources.getIcon(SquirrelResources.IImageNames.FILTER));
      _btnApplyAsFilter.setBorder(BorderFactory.createEtchedBorder());
      _btnApplyAsFilter.setToolTipText(s_stringMgr.getString("FindInObjectTreePanel.applyAsFilter"));
      add(_btnApplyAsFilter, gbc);

      Dimension preferredSize = textComponent.getPreferredSize();
      preferredSize.height = _btnFind.getPreferredSize().height;
      textComponent.setPreferredSize(preferredSize);

      textComponent.setBorder(BorderFactory.createEtchedBorder());

   }
}
