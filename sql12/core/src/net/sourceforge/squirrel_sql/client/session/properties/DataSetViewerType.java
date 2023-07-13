package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerEditableTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;

public final class DataSetViewerType
{
   public static final DataSetViewerType TEXT = new DataSetViewerType(GeneralSessionPropertiesPanelI18n.TEXT, DataSetViewerTextPanel.class);
   public static final DataSetViewerType TABLE = new DataSetViewerType(GeneralSessionPropertiesPanelI18n.TABLE, DataSetViewerTablePanel.class);
   public static final DataSetViewerType EDITABLE_TABLE = new DataSetViewerType(GeneralSessionPropertiesPanelI18n.EDITABLE_TABLE, DataSetViewerEditableTablePanel.class);
   private final String _name;
   private final Class<? extends BaseDataSetViewerDestination> _dataSetViewerClass;

   DataSetViewerType(String name, Class<? extends BaseDataSetViewerDestination> dataSetViewerClass)
   {
      _name = name;
      _dataSetViewerClass = dataSetViewerClass;
   }

   public String toString()
   {
      return _name;
   }

   String getDataSetViewerClassName()
   {
      return _dataSetViewerClass.getName();
   }

   public Class<? extends BaseDataSetViewerDestination> getDataSetViewerClass()
   {
      return _dataSetViewerClass;
   }
}
