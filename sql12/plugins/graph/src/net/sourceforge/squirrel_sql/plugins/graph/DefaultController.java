package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;

public class DefaultController
{
   private JPanel _defaultPanel;

   public DefaultController(StartButtonHandler startButtonHandler)
   {
      _defaultPanel = new JPanel(new BorderLayout(10,0));
      _defaultPanel.add(startButtonHandler.getButton(), BorderLayout.WEST);
      _defaultPanel.add(new JPanel(), BorderLayout.CENTER);
   }

   public void activate(boolean b)
   {
   }

   public JPanel getBottomPanel()
   {
      return _defaultPanel;
   }
}
