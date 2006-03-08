package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import javax.swing.*;

public class ToolsPopupCompletionInfo extends CompletionInfo
{
   private String _selectionString;
   private Action _action;
   private int _maxCandidateSelectionStringName;
   private String _description;

   public ToolsPopupCompletionInfo(String selectionString, Action action)
   {
      _selectionString = selectionString;
      _action = action;
   }

   public String getCompareString()
   {
      return _selectionString;
   }

   public String getCompletionString()
   {
      return "";
   }

   public Action getAction()
   {
      return _action;
   }

   public String toString()
   {
      return _selectionString + getDist() + getDescription();
   }

   private Object getDescription()
   {
      if(null == _description)
      {
         if(null != _action.getValue(Action.SHORT_DESCRIPTION))
         {
            _description = _action.getValue(Action.SHORT_DESCRIPTION).toString();
         }
         else
         {
            _description = "";
         }

         if(    false == _action instanceof SquirrelAction  // SquirrelAction descriptions already contain the accelerator 
             && null != _action.getValue(Resources.ACCELERATOR_STRING)
             && 0 != _action.getValue(Resources.ACCELERATOR_STRING).toString().trim().length())
         {
            _description += " (" + _action.getValue(Resources.ACCELERATOR_STRING) + ")";
         }
      }
      return _description;
   }

   private String getDist()
   {
      int len = _maxCandidateSelectionStringName - _selectionString.length() + 4;

      StringBuffer ret = new StringBuffer();

      for(int i=0; i < len; ++i)
      {
         ret.append(' ');
      }

      return ret.toString();
   }

   public void setMaxCandidateSelectionStringName(int maxCandidateSelectionStringName)
   {
      _maxCandidateSelectionStringName = maxCandidateSelectionStringName;
   }


   public String getSelectionString()
   {
      return _selectionString;
   }
}
