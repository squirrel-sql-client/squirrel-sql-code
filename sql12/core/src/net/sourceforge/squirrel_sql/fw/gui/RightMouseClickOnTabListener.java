package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.*;

@FunctionalInterface
public interface RightMouseClickOnTabListener
{
   void rightMouseClickedOnTabComponent(int tabIndex, Component tabComponent, int clickPosX, int clickPosY);
}
