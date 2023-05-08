package net.sourceforge.squirrel_sql.client.gui.jmeld;

import net.sourceforge.squirrel_sql.fw.props.Props;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.JMeldPanel;

public class JMeldConfigCtrl
{
   public static final String PREF_IGNORE_WHITE_SPACES = "JMeldConfigCtrl.PREF_IGNORE_WHITE_SPACES";
   public static final String PREF_IGNORE_CASE = "JMeldConfigCtrl.PREF_IGNORE_CASE";
   public static final String PREF_DRAW_CURVES = "JMeldConfigCtrl.PREF_DRAW_CURVES";
   public static final String PREF_SELECTED_CURVE_TYPE = "JMeldConfigCtrl.PREF_SELECTED_CURVE_TYPE";
   private final JMeldPanel _meldPanel;


   private JMeldConfigPanel _jMeldConfigPanel;

   public JMeldConfigCtrl(JMeldPanel meldPanel)
   {
      _meldPanel = meldPanel;
      _jMeldConfigPanel = new JMeldConfigPanel();

      _jMeldConfigPanel.chkIgnoreWhiteSpaces.setSelected(Props.getBoolean(PREF_IGNORE_WHITE_SPACES, false));
      _jMeldConfigPanel.chkIgnoreCase.setSelected(Props.getBoolean(PREF_IGNORE_CASE, false));
      _jMeldConfigPanel.chkDrawCurves.setSelected(Props.getBoolean(PREF_DRAW_CURVES, false));
      _jMeldConfigPanel.cboCurveType.setSelectedItem(getCurveTypeFromPrefs());

      onUpdateMeldPanel(_meldPanel);
      _jMeldConfigPanel.chkIgnoreWhiteSpaces.addActionListener(e -> onUpdateMeldPanel(_meldPanel));
      _jMeldConfigPanel.chkIgnoreCase.addActionListener(e -> onUpdateMeldPanel(_meldPanel));
      _jMeldConfigPanel.chkDrawCurves.addActionListener(e -> onUpdateMeldPanel(_meldPanel));
      _jMeldConfigPanel.cboCurveType.addActionListener(e -> onUpdateMeldPanel(_meldPanel));
   }   
   public JMeldConfigPanel getPanel()
   {
      return _jMeldConfigPanel;
   }

   private static JMeldCurveType getCurveTypeFromPrefs()
   {
      String name = Props.getString(PREF_SELECTED_CURVE_TYPE, JMeldCurveType.TYPE_ZERO.name());
      try
      {
         return JMeldCurveType.valueOf(name);
      }
      catch(IllegalArgumentException e)
      {
         // In Case a constant was renamed.
         return JMeldCurveType.TYPE_ZERO;
      }
   }

   private void onUpdateMeldPanel(JMeldPanel meldPanel)
   {
      JMeldSettings.getInstance().getEditor().setIgnoreBlankLines(_jMeldConfigPanel.chkIgnoreWhiteSpaces.isSelected());
      JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceAtBegin(_jMeldConfigPanel.chkIgnoreWhiteSpaces.isSelected());
      JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceAtEnd(_jMeldConfigPanel.chkIgnoreWhiteSpaces.isSelected());
      JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceInBetween(_jMeldConfigPanel.chkIgnoreWhiteSpaces.isSelected());

      JMeldSettings.getInstance().getEditor().setIgnoreCase(_jMeldConfigPanel.chkIgnoreCase.isSelected());

      _jMeldConfigPanel.cboCurveType.setEnabled(_jMeldConfigPanel.chkDrawCurves.isSelected());

      JMeldSettings.getInstance().setDrawCurves(_jMeldConfigPanel.chkDrawCurves.isSelected());
      if(_jMeldConfigPanel.chkDrawCurves.isSelected())
      {
         JMeldSettings.getInstance().setCurveType(_jMeldConfigPanel.cboCurveType.getItemAt(_jMeldConfigPanel.cboCurveType.getSelectedIndex()).getTypeId());
      }

      for( AbstractContentPanel abstractContentPanel : JMeldPanel.getContentPanelList(meldPanel.getTabbedPane()) )
      {
         if(abstractContentPanel instanceof BufferDiffPanel)
         {
            ((BufferDiffPanel)abstractContentPanel).configurationChanged();
         }
         //abstractContentPanel.doRefresh();
      }
      meldPanel.revalidate();

      Props.putBoolean(PREF_IGNORE_WHITE_SPACES, _jMeldConfigPanel.chkIgnoreWhiteSpaces.isSelected());
      Props.putBoolean(PREF_IGNORE_CASE, _jMeldConfigPanel.chkIgnoreCase.isSelected());
      Props.putBoolean(PREF_DRAW_CURVES, _jMeldConfigPanel.chkDrawCurves.isSelected());
      Props.putString(PREF_SELECTED_CURVE_TYPE, ((JMeldCurveType)_jMeldConfigPanel.cboCurveType.getSelectedItem()).name());
   }


}
