/*
 * Copyright (C) 2005 Gerd Wagner
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;

import java.util.Hashtable;

public class AutoCorrectProvider
{
   public Hashtable<String, String> getAutoCorrects()
   {
      AutoCorrectData acd = getAutoCorrectData();

      if(acd.isEnableAutoCorrects())
      {
         return acd.getAutoCorrectsHash();
      }
      else
      {
         return new Hashtable<>();
      }
   }

   public AutoCorrectData getAutoCorrectData()
   {
      return Main.getApplication().getSyntaxManager().getAutoCorrectData();
   }

   public void setAutoCorrects(Hashtable<String, String> newAutoCorrects, boolean enableAutoCorrects)
   {
      Main.getApplication().getSyntaxManager().saveAutoCorrectData(newAutoCorrects, enableAutoCorrects);
   }
}
