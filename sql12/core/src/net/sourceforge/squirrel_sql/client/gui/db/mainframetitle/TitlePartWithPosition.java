package net.sourceforge.squirrel_sql.client.gui.db.mainframetitle;

public class TitlePartWithPosition
{
   private final String _titlePart;
   private final PositionInMainFrameTitle _pos;

   public TitlePartWithPosition(String titlePart, PositionInMainFrameTitle pos)
   {
      _titlePart = titlePart;
      _pos = pos;
   }

   public String getTitlePart()
   {
      return _titlePart;
   }

   public PositionInMainFrameTitle getPos()
   {
      return _pos;
   }
}
