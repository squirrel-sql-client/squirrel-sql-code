package net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab;

import java.awt.*;

public interface LazyResultTabControllerFactory <T extends LazyTabControllerCtrl>
{
   T create();

   boolean isMatchingPanel(Component comp);
}
