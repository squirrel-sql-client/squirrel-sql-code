package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;

import java.util.Vector;

public class OpeningSequenceBasedSessionWidgetIndexHandler implements ISessionWidgetIndexHandler
{
   private Vector<ISessionWidget> _framesInOpeningSequence;

   public OpeningSequenceBasedSessionWidgetIndexHandler(Vector<ISessionWidget> framesInOpeningSequence)
   {
      _framesInOpeningSequence = framesInOpeningSequence;
   }



   @Override
   public ISessionWidget getPreviousWidget(ISessionWidget sessionWindow)
   {
      int prevIx = _framesInOpeningSequence.indexOf(sessionWindow) -1;

      if( 0 <= prevIx )
      {
         return _framesInOpeningSequence.get(prevIx);
      }

      return null;
   }

   @Override
   public ISessionWidget getNextWidget(ISessionWidget sessionWindow)
   {
      int nextIx = _framesInOpeningSequence.indexOf(sessionWindow) + 1;

      if(nextIx < _framesInOpeningSequence.size())
      {
         return _framesInOpeningSequence.get(nextIx);
      }

      return null;
   }

   @Override
   public int size()
   {
      return _framesInOpeningSequence.size();
   }


   @Override
   public ISessionWidget getLastSessionWidget()
   {
      return _framesInOpeningSequence.get(_framesInOpeningSequence.size() - 1);
   }

   @Override
   public ISessionWidget getFirstSessionWidget()
   {
      return _framesInOpeningSequence.get(0);
   }
}
