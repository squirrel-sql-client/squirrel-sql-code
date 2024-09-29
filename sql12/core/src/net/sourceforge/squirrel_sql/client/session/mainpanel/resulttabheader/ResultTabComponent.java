package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.Main;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

public class ResultTabComponent extends JPanel
{
   private final JLabel _titleLabel;

   public ResultTabComponent(JLabel titleLabel)
   {
      setLayout(new GridLayout(1,1));
      _titleLabel = titleLabel;
      add(_titleLabel);
      showTabHeaderMark(false);
      setOpaque(false);
   }

   public void setIcon(ImageIcon icon)
   {
      _titleLabel.setIcon(icon);
   }

   public void showTabHeaderMark(boolean b)
   {
      if(false == Main.getApplication().getSquirrelPreferences().isResultTabHeaderMarkCurrentSQLsHeader())
      {
         return;
      }

      int markThickness = Main.getApplication().getSquirrelPreferences().getResultTabHeaderMarkThickness();;

      if(b)
      {
         Color color = new Color(Main.getApplication().getSquirrelPreferences().getResultTabHeaderMarkColorRGB());
         setBorder(BorderFactory.createLineBorder(color, markThickness));
      }
      else
      {
         setBorder(BorderFactory.createEmptyBorder(markThickness,markThickness,markThickness,markThickness));
      }
   }

   public ImageIcon getIcon()
   {
      return (ImageIcon) _titleLabel.getIcon();
   }
}
