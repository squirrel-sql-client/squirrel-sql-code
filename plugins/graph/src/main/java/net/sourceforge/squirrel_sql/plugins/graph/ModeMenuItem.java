package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModeMenuItem extends JPanel
{
   private JRadioButton _btnDefault;
   private JRadioButton _btnZoomPrint;
   private JRadioButton _btnQueryBuilder;
   private ActionListener _actionListener;

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ModeMenuItem.class);

   public ModeMenuItem(ActionListener actionListener)
   {
      super(new GridLayout(1,3));
      _actionListener = actionListener;

      ButtonGroup bg = new ButtonGroup();

      _btnDefault = new JRadioButton(Mode.DEFAULT.toString());
      bg.add(_btnDefault);
      add(_btnDefault);
      _btnDefault.setSelected(true);

      _btnZoomPrint = new JRadioButton(Mode.ZOOM_PRINT.toString());
      bg.add(_btnZoomPrint);
      add(_btnZoomPrint);

      _btnQueryBuilder = new JRadioButton(Mode.QUERY_BUILDER.toString());
      bg.add(_btnQueryBuilder);
      add(_btnQueryBuilder);


      ActionListener l = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onModeChanged(e);
         }
      };

      _btnDefault.addActionListener(l);
      _btnZoomPrint.addActionListener(l);
      _btnQueryBuilder.addActionListener(l);

   }

   private void onModeChanged(ActionEvent e)
   {
      _actionListener.actionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers()));
   }


   public void setMode(Mode mode)
   {
      if(Mode.DEFAULT == mode)
      {
         _btnDefault.setSelected(true);
      }
      else if(Mode.ZOOM_PRINT == mode)
      {
         _btnZoomPrint.setSelected(true);
      }
      else if(Mode.QUERY_BUILDER == mode)
      {
         _btnQueryBuilder.setSelected(true);
      }
   }

   public boolean isZoomPrint()
   {
      return _btnZoomPrint.isSelected();
   }

   public boolean isDefault()
   {
      return _btnDefault.isSelected();
   }

   public boolean isQueryBuilder()
   {
      return _btnQueryBuilder.isSelected();
   }

   public Mode getMode()
   {
      if(_btnDefault.isSelected())
      {
         return Mode.DEFAULT;
      }
      else if(_btnZoomPrint.isSelected())
      {
         return Mode.ZOOM_PRINT;
      }
      else if(_btnQueryBuilder.isSelected())
      {
         return Mode.QUERY_BUILDER;
      }
      else
      {
         throw new IllegalStateException("No mode");
      }
   }
}
