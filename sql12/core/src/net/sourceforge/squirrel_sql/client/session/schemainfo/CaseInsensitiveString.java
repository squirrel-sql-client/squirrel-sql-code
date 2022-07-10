package net.sourceforge.squirrel_sql.client.session.schemainfo;

import java.io.Serializable;
import java.util.HashMap;


/**
 * This class was introduced to allow the Syntax plugin
 * to find out if a token is a column, table, etc without
 * creating new String objects.
 * Since syntax highlightning needs a lot of those checks
 * the usage of this class leads to better performance and
 * memory finger print.
 */
public class CaseInsensitiveString implements Comparable<CaseInsensitiveString>, Serializable
{
   private char[] _value = new char[0];
   private int _offset = 0;
   private int _len = 0;
   private int _hash = 0;
   private boolean _isMutable;

   private static HashMap<Character,Character> upChars = new HashMap<>();
   private static HashMap<Character,Character> lcChars = new HashMap<>();

   public CaseInsensitiveString(String s)
   {
      _value = new char[s.length()];
      s.getChars(0, s.length(), _value, 0);
      _offset = 0;
      _len = s.length();
      _hash = 0;
      _isMutable = false;
   }

   public CaseInsensitiveString()
   {
      _isMutable = true;
   }

   public void setCharBuffer(char[] buffer, int beginIndex, int len)
   {
      setCharBuffer(buffer, beginIndex, len, false);
   }

   public void setCharBuffer(char[] buffer, int offset, int len, boolean stripDoubleQuotes)
   {
      if(false == _isMutable)
      {
         throw new UnsupportedOperationException("This CaseInsensitiveString is immutable");
      }

      _value = buffer;
      _offset = offset;
      _len = len;
      _hash = 0;

      if(   stripDoubleQuotes
         && 2 < _len
         && '"' == buffer[_offset] && '"' == buffer[_offset + _len - 1])
      {
         ++_offset;
         _len -=2;
      }

   }

   public int hashCode()
   {
      int h = _hash;
      if (h == 0)
      {
         int off = _offset;
         char val[] = _value;
         int len = _len;

         for (int i = 0; i < len; i++)
         {
            h = 31 * h + toUpperCase(val[off++]);
         }
         _hash = h;
      }
      return h;
   }

   public boolean equals(Object obj)
   {
      if(obj == null)
      {
         return false;
      }

      if(obj instanceof String)
      {
         String other = (String) obj;

         if(other.length() != _len)
         {
            return false;
         }

         for(int i = 0; i < _len; ++i)
         {
            char c1 = _value[_offset + i];
            char c2 = other.charAt(i);


            // If characters don't match but case may be ignored,
            // try converting both characters to uppercase.
            // If the results match, then the comparison scan should
            // continue.
            char u1 = toUpperCase(c1);
            char u2 = toUpperCase(c2);
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
      else if(obj.getClass() == this.getClass())
      {
         CaseInsensitiveString other = (CaseInsensitiveString) obj;


         if(other._len != _len)
         {
            return false;
         }

         for(int i = 0; i < _len; ++i)
         {
            char c1 = _value[_offset + i];
            char c2 = other._value[other._offset + i];


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
            if (toLowerCase(u1) == toLowerCase(u2))
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
      return new String(_value, _offset, _len);
   }

   public int compareTo(CaseInsensitiveString anotherString)
   {
      int len1 = _len;
      int len2 = anotherString._len;
      int n = Math.min(len1, len2);
      char v1[] = _value;
      char v2[] = anotherString._value;
      int i = _offset;
      int j = anotherString._offset;

      if (i == j)
      {
         int k = i;
         int lim = n + i;
         while (k < lim)
         {
            char c1 = v1[k];
            char c2 = v2[k];
            if (toLowerCase(c1) != toLowerCase(c2))
            {
               return toLowerCase(c1) - toLowerCase(c2);
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
            if (toLowerCase(c1) != toLowerCase(c2))
            {
               return toLowerCase(c1) - toLowerCase(c2);
            }
         }
      }
      return len1 - len2;
   }
   
   private char toLowerCase(char c) {
       char result = c;
       Character key = Character.valueOf(c);
       if (lcChars.containsKey(key)) {
           return lcChars.get(key).charValue();
       }
       if (Character.isUpperCase(c)) {
           result = Character.toLowerCase(c);
       }
       lcChars.put(key, Character.valueOf(result));
       return result;
   }
   
   private char toUpperCase(char c) {
       char result = c;
       Character key = Character.valueOf(c);
       if (upChars.containsKey(key)) {
           return upChars.get(key).charValue();
       }
       if (Character.isLowerCase(c)) {
           result = Character.toUpperCase(c);
       }
       upChars.put(key, Character.valueOf(result));
       return result;
   }
   
}
