package org.squirrelsql.session.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenParser
{
   private final String _tokenAtCarret;
   private final List<String> _splits = new ArrayList<>();
   private String _ucUncompletedSplit;

   public TokenParser(String tokenAtCarret)
   {
      _tokenAtCarret = tokenAtCarret.trim();


      _splits.addAll(Arrays.asList(_tokenAtCarret.split("\\.")));


      if(_tokenAtCarret.endsWith("."))
      {
         _splits.add("");
      }

      _ucUncompletedSplit = getUncompletedSplit().toUpperCase();
   }

   public String getCompletedSplitAt(int ix)
   {
      if(ix >= completedSplitsCount())
      {
         throw new IllegalArgumentException("ix=" + ix + " exceeds " + completedSplitsCount());
      }

      return _splits.get(ix);
   }

   public boolean uncompletedSplitMatches(String name)
   {
      return name.toUpperCase().startsWith(_ucUncompletedSplit);
   }


   public int completedSplitsCount()
   {
      return _splits.size() - 1;

   }


   public static void main(String[] args)
   {
      System.out.println("completedSplitsCount " + new TokenParser("").completedSplitsCount());
      System.out.println("completedSplitsCount " + new TokenParser("dsdsf").completedSplitsCount());

      System.out.println("completedSplitsCount " + new TokenParser("a.ff.").completedSplitsCount());
   }

   public String getUncompletedSplit()
   {
      return _splits.get(_splits.size()-1);
   }

   public int getCompletedSplitsStringLength()
   {
      int ret = _tokenAtCarret.lastIndexOf('.');

      if(0 > ret)
      {
         return 0;
      }

      return ret + 1;
   }
}
