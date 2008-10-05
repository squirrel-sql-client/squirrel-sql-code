package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class FindInObjectTreePanel extends JPanel
{
   JButton _btnFind;

   public FindInObjectTreePanel(JTextComponent textComponent, SquirrelResources resources)
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      add(textComponent, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      _btnFind = new JButton(resources.getIcon(SquirrelResources.IImageNames.FIND));
      add(_btnFind, gbc);

      Dimension preferredSize = textComponent.getPreferredSize();
      preferredSize.height = _btnFind.getPreferredSize().height;
      textComponent.setPreferredSize(preferredSize);

      textComponent.setBorder(BorderFactory.createEtchedBorder());

   }
}
