package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import java.awt.event.ActionEvent;

import com.intersys.objects.*;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.cache.Dataholder;
import com.intersys.cache.CacheObject;
import com.intersys.classes.CharacterStream;


public class ShowProcessesAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public ShowProcessesAction(IApplication app, Resources rsrc, CachePlugin plugin)
		throws IllegalArgumentException
	{
		super(app, rsrc);
   }

   public void setSession(ISession session)
   {
      _session = session;
   }


   public void actionPerformed(ActionEvent e)
   {
      new ShowProcessesCommand(_session).execute();
   }




}
