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

import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * Allows to acces open Session windows by Session and by opening sequence.
 */
public class SessionWindowsHolder
{
   HashMap<IIdentifier, List<BaseSessionInternalFrame>> _framesBySessionIdentifier = new HashMap<IIdentifier, List<BaseSessionInternalFrame>>();
   HashMap<BaseSessionInternalFrame, IIdentifier> _sessionIdentifierByFrame = new HashMap<BaseSessionInternalFrame, IIdentifier>();
   Vector<BaseSessionInternalFrame> _framesInOpeningSequence = new Vector<BaseSessionInternalFrame>();

   public int addFrame(IIdentifier sessionIdentifier, BaseSessionInternalFrame internalFrame)
   {
      List<BaseSessionInternalFrame> windowList = _framesBySessionIdentifier.get(sessionIdentifier);
      if (windowList == null)
      {
         windowList = new ArrayList<BaseSessionInternalFrame>();
         _framesBySessionIdentifier.put(sessionIdentifier, windowList);
      }
      windowList.add(internalFrame);

      _framesInOpeningSequence.add(internalFrame);

      _sessionIdentifierByFrame.put(internalFrame, sessionIdentifier);

      return windowList.size();
   }

   public BaseSessionInternalFrame[] getFramesOfSession(IIdentifier sessionIdentifier)
   {
      List<BaseSessionInternalFrame> list = _framesBySessionIdentifier.get(sessionIdentifier);

      if(null == list)
      {
         return new BaseSessionInternalFrame[0];
      }
      else
      {
         return list.toArray(new BaseSessionInternalFrame[list.size()]);
      }
   }

   public void removeWindow(BaseSessionInternalFrame internalFrame)
   {
      IIdentifier sessionIdentifier = _sessionIdentifierByFrame.get(internalFrame);

      if(null == sessionIdentifier)
      {
         throw new IllegalArgumentException("Unknown Frame " + internalFrame.getTitle());
      }

      List<BaseSessionInternalFrame> framesOfSession = _framesBySessionIdentifier.get(sessionIdentifier);
      framesOfSession.remove(internalFrame);

      _framesInOpeningSequence.remove(internalFrame);
      _sessionIdentifierByFrame.remove(internalFrame);
   }

   public void removeAllWindows(IIdentifier sessionId)
   {
      BaseSessionInternalFrame[] framesOfSession = getFramesOfSession(sessionId);

      for (int i = 0; i < framesOfSession.length; i++)
      {
         _framesInOpeningSequence.remove(framesOfSession[i]);
         _sessionIdentifierByFrame.remove(framesOfSession[i]);
      }

      _framesBySessionIdentifier.remove(sessionId);
   }

   public BaseSessionInternalFrame getNextSessionWindow(BaseSessionInternalFrame sessionWindow)
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

   public BaseSessionInternalFrame getPreviousSessionWindow(BaseSessionInternalFrame sessionWindow)
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
