package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import javax.swing.AbstractButton;

public class ButtonHolder
{
   private AbstractButton _btn;
   private final String _text;

   public ButtonHolder(AbstractButton btn)
   {
      _btn = btn;
      _text = btn.getText();

      _btn.setText(null);
      _btn.revalidate();
      _btn.repaint();
   }

   public AbstractButton getBtn()
   {
      return _btn;
   }

   public String getText()
   {
      return _text;
   }
}
