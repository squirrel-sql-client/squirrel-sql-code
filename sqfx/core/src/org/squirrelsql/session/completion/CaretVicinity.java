package org.squirrelsql.session.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaretVicinity
{
   private final String _tokenTillCaret;
   private final List<String> _tokenTillCaretSplits = new ArrayList<>();
   private final String _lineTillCaret;
   private String _ucUncompletedSplit;

   public CaretVicinity(String tokenTillCaret, String lineTillCaret)
   {
      _lineTillCaret = lineTillCaret.trim();
      _tokenTillCaret = tokenTillCaret.trim();


      _tokenTillCaretSplits.addAll(Arrays.asList(_tokenTillCaret.split("\\.")));


      if(_tokenTillCaret.endsWith("."))
      {
         _tokenTillCaretSplits.add("");
      }

      _ucUncompletedSplit = getUncompletedSplit().toUpperCase();
   }

   public String getCompletedSplitAt(int ix)
   {
      if(ix >= completedSplitsCount())
      {
         throw new IllegalArgumentException("ix=" + ix + " exceeds " + completedSplitsCount());
      }

      return _tokenTillCaretSplits.get(ix);
   }

   public boolean uncompletedSplitMatches(String name)
   {
      return name.toUpperCase().startsWith(_ucUncompletedSplit);
   }


   public int completedSplitsCount()
   {
      return _tokenTillCaretSplits.size() - 1;

   }


   public String getUncompletedSplit()
   {
      return _tokenTillCaretSplits.get(_tokenTillCaretSplits.size()-1);
   }

   public int getCompletedSplitsStringLength()
   {
      int ret = _tokenTillCaret.lastIndexOf('.');

      if(0 > ret)
      {
         return 0;
      }

      return ret + 1;
   }

   public String getTokenTillCaret()
   {
      return _tokenTillCaret;
   }

   public String getLineTillCaret()
   {
      return _lineTillCaret;
   }

   public static void main(String[] args)
   {
      System.out.println("completedSplitsCount " + new CaretVicinity("", "").completedSplitsCount());
      System.out.println("completedSplitsCount " + new CaretVicinity("dsdsf", "sadds").completedSplitsCount());

      System.out.println("completedSplitsCount " + new CaretVicinity("a.ff.", "asdfd").completedSplitsCount());
   }
}
