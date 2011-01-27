package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.completion.MappingInfoProvider;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.MappedClassInfoData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

public class MappedClassInfo extends CompletionInfo
{
   private CompletionParser _lastParser;
   private MappedClassInfoData _mappedClassInfoData;
   private PropertyInfo[] _propertyInfos;

   public MappedClassInfo(MappedClassInfoData mappedClassInfoData)
   {
      _mappedClassInfoData = mappedClassInfoData;

      _propertyInfos = new PropertyInfo[_mappedClassInfoData.getHibernatePropertyInfos().length + 1];

      _propertyInfos[0] = new PropertyInfo(_mappedClassInfoData.getIndentifierHibernatePropertyInfo(), _mappedClassInfoData.getMappedClassName());
      for (int i = 0; i < _mappedClassInfoData.getHibernatePropertyInfos().length; i++)
      {
         _propertyInfos[i+1] = new PropertyInfo(_mappedClassInfoData.getHibernatePropertyInfos()[i], _mappedClassInfoData.getMappedClassName());
      }
   }

   public String getCompareString()
   {
      if(null != _lastParser && _mappedClassInfoData.getMappedClassName().startsWith(_lastParser.getStringToParse()))
      {
         return _mappedClassInfoData.getMappedClassName();
      }

      return _mappedClassInfoData.getSimpleMappedClassName();
   }

   public boolean matches(CompletionParser parser, boolean matchNameExact, boolean stateless)
   {
      if(false == stateless)
      {
         _lastParser = parser;
      }

      if(matchNameExact)
      {
         return _mappedClassInfoData.getMappedClassName().equals(parser.getStringToParse()) || _mappedClassInfoData.getSimpleMappedClassName().equals(parser.getStringToParse());
      }
      else
      {
         return _mappedClassInfoData.getMappedClassName().startsWith(parser.getStringToParse()) || _mappedClassInfoData.getSimpleMappedClassName().startsWith(parser.getStringToParse());
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
      if( stringToParse.startsWith(_mappedClassInfoData.getMappedClassName() + ".") )
      {
         propertyChainBegin = stringToParse.substring((_mappedClassInfoData.getMappedClassName() + ".").length());
      }
      else if ( stringToParse.startsWith(_mappedClassInfoData.getSimpleMappedClassName() + ".") )
      {
         propertyChainBegin = stringToParse.substring((_mappedClassInfoData.getSimpleMappedClassName() + ".").length());
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

      for (PropertyInfo propertyInfo : _propertyInfos )
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
      return _mappedClassInfoData.getMappedClassName().equals(name) || _mappedClassInfoData.getSimpleMappedClassName().equals(name);
   }

   public String getClassName()
   {
      return _mappedClassInfoData.getMappedClassName();
   }

   public String getSimpleClassName()
   {
      return _mappedClassInfoData.getSimpleMappedClassName();
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
      for (PropertyInfo propertyInfo : _propertyInfos )
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
      return _mappedClassInfoData.getTableName();
   }

   public void initAttributesWithClassInfo(MappingInfoProvider mappingInfoProvider)
   {
      for (PropertyInfo propertyInfo : _propertyInfos )
      {
         propertyInfo.setMappedClassInfo(mappingInfoProvider.getExactMappedClassInfoFor(propertyInfo.getHibernatePropertyInfo().getClassName()));
      }
   }


}
