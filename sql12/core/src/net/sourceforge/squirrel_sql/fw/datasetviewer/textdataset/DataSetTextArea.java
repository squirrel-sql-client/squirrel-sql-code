package net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DataSetTextArea extends JTextArea
{
   private TextPopupMenu _textPopupMenu;

   DataSetTextArea()
   {
      setEditable(false);
      setLineWrap(false);
      setFont(new Font("Monospaced", Font.PLAIN, 12));

      _textPopupMenu = new TextPopupMenu();
      _textPopupMenu.setTextComponent(this);

      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
            {
               DataSetTextArea.this.displayPopupMenu(evt);
            }
         }

			public void mouseReleased(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
            {
               DataSetTextArea.this.displayPopupMenu(evt);
            }
         }
      });

   }

   private void displayPopupMenu(MouseEvent evt)
   {
      _textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }

//	public TextPopupMenu getPopupMenu()
//	{
//		return _textPopupMenu;
//	}
}
