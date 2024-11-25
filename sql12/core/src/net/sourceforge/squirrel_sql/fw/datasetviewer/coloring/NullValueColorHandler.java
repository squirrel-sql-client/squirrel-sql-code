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

      final SessionProperties sessionProperties;

      sessionProperties = getSessionProperties();
      _nullValueColor = new Color(sessionProperties.getNullValueColorRGB());
      _colorNullValues = sessionProperties.isColorNullValues();

      _lastPropertiesCheckTime = System.currentTimeMillis();
   }

   private SessionProperties getSessionProperties()
   {
      final SessionProperties sessionProperties;
      final ISession session = _dataSetViewerTable.getSessionOrNull();
      if(null == session)
      {
         sessionProperties = Main.getApplication().getSquirrelPreferences().getSessionProperties();
      }
      else
      {
         sessionProperties = session.getProperties();
      }
      return sessionProperties;
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

      SessionProperties sessionProperties = getSessionProperties();

      if(   _nullValueColor.getRGB() != sessionProperties.getNullValueColorRGB()
            || _colorNullValues != sessionProperties.isColorNullValues())
      {
         _nullValueColor = new Color(sessionProperties.getNullValueColorRGB());
         _colorNullValues = sessionProperties.isColorNullValues();
         _dataSetViewerTable.repaint();
      }

   }
}
