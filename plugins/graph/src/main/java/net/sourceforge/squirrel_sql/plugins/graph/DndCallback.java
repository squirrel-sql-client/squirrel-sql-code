package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.event.MouseEvent;
import java.awt.*;

public interface DndCallback
{
   void dndImportDone(DndEvent e, Point dropPoint);

   DndEvent createDndEvent(MouseEvent lastDndExportedMousePressedEvent);
}
