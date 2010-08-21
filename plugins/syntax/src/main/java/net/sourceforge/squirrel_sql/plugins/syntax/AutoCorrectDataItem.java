package net.sourceforge.squirrel_sql.plugins.syntax;

public class AutoCorrectDataItem
{
   private String err;
   private String corr;

   public AutoCorrectDataItem(String err, String corr)
   {
      this.setErr(err);
      this.setCorr(corr);
   }

   public AutoCorrectDataItem()
   {
   }

   public String getErr()
   {
      return err;
   }

   public void setErr(String err)
   {
      this.err = err;
   }

   public String getCorr()
   {
      return corr;
   }

   public void setCorr(String corr)
   {
      this.corr = corr;
   }
}
