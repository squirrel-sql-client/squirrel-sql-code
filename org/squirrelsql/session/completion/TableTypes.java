package org.squirrelsql.session.completion;

/**
 * @see java.sql.DatabaseMetaData#getTableTypes()
 */
public enum TableTypes
{
   TABLE,
   VIEW,
   SYSTEM_TABLE("SYSTEM_TABLE"),
   GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
   LOCAL_TEMPORARY("LOCAL TEMPORARY"),
   ALIAS,
   SYNONYM;

   private String _nameNonJavaIdentifier;


   TableTypes()
   {

   }

   TableTypes(String nameNonJavaIdentifier)
   {
      _nameNonJavaIdentifier = nameNonJavaIdentifier;
   }

   public static TableTypes[] getTableAndView()
   {
      return new TableTypes[]{TABLE, VIEW};
   }


   @Override
   public String toString()
   {
      if(null == _nameNonJavaIdentifier)
      {
         return super.toString();
      }

      return _nameNonJavaIdentifier;
   }
}
