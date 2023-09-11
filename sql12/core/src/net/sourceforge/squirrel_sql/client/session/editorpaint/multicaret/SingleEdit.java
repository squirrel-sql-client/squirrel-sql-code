package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SingleEdit
{
   //public static final SingleEdit EMPTY = new SingleEdit(null, -1, -1);

   private final String _text;
   private int _start;
   private int _end;
   private Object _highLightTag;
   private boolean _initialEdit;

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


   public boolean isEmptySelection()
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

   public boolean isInitialEdit()
   {
      return _initialEdit;
   }

   public void setInitialEdit(boolean initialEdit)
   {
      _initialEdit = initialEdit;
   }

   public void adjustByAncestorShift(int shiftLen)
   {
      _start += shiftLen;
      _end += shiftLen;
   }

   public int getLength()
   {
      if( null == _text )
      {
         return 0;
      }

      return _text.length();
   }
}
