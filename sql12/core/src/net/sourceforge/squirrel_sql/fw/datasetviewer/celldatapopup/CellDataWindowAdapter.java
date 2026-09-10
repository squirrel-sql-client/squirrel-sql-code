package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.CloseByEscapeListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class CellDataWindowAdapter
{
   private final CellWindowType _cellWindowType;
   private JDialog _dialog;
   private JFrame _frame;
   private Window _parentWindow;

   public CellDataWindowAdapter(CellWindowType cellWindowType)
   {
      _cellWindowType = cellWindowType;
   }

   public void initWindow(Window parentWindow)
   {
      _parentWindow = parentWindow;
      switch(_cellWindowType)
      {
         case DIALOG -> _dialog = new JDialog(_parentWindow);
         case FRAME -> _frame = new JFrame();
         default -> throw new IllegalStateException("Unknown CellWindowType: " + _cellWindowType.name());
      }
   }

   public Container getContentPane()
   {
      if(null != _dialog)
      {
         return _dialog.getContentPane();
      }
      else
      {
         return _frame.getContentPane();
      }
   }

   public void enableCloseByEscape(CloseByEscapeListener closeByEscapeListener)
   {
      if(null != _dialog)
      {
         GUIUtils.enableCloseByEscape(_dialog, (CloseByEscapeListener<JDialog>)closeByEscapeListener);
      }
      else
      {
         GUIUtils.enableCloseByEscape(_frame, (CloseByEscapeListener<JFrame>) closeByEscapeListener);
      }
   }

   public void addWindowListener(WindowListener windowListener)
   {
      getAsWindow().addWindowListener(windowListener);
   }

   public void setTitle(String title)
   {
      if(null != _dialog)
      {
         _dialog.setTitle(title);
      }
      else
      {
         _frame.setTitle(title);
      }
   }

   public void removeWindowListener(WindowListener windowListener)
   {
      getAsWindow().removeWindowListener(windowListener);
   }

   public void pack()
   {
      getAsWindow().pack();
   }

   public Dimension getSize()
   {
      return getAsWindow().getSize();
   }

   public void setSize(Dimension dim)
   {
      getAsWindow().setSize(dim);
   }

   public void setBounds(Rectangle bounds)
   {
      getAsWindow().setBounds(bounds);
   }

   public Rectangle getBounds()
   {
      return getAsWindow().getBounds();
   }



   public void centerWithinParent()
   {
      if(null != _dialog)
      {
         GUIUtils.centerWithinParent(_dialog);
      }
      else
      {
         GUIUtils.centerWithin(_frame, Main.getApplication().getMainFrame());
      }
   }

   public void setVisible(boolean b)
   {
      getAsWindow().setVisible(b);
   }

   public void dispose()
   {
      getAsWindow().dispose();
   }

   public String getTitle()
   {
      if(null != _dialog)
      {
         return _dialog.getTitle();
      }
      else
      {
         return _frame.getTitle();
      }
   }

   public void toFront()
   {
      getAsWindow().toFront();
   }

   public Window getParent()
   {
      if(null != getAsWindow().getOwner())
      {
         return getAsWindow().getOwner();
      }
      else
      {
         return _parentWindow;
      }
   }


   private Window getAsWindow()
   {
      if(null != _dialog)
      {
         return _dialog;
      }
      else
      {
         return _frame;
      }
   }
}
