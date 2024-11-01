package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DataTypeGeneral
{
   public static final String USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME = "useColumnLabelInsteadColumnName";
   public static final String FORMAT_XML_JSON_WHEN_DISPLAYED_IN_POPUP_PANEL = "formatXmlJsonWhenDisplayedInPopupPanel";
   public static final String RIGHT_ALIGN_NUMERIC_TYPES = "rightAlignNumericTypes";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeGeneral.class);


   private static boolean propertiesAlreadyLoaded = false;
   private static boolean _useColumnLabelInsteadColumnName = false;
   private static boolean _formatXmlJsonWhenDisplayedInPopupPanel = false;
   private static boolean _rightAlignNumericTypes = false;

   public static OkJPanel getControlPanel()
   {
      loadProperties();
      return new GeneralOkJPanel();
   }

   public static boolean isUseColumnLabelInsteadColumnName()
   {
      loadProperties();
      return _useColumnLabelInsteadColumnName;
   }

   public static boolean isRightAlignNumericTypes()
   {
      loadProperties();
      return _rightAlignNumericTypes;
   }

   public static boolean isFormatXmlJsonWhenDisplayedInPopupPanel()
   {
      loadProperties();
      return _formatXmlJsonWhenDisplayedInPopupPanel;
   }

   private static void loadProperties()
   {

      if (propertiesAlreadyLoaded == false)
      {
         String useColumnLabelInsteadColumnName =
               DataTypeProps.getProperty(DataTypeGeneral.class.getName(), USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME);

         if (useColumnLabelInsteadColumnName != null && useColumnLabelInsteadColumnName.equals("true"))
         {
            _useColumnLabelInsteadColumnName = true;
         }

         String formatXmlJsonWhenDisplayedInPopupPanel =
               DataTypeProps.getProperty(DataTypeGeneral.class.getName(), FORMAT_XML_JSON_WHEN_DISPLAYED_IN_POPUP_PANEL);

         if (formatXmlJsonWhenDisplayedInPopupPanel != null && formatXmlJsonWhenDisplayedInPopupPanel.equals("true"))
         {
            _formatXmlJsonWhenDisplayedInPopupPanel = true;
         }

         String rightAlignNumericTypes =
               DataTypeProps.getProperty(DataTypeGeneral.class.getName(), RIGHT_ALIGN_NUMERIC_TYPES);

         if (rightAlignNumericTypes != null && rightAlignNumericTypes.equals("true"))
         {
            _rightAlignNumericTypes = true;
         }

         propertiesAlreadyLoaded = true;
      }
   }

   private static class GeneralOkJPanel extends OkJPanel
   {
      private JCheckBox _chkUseColumnLabelInsteadColumnName =
            new JCheckBox(s_stringMgr.getString("dataTypeBlob.useColumnLabelInsteadColumnName"));

      private JCheckBox _chkFormatXmlJsonWhenDisplayedInPopupPanel =
            new JCheckBox(s_stringMgr.getString("dataTypeGeneral.formatXmlJsonWhenDisplayedInPopupPanel"));

      private JCheckBox _chkRightAlignNumericTypes =
            new JCheckBox(s_stringMgr.getString("dataTypeGeneral.rightAlignNumericTypes"));


      public GeneralOkJPanel()
      {
         loadProperties();

         _chkUseColumnLabelInsteadColumnName.setSelected(DataTypeGeneral._useColumnLabelInsteadColumnName);
         _chkFormatXmlJsonWhenDisplayedInPopupPanel.setSelected(DataTypeGeneral._formatXmlJsonWhenDisplayedInPopupPanel);
         _chkRightAlignNumericTypes.setSelected(DataTypeGeneral._rightAlignNumericTypes);

         setLayout(new GridBagLayout());
         setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeGeneral.generalType")));

         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0,0);
         add(_chkUseColumnLabelInsteadColumnName, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0,0);
         add(_chkFormatXmlJsonWhenDisplayedInPopupPanel, gbc);

         gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0,0);
         add(_chkRightAlignNumericTypes, gbc);
      }


      /**
       * User has clicked OK in the surrounding JPanel,
       * so save the current state of all variables
       */
      public void ok()
      {
         // get the values from the controls and set them in the static properties
         _useColumnLabelInsteadColumnName = _chkUseColumnLabelInsteadColumnName.isSelected();
         DataTypeProps.putDataTypeProperty(DataTypeGeneral.class.getName(), USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME, Boolean.valueOf(_useColumnLabelInsteadColumnName).toString());

         _formatXmlJsonWhenDisplayedInPopupPanel = _chkFormatXmlJsonWhenDisplayedInPopupPanel.isSelected();
         DataTypeProps.putDataTypeProperty(DataTypeGeneral.class.getName(), FORMAT_XML_JSON_WHEN_DISPLAYED_IN_POPUP_PANEL, Boolean.valueOf(_formatXmlJsonWhenDisplayedInPopupPanel).toString());

         _rightAlignNumericTypes = _chkRightAlignNumericTypes.isSelected();
         DataTypeProps.putDataTypeProperty(DataTypeGeneral.class.getName(), RIGHT_ALIGN_NUMERIC_TYPES, Boolean.valueOf(_rightAlignNumericTypes).toString());
      }

   }
}
