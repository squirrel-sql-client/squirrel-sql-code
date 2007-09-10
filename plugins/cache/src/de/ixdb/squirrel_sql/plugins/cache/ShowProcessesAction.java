package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Vector;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;

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
