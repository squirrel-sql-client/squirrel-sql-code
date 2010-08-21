package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

public class StringCalculator implements Calculator<String> 
{
   private CharRange _charRange;

   public StringCalculator(CharRange charRange)
   {
      _charRange = charRange;
   }

   /**
    * @return min if no value exisits between min and max
    */
   public String getMid(String min, String max)
   {
      String dist = getDist(min, max);

      String halfDist = getHalf(dist);

      String ret = add(min, halfDist);
      return ret;
   }

   private String add(String min, String halfDist)
   {
      StringBuffer bufHalfDist = new StringBuffer(halfDist);

      int len = bufHalfDist.length();

      StringBuffer bufRet;

      if (null == min)
      {
         bufRet = new StringBuffer();
      }
      else
      {
         bufRet = new StringBuffer(min);
      }

      minCharUpToLen(bufRet, len);
      bufRet.reverse();

      bufHalfDist.reverse();


      boolean einsImSinn = false;
      for (int i = 0; i < len; i++)
      {
         char sumChar = (char) (bufRet.charAt(i) + bufHalfDist.charAt(i));

         if(einsImSinn)
         {
            ++sumChar;
            einsImSinn = false;
         }

         if(sumChar > _charRange.getMaxChar())
         {
            einsImSinn = true;
            sumChar = (char) (sumChar - _charRange.getRange());
         }

         bufRet.setCharAt(i, sumChar);
      }
      return bufRet.reverse().toString();
   }

   private String getHalf(String dist)
   {
      StringBuffer bufDist = new StringBuffer(dist);

      StringBuffer bufHalfDist = new StringBuffer(bufDist.length());

      for (int i = 0; i < bufDist.length(); i++)
      {
         char c = bufDist.charAt(i);

         bufHalfDist.append((char) (c / 2));

         if(1 == c % 2 && i + 1 < bufDist.length())
         {
            bufDist.setCharAt(i +1 , (char) (bufDist.charAt(i+1) + _charRange.getRange()));
         }
      }
      return bufHalfDist.toString();
   }

   private String getDist(String min, String max)
   {
      int len = Math.max(getLen(min), getLen(max));

      StringBuffer bufMin = new StringBuffer();
      StringBuffer bufMax = new StringBuffer();
      if(null != min)
      {
         bufMin.append(min);
      }
      if(null != max)
      {
         bufMax.append(max);
      }

      minCharUpToLen(bufMin, len);
      minCharUpToLen(bufMax, len);

      bufMin.reverse();
      bufMax.reverse();

      StringBuffer bufDist = new StringBuffer(len);

      for (int i = 0; i < len; i++)
      {
         char cMax = bufMax.charAt(i);
         char cMin = bufMin.charAt(i);
         if(cMax >= cMin)
         {
            bufDist.append( (char)((cMax - cMin)) );
         }
         else
         {
            bufDist.append( (char)(( (cMax + _charRange.getRange()) - cMin)) );
            bufMin.setCharAt(i+1, (char) (bufMin.charAt(i+1) + 1));
         }
      }

      return bufDist.reverse().toString();
   }

   private int getLen(String s)
   {
      if(null == s)
      {
         return 0;
      }

      return s.length();


   }

   private void minCharUpToLen(StringBuffer toInit, int len)
   {
      for (int i = toInit.length(); i < len; i++)
      {
         toInit.append(_charRange.getMinChar());
      }
   }
}
