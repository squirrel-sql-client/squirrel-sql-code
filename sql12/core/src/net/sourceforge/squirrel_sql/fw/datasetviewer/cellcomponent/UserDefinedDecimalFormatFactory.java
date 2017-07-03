package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class UserDefinedDecimalFormatFactory
{
   public static final String GROUPING_SEPARATOR_NONE = "NONE";

   public static NumberFormat createUserDefinedFormat(String userDefinedDecimalSeparator, String userDefinedGroupingSeparator, int userDefinedMinimumFractionDigits, int userDefinedMaximumFractionDigits)
   {
      DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();

      otherSymbols.setDecimalSeparator(userDefinedDecimalSeparator.charAt(0));


      if (false == GROUPING_SEPARATOR_NONE.equalsIgnoreCase(userDefinedGroupingSeparator))
      {
         otherSymbols.setGroupingSeparator(userDefinedGroupingSeparator.charAt(0));
      }

      NumberFormat numberFormat = new DecimalFormat(new DecimalFormat().toPattern(), otherSymbols);

      if (GROUPING_SEPARATOR_NONE.equalsIgnoreCase(userDefinedGroupingSeparator))
      {
         numberFormat.setGroupingUsed(false);
      }


      numberFormat.setMinimumFractionDigits(userDefinedMinimumFractionDigits);
      numberFormat.setMaximumFractionDigits(userDefinedMaximumFractionDigits);
      return numberFormat;
   }
}
