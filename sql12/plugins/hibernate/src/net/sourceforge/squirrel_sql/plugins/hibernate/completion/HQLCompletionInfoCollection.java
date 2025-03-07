package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class HQLCompletionInfoCollection implements MappingInfoProvider
{
   private ArrayList<MappedClassInfo> _mappedClassInfos;
   private ArrayList<SimpleHQLCompletionInfo> _simpleInfos;

   /**
    * Hint in case troubles arise: Migth need to be synchronized.
    */
   private ArrayList<AliasInfo> _currentAliasInfos = new ArrayList<AliasInfo>();

   private HashMap<String, MappedClassInfo> _mappedClassInfoByClassName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, MappedClassInfo> _mappedClassInfoBySimpleClassName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, SimpleHQLCompletionInfo> _simpleInfosByName = new HashMap<String, SimpleHQLCompletionInfo>();
   private HashMap<String, String> _attributeNames = new HashMap<String, String>();
   private HqlSyntaxHighlightTokenMatcher _hqlSyntaxHighlightTokenMatcher = new HqlSyntaxHighlightTokenMatcher(this);

   private MappedClassInfo _lastFoundMappedClassInfo;

   public HQLCompletionInfoCollection(HibernateConnection con)
   {
      _mappedClassInfos = con.getMappedClassInfos();

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         _mappedClassInfoByClassName.put(mappedClassInfo.getClassName(), mappedClassInfo);
         _mappedClassInfoBySimpleClassName.put(mappedClassInfo.getSimpleClassName(), mappedClassInfo);

         for (String  attrName : mappedClassInfo.getAttributeNames())
         {
            _attributeNames.put(attrName, attrName);
         }
      }


      _simpleInfos = new ArrayList<SimpleHQLCompletionInfo>();
      _simpleInfos.addAll(HQLKeywordInfo.createInfos());
      _simpleInfos.addAll(HQLFunctionInfo.createInfos());

      for (SimpleHQLCompletionInfo simpleInfo : _simpleInfos)
      {
         _simpleInfosByName.put(simpleInfo.getCompareString(), simpleInfo);
      }

      _hqlSyntaxHighlightTokenMatcher.addSQLTokenListener(new SQLTokenListener()
      {
         public void tableOrViewFound(String name)
         {
            onTableOrViewFound(name);
         }
      });


      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         mappedClassInfo.initAttributesWithClassInfo(this);
      }

   }

   public CompletionCandidates getInfosStartingWith(CompletionParser parser)
   {

      // Tricky alias and chaining completion examples
      //
      // au to auftr in
      // from Kv auftr where au
      //
      // au to auftr in
      // from Kv.positionen.kv auftr where au
      //
      // positionen. to fields of Kv aggregate positionen in
      // from Kv auftr where auftr.positionen.
      //
      // positionen. to fields of Kv aggregate positionen in
      // from Kv where positionen.


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
         if(mappedClassInfo.matches(parser, false, false))
         {
            ciClasses.add(mappedClassInfo);
         }

         ciAttrs.addAll(mappedClassInfo.getQualifiedMatchingAttributes(parser));
      }



      ArrayList<CompletionInfo> ret = new ArrayList<CompletionInfo>();
      MappedClassInfo lastFoundBuf = _lastFoundMappedClassInfo;
      if(null != lastFoundBuf)
      {
         if(1 == parser.size())
         {
            ret.addAll(lastFoundBuf.getMatchingAttributes(parser));
         }
         else
         {

            for (PropertyInfo propertyInfo : lastFoundBuf.getAttributes())
            {
               if(propertyInfo.getHibernatePropertyInfo().getPropertyName().equals(parser.getToken(0)))
               {
                  MappedClassInfo mappedClassInfo = propertyInfo.getMappedClassInfo();
                  CompletionParser simpleAttrFakeParser = new CompletionParser(mappedClassInfo.getClassName() + "." + parser.getAllButFirst());
                  ArrayList<PropertyInfo> matchingAttributes = mappedClassInfo.getQualifiedMatchingAttributes(simpleAttrFakeParser);
                  ret.addAll(matchingAttributes);
               }
            }
         }
      }


      int replacementStart;
      String stringToReplace;
      if(0 < ciClasses.size())
      {
         // We assume that classes and attributes won't be in the same completion list.
         // Classes will be completed fully qualified when the user works with fully qualified class names ...
         ret.addAll(ciClasses);
         replacementStart = parser.getReplacementStart();
         stringToReplace = parser.getStringToReplace();
      }
      else
      {
         // ... while attributes used in qualified expressions will not be completed qualified.
         // That means for pack.Foo. the completion popup will be placed behind the last dot.
         ret.addAll(ciAttrs);
         replacementStart = parser.getTextTillCaret().length() - parser.getLastToken().length();
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

   private void onTableOrViewFound(String name)
   {
      MappedClassInfo mappedClassInfo = _mappedClassInfoBySimpleClassName.get(name);

      if(null != mappedClassInfo)
      {
         _lastFoundMappedClassInfo = mappedClassInfo;
      }
      else
      {
         _lastFoundMappedClassInfo = _mappedClassInfoByClassName.get(name);
      }
   }


   public void setCurrentAliasInfos(ArrayList<AliasInfo> aliasInfos)
   {
      _currentAliasInfos = aliasInfos;
   }

   public MappedClassInfo getMappedClassInfoFor(String token)
   {
      // Example for this code:
      // Completion should
      // from Kv k inner join fetch k.positionen as posses where posses.artNr = 'sdfsdf'

      CompletionParser cp = new CompletionParser(token);

      if(2 > cp.size())
      {
         return getMappedClassInfoForNonAliasedToken(cp, true);
      }

      String aliasCandidate = cp.getToken(0);

      // We need this buffer because this method may be called asynchronously to the event dispatch thread
      // What could happen is, that _currentAliasInfos ist changed.

      ArrayList<AliasInfo> buf = _currentAliasInfos;

      for (AliasInfo currentAliasInfo : buf)
      {
         if(currentAliasInfo.getCompareString().equals(aliasCandidate))
         {
            ArrayList<PropertyInfo> matchingAttributes = currentAliasInfo.getQualifiedMatchingAttributes(cp);
            for (PropertyInfo matchingAttribute : matchingAttributes)
            {
               if(matchingAttribute.getHibernatePropertyInfo().getPropertyName().equals(cp.getLastToken()))
               {
                  return matchingAttribute.getMappedClassInfo();
               }
            }
         }
      }

      return getMappedClassInfoForNonAliasedToken(cp, false);
   }

   private MappedClassInfo getMappedClassInfoForNonAliasedToken(CompletionParser cp, boolean matchNameExact)
   {
      MappedClassInfo ret = null;

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         if(mappedClassInfo.matches(cp, true, true))
         {
            // An exact match always ends search
            return mappedClassInfo;
         }
         else if(false == matchNameExact && mappedClassInfo.matches(cp, false, true))
         {
            // We have a non exact match here.
            // Nonetheless we continue the loop to see if an exact match exists
            ret = mappedClassInfo;
         }

         if(
              cp.getStringToParse().startsWith(mappedClassInfo.getClassName()) ||
              cp.getStringToParse().startsWith(mappedClassInfo.getSimpleClassName())
           )
         {
            ArrayList<PropertyInfo> matchingAttributes = mappedClassInfo.getQualifiedMatchingAttributes(cp);
            for (PropertyInfo matchingAttribute : matchingAttributes)
            {
               if(matchingAttribute.getHibernatePropertyInfo().getPropertyName().equals(cp.getLastToken()))
               {
                  return matchingAttribute.getMappedClassInfo();
               }
            }
         }
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

   @Override
   public MappedClassInfo getExactMappedClassInfoFor(String className)
   {
      return _mappedClassInfoByClassName.get(className);
   }

   public ISyntaxHighlightTokenMatcher getHqlSyntaxHighlightTokenMatcher()
   {
      return _hqlSyntaxHighlightTokenMatcher;
   }

   public boolean isMappeadClass(String name)
   {
      return _mappedClassInfoByClassName.containsKey(name) || _mappedClassInfoBySimpleClassName.containsKey(name);
   }

   public boolean isFunction(String name)
   {
      SimpleHQLCompletionInfo completionInfo = _simpleInfosByName.get(name);
      return null != completionInfo && completionInfo instanceof HQLFunctionInfo;
   }

   public boolean isKeyword(String name)
   {
      SimpleHQLCompletionInfo completionInfo = _simpleInfosByName.get(name);
      return null != completionInfo && completionInfo instanceof HQLKeywordInfo;
   }

   public boolean isMappedAttribute(String name)
   {
      return _attributeNames.containsKey(name);
   }
}
