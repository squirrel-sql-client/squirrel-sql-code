package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.services.*;
import org.squirrelsql.session.completion.CaretVicinity;
import org.squirrelsql.session.completion.CompletionCandidate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InnerJoinGenerator
{
   public static final java.lang.String FUNCTION_START = "#";
   public static final java.lang.String FUNCTION_NAME = FUNCTION_START + "i";

   private I18n _i18n = new I18n(getClass());
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private CaretVicinity _caretVicinity;

   public InnerJoinGenerator(CaretVicinity caretVicinity)
   {
      _caretVicinity = caretVicinity;
   }

   private List<String> getReplacements()
   {
      String lineTillCaret = _caretVicinity.getLineTillCaret();
      int fctBegin = lineTillCaret.indexOf(FUNCTION_NAME);

      if (-1 == fctBegin)
      {
         return Collections.EMPTY_LIST;
      }

      String tableList = lineTillCaret.substring(fctBegin + FUNCTION_NAME.length());

      List<String> filteredSplits = CollectionUtil.filter(tableList.split(","), s -> false == Utils.isEmptyString(s));

      if(2 > filteredSplits.size())
      {
         _mh.warning(_i18n.t("codecompletion.function.needsTwoArgs"));
         return new ArrayList<>();
      }


      String ret = "";

      for (int i = 1; i < filteredSplits.size(); i++)
      {
         if(i > 1)
         {
            ret += "\n";
         }

         ret += "INNER JOIN " + filteredSplits.get(i) + getJoinColumns(filteredSplits.get(i-1), filteredSplits.get(i));
      }


      return Arrays.asList(ret);
   }

   private String getJoinColumns(String table1, String table2)
   {
      // TODO
      return " ON " + table1 + ".col = " + table2 + ".col";
   }

   public List<CompletionCandidate> getCompletionCandidates()
   {
      List<String> replacements = getReplacements();

      if (0 < replacements.size())
      {
         return CollectionUtil.transform(replacements, r -> new InnerJoinCompletionCandidate(r));
      }


      if(_caretVicinity.uncompletedSplitMatches(FUNCTION_NAME))
      {
         return Arrays.asList(new InnerJoinCompletionCandidate());
      }

      return new ArrayList<>();
   }
}
