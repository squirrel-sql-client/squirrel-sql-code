package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;

import java.awt.Color;
import java.beans.PropertyChangeEvent;

public class NullValueColorHandler
{
   private boolean _colorNullValues;
   private DataSetViewerTable _dataSetViewerTable;
   private Color _nullValueColor;

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
         session.getProperties().addPropertyChangeListener(evt -> onPropertyChange(evt));
      }


   }

   private void onPropertyChange(PropertyChangeEvent evt)
   {
      if(SessionProperties.IPropertyNames.COLOR_NULL_VALUES.equals(evt.getPropertyName())
         || SessionProperties.IPropertyNames.NULL_VALUE_COLOR_RGB.equals(evt.getPropertyName())
      )
      {
         _nullValueColor = new Color(_dataSetViewerTable.getSessionOrNull().getProperties().getNullValueColorRGB());
         _colorNullValues = _dataSetViewerTable.getSessionOrNull().getProperties().isColorNullValues();
         _dataSetViewerTable.repaint();
      }
   }

   public Color getNullValueColor()
   {
      return _nullValueColor;
   }

   public boolean isColorNullValues()
   {
      return _colorNullValues;
   }
}
