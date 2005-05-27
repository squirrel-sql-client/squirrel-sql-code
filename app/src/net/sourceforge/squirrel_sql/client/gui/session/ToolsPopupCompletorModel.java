package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.util.Collections;
import java.util.Vector;


public class ToolsPopupCompletorModel implements ICompletorModel
{

   Vector _toolsPopupCompletionInfos = new Vector();

   public CompletionCandidates getCompletionCandidates(String selectionStringBegin)
   {


      Vector ret = new Vector();
      int maxNameLen = 0;
      for (int i = 0; i < _toolsPopupCompletionInfos.size(); i++)
      {
         ToolsPopupCompletionInfo tpci = (ToolsPopupCompletionInfo) _toolsPopupCompletionInfos.get(i);
         if(tpci.getSelectionString().startsWith(selectionStringBegin))
         {
            ret.add(tpci);
            maxNameLen = Math.max(maxNameLen, tpci.getSelectionString().length());
         }
      }

      ToolsPopupCompletionInfo[] candidates = (ToolsPopupCompletionInfo[]) ret.toArray(new ToolsPopupCompletionInfo[ret.size()]);

      for (int i = 0; i < candidates.length; i++)
      {
         candidates[i].setMaxCandidateSelectionStringName(maxNameLen);
      }

      return new CompletionCandidates(candidates);
   }



   public void addAction(String selectionString, Action action)
   {
      _toolsPopupCompletionInfos.add(new ToolsPopupCompletionInfo(selectionString, action));

      Collections.sort(_toolsPopupCompletionInfos);

   }
}
