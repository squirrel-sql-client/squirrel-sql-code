package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

public interface IBaseList
{
   void selectListEntryAtPoint(Point point);

   JComponent getComponent();

   void addMouseListener(MouseListener mouseListener);

   void removeMouseListener(MouseListener mouseListener);

}
