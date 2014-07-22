package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

public enum ReferenceType
{
   EXPORTED_KEY("Exported Keys"), IMPORTED_KEY("Imported Keys");
   private String _name;


   ReferenceType(String name)
   {
      _name = name;
   }


   @Override
   public String toString()
   {
      return _name;
   }
}
