/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;


public class CodeCompletorModel
{
   private StandardCompletorModel _standardCompletorModel;
   private CompletionFunctionsModel _completionFunctionsModel;
   private boolean _functionsAdded;

   CodeCompletorModel(ISession session, CodeCompletionInfoCollection codeCompletionInfos)
   {
      _completionFunctionsModel = new CompletionFunctionsModel(session);
      _standardCompletorModel = new StandardCompletorModel(session, codeCompletionInfos);

   }

   public CodeCompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      if(false == _functionsAdded)
      {
         // This is here because addCompletionsAtListBegin() won't work when
         // schema info is still loading
         _functionsAdded = _standardCompletorModel.getCodeCompletionInfoCollection().addCompletionsAtListBegin(null, null, _completionFunctionsModel.getCompletions());
      }

      CodeCompletionCandidates functionResult = _completionFunctionsModel.getCompletionCandidates(textTillCarret);

      if(null == functionResult)
      {
         return _standardCompletorModel.getCompletionCandidates(textTillCarret);
      }
      else
      {
         return functionResult;
      }
   }

   public SQLTokenListener getSQLTokenListener()
   {
      return _standardCompletorModel.getSQLTokenListener();
   }

}
