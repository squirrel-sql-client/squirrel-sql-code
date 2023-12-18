package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasProp;

import java.beans.PropertyDescriptor;

public class AliasChangesReport
{
   private StringBuilder _changes = new StringBuilder();
   public boolean isEmpty()
   {
      return 0 == _changes.length();
   }

   public String getReport()
   {
      return _changes.toString();
   }

   public void indentInnerBean(PropertyDescriptor pd)
   {

   }

   public void unindentInnerBean(PropertyDescriptor pd)
   {

   }


   public void addChange(SQLAliasProp sqlAliasProp, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      _changes.append(sqlAliasProp.sqlAliasPropI18n().getString() + " changed from \"" + previousAliasPropValue + "\" to \"" + editedAliasPropValue);
      _changes.append('\n');
   }
}
