package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.ThreadSafeDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Inner class that extends OkJPanel so that we can call the ok()
 * method to save the data when the user is happy with it.
 */
class TimestampOkPanel extends OkJPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeTimestamp.class);


   private Timestamp currentTimeStamp = new Timestamp(new java.util.Date().getTime());

   private JRadioButton useJavaDefaultFormatRad = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.defaultFormat") + "(" + currentTimeStamp + ")");

   private JCheckBox useThreeDigitMillisChk= new JCheckBox(s_stringMgr.getString("dateTypeTimestamp.useThreeDigitMillis") + "(" + createThreeDigitMillisExample() + ")");

   private JRadioButton useLocaleDependentFormatRad = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.orLocaleDependent"));

   private final ButtonGroup formatButtonGroup;

   private DateFormatTypeCombo dateFormatTypeCbo = new DateFormatTypeCombo();

   // checkbox for whether to interpret input leniently or not
   // i18n[dateTypeTimestamp.allowInexact=allow inexact format on input]
   private JCheckBox lenientChk = new JCheckBox(s_stringMgr.getString("dateTypeTimestamp.allowInexact"));

   // Objects needed to handle radio buttons
   private JRadioButton doNotUseButton = new JRadioButton(s_stringMgr.getString("dateTypeTimestamp.timestampInWhere"));

   // i18n[dateTypeTimestamp.jdbcEscape=Use JDBC standard escape format ]
   String jdbcEscapeMsg = s_stringMgr.getString("dateTypeTimestamp.jdbcEscape");

   private JRadioButton useTimestampFormatButton = new JRadioButton(jdbcEscapeMsg + "( \"{ts '" + currentTimeStamp + "'}\")");

   // i18n[dateTypeTimestamp.stringVersion=Use String version of Timestamp ]
   String stringVersionMsg = s_stringMgr.getString("dateTypeTimestamp.stringVersion");

   private JRadioButton useStringFormatButton = new JRadioButton(stringVersionMsg + "('" + currentTimeStamp + "')");

   // IMPORTANT: put the buttons into the array in same order as their
   // associated values defined for whereClauseUsage.

   private ButtonModel radioButtonModels[] =
         {
            doNotUseButton.getModel(),
            useTimestampFormatButton.getModel(),
            useStringFormatButton.getModel()
         };

   private ButtonGroup whereClauseUsageGroup = new ButtonGroup();
   private DataTypeTimestampStatics _dataTypeTimestampStatics;


   public TimestampOkPanel(DataTypeTimestampStatics dataTypeTimestampStatics)
   {
      _dataTypeTimestampStatics = dataTypeTimestampStatics;

      useJavaDefaultFormatRad.setSelected(_dataTypeTimestampStatics.isUseJavaDefaultFormat());
      useLocaleDependentFormatRad.setSelected(false == _dataTypeTimestampStatics.isUseJavaDefaultFormat());
      useThreeDigitMillisChk.setSelected(_dataTypeTimestampStatics.isUseThreeDigitMillis());

      formatButtonGroup = new ButtonGroup();
      formatButtonGroup.add(useJavaDefaultFormatRad);
      formatButtonGroup.add(useLocaleDependentFormatRad);


      useJavaDefaultFormatRad.addActionListener(e -> onRadioButtonChanged());
      useLocaleDependentFormatRad.addActionListener(e -> onRadioButtonChanged());
      onRadioButtonChanged();


      // Combo box for read-all/read-part of blob
      dateFormatTypeCbo.setSelectedIndex(_dataTypeTimestampStatics.getLocaleFormat());

      // lenient checkbox
      lenientChk.setSelected(_dataTypeTimestampStatics.isLenient());

      // where clause usage group
      whereClauseUsageGroup.add(doNotUseButton);
      whereClauseUsageGroup.add(useTimestampFormatButton);
      whereClauseUsageGroup.add(useStringFormatButton);
      whereClauseUsageGroup.setSelected(radioButtonModels[_dataTypeTimestampStatics.getWhereClauseUsage()], true);



      /*
       * Create the panel and add the GUI items to it
       */

      layoutPanel();


   }

   private void onRadioButtonChanged()
   {
      dateFormatTypeCbo.setEnabled(useLocaleDependentFormatRad.isSelected());
      lenientChk.setEnabled(useLocaleDependentFormatRad.isSelected());

      useThreeDigitMillisChk.setEnabled(useJavaDefaultFormatRad.isSelected());

//      dateFormatTypeDrop.setEnabled(!useJavaDefaultFormatRad.isSelected());
//      lenientChk.setEnabled(!useJavaDefaultFormatRad.isSelected());

   }

   private void layoutPanel()
   {
      // i18n[dateTypeTimestamp.typeTimestamp=Timestamp   (SQL type 93)]
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dateTypeTimestamp.typeTimestamp")));

      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      add(createUseDefaultPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      add(createLocaleDependentPanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      add(createWhereClausePanel(), gbc);
   }

   private JPanel createWhereClausePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("dateTypeTimestamp.generateWhereClause")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(doNotUseButton, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(useTimestampFormatButton, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(useStringFormatButton, gbc);

      return ret;
   }

   private JPanel createLocaleDependentPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(useLocaleDependentFormatRad, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,3), 0,0);
      ret.add(dateFormatTypeCbo, gbc);

      gbc = new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(lenientChk, gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }

   private JPanel createUseDefaultPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(useJavaDefaultFormatRad, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0);
      ret.add(useThreeDigitMillisChk, gbc);

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
      _dataTypeTimestampStatics.setUseJavaDefaultFormat(useJavaDefaultFormatRad.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "useJavaDefaultFormat", Boolean.valueOf(_dataTypeTimestampStatics.isUseJavaDefaultFormat()).toString());

      _dataTypeTimestampStatics.setUseThreeDigitMillis(useThreeDigitMillisChk.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "useThreeDigitMillis", Boolean.valueOf(_dataTypeTimestampStatics.isUseThreeDigitMillis()).toString());


      _dataTypeTimestampStatics.setLocaleFormat(dateFormatTypeCbo.getValue());
      DTProperties.put(DataTypeTimestamp.class.getName(), "localeFormat", Integer.toString(_dataTypeTimestampStatics.getLocaleFormat()));

      _dataTypeTimestampStatics.setLenient(lenientChk.isSelected());
      DTProperties.put(DataTypeTimestamp.class.getName(), "lenient", Boolean.valueOf(_dataTypeTimestampStatics.isLenient()).toString());

      //WARNING: this depends on entries in ButtonGroup being in the same order
      // as the values for whereClauseUsage
      int buttonIndex;
      for (buttonIndex = 0; buttonIndex < radioButtonModels.length; buttonIndex++)
      {
         if (whereClauseUsageGroup.isSelected(radioButtonModels[buttonIndex]))
         {
            break;
         }
      }
      if (buttonIndex > radioButtonModels.length)
      {
         buttonIndex = DataTypeTimestampStatics.USE_JDBC_ESCAPE_FORMAT;
      }
      _dataTypeTimestampStatics.setWhereClauseUsage(buttonIndex);
      DTProperties.put(DataTypeTimestamp.class.getName(), "whereClauseUsage", Integer.toString(_dataTypeTimestampStatics.getWhereClauseUsage()));


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
      return new SimpleDateFormat(ThreadSafeDateFormat.DEFAULT_WITH_THREE_MILLI_DIGITS).format(millisTo0(currentTimeStamp));
   }

}
