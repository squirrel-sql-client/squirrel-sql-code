package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ToolsPopupCompletorModel implements ICompletorModel
{

   List<ToolsPopupCompletionInfo> _toolsPopupCompletionInfos = new ArrayList<>();

   public CompletionCandidates getCompletionCandidates(String selectionStringBegin)
   {


      List<ToolsPopupCompletionInfo> ret = new ArrayList<>();
      int maxNameLen = 0;
      for (int i = 0; i < _toolsPopupCompletionInfos.size(); i++)
      {
         ToolsPopupCompletionInfo tpci = _toolsPopupCompletionInfos.get(i);
         if(tpci.getSelectionString().startsWith(selectionStringBegin))
         {
            ret.add(tpci);
            maxNameLen = Math.max(maxNameLen, tpci.getSelectionString().length());
         }
      }

      ToolsPopupCompletionInfo[] candidates = ret.toArray(new ToolsPopupCompletionInfo[ret.size()]);

      for (int i = 0; i < candidates.length; i++)
      {
         candidates[i].setMaxCandidateSelectionStringName(maxNameLen);
      }

      return new CompletionCandidates(candidates);
   }



   public void addAction(String selectionString, Action action)
   {
      _toolsPopupCompletionInfos.add(new ToolsPopupCompletionInfo(selectionString, action));

      Collections.sort(_toolsPopupCompletionInfos, new ToolsPopupCompletionInfoComparator());
      
   }
   
   private static class ToolsPopupCompletionInfoComparator implements Comparator<ToolsPopupCompletionInfo>
   {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(ToolsPopupCompletionInfo arg0, ToolsPopupCompletionInfo arg1)
        {
            return arg0.getSelectionString().compareTo(arg1.getSelectionString());
        }
       
   }
   
}
