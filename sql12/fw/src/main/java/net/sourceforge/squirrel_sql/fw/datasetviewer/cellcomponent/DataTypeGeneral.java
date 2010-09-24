package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class DataTypeGeneral
{
   public static final String USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME = "useColumnLabelInsteadColumnName";

   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(DataTypeGeneral.class);


   private static boolean propertiesAlreadyLoaded = false;
   private static boolean _useColumnLabelInsteadColumnName = false;

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

   private static void loadProperties()
   {

      if (propertiesAlreadyLoaded == false)
      {
         String useColumnLabelInsteadColumnName =
               DTProperties.get(DataTypeGeneral.class.getName(), USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME);

         if (useColumnLabelInsteadColumnName != null && useColumnLabelInsteadColumnName.equals("true"))
         {
            _useColumnLabelInsteadColumnName = true;
         }

         propertiesAlreadyLoaded = true;
      }
   }

   private static class GeneralOkJPanel extends OkJPanel
   {
      private JCheckBox _chkUseColumnLabelInsteadColumnName =
            new JCheckBox(s_stringMgr.getString("dataTypeBlob.useColumnLabelInsteadColumnName"));


      public GeneralOkJPanel()
      {
         _chkUseColumnLabelInsteadColumnName.setSelected(DataTypeGeneral._useColumnLabelInsteadColumnName);

         setLayout(new GridBagLayout());
         setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeGeneral.generalType")));
         final GridBagConstraints gbc = new GridBagConstraints();
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(4, 4, 4, 4);
         gbc.anchor = GridBagConstraints.WEST;

         gbc.gridx = 0;
         gbc.gridy = 0;

         gbc.gridwidth = 1;
         add(_chkUseColumnLabelInsteadColumnName, gbc);

      }


      /**
       * User has clicked OK in the surrounding JPanel,
       * so save the current state of all variables
       */
      public void ok()
      {
         // get the values from the controls and set them in the static properties
         _useColumnLabelInsteadColumnName = _chkUseColumnLabelInsteadColumnName.isSelected();
         DTProperties.put(
               DataTypeGeneral.class.getName(),
               USE_COLUMN_LABEL_INSTEAD_COLUMN_NAME, Boolean.valueOf(_useColumnLabelInsteadColumnName).toString());
      }

   }
}
