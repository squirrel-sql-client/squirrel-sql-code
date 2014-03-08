package org.squirrelsql.session.completion;

import org.squirrelsql.session.ColumnInfo;

import java.util.ArrayList;

public class ColumnCompletionCandidate extends CompletionCandidate
{
   private ColumnInfo _columnInfo;
   private TableCompletionCandidate _tableCompletionCandidate;

   public ColumnCompletionCandidate(ColumnInfo columnInfo, TableCompletionCandidate tableCompletionCandidate)
   {
      _columnInfo = columnInfo;
      _tableCompletionCandidate = tableCompletionCandidate;
   }

   @Override
   public String getReplacement()
   {
      return getQualifiyingPrefix() + _columnInfo.getColName();
   }

   private String getQualifiyingPrefix()
   {
      String qualifyingString = "";

      if (null != _tableCompletionCandidate)
      {
         qualifyingString = _tableCompletionCandidate.getReplacement() + ".";
      }
      return qualifyingString;
   }

   @Override
   public String getObjectTypeName()
   {
      return null;
   }

   @Override
   public String toString()
   {
      return getQualifiyingPrefix() + _columnInfo.getDescription();
   }
}
