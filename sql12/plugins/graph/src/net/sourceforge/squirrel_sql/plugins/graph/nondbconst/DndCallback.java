package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import java.awt.*;
import java.awt.event.MouseEvent;

public interface DndCallback
{
   void dndImportDone(DndEvent e, Point dropPoint);

   DndEvent createDndEvent(MouseEvent lastDndExportedMousePressedEvent);
}
