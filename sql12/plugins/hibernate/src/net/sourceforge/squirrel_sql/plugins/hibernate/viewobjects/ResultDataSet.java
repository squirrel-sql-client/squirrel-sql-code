package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.plugins.hibernate.ReflectionCaller;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import java.util.ArrayList;

public class ResultDataSet implements IDataSet
{
   private static final int DISPLAY_WIDTH = 20;

   private int _curIx = -1;
   private ColumnDisplayDefinition[] _columnDisplayDefinitions;
   private SingleType _singleType;

   public ResultDataSet(SingleType singleType)
   {
      _singleType = singleType;

      ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<ColumnDisplayDefinition>();

      for (PropertyInfo propertyInfo : singleType.getMappedClassInfo().getAttributes())
      {
         String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();
         columnDisplayDefinitions.add(new ColumnDisplayDefinition(DISPLAY_WIDTH, propertyName));
      }

      _columnDisplayDefinitions = columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]);
   }

   public int getColumnCount() throws DataSetException
   {
      return _columnDisplayDefinitions.length;
   }

   public DataSetDefinition getDataSetDefinition() throws DataSetException
   {
      return new DataSetDefinition(_columnDisplayDefinitions);
   }

   public boolean next(IMessageHandler msgHandler) throws DataSetException
   {
      return ++_curIx < _singleType.getResults().size();
   }

   public Object get(int columnIndex) throws DataSetException
   {
      Object obj = _singleType.getResults().get(_curIx).getObject();
      HibernatePropertyReader hpr = new HibernatePropertyReader(_columnDisplayDefinitions[columnIndex].getColumnName(), obj);

      Object value = hpr.getValue();
      if(null == value)
      {
         return "<null>";
      }

      if (_singleType.getPersistenCollectionClass().isAssignableFrom(value.getClass()))
      {
         boolean wasInitialized = (Boolean) new ReflectionCaller(value).callMethod("wasInitialized").getCallee();

         if(false == wasInitialized)
         {
            return "<unitialized persistent collection>";
         }
      }

      return hpr.getValue();
   }

}
