package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.beans.PropertyDescriptor;

public class AliasChange
{
   private final SQLAliasPropType _sqlAliasPropType;
   private final PropertyDescriptor _propertyDescriptor;
   private final Object _editedAliasPropValue;

   public AliasChange(PropertyDescriptor propertyDescriptor, SQLAliasPropType sqlAliasPropType, Object editedAliasPropValue)
   {
      _sqlAliasPropType = sqlAliasPropType;
      _propertyDescriptor = propertyDescriptor;
      _editedAliasPropValue = editedAliasPropValue;
   }

   public void applyChange(SQLAlias newSelectedAlias)
   {
      try
      {
         if (_sqlAliasPropType.isSchemaProp())
         {
            _propertyDescriptor.getWriteMethod().invoke(newSelectedAlias.getSchemaProperties(), _editedAliasPropValue);
         }
         else if (_sqlAliasPropType.isColorProp())
         {
            _propertyDescriptor.getWriteMethod().invoke(newSelectedAlias.getColorProperties(), _editedAliasPropValue);
         }
         else if (_sqlAliasPropType.isConnectionProp())
         {
            _propertyDescriptor.getWriteMethod().invoke(newSelectedAlias.getConnectionProperties(), _editedAliasPropValue);
         }
         else
         {
            _propertyDescriptor.getWriteMethod().invoke(newSelectedAlias, _editedAliasPropValue);
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
