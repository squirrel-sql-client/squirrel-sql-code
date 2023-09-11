package net.sourceforge.squirrel_sql.fw.gui.textfind;

public class MatchBounds
{
   private int _beginIx;
   private int _endIx;

   public MatchBounds()
   {
   }

   public MatchBounds(int beginIx, int endIx)
   {
      _beginIx = beginIx;
      _endIx = endIx;
   }

   public void setBeginIx(int beginIx)
   {
      _beginIx = beginIx;
   }

   public void setEndIx(int endIx)
   {
      _endIx = endIx;
   }

   public int getBeginIx()
   {
      return _beginIx;
   }

   public int getEndIx()
   {
      return _endIx;
   }
}

