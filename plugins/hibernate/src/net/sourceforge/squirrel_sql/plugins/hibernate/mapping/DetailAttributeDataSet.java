package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DetailAttributeDataSet implements IDataSet
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DetailAttributeDataSet.class);


   private static final int DISPLAY_WIDTH = 20;

   private DetailAttribute[] _attributes;
   private int _curIx = -1;
   private ColumnDisplayDefinition[] _columnDisplayDefinitions
         = new ColumnDisplayDefinition[]
               {
                  // i18n[HibernateAttribuiteDataSet.attrName=Attribute name]
                  new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("HibernateAttribuiteDataSet.attrName")),
                  // i18n[HibernateAttribuiteDataSet.attrClassName=Attribute class name]
                  new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("HibernateAttribuiteDataSet.attrClassName")),
                  // i18n[HibernateAttribuiteDataSet.identifier=Identifier]
                  new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("HibernateAttribuiteDataSet.identifier")),
                  // i18n[HibernateAttribuiteDataSet.dbTableName=Database table name]
                  new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("HibernateAttribuiteDataSet.dbTableName")),
                  // i18n[HibernateAttribuiteDataSet.tableCols=Table columns]
                  new ColumnDisplayDefinition(DISPLAY_WIDTH, s_stringMgr.getString("HibernateAttribuiteDataSet.tableCols"))
               };

   public DetailAttributeDataSet(DetailAttribute[] attributes)
   {
      _attributes = attributes;
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
      return ++_curIx < _attributes.length;
   }

   public Object get(int columnIndex) throws DataSetException
   {
      switch(columnIndex)
      {
         case 0:
            return _attributes[_curIx].getAttributeName();
         case 1:
            return _attributes[_curIx].getClassNameRegardingCollection();
         case 2:
            return _attributes[_curIx].isIdentifier();
         case 3:
            return _attributes[_curIx].getTableName();
         case 4:
            return _attributes[_curIx].getColumnNamesString();
         default:
            throw new IndexOutOfBoundsException("Invalid column index " + columnIndex);
      }
   }
}
