package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.TemporalUtils;
import net.sourceforge.squirrel_sql.fw.util.ThreadSafeDateFormat;

/**
 * Inner class that extends OkJPanel so that we can call the ok()
 * method to save the data when the user is happy with it.
 */
class TimestampOkPanel extends OkJPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeTimestamp.class);

   private Timestamp _currentTimeStamp = new Timestamp(new java.util.Date().getTime());

   private JRadioButton _radUseJavaDefaultFormat = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.defaultFormat") + "(" + _currentTimeStamp + ")");

   private JCheckBox _useThreeDigitMillisChk = new JCheckBox(s_stringMgr.getString("dateTypeTimestamp.useThreeDigitMillis") + "(" + createThreeDigitMillisExample() + ")");

   private JRadioButton _radUseLocaleDependentFormat = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.orLocaleDependent"));

   private final ButtonGroup _formatButtonGroup;

   private DateFormatTypeCombo _dateFormatTypeCbo = new DateFormatTypeCombo();

   // checkbox for whether to interpret input leniently or not
   // i18n[dateTypeTimestamp.allowInexact=allow inexact format on input]
   private JCheckBox _lenientChk = new JCheckBox(s_stringMgr.getString("dateTypeTimestamp.allowInexact"));

   // Objects needed to handle radio buttons
   private JRadioButton _radInternalWhereDoNotUse = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.timestampInWhere"));

   private String _internalWhereEscapeMsg = s_stringMgr.getString("dateTypeTimestamp.jdbcEscape");

   private JRadioButton _radInternalWhereUseTimestampFormat = new JRadioButton(_internalWhereEscapeMsg + "( \"" + TemporalUtils.getStdJDBCFormat(_currentTimeStamp) + "\")");

   private String _stringVersionMsg = s_stringMgr.getString("dateTypeTimestamp.stringVersion");

   private JRadioButton _radInternalWhereUseStringFormat = new JRadioButton(_stringVersionMsg + "(\"" + TemporalUtils.getStringFormat(_currentTimeStamp) + "\")");

   /**
    * IMPORTANT: put the buttons into the array in same order as their
    * associated values defined for whereClauseUsage.
    */
   private ButtonModel _radioButtonModels[] =
         {
            _radInternalWhereDoNotUse.getModel(),
            _radInternalWhereUseTimestampFormat.getModel(),
            _radInternalWhereUseStringFormat.getModel()
         };

   private ButtonGroup _btnGroupInternalWhereClauseUsage = new ButtonGroup();

   private TemporalScriptGenerationCtrl _temporalScriptGenerationCtrl;

   private DataTypeTimestampStatics _dataTypeTimestampStatics;


   public TimestampOkPanel(DataTypeTimestampStatics dataTypeTimestampStatics)
   {
      _dataTypeTimestampStatics = dataTypeTimestampStatics;

      _radUseJavaDefaultFormat.setSelected(_dataTypeTimestampStatics.isUseJavaDefaultFormat());
      _radUseLocaleDependentFormat.setSelected(false == _dataTypeTimestampStatics.isUseJavaDefaultFormat());
      _useThreeDigitMillisChk.setSelected(_dataTypeTimestampStatics.isUseThreeDigitMillis());

      _formatButtonGroup = new ButtonGroup();
      _formatButtonGroup.add(_radUseJavaDefaultFormat);
      _formatButtonGroup.add(_radUseLocaleDependentFormat);


      _radUseJavaDefaultFormat.addActionListener(e -> onRadioButtonChanged());
      _radUseLocaleDependentFormat.addActionListener(e -> onRadioButtonChanged());
      onRadioButtonChanged();


      // Combo box for read-all/read-part of blob
      _dateFormatTypeCbo.setSelectedIndex(_dataTypeTimestampStatics.getLocaleFormat());

      // lenient checkbox
      _lenientChk.setSelected(_dataTypeTimestampStatics.isLenient());

      // where clause usage group
      _btnGroupInternalWhereClauseUsage.add(_radInternalWhereDoNotUse);
      _btnGroupInternalWhereClauseUsage.add(_radInternalWhereUseTimestampFormat);
      _btnGroupInternalWhereClauseUsage.add(_radInternalWhereUseStringFormat);
      _btnGroupInternalWhereClauseUsage.setSelected(_radioButtonModels[_dataTypeTimestampStatics.getInternalWhereClauseUsage()], true);

      _temporalScriptGenerationCtrl = new TemporalScriptGenerationCtrl(TemporalUtils.getStdJDBCFormat(_currentTimeStamp),
                                                                       TemporalUtils.getStringFormat(_currentTimeStamp),
                                                                       _dataTypeTimestampStatics.getTimestampScriptFormat());


      layoutPanel();
   }

   private void onRadioButtonChanged()
   {
      _dateFormatTypeCbo.setEnabled(_radUseLocaleDependentFormat.isSelected());
      _lenientChk.setEnabled(_radUseLocaleDependentFormat.isSelected());

      _useThreeDigitMillisChk.setEnabled(_radUseJavaDefaultFormat.isSelected());

//      dateFormatTypeDrop.setEnabled(!useJavaDefaultFormatRad.isSelected());
//      lenientChk.setEnabled(!useJavaDefaultFormatRad.isSelected());

   }

   private void layoutPanel()
   {
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dateTypeTimestamp.typeTimestamp")));

      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,1,0,1), 0,0);
      final JPanel useDefaultPanel = createFormatPanel();
      add(useDefaultPanel, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,1,1,1), 0,0);
      final JPanel whereClausePanel = createInternalWhereClausePanel();
      add(whereClausePanel, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,1,1,1), 0,0);
      final JPanel scriptGenerationPanel = _temporalScriptGenerationCtrl.getPanel();;
      add(scriptGenerationPanel, gbc);

      GUIUtils.alignPreferredWidths(useDefaultPanel, whereClausePanel, scriptGenerationPanel);
   }

   private JPanel createInternalWhereClausePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("dateTypeTimestamp.generateWhereClause")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,8,0,0), 0,0);
      ret.add(_radInternalWhereDoNotUse, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,8,0,0), 0,0);
      ret.add(_radInternalWhereUseTimestampFormat, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,8,0,0), 0,0);
      ret.add(_radInternalWhereUseStringFormat, gbc);


      // dist
      gbc = new GridBagConstraints(1,4,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }

   private JPanel createFormatPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(_radUseJavaDefaultFormat, gbc);

      gbc = new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,20,0,0), 0,0);
      ret.add(_useThreeDigitMillisChk, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,0,0,0), 0,0);
      ret.add(_radUseLocaleDependentFormat, gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,0,3), 0,0);
      GUIUtils.setPreferredWidth(_dateFormatTypeCbo, 250);
      ret.add(_dateFormatTypeCbo, gbc);

      gbc = new GridBagConstraints(0,3,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,20,0,0), 0,0);
      ret.add(_lenientChk, gbc);


      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }

   private Timestamp millisTo0(Timestamp ts)
   {
      return new Timestamp(ts.getTime() - ts.getTime() % 1000);
   }


   /**
    * User has clicked OK in the surrounding JPanel,
    * so save the current state of all variables
    */
   public void ok()
   {
      // get the values from the controls and set them in the static properties
      _dataTypeTimestampStatics.setUseJavaDefaultFormat(_radUseJavaDefaultFormat.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "useJavaDefaultFormat", Boolean.valueOf(_dataTypeTimestampStatics.isUseJavaDefaultFormat()).toString());

      _dataTypeTimestampStatics.setUseThreeDigitMillis(_useThreeDigitMillisChk.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "useThreeDigitMillis", Boolean.valueOf(_dataTypeTimestampStatics.isUseThreeDigitMillis()).toString());


      _dataTypeTimestampStatics.setLocaleFormat(_dateFormatTypeCbo.getValue());
      DTProperties.put(DataTypeTimestamp.class.getName(), "localeFormat", Integer.toString(_dataTypeTimestampStatics.getLocaleFormat()));

      _dataTypeTimestampStatics.setLenient(_lenientChk.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "lenient", Boolean.valueOf(_dataTypeTimestampStatics.isLenient()).toString());

      //WARNING: this depends on entries in ButtonGroup being in the same order
      // as the values for whereClauseUsage
      int buttonIndex;
      for (buttonIndex = 0; buttonIndex < _radioButtonModels.length; buttonIndex++)
      {
         if (_btnGroupInternalWhereClauseUsage.isSelected(_radioButtonModels[buttonIndex]))
         {
            break;
         }
      }
      if (buttonIndex > _radioButtonModels.length)
      {
         buttonIndex = DataTypeTimestampStatics.USE_JDBC_ESCAPE_FORMAT;
      }
      _dataTypeTimestampStatics.setInternalWhereClauseUsage(buttonIndex);
      DTProperties.put(DataTypeTimestamp.class.getName(), "whereClauseUsage", Integer.toString(_dataTypeTimestampStatics.getInternalWhereClauseUsage()));

      _dataTypeTimestampStatics.setTimestampScriptFormat(_temporalScriptGenerationCtrl.getFormat());
      DTProperties.put(DataTypeTimestamp.class.getName(), "timestampScriptFormat", _dataTypeTimestampStatics.getTimestampScriptFormat().name());

      _dataTypeTimestampStatics.initDateFormat();
   }

   // Class that displays the various formats available for dates
   public static class DateFormatTypeCombo extends JComboBox
   {
      public DateFormatTypeCombo()
      {

         // i18n[dataTypeTimestamp.full=Full ({0})]
         addItem(s_stringMgr.getString("dataTypeTimestamp.full", DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new java.util.Date())));
         // i18n[dataTypeTimestamp.long=Long ({0})]
         addItem(s_stringMgr.getString("dataTypeTimestamp.long", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new java.util.Date())));
         // i18n[dataTypeTimestamp.medium=Medium ({0})]
         addItem(s_stringMgr.getString("dataTypeTimestamp.medium", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new java.util.Date())));
         // i18n[dataTypeTimestamp.short=Short ({0})]
         addItem(s_stringMgr.getString("dataTypeTimestamp.short", DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new java.util.Date())));
      }

      public void setSelectedIndex(int option)
      {
         if (option == DateFormat.SHORT)
         {
            super.setSelectedIndex(3);
         }
         else if (option == DateFormat.MEDIUM)
         {
            super.setSelectedIndex(2);
         }
         else if (option == DateFormat.LONG)
         {
            super.setSelectedIndex(1);
         }
         else
         {
            super.setSelectedIndex(0);
         }
      }

      public int getValue()
      {
         if (getSelectedIndex() == 3)
         {
            return DateFormat.SHORT;
         }
         else if (getSelectedIndex() == 2)
         {
            return DateFormat.MEDIUM;
         }
         else if (getSelectedIndex() == 1)
         {
            return DateFormat.LONG;
         }
         else
         {
            return DateFormat.FULL;
         }
      }
   }

   private String createThreeDigitMillisExample()
   {
      return new SimpleDateFormat(ThreadSafeDateFormat.DEFAULT_WITH_THREE_MILLI_DIGITS).format(millisTo0(_currentTimeStamp));
   }

}
