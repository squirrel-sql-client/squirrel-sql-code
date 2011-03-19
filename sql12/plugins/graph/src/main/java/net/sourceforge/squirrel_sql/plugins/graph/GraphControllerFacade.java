package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;

public interface GraphControllerFacade
{
   void showDock(JPanel panel, int lastHeight);
   void hideDock();

   void showPopupAbove(Point loc, GraphControllerPopupListener graphControllerPopupListener);

   void hidePopup();
}
