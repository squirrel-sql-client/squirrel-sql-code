package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.completion.MappingInfoProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

public class MappedClassInfo extends CompletionInfo
{
   private String _mappedClassName;
   private String _tableName;
   private PropertyInfo[] _propertyInfos;
   private String _simpleMappedClassName;
   private CompletionParser _lastParser;

   public MappedClassInfo(String mappedClassName, String tableName, HibernatePropertyInfo indentifierHibernatePropertyInfo, HibernatePropertyInfo[] hibernatePropertyInfos)
   {
      _mappedClassName = mappedClassName;
      _tableName = tableName;
      _simpleMappedClassName = MappingUtils.getSimpleClassName(mappedClassName);

      _propertyInfos = new PropertyInfo[hibernatePropertyInfos.length + 1];

      _propertyInfos[0] = new PropertyInfo(indentifierHibernatePropertyInfo, _mappedClassName);
      for (int i = 0; i < hibernatePropertyInfos.length; i++)
      {
         _propertyInfos[i+1] = new PropertyInfo(hibernatePropertyInfos[i], _mappedClassName);
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

   public boolean matches(CompletionParser parser, boolean matchNameExact, boolean stateless)
   {
      if(false == stateless)
      {
         _lastParser = parser;
      }

      if(matchNameExact)
      {
         return _mappedClassName.equals(parser.getStringToParse()) || _simpleMappedClassName.equals(parser.getStringToParse());
      }
      else
      {
         return _mappedClassName.startsWith(parser.getStringToParse()) || _simpleMappedClassName.startsWith(parser.getStringToParse());
      }
   }


   public boolean hasColumns()
   {
      return true;
   }

   public ArrayList<PropertyInfo> getQualifiedMatchingAttributes(CompletionParser parser)
   {
      ArrayList<PropertyInfo> ret = new ArrayList<PropertyInfo>();

      String stringToParse = parser.getStringToParse();


      String propertyChainBegin;
      if( stringToParse.startsWith(_mappedClassName + ".") )
      {
         propertyChainBegin = stringToParse.substring((_mappedClassName + ".").length());
      }
      else if ( stringToParse.startsWith(_simpleMappedClassName + ".") )
      {
         propertyChainBegin = stringToParse.substring((_simpleMappedClassName + ".").length());
      }
      else
      {
         return ret;
      }

      ArrayList<String> props = getArrayFormChain(propertyChainBegin);

      PropertyInfo[] propInfoBuf = _propertyInfos;

      for (int i = 0; i < props.size(); i++)
      {
         for (PropertyInfo propertyInfo : propInfoBuf)
         {
            if(propertyInfo.matchesUnQualified(props.get(i)))
            {
               if(i == props.size() -1)
               {
                  ret.add(propertyInfo);
               }
               else if(i < props.size() - 1 && props.get(i).equals(propertyInfo.getHibernatePropertyInfo().getPropertyName()))
               {
                  // This could (perhaps more elegantly) be done by recursion
                  propInfoBuf = new PropertyInfo[0];

                  MappedClassInfo mappedClassInfo = propertyInfo.getMappedClassInfo();
                  if(null != mappedClassInfo)
                  {
                     propInfoBuf = mappedClassInfo.getAttributes();
                  }
                  break;
               }
            }
         }
      }

      return ret;

   }

   private ArrayList<String> getArrayFormChain(String propertyChainBegin)
   {
      ArrayList<String> ret = new ArrayList<String>();

      ret.addAll(Arrays.asList(propertyChainBegin.split("\\.")));

      if(propertyChainBegin.endsWith("."))
      {
         ret.add("");
      }

      return ret;

   }


   public Collection<? extends CompletionInfo> getMatchingAttributes(CompletionParser parser)
   {
      ArrayList<CompletionInfo> ret = new ArrayList<CompletionInfo>();

      for (PropertyInfo propertyInfo : _propertyInfos)
      {
         if(propertyInfo.matchesUnQualified(parser.getLastToken()))
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

   public PropertyInfo getAttributeByName(String attrName)
   {
      for (PropertyInfo propertyInfo : _propertyInfos)
      {
         if(propertyInfo.getCompareString().equals(attrName))
         {
            return propertyInfo;
         }
      }

      return null;


   }

   public PropertyInfo[] getAttributes()
   {
      return _propertyInfos;
   }


   public String getTableName()
   {
      return _tableName;
   }

   public void initAttributesWithClassInfo(MappingInfoProvider mappingInfoProvider)
   {
      for (PropertyInfo propertyInfo : _propertyInfos)
      {
         propertyInfo.setMappedClassInfo(mappingInfoProvider.getMappedClassInfoFor(propertyInfo.getHibernatePropertyInfo().getClassName(), false, false));
      }
   }
}
