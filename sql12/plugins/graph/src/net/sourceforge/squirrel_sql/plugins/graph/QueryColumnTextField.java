package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndColumn;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndEvent;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndHandler;

import javax.swing.*;
import java.awt.*;

public class QueryColumnTextField extends JTextField  implements DndColumn
{
   private DndHandler _dndHandler;

   public QueryColumnTextField(String text, DndCallback dndCallback, ISession session)
   {
      super(text);
      _dndHandler = new DndHandler(dndCallback, this, session);
   }

   public DndEvent getDndEvent()
   {
      return _dndHandler.getDndEvent();
   }

   public void setDndEvent(DndEvent dndEvent)
   {
      _dndHandler.setDndEvent(dndEvent);
   }

   @Override
   public Point getLocationInColumnTextArea()
   {
      return QueryTextArea.getLocationInQueryTextArea(this);
   }

}
