package net.sourceforge.squirrel_sql.client.update.gui;

/*
 * Copyright (C) 2007 Rob Manning
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This dialog displays a summary of update information
 */
public class UpdateSummaryDialog extends JDialog {
   private static final long serialVersionUID = 1L;

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(UpdateSummaryDialog.class);

   private UpdateSummaryTable _updateSummaryTable;

   private JLabel _installedVersionLabel = null;
   
   private JLabel _availableVersionLabel = null;
   
   private String _installedVersion = null;
   
   private String _availableVersion = null;
   
   static interface i18n {
      // i18n[UpdateSummaryDialog.applyLabel=Apply Changes]
      String APPLY_LABEL = s_stringMgr.getString("UpdateSummaryDialog.applyLabel");

      // i18n[UpdateSummaryDialog.title=Update Summary]
      String TITLE = s_stringMgr.getString("UpdateSummaryDialog.title");
      
      // i18n[UpdateSummaryDialog.currentVersionPrefix=Current Version:]
      String AVAILABLE_VERSION_PREFIX = 
         s_stringMgr.getString("UpdateSummaryDialog.currentVersionPrefix");
      
      // i18n[UpdateSummaryDialog.installedVersionPrefix=Installed Version:]
      String INSTALLED_VERSION_PREFIX = 
         s_stringMgr.getString("UpdateSummaryDialog.installedVersionPrefix");
      
      //i18n[UpdateSummaryDialog.close=Close]
      String CLOSE_LABEL = 
         s_stringMgr.getString("UpdateSummaryDialog.close");
   }

   public UpdateSummaryDialog(Frame owner, List<ArtifactStatus> artifactStatus,
         UpdateController updateController) 
   {
      super(owner, i18n.TITLE);
      createGUI(artifactStatus, updateController);
   }

   public void setInstalledVersion(String installedVersion) {
   	_installedVersion = installedVersion;
      StringBuilder tmp = new StringBuilder(i18n.INSTALLED_VERSION_PREFIX);
      tmp.append(" ");
      tmp.append(installedVersion);
      _installedVersionLabel.setText(tmp.toString());
      setReleaseVersionWillChangeFlag();
   }
   
   public void setAvailableVersion(String availableVersion) {
   	_availableVersion = availableVersion;
      StringBuilder tmp = new StringBuilder(i18n.AVAILABLE_VERSION_PREFIX);
      tmp.append(" ");
      tmp.append(availableVersion);
      _availableVersionLabel.setText(tmp.toString());   
      setReleaseVersionWillChangeFlag();
   }
   
   private void setReleaseVersionWillChangeFlag() {
   	if (_availableVersion == null || _installedVersion == null) {
   		return;
   	}
   	_updateSummaryTable.setReleaseVersionWillChange(!_availableVersion.equals(_installedVersion));
   }
   
   private void createGUI(final List<ArtifactStatus> artifactStatus, 
                          final UpdateController updateController) {
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      final Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      _installedVersionLabel = new JLabel();
      _installedVersionLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
      
      _availableVersionLabel = new JLabel();
      _availableVersionLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
      
      JPanel labelPanel = new JPanel();
      labelPanel.add(_installedVersionLabel);
      labelPanel.add(_availableVersionLabel);
      // Label panel containing the versions for the update at top of dialog.
      contentPane.add(labelPanel, BorderLayout.NORTH);

      UpdateSummaryTableModel model = new UpdateSummaryTableModel(artifactStatus);
      _updateSummaryTable = new UpdateSummaryTable(artifactStatus, model);
      contentPane.add(new JScrollPane(_updateSummaryTable), BorderLayout.CENTER);

      final JPanel btnsPnl = new JPanel();
      final JButton okBtn = new JButton(i18n.APPLY_LABEL);
      okBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            List<ArtifactStatus> changes = 
               _updateSummaryTable.getUserRequestedChanges();
            updateController.applyChanges(changes, _updateSummaryTable.getReleaseVersionWillChange());
         }
      });
      btnsPnl.add(okBtn);

      final JButton closeBtn = new JButton(i18n.CLOSE_LABEL);
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            dispose();
         }
      });
      btnsPnl.add(closeBtn);
      contentPane.add(btnsPnl, BorderLayout.SOUTH);

      AbstractAction closeAction = new AbstractAction() {
         private static final long serialVersionUID = 1L;

         public void actionPerformed(ActionEvent actionEvent) {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                   .put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke,
                                                             "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);

      pack();
      setSize(655, 500);
      GUIUtils.centerWithinParent(this);
      setResizable(true);

   }
}
