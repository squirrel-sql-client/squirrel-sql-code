package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class DockFrame extends JPanel
{

   private static final String PREFS_KEY_DOCK_AUTO_HIDE_ON = "squirrelSql_dock_auto_hide." ;
   
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DockFrame.class);


   private Container _comp;
   private String _title;
   private JLabel _lblTitle;
   private JButton _btnAutoHide;
   private JButton _btnMinimize;
   private ImageIcon _iconAutohideOff;
   private ImageIcon _iconAutohideOn;

   public DockFrame(IApplication app, Container comp, String title)
   {
      _comp = comp;
      _title = title;

      setLayout(new BorderLayout());
      add(createTitlePanel(app), BorderLayout.NORTH);
      add(_comp, BorderLayout.CENTER);
   }

   private JPanel createTitlePanel(IApplication app)
   {
      JPanel ret = new JPanel();
      ret.setBorder(BorderFactory.createEtchedBorder());

      Color bg = new Color(102,153,255);
      ret.setBackground(bg);
      ret.setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      _lblTitle = new JLabel(_title);
      _lblTitle.setBackground(bg);
      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
      ret.add(_lblTitle, gbc);


      _iconAutohideOff = app.getResources().getIcon(SquirrelResources.IImageNames.AUTOHIDE_OFF);
      _iconAutohideOn = app.getResources().getIcon(SquirrelResources.IImageNames.AUTOHIDE_ON);


      if(Preferences.userRoot().getBoolean(PREFS_KEY_DOCK_AUTO_HIDE_ON + _title, true))
      {
         _btnAutoHide = new JButton(_iconAutohideOn);
      }
      else
      {
         _btnAutoHide = new JButton(_iconAutohideOff);
      }
      _btnAutoHide.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAutoHide();
         }
      });

      _btnAutoHide.setToolTipText(s_stringMgr.getString("DockFrame.autoHideToolTip"));


      _btnAutoHide.setBorder(BorderFactory.createEmptyBorder());
      _btnAutoHide.setBackground(bg);
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(_btnAutoHide, gbc);

      _btnMinimize = new JButton(app.getResources().getIcon(SquirrelResources.IImageNames.MINIMIZE));
      _btnMinimize.setToolTipText(s_stringMgr.getString("DockFrame.minimizeToolTip"));
      _btnMinimize.setBorder(BorderFactory.createEmptyBorder());
      _btnMinimize.setBackground(bg);
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      ret.add(_btnMinimize, gbc);

      return ret;
   }

   private void onAutoHide()
   {
      if(_btnAutoHide.getIcon() == _iconAutohideOn)
      {
         _btnAutoHide.setIcon(_iconAutohideOff);
         Preferences.userRoot().putBoolean(PREFS_KEY_DOCK_AUTO_HIDE_ON + _title, false);
      }
      else if(_btnAutoHide.getIcon() == _iconAutohideOff)
      {
         _btnAutoHide.setIcon(_iconAutohideOn);
         Preferences.userRoot().putBoolean(PREFS_KEY_DOCK_AUTO_HIDE_ON + _title, true);
      }
      else
      {
         throw new IllegalStateException("undefined autohide state");
      }
   }

   public Container getComp()
   {
      return _comp;
   }


   public JButton getMinimizeButton()
   {
      return _btnMinimize;
   }

   public boolean isAutoHide()
   {
      return _btnAutoHide.getIcon() == _iconAutohideOn;
   }
}
