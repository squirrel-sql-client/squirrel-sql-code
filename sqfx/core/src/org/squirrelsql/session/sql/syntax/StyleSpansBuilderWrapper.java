package org.squirrelsql.session.sql.syntax;


import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;

public class StyleSpansBuilderWrapper
{
   private StyleSpansBuilder<Collection<String>> _spansBuilder = new StyleSpansBuilder<>();
   private boolean _hasSpans = false;
   private StyleSpans<Collection<String>> _styleSpans;

   public void add(Collection<String> col, int len)
   {
      _spansBuilder.add(col, len);
      _hasSpans = true;

   }

   public boolean hasSpans()
   {
      return _hasSpans;
   }

   public StyleSpans<? extends Collection<String>> create()
   {
      if (null == _styleSpans)
      {
         _styleSpans = _spansBuilder.create();
      }
      return _styleSpans;
   }
}
