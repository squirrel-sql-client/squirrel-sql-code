package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class HQLCompletionInfoCollection
{
   private ArrayList<MappedClassInfo> _mappedClassInfos;
   private ArrayList<SimpleHQLCompletionInfo> _simpleInfos;
   private ArrayList<AliasInfo> _currentAliasInfos = new ArrayList<AliasInfo>();

   private HashMap<String, MappedClassInfo> _mappedClassInfoByName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, MappedClassInfo> _mappedClassInfoBySimpleClassName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, SimpleHQLCompletionInfo> _simpleInfosByName = new HashMap<String, SimpleHQLCompletionInfo>();

   public HQLCompletionInfoCollection(HibernateConnection con)
   {
      _mappedClassInfos = con.getMappedClassInfos();

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         _mappedClassInfoByName.put(mappedClassInfo.getClassName(), mappedClassInfo);
         _mappedClassInfoBySimpleClassName.put(mappedClassInfo.getSimpleClassName(), mappedClassInfo);
      }


      _simpleInfos = new ArrayList<SimpleHQLCompletionInfo>();
      _simpleInfos.addAll(HQLKeywordInfo.createInfos());
      _simpleInfos.addAll(HQLFunctionInfo.createInfos());

      for (SimpleHQLCompletionInfo simpleInfo : _simpleInfos)
      {
         _simpleInfosByName.put(simpleInfo.getCompareString(), simpleInfo);
      }

   }

   public CompletionCandidates getInfosStartingWith(CompletionParser parser)
   {

      ArrayList<CompletionInfo> ciClasses = new ArrayList<CompletionInfo>();
      ArrayList<CompletionInfo> ciAttrs = new ArrayList<CompletionInfo>();


      for (AliasInfo aliasInfo : _currentAliasInfos)
      {
         if(aliasInfo.matches(parser))
         {
            ciClasses.add(aliasInfo);
         }
         ciAttrs.addAll(aliasInfo.getQualifiedMatchingAttributes(parser));

      }

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         if(mappedClassInfo.matches(parser))
         {
            ciClasses.add(mappedClassInfo);
         }

         ciAttrs.addAll(mappedClassInfo.getQualifiedMatchingAttributes(parser));
      }



      ArrayList<CompletionInfo> ret;
      int replacementStart;
      String stringToReplace;
      if(0 < ciClasses.size())
      {
         // We assume that classes and attributes won't be in the same completion list.
         // Classes will be completed fully qualified when the user works with fully qualified class names ...
         ret = ciClasses;
         replacementStart = parser.getReplacementStart();
         stringToReplace = parser.getStringToReplace();
      }
      else
      {
         // ... while attributes used in qualified expressions will not be completed qualified.
         // That means for pack.Foo. the completion popup will be placed behind the last dot.
         ret = ciAttrs;
         replacementStart = parser.getTextTillCarret().length() - parser.getLastToken().length();
         stringToReplace = parser.getLastToken();
      }



      for (SimpleHQLCompletionInfo simpleInfo : _simpleInfos)
      {
         if(simpleInfo.matches(parser))
         {
            ret.add(simpleInfo);
         }
      }
      

      return new CompletionCandidates(ret.toArray(new CompletionInfo[ret.size()]), replacementStart, stringToReplace);


   }

   public void setCurrentAliasInfos(ArrayList<AliasInfo> aliasInfos)
   {
      _currentAliasInfos = aliasInfos;
   }

   public MappedClassInfo getMappedClassInfo(String className)
   {
      MappedClassInfo ret = _mappedClassInfoBySimpleClassName.get(className);

      if(null == ret)
      {
         ret = _mappedClassInfoByName.get(className);
      }

      return ret;

   }

   public boolean mayBeClassOrAliasName(String token)
   {
      if(0 == token.length())
      {
         return false;
      }

      if(false == Character.isJavaIdentifierStart(token.charAt(0)))
      {
         return false;
      }

      if(_simpleInfosByName.containsKey(token))
      {
         return false;
      }

      for (int i = 1; i < token.length(); i++)
      {
         char c = token.charAt(i);
         if(false == Character.isJavaIdentifierPart(c) && '.' != c)
         {
            return false;
         }
      }

      return true;


   }
}
