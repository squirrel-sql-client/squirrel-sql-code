package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SavedSessionMoreDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionMoreDlg.class);

   JTextField txtToSearch = new JTextField();
   JCheckBox chkRememberLastSearch = new JCheckBox(s_stringMgr.getString("SavedSessionMoreDlg.remember.last.search"));

   OpenInSessionPanel openInSessionPanel;
   JList<SavedSessionGrouped> lstSavedSessions;
   JButton btnClose;
   JButton btnOpenSelected;
   JButton btnDeleteSelected;
   JCheckBox chkShowDefaultAliasMsg;
   IntegerField txtMaxNumberSavedSessions;

   public SavedSessionMoreDlg(Frame parentFrame, SavedSessionMoreDlgState state)
   {
      super(parentFrame, s_stringMgr.getString("SavedSessionMoreDlg.open.or.manage.saved-sessions"), false);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createTopPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      lstSavedSessions = new JList<>();
      lstSavedSessions.setCellRenderer(new SavedSessionGroupedListCellRenderer());
      getContentPane().add(new JScrollPane(lstSavedSessions), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(createButtonPanel(state), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,0,5), 0,0);
      getContentPane().add(createConfigPanel(), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,10,0), 0,0);
      btnClose = new JButton(s_stringMgr.getString("SavedSessionMoreDlg.close"));
      getContentPane().add(btnClose, gbc);

      getRootPane().setDefaultButton(btnOpenSelected);
   }

   private JPanel createTopPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SavedSessionMoreDlg.available.saved.sessions")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new SmallToolTipInfoButton(s_stringMgr.getString("SavedSessionMoreDlg.saved.sessions.info.html"), 10000).getButton(), gbc);


      gbc = new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SavedSessionMoreDlg.enter.search.text")), gbc);


      gbc = new GridBagConstraints(0,2,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0,0);
      ret.add(txtToSearch, gbc);


      gbc = new GridBagConstraints(0,3,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(chkRememberLastSearch, gbc);


      return ret;
   }

   private JPanel createConfigPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      chkShowDefaultAliasMsg = new JCheckBox(s_stringMgr.getString("SavedSessionMoreDlg.show.change.default.alias.offer"));
      chkShowDefaultAliasMsg.setToolTipText(s_stringMgr.getString("SavedSessionMoreDlg.show.change.default.alias.offer.tooltip"));
      ret.add(chkShowDefaultAliasMsg, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,3,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SavedSessionMoreDlg.max.savedSession")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      txtMaxNumberSavedSessions = new IntegerField(5, 0);
      ret.add(txtMaxNumberSavedSessions, gbc);

      // dist
      gbc = new GridBagConstraints(2,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);


      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SavedSessionMoreDlg.config.title")));

      return ret;
   }

   private JPanel createButtonPanel(SavedSessionMoreDlgState state)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      int gridX = 0;

      gbc = new GridBagConstraints(gridX,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnOpenSelected = new JButton(s_stringMgr.getString("SavedSessionMoreDlg.open.selected"));
      ret.add(btnOpenSelected, gbc);

      if(state == SavedSessionMoreDlgState.CURRENT_SESSION || state == SavedSessionMoreDlgState.CURRENT_SESSION_WARN_DISCARD_SQL_EDITORS)
      {
         ++gridX;
         gbc = new GridBagConstraints(gridX,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,2,0,0), 0,0);
         openInSessionPanel = new OpenInSessionPanel(null, state == SavedSessionMoreDlgState.CURRENT_SESSION_WARN_DISCARD_SQL_EDITORS);
         ret.add(openInSessionPanel, gbc);
         openInSessionPanel.setBorder(BorderFactory.createEtchedBorder());
      }

      ++ gridX;
      gbc = new GridBagConstraints(gridX,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0,0);
      btnDeleteSelected = new JButton(s_stringMgr.getString("SavedSessionMoreDlg.delete.selected"));
      ret.add(btnDeleteSelected, gbc);


      return ret;
   }
}
