package net.sourceforge.squirrel_sql.client.session.schemainfo;

import java.io.Serializable;


/**
 * This class was introduced to allow the Syntax plugin
 * to find out if a token is a column, table, etc without
 * creating new String objects.
 * Since syntax highlightning needs a lot of those checks
 * the usage of this class leads to better performance and
 * memory finger print.
 */
public class CaseInsensitiveString implements Comparable, Serializable
{
   private char[] value = new char[0];
   private int offset = 0;
   private int count = 0;
   private int hash = 0;
   private boolean _isMutable;

   public CaseInsensitiveString(String s)
   {
      value = new char[s.length()];
      s.getChars(0, s.length(), value, 0);
      offset = 0;
      count = s.length();
      hash = 0;
      _isMutable = false;
   }

   public CaseInsensitiveString()
   {
      _isMutable = true;
   }

   public void setCharBuffer(char[] buffer, int beginIndex, int len)
   {
      if(false == _isMutable)
      {
         throw new UnsupportedOperationException("This CaseInsensitiveString is immutable");
      }

      value = buffer;
      offset = beginIndex;
      count = len;
      hash = 0;
   }

   public int hashCode()
   {
      int h = hash;
      if (h == 0)
      {
         int off = offset;
         char val[] = value;
         int len = count;

         for (int i = 0; i < len; i++)
         {
            h = 31 * h + Character.toUpperCase(val[off++]);
         }
         hash = h;
      }
      return h;
   }

   public boolean equals(Object obj)
   {
      if(obj instanceof String)
      {
         String other = (String) obj;

         if(other.length() != count)
         {
            return false;
         }

         for(int i=0; i < count; ++i)
         {
            char c1 = value[offset + i];
            char c2 = other.charAt(i);


            // If characters don't match but case may be ignored,
            // try converting both characters to uppercase.
            // If the results match, then the comparison scan should
            // continue.
            char u1 = Character.toUpperCase(c1);
            char u2 = Character.toUpperCase(c2);
            if (u1 == u2)
            {
               continue;
            }
            // Unfortunately, conversion to uppercase does not work properly
            // for the Georgian alphabet, which has strange rules about case
            // conversion.  So we need to make one last check before
            // exiting.
            if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
            {
               continue;
            }

            return false;
         }
         return true;
      }
      else if(obj instanceof CaseInsensitiveString)
      {
         CaseInsensitiveString other = (CaseInsensitiveString) obj;


         if(other.count != count)
         {
            return false;
         }

         for(int i=0; i < count; ++i)
         {
            char c1 = value[offset + i];
            char c2 = other.value[other.offset + i];


            // If characters don't match but case may be ignored,
            // try converting both characters to uppercase.
            // If the results match, then the comparison scan should
            // continue.
            char u1 = Character.toUpperCase(c1);
            char u2 = Character.toUpperCase(c2);
            if (u1 == u2)
            {
               continue;
            }
            // Unfortunately, conversion to uppercase does not work properly
            // for the Georgian alphabet, which has strange rules about case
            // conversion.  So we need to make one last check before
            // exiting.
            if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
            {
               continue;
            }

            return false;
         }
         return true;

      }
      else
      {
         return false;
      }
   }

   public String toString()
   {
      return new String(value, offset, count);
   }

   public int compareTo(Object o)
   {
//      return this.toString().toLowerCase().compareTo(o.toString().toLowerCase());

      CaseInsensitiveString anotherString = (CaseInsensitiveString) o;

      int len1 = count;
      int len2 = anotherString.count;
      int n = Math.min(len1, len2);
      char v1[] = value;
      char v2[] = anotherString.value;
      int i = offset;
      int j = anotherString.offset;

      if (i == j)
      {
         int k = i;
         int lim = n + i;
         while (k < lim)
         {
            char c1 = v1[k];
            char c2 = v2[k];
            if (Character.toLowerCase(c1) != Character.toLowerCase(c2))
            {
               return Character.toLowerCase(c1) - Character.toLowerCase(c2);
            }
            k++;
         }
      }
      else
      {
         while (n-- != 0)
         {
            char c1 = v1[i++];
            char c2 = v2[j++];
            if (Character.toLowerCase(c1) != Character.toLowerCase(c2))
            {
               return Character.toLowerCase(c1) - Character.toLowerCase(c2);
            }
         }
      }
      return len1 - len2;
   }
}
