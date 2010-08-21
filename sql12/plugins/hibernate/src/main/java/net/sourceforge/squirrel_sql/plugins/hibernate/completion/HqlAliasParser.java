package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.util.ArrayList;

public class HqlAliasParser
{
   private StringBuffer _token = new StringBuffer();


   public ArrayList<AliasInfo> parse(String hql, MappingInfoProvider mappingInfoProvider)
   {
      ArrayList<AliasInfo> ret = new ArrayList<AliasInfo>();

      int[] i = new int[1];

      MappedClassInfo lastMappedClass = null;

      while(i[0] < hql.length())
      {
         String token = nextToken(i, hql);

         if("as".equals(token))
         {
            continue;
         }


         if(mappingInfoProvider.mayBeClassOrAliasName(token))
         {
            if(null != lastMappedClass)
            {
               ret.add(new AliasInfo(lastMappedClass, token));
               lastMappedClass = null;
            }
            else
            {
               lastMappedClass = mappingInfoProvider.getMappedClassInfoFor(token, true, true);
            }
         }
         else
         {
            lastMappedClass = null;
         }
      }

      return ret;
   }

   private String nextToken(int[] i, String hql)
   {
      _token.setLength(0);
      for (int j = i[0]; j < hql.length(); j++)
      {
         char c = hql.charAt(j);
         if(Character.isWhitespace(c))
         {
            if(0 == _token.length())
            {
               continue;
            }
            else
            {
               i[0] = j+1;
               return _token.toString();
            }
         }

         if(isSepartor(c))
         {
            if(0 == _token.length())
            {
               i[0] = j+1;
               return _token.append(c).toString();
            }
            else
            {
               i[0] = j;
               return _token.toString();
            }
         }

         _token.append(c);

      }

      i[0] = hql.length();
      return _token.toString();

   }

   private boolean isSepartor(char c)
   {
      return ',' == c || '(' == c || ')' == c;
   }


}
