package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;

import java.awt.Color;

public class NullValueColorHandler
{
   private boolean _colorNullValues;
   private DataSetViewerTable _dataSetViewerTable;
   private Color _nullValueColor;
   private long _lastPropertiesCheckTime;

   public NullValueColorHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;

      final ISession session = _dataSetViewerTable.getSessionOrNull();
      if(null == session)
      {
         final SessionProperties newSessionProperties = Main.getApplication().getSquirrelPreferences().getSessionProperties();
         _nullValueColor = new Color(newSessionProperties.getNullValueColorRGB());
         _colorNullValues = newSessionProperties.isColorNullValues();
      }
      else
      {
         _nullValueColor = new Color(session.getProperties().getNullValueColorRGB());
         _colorNullValues = session.getProperties().isColorNullValues();
      }

      _lastPropertiesCheckTime = System.currentTimeMillis();
   }

   public Color getNullValueColor()
   {
      checkForPropertiesUpdates();
      return _nullValueColor;
   }

   public boolean isColorNullValues()
   {
      checkForPropertiesUpdates();
      return _colorNullValues;
   }

   private void checkForPropertiesUpdates()
   {
      long currentTimeMillis = System.currentTimeMillis();
      if(currentTimeMillis - _lastPropertiesCheckTime < 2000)
      {
         return;
      }

      _lastPropertiesCheckTime = currentTimeMillis;

      if(   _nullValueColor.getRGB() != _dataSetViewerTable.getSessionOrNull().getProperties().getNullValueColorRGB()
         || _colorNullValues != _dataSetViewerTable.getSessionOrNull().getProperties().isColorNullValues())
      {
         _nullValueColor = new Color(_dataSetViewerTable.getSessionOrNull().getProperties().getNullValueColorRGB());
         _colorNullValues = _dataSetViewerTable.getSessionOrNull().getProperties().isColorNullValues();
         _dataSetViewerTable.repaint();
      }
   }
}
