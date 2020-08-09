package net.sourceforge.squirrel_sql.fw.gui;

public class DontShowAgainResult
{
   private boolean _yes;
   private boolean _no;
   private boolean _cancel = true;

   private boolean _dontShowAgain;

   public void setYes(boolean yes)
   {
      _yes = yes;
      _no = false;
      _cancel = false;
   }

   public boolean isYes()
   {
      return _yes;
   }

   public void setNo(boolean no)
   {
      _no = no;
      _yes = false;
      _cancel = false;
   }

   public boolean isNo()
   {
      return _no;
   }

   public void setCancel(boolean cancel)
   {
      _cancel = cancel;
      _yes = false;
      _no = false;
   }

   public boolean isCancel()
   {
      return _cancel;
   }

   public void setDontShowAgain(boolean dontShowAgain)
   {
      _dontShowAgain = dontShowAgain;
   }

   public boolean isDontShowAgain()
   {
      return _dontShowAgain;
   }

}
