package net.sourceforge.squirrel_sql.plugins.mysql.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeInteger;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * A factory that creates DataTypeShort for rendering columns of MySQL SMALLINT UNSIGNED.
 */
public class MySQLSmallIntTypeDataTypeComponentFactory implements IDataTypeComponentFactory
{

   /**
    * @see IDataTypeComponentFactory#constructDataTypeComponent()
    */
   @Override
   public IDataTypeComponent constructDataTypeComponent()
   {
      return new DataTypeInteger(null, new ColumnDisplayDefinition(20, "dummy"));
   }

   /**
    * @see IDataTypeComponentFactory#getDialectType()
    */
   @Override
   public DialectType getDialectType()
   {
      return DialectType.MYSQL;
   }


   @Override
   public boolean matches(DialectType dialectType, int sqlType,
                          String sqlTypeName)
   {
      return new EqualsBuilder().append(getDialectType(), dialectType)
                                .append(5, sqlType)
                                .append("SMALLINT UNSIGNED", sqlTypeName).isEquals();
   }

}
