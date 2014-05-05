package org.squirrelsql.session.completion;

import org.squirrelsql.session.parser.kernel.TableAliasInfo;

public class AliasCompletionCandidate extends CompletionCandidate
{
   private TableAliasInfo _currentAliasInfo;

   public AliasCompletionCandidate(TableAliasInfo currentAliasInfo)
   {
      _currentAliasInfo = currentAliasInfo;
   }

   @Override
   public String getReplacement()
   {
      return _currentAliasInfo.aliasName;
   }

   @Override
   public String getObjectTypeName()
   {
      return "ALIAS for table " + _currentAliasInfo.tableName;
   }
}
