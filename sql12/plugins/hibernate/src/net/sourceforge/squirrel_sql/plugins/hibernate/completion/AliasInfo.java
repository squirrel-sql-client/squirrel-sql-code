package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.util.Collection;

public class AliasInfo extends CompletionInfo
{
   private MappedClassInfo _mci;
   private String _alias;

   public AliasInfo(MappedClassInfo mci, String alias)
   {
      _mci = mci;
      _alias = alias;
   }

   public boolean matches(CompletionParser parser)
   {
      return _alias.startsWith(parser.getStringToReplace());
   }


   public String getCompareString()
   {
      return _alias;
   }

   public Collection<? extends CompletionInfo> getQualifiedMatchingAttributes(CompletionParser parser)
   {
      return _mci.getQualifiedMatchingAttributes(parser);
   }

   public Collection<? extends CompletionInfo> getMatchingAttributes(CompletionParser parser)
   {
      return _mci.getMatchingAttributes(parser);
   }
}
