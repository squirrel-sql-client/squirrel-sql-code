package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import java.awt.Color;

public class ResultTabHeaderPrefsPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultTabHeaderPrefsPanelController.class);

   private ResultTabHeaderPrefsPanel _panel;

   public ResultTabHeaderPrefsPanelController()
   {
      _panel = new ResultTabHeaderPrefsPanel();

      _panel.chkMarkCurrentSQLsResultTabHeader.addActionListener(e -> onMarkCurrentSQLsResultTabHeader());

      _panel.btnSqlResultTabsMarkColor.addActionListener(e -> onChooseResultMarkColor());
   }

   private void onChooseResultMarkColor()
   {
      String title = s_stringMgr.getString("ResultTabHeaderPrefsPanelController.result.mark.color.choose");
      Color color = JColorChooser.showDialog(_panel, title, _panel.getBtnSqlResultTabsMarkIcon().getColor());

      if (null != color)
      {
         _panel.getBtnSqlResultTabsMarkIcon().setColor(color);
      }

   }

   private void onMarkCurrentSQLsResultTabHeader()
   {
      _panel.btnSqlResultTabsMarkColor.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      _panel.spnThickness.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      _panel.chkMarkLastTabHeaderOnly.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      _panel.chkCompareSqlsNormalized.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());

      _panel.lblMarkLineThickness.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      _panel.txtMaxSqlLengthToCheck.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());

      _panel.lblMaxSqlLengthToCheckStart.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      _panel.lblMaxSqlLengthToCheckEnd.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());

      _panel.lblNoteManualActivate.setEnabled(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
   }

   public void loadData(SquirrelPreferences prefs)
   {
      _panel.spnMaxCharsInTab.setValue(prefs.getResultTabHeaderMaxCharsInTab());

      _panel.chkMarkCurrentSQLsResultTabHeader.setSelected(prefs.isResultTabHeaderMarkCurrentSQLsHeader());
      _panel.getBtnSqlResultTabsMarkIcon().setColor(new Color(prefs.getResultTabHeaderMarkColorRGB()));
      _panel.spnThickness.setValue(prefs.getResultTabHeaderMarkThickness());
      _panel.chkMarkLastTabHeaderOnly.setSelected(prefs.isResultTabHeaderMarkLastOnly());
      _panel.chkCompareSqlsNormalized.setSelected(prefs.isResultTabHeaderCompareSqlsNormalized());
      _panel.txtMaxSqlLengthToCheck.setInt(prefs.getResultTabHeaderMarkMaxSqlLengthToCheck());

      onMarkCurrentSQLsResultTabHeader();
   }

   public void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setResultTabHeaderMaxCharsInTab((Integer) _panel.spnMaxCharsInTab.getValue());

      prefs.setResultTabHeaderMarkCurrentSQLsHeader(_panel.chkMarkCurrentSQLsResultTabHeader.isSelected());
      prefs.setResultTabHeaderMarkColorRGB(_panel.getBtnSqlResultTabsMarkIcon().getColor().getRGB());
      prefs.setResultTabHeaderMarkThickness((Integer) _panel.spnThickness.getValue());
      prefs.setResultTabHeaderMarkLastOnly(_panel.chkMarkLastTabHeaderOnly.isSelected());
      prefs.setResultTabHeaderCompareSqlsNormalized(_panel.chkCompareSqlsNormalized.isSelected());

      if(0 < _panel.txtMaxSqlLengthToCheck.getInt())
      {
         prefs.setResultTabHeaderMarkMaxSqlLengthToCheck(_panel.txtMaxSqlLengthToCheck.getInt());
      }
   }

   public ResultTabHeaderPrefsPanel getPanel()
   {
      return _panel;
   }
}
