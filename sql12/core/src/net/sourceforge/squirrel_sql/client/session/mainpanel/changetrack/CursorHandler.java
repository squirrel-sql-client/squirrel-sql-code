package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

public class CursorHandler
{
   private boolean _clickable = false;

   public void setClickable(boolean b)
   {
      if(b)
      {
         _clickable = true;
      }

   }

   public void reset()
   {
      _clickable = false;
   }

   public boolean isClickable()
   {
      return _clickable;
   }
}
