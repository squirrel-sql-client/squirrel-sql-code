package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public class QualifiedColumnInfo
{
   private CompletionParser _parser;

   public QualifiedColumnInfo(CompletionParser parser)
   {
      _parser = parser;
   }

   public QualifiedColumnInfo()
   {
      this(null);
   }

   public int getColumnPosition()
   {
      if(null != _parser)
      {
         return _parser.getStringToParsePosition();
      }

      return -1;
   }

   /**
    * Was introduced to make the feature the check box "Complete columns qualified by table name"
    * in "New Session properties" --> tab Code completion work.
    * I.e. prevents columns that are already qualified by the user to be qualified twice.
    *
    * @return  When completion is done for MY_TABLE. or MY_TABLE.my then this method returns true, else false.
    *          Note: Must also work with table aliases, e.g. SELECT * FROM MY_TABLE MT WHERE MT.my
    */
   public boolean isColumnQualifiedInEditor()
   {
      return null != _parser && _parser.isQualified();
   }
}
