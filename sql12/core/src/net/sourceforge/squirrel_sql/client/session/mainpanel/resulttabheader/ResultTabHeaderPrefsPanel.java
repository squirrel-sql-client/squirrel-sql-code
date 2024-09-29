package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.preferences.ColorIcon;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ResultTabHeaderPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultTabHeaderPrefsPanel.class);

   final JSpinner spnMaxCharsInTab;

   final JCheckBox chkMarkCurrentSQLsResultTabHeader;

   final JButton btnSqlResultTabsMarkColor;
   final JLabel lblMarkLineThickness;
   final JSpinner spnThickness;
   final JCheckBox chkCompareSqlsNormalized;

   final MultipleLineLabel lblNoteManualActivate;

   public ResultTabHeaderPrefsPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0);
      add(new JLabel(s_stringMgr.getString("ResultTabHeaderPrefsPanel.max.chars.in.tab")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,3,0,0), 0,0);
      spnMaxCharsInTab = new JSpinner(new SpinnerNumberModel(20, 1, 40, 1));
      add(spnMaxCharsInTab, gbc);



      JPanel pnlMark = new JPanel(new GridBagLayout());
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      chkMarkCurrentSQLsResultTabHeader = new JCheckBox(s_stringMgr.getString("ResultTabHeaderPrefsPanel.activate"));
      pnlMark.add(chkMarkCurrentSQLsResultTabHeader, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      btnSqlResultTabsMarkColor = new JButton(s_stringMgr.getString("ResultTabHeaderPrefsPanel.markColor"));
      btnSqlResultTabsMarkColor.setHorizontalTextPosition(JButton.LEFT);
      btnSqlResultTabsMarkColor.setIcon(new ColorIcon(16, 16));
      pnlMark.add(btnSqlResultTabsMarkColor, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      lblMarkLineThickness = new JLabel(s_stringMgr.getString("ResultTabHeaderPrefsPanel.mark.line.thickness"));
      pnlMark.add(lblMarkLineThickness, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      spnThickness = new JSpinner(new SpinnerNumberModel(2, 1, 5, 1));
      pnlMark.add(spnThickness, gbc);

      gbc = new GridBagConstraints(0,1,GridBagConstraints.REMAINDER,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,0,0), 0,0);
      add(pnlMark, gbc);


      gbc = new GridBagConstraints(0,2,GridBagConstraints.REMAINDER,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      chkCompareSqlsNormalized = new JCheckBox(s_stringMgr.getString("ResultTabHeaderPrefsPanel.compare.sqls.normalized"));
      add(chkCompareSqlsNormalized, gbc);


      gbc = new GridBagConstraints(0,3,GridBagConstraints.REMAINDER,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,5,5), 0,0);
      lblNoteManualActivate = new MultipleLineLabel(s_stringMgr.getString("ResultTabHeaderPrefsPanel.automatically.select.manually.by.shortcut"));
      add(lblNoteManualActivate, gbc);


      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ResultTabHeaderPrefsPanel.title")));
   }

   public ColorIcon getBtnSqlResultTabsMarkIcon()
   {
      return (ColorIcon) btnSqlResultTabsMarkColor.getIcon();
   }
}
