package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

import java.util.ArrayList;
import java.util.Collection;

public class MappedClassInfo extends CompletionInfo
{
   private String _mappedClassName;
   private PropertyInfo[] _propertyInfos;
   private String _simpleMappedClassName;
   private CompletionParser _lastParser;

   public MappedClassInfo(String mappedClassName, HibernatePropertyInfo indentifierHibernatePropertyInfo, HibernatePropertyInfo[] hibernatePropertyInfos)
   {
      _mappedClassName = mappedClassName;
      _simpleMappedClassName = MappingUtils.getSimpleClassName(mappedClassName);

      _propertyInfos = new PropertyInfo[hibernatePropertyInfos.length + 1];

      _propertyInfos[0] = new PropertyInfo(indentifierHibernatePropertyInfo, mappedClassName);
      for (int i = 0; i < hibernatePropertyInfos.length; i++)
      {
         _propertyInfos[i+1] = new PropertyInfo(hibernatePropertyInfos[i], mappedClassName);
      }


   }

   public String getCompareString()
   {
      if(null != _lastParser && _mappedClassName.startsWith(_lastParser.getStringToParse()))
      {
         return _mappedClassName;
      }

      return _simpleMappedClassName;
   }

   public boolean matches(CompletionParser parser)
   {
      _lastParser = parser;
      return _mappedClassName.startsWith(parser.getStringToParse()) || _simpleMappedClassName.startsWith(parser.getStringToParse());
   }


   public boolean hasColumns()
   {
      return true;
   }

   public ArrayList<CompletionInfo> getQualifiedMatchingAttributes(CompletionParser parser)
   {
      ArrayList<CompletionInfo> ret = new ArrayList<CompletionInfo>();

      for (PropertyInfo propertyInfo : _propertyInfos)
      {
         if(1 < parser.size() && propertyInfo.matchesQualified(parser))
         {
            ret.add(propertyInfo);
         }
      }

      return ret;

   }


   public Collection<? extends CompletionInfo> getMatchingAttributes(CompletionParser parser)
   {
      ArrayList<CompletionInfo> ret = new ArrayList<CompletionInfo>();

      for (PropertyInfo propertyInfo : _propertyInfos)
      {
         if(/*1 < parser.size() && */propertyInfo.matchesUnQualified(parser))
         {
            ret.add(propertyInfo);
         }
      }

      return ret;
   }


   public boolean isSame(String name)
   {
      return _mappedClassName.equals(name) || _simpleMappedClassName.equals(name);
   }

   public String getClassName()
   {
      return _mappedClassName;
   }

   public String getSimpleClassName()
   {
      return _simpleMappedClassName;
   }

   public String[] getAttributeNames()
   {
      String[] ret = new String[_propertyInfos.length];

      for (int i = 0; i < _propertyInfos.length; i++)
      {
         ret[i] = _propertyInfos[i].getCompareString();

      }

      return ret;
   }
}
