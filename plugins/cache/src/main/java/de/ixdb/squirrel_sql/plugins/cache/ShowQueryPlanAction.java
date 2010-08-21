package de.ixdb.squirrel_sql.plugins.cache;

import com.intersys.cache.CacheObject;
import com.intersys.cache.Dataholder;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.classes.CharacterStream;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheReader;
import com.intersys.objects.Database;
import com.intersys.objects.CacheException;
import net.n3.nanoxml.*;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Statement;


public class ShowQueryPlanAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private CachePlugin _plugin;

   public static final String HREF_CLOSE_QUERY_PLAN = "#close query plan";
   private QueryPlanTab _queryPlanTab;

   public ShowQueryPlanAction(IApplication app, Resources rsrc, CachePlugin plugin)
		throws IllegalArgumentException
	{
		super(app, rsrc);
      _plugin = plugin;
   }

   public void setSession(ISession session)
   {
      _session = session;
   }


	public void actionPerformed(ActionEvent evt)
	{
      new ShowQueryPlanCommand(_session).execute();
   }



}
