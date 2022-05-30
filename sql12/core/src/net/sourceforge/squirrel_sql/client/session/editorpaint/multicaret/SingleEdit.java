package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SingleEdit
{
   public static final SingleEdit EMPTY = new SingleEdit(null, -1, -1);

   private final String _text;
   private int _start;
   private int _end;
   private Object _highLightTag;
   private boolean _fromSelection;

   public SingleEdit(String text, int start, int end)
   {
      _text = text;
      _start = start;
      _end = end;
   }

   public String getText()
   {
      return _text;
   }

   public int getStart()
   {
      return _start;
   }

   public int getEnd()
   {
      return _end;
   }


   public boolean isEmpty()
   {
      return StringUtilities.isEmpty(_text, true);
   }

   public void setHighLightTag(Object highLightTag)
   {
      _highLightTag = highLightTag;
   }

   public Object getHighLightTag()
   {
      return _highLightTag;
   }

   public boolean isFromSelection()
   {
      return _fromSelection;
   }

   public void setFromSelection(boolean fromSelection)
   {
      _fromSelection = fromSelection;
   }

   public void adjustByAnchestorShift(int shiftLen)
   {
      _start += shiftLen;
      _end += shiftLen;
   }
}
