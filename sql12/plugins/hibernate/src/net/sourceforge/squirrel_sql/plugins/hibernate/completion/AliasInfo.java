package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class AliasInfo extends CompletionInfo
{
   private MappedClassInfo _mci;
   private String _alias;
   private String _toString;

   public AliasInfo(MappedClassInfo mci, String alias)
   {
      _mci = mci;
      _alias = alias;

      _toString = alias + " (alias for " + _mci.getSimpleClassName() + ")";
   }

   public boolean matches(CompletionParser parser)
   {
      return _alias.startsWith(parser.getStringToReplace());
   }


   public String getCompareString()
   {
      return _alias;
   }
   


   public ArrayList<PropertyInfo> getQualifiedMatchingAttributes(CompletionParser parser)
   {
      if(1 < parser.size() && parser.getToken(0).equals(_alias))
      {
         return _mci.getQualifiedMatchingAttributes(new CompletionParser(_mci.getClassName() + "." + parser.getAllButFirst()));
      }
      else
      {
         return new ArrayList<PropertyInfo>();
      }
   }


   public String toString()
   {
      return _toString;
   }

   public PropertyInfo getAttributeByName(String attrName)
   {
      return _mci.getAttributeByName(attrName);
   }
}
