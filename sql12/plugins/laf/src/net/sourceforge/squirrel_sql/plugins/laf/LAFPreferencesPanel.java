package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * "Change L&F" panel to be displayed in the preferences dialog.
 */
final class LAFPreferencesPanel extends JPanel
{
   StringManager s_stringMgr = StringManagerFactory.getStringManager(LAFPreferencesPanel.class);

   private static ILogger s_log = LoggerController.createLogger(LAFPreferencesPanel.class);

   private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
   private JCheckBox _allowSetBorder = new JCheckBox(s_stringMgr.getString("laf.allowsetborder"));


   private LAFPlugin _plugin;
   private LAFRegister _lafRegister;

   private LAFPreferences _prefs;


   private JPanel _lafPnl;
   private JPanel _configHolderPnl = new JPanel(new GridLayout(1, 1));

   /**
    * Listener on the Look and Feel combo box.
    */
   private LookAndFeelComboListener _lafComboListener;

   /**
    * Component for extra config for the current Look and Feel. This
    * will be <TT>null</TT> if the Look and Feel doesn't require extra
    * configuration information.
    */
   private BaseLAFPreferencesPanelComponent _curLAFConfigComp;

   LAFPreferencesPanel(LAFPlugin plugin, LAFRegister lafRegister)
   {
      super(new GridBagLayout());
      _plugin = plugin;
      _lafRegister = lafRegister;
      _prefs = _plugin.getLAFPreferences();
      createUserInterface();
   }

   public void addNotify()
   {
      super.addNotify();
      _lafComboListener = new LookAndFeelComboListener();
      _lafCmb.addActionListener(_lafComboListener);
   }

   public void removeNotify()
   {
      if(_lafComboListener != null)
      {
         _lafCmb.removeActionListener(_lafComboListener);
         _lafComboListener = null;
      }
      super.removeNotify();
   }

   void loadData()
   {
      final String selLafClassName = _prefs.getLookAndFeelClassName();
      _allowSetBorder.setSelected(_prefs.getCanLAFSetBorder());
      _lafCmb.setSelectedLookAndFeelClassName(selLafClassName);

      updateLookAndFeelConfigControl();
   }

   void applyChanges()
   {
      _prefs.setCanLAFSetBorder(_allowSetBorder.isSelected());
      _prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());

      _lafRegister.applyPreferences();

      boolean forceChange = false;
      if(_curLAFConfigComp != null)
      {
         forceChange = _curLAFConfigComp.applyChanges();
      }

      try
      {
         _lafRegister.setLookAndFeel(forceChange);
      }
      catch (Exception ex)
      {
         s_log.error("Error setting Look and Feel", ex);
      }
   }

   private void createUserInterface()
   {
      GridBagConstraints gbc;

      _lafPnl = createLookAndFeelPanel();
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
      add(_lafPnl, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
      add(new MultipleLineLabel(s_stringMgr.getString("laf.lafWarning")), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
      MultipleLineLabel enforedWarningLabel = new MultipleLineLabel(s_stringMgr.getString("laf.lafCriticalWarning"));
      enforedWarningLabel.setForeground(Color.red);
      add(enforedWarningLabel, gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
      add(new JPanel(), gbc);
   }

   private JPanel createLookAndFeelPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.broderLaf")));

      GridBagConstraints gbc = new GridBagConstraints();

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4),0,0);
      ret.add(new JLabel(s_stringMgr.getString("laf.lookAndFeel")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4),0,0);
      ret.add(_lafCmb, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4),0,0);
      ret.add(new JLabel(s_stringMgr.getString("laf.jars")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4),0,0);
      ret.add(GUIUtils.styleTextFieldToCopyableLabel(new JTextField(_plugin.getLookAndFeelFolder().getAbsolutePath())), gbc);


      gbc = new GridBagConstraints(0,2,GridBagConstraints.REMAINDER, 1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 0, 4, 4),0,0);
      //_configHolderPnl.setBorder(BorderFactory.createLineBorder(Color.red));
      ret.add(_configHolderPnl, gbc);

      gbc = new GridBagConstraints(1,3,GridBagConstraints.REMAINDER, 1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createSettingsPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("laf.general")));

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.anchor = GridBagConstraints.WEST;

      gbc.gridx = 0;
      gbc.gridy = 0;
      pnl.add(_allowSetBorder, gbc);


      return pnl;
   }

   private void updateLookAndFeelConfigControl()
   {
      if(_curLAFConfigComp != null)
      {
         _configHolderPnl.remove(_curLAFConfigComp);
         _curLAFConfigComp = null;
      }

      UIManager.LookAndFeelInfo lafInfo = _lafCmb.getSelectedLookAndFeel();
      if(lafInfo != null)
      {
         final String selLafClassName = lafInfo.getClassName();
         if(selLafClassName != null)
         {
            ILookAndFeelController ctrl = _lafRegister.getLookAndFeelController(selLafClassName);
            if(ctrl != null)
            {
               _curLAFConfigComp = ctrl.getPreferencesComponent();
               if(_curLAFConfigComp != null)
               {
                  _curLAFConfigComp.loadPreferencesPanel();

//                  final GridBagConstraints gbc = new GridBagConstraints();
//                  gbc.fill = GridBagConstraints.HORIZONTAL;
//                  gbc.insets = new Insets(4, 4, 4, 4);
//                  gbc.gridx = 0;
//                  gbc.gridy = GridBagConstraints.RELATIVE;
//                  gbc.gridwidth = GridBagConstraints.REMAINDER;
//                  _lafPnl.add(_curLAFConfigComp, gbc);

                  _configHolderPnl.add(_curLAFConfigComp);
                  //_configHolderPnl.invalidate();
               }
            }
         }
      }
      validate();
   }

   private class LookAndFeelComboListener implements ActionListener
   {
      public void actionPerformed(ActionEvent evt)
      {
         LAFPreferencesPanel.this.updateLookAndFeelConfigControl();
      }
   }

}
