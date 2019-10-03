package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import java.awt.event.ActionEvent;

public class QuitSoundAction  extends SquirrelAction
{
   public QuitSoundAction()
   {
      super(Main.getApplication());
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      SoundPlayer.PLAYER.quit();
   }
}
