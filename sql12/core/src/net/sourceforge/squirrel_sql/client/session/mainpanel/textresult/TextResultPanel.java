package net.sourceforge.squirrel_sql.client.session.mainpanel.textresult;

import javax.swing.*;
import java.awt.*;

public class TextResultPanel extends JPanel
{
   JScrollPane _scrollPane;

   public TextResultPanel()
   {
      setLayout(new GridLayout(1,1));

      _scrollPane = new JScrollPane();
      add(_scrollPane);
   }
}
