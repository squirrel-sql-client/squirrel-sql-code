/*
 * Copyright (C) 2003-2006 Gerd Wagner
 * colbell@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Allows to acces open Session windows by Session and by opening sequence.
 */
public class SessionWindowsHolder
{
   HashMap<IIdentifier, List<ISessionWidget>> _framesBySessionIdentifier = new HashMap<IIdentifier, List<ISessionWidget>>();
   HashMap<ISessionWidget, IIdentifier> _sessionIdentifierByFrame = new HashMap<ISessionWidget, IIdentifier>();
   Vector<ISessionWidget> _framesInOpeningSequence = new Vector<ISessionWidget>();

   public int addFrame(IIdentifier sessionIdentifier, ISessionWidget sessionWidget)
   {
      List<ISessionWidget> windowList = _framesBySessionIdentifier.get(sessionIdentifier);
      if (windowList == null)
      {
         windowList = new ArrayList<ISessionWidget>();
         _framesBySessionIdentifier.put(sessionIdentifier, windowList);
      }
      windowList.add(sessionWidget);

      _framesInOpeningSequence.add(sessionWidget);

      _sessionIdentifierByFrame.put(sessionWidget, sessionIdentifier);

      return windowList.size();
   }

   public ISessionWidget[] getFramesOfSession(IIdentifier sessionIdentifier)
   {
      List<ISessionWidget> list = _framesBySessionIdentifier.get(sessionIdentifier);

      if(null == list)
      {
         return new ISessionWidget[0];
      }
      else
      {
         return list.toArray(new ISessionWidget[list.size()]);
      }
   }

   public void removeWindow(ISessionWidget sessionWidget)
   {
      IIdentifier sessionIdentifier = _sessionIdentifierByFrame.get(sessionWidget);

      if(null == sessionIdentifier)
      {
         throw new IllegalArgumentException("Unknown Frame " + sessionWidget.getTitle());
      }

      List<ISessionWidget> framesOfSession = _framesBySessionIdentifier.get(sessionIdentifier);
      framesOfSession.remove(sessionWidget);

      _framesInOpeningSequence.remove(sessionWidget);
      _sessionIdentifierByFrame.remove(sessionWidget);
   }

   public void removeAllWindows(IIdentifier sessionId)
   {
      ISessionWidget[] framesOfSession = getFramesOfSession(sessionId);

      for (int i = 0; i < framesOfSession.length; i++)
      {
         _framesInOpeningSequence.remove(framesOfSession[i]);
         _sessionIdentifierByFrame.remove(framesOfSession[i]);
      }

      _framesBySessionIdentifier.remove(sessionId);
   }

   public ISessionWidget getNextSessionWindow(ISessionWidget sessionWindow)
   {
      int nextIx = _framesInOpeningSequence.indexOf(sessionWindow) + 1;

      if(nextIx < _framesInOpeningSequence.size())
      {
         return _framesInOpeningSequence.get(nextIx);
      }
      else
      {
         if(1 < _framesInOpeningSequence.size())
         {
            return _framesInOpeningSequence.get(0);
         }
         else
         {
            return sessionWindow;
         }
      }
   }

   public ISessionWidget getPreviousSessionWindow(ISessionWidget sessionWindow)
   {
      int prevIx = _framesInOpeningSequence.indexOf(sessionWindow) -1;

      if( 0 <= prevIx)
      {
         return _framesInOpeningSequence.get(prevIx);
      }
      else
      {

         if(1 < _framesInOpeningSequence.size())
         {
            return _framesInOpeningSequence.get(_framesInOpeningSequence.size() - 1);
         }
         else
         {
            return sessionWindow;
         }
      }
   }
}
