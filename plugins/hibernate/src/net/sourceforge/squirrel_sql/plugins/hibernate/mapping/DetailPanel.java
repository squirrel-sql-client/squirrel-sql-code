package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import javax.swing.*;
import java.awt.*;

public class DetailPanel extends JPanel
{
   public DetailPanel()
   {
      super(new GridLayout(1,1));

      add(new JLabel("Details will be shown here shortly"));
   }
}
