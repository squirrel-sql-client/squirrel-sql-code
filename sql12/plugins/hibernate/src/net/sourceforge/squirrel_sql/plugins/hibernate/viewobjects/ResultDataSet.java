package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HibernateUtil;

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
      ObjectSubstitute obj = _singleType.getResults().get(_curIx).getObject();

      if(null == obj)
      {
         return HibernateUtil.OBJECT_IS_NULL;
      }

      HibernatePropertyReader hpr = new HibernatePropertyReader(_columnDisplayDefinitions[columnIndex].getColumnName(), obj);

      if(hpr.isNull())
      {
         return StringUtilities.NULL_AS_STRING;
      }


      MappedClassInfo mappedClassInfo = ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), _singleType.getAllMappedClassInfos(), true);

      if (hpr.isPersistenCollection())
      {
         if (false == hpr.wasInitialized())
         {
            return HibernateUtil.UNITIALIZED_PERSISTENT_COLLECTION;
         }
         else if (null == mappedClassInfo)
         {
            // Happens when hpr is a mapped basic type (e.g. Integer) collection
            return ViewObjectsUtil.getPrimitivePersistentCollectionString(hpr);
         }
         else
         {
            PropertyInfo propertyInfo = new PropertyInfo(hpr.getHibernatePropertyInfo(), hpr.getHibernatePropertyInfo().getClassName());
            propertyInfo.setMappedClassInfo(mappedClassInfo);
            return new PersistentCollectionResult(hpr, propertyInfo, _singleType.getAllMappedClassInfos());
         }
         
      }

      if (null != mappedClassInfo && false == mappedClassInfo.isPlainValueArray())
      {
         return new SingleResult((ObjectSubstitute)hpr.getValue(), mappedClassInfo);
      }

      return hpr.getValue();
   }

}
