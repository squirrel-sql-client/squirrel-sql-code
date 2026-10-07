package net.sourceforge.squirrel_sql.fw.gui.switchablemenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SwitchableMenuCtrl<OPT extends SwitchableMenuOption>
{
   private static final String CHILD_OPTION_PROP = "SwitchableMenuCtrl_child_option_prop";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SwitchableMenuCtrl.class);
   private final JMenu _parentMenu;
   private boolean _dontReactToChildMenuSelected = false;


   public SwitchableMenuCtrl(JMenu parentMenu, OPT[] allOptions, SwitchableMenuCtrlListener<OPT> listener)
   {
      _parentMenu = parentMenu;
      ButtonGroup buttonGroup = new ButtonGroup();
      for(OPT opt : allOptions)
      {
         JCheckBoxMenuItem item = new JCheckBoxMenuItem(opt.getChildMenuText());
         buttonGroup.add(item);
         item.putClientProperty(CHILD_OPTION_PROP, opt);

         item.addActionListener(e -> onChildMenuSelected(listener, opt, parentMenu));
         _parentMenu.add(item);
      }

      _parentMenu.setToolTipText(s_stringMgr.getString("SwitchableMenuCtrl.tooltip"));

      // A JMenu does not execute on click by itself. Clicking it copies using the current leading keyword.
      _parentMenu.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            if(SwingUtilities.isLeftMouseButton(e) && parentMenu.contains(e.getPoint()))
            {
               MenuSelectionManager.defaultManager().clearSelectedPath();
               listener.selected(getSelectedOption());
            }
         }
      });

   }

   private OPT getSelectedOption()
   {
      for(int i = 0; i < _parentMenu.getItemCount(); i++)
      {
         JMenuItem item = _parentMenu.getItem(i);
         if(item.isSelected())
         {
            return (OPT) item.getClientProperty(CHILD_OPTION_PROP);
         }
      }

      throw new IllegalStateException("setSelectedOption needs to be initially invoked");
   }

   private void onChildMenuSelected(SwitchableMenuCtrlListener<OPT> listener, OPT selectedOption, JMenu parentMenu)
   {
      if(_dontReactToChildMenuSelected)
      {
         return;
      }

      parentMenu.setText(selectedOption.getParentMenuText());
      listener.selected(selectedOption);
   }

   public void setSelectedOption(OPT selectedOption)
   {
      _parentMenu.setText(selectedOption.getParentMenuText());

      for(int i = 0; i < _parentMenu.getItemCount(); i++)
      {
         JMenuItem item = _parentMenu.getItem(i);
         if(selectedOption == item.getClientProperty(CHILD_OPTION_PROP))
         {
            try
            {
               _dontReactToChildMenuSelected = true;
               item.setSelected(true);
               break;
            }
            finally
            {
               _dontReactToChildMenuSelected = false;
            }
         }
      }
   }
}
