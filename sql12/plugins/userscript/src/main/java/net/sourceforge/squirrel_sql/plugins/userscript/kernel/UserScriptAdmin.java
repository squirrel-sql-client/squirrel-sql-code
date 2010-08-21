package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.userscript.UserScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.userscript.FrameWorkAcessor;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;

public class UserScriptAdmin
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UserScriptAdmin.class);


	public static final String SCRIPT_PROPERTIES_FILE = "UserScriptProperties.xml";

	public static final boolean TARGET_TYPE_DB_OBJECT = false;
	public static final boolean TARGET_TYPE_SQL = true;

	private UserScriptPlugin m_plugin;
	private ISession m_session;

	public UserScriptAdmin(UserScriptPlugin plugin, ISession session)
	{
		try
		{
			m_plugin = plugin;
			m_session = session;

			ScriptProps props = readScriptProps();
			if(null != props)
			{
				for (int i = 0; i < props.getScripts().length; i++)
				{
					if(props.getScripts()[i].isShowInStandard())
					{


						GenericScriptPopupAction actDbObject = new GenericScriptPopupAction(props.getScripts()[i], this, TARGET_TYPE_DB_OBJECT);

                  //IObjectTreeAPI api = m_session.getObjectTreeAPI(m_plugin);
                  IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(m_session, m_plugin);

						api.addToPopup(DatabaseObjectType.TABLE, actDbObject);
						api.addToPopup(DatabaseObjectType.PROCEDURE, actDbObject);
						api.addToPopup(DatabaseObjectType.SESSION, actDbObject);

						GenericScriptPopupAction actSql = new GenericScriptPopupAction(props.getScripts()[i], this, TARGET_TYPE_SQL);

						//m_session.getSQLPanelAPI(m_plugin).addToSQLEntryAreaMenu(actSql);
						FrameWorkAcessor.getSQLPanelAPI(m_session, m_plugin).addToSQLEntryAreaMenu(actSql);
					}
				}
			}
			initUserScriptClassLoader(props);


		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	ScriptProps readScriptProps()
	{
		try
		{
			if(getScriptPropertiesFile().exists())
			{
				XMLBeanReader br = new XMLBeanReader();
				br.load(getScriptPropertiesFile(), this.getClass().getClassLoader());
				return (ScriptProps) br.iterator().next();
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);

		}
	}

	private void initUserScriptClassLoader(ScriptProps props)
	{
		try
		{
			URL[] cp;
			if(null == props)
			{
				cp = new URL[0];
			}
			else
			{
				cp = new URL[props.getExtraClassPath().length];

				for (int i = 0; i < props.getExtraClassPath().length; i++)
				{
					String path = props.getExtraClassPath()[i].getEntry();
					cp[i] = (new File(path)).toURI().toURL();
				}
			}
			m_plugin.setUserScriptClassLoader(URLClassLoader.newInstance(cp));
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	private File getScriptPropertiesFile()
	{
		try
		{
			return new File(m_plugin.getPluginUserSettingsFolder().getPath() + File.separator + SCRIPT_PROPERTIES_FILE);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public ScriptTargetCollection getTargets(boolean targetType)
	{
		if(targetType == TARGET_TYPE_DB_OBJECT)
		{
			//IObjectTreeAPI api = m_session.getObjectTreeAPI(m_plugin);
			IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(m_session, m_plugin);

         IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

			ScriptTargetCollection targets = new ScriptTargetCollection();
			for (int i = 0; i < dbObjs.length; i++)
			{
				if(dbObjs[i].getDatabaseObjectType().equals(DatabaseObjectType.TABLE))
				{
					ITableInfo tableInfo = (ITableInfo) dbObjs[i];
					if("VIEW".equals(tableInfo.getType()))
					{
						targets.add(new ScriptTarget(dbObjs[i].getSimpleName(), ScriptTarget.DB_OBJECT_TYPE_VIEW));
					}
					else if("TABLE".equals(tableInfo.getType()) || "SYSTEM TABLE".equals(tableInfo.getType()))
					{
						targets.add(new ScriptTarget(dbObjs[i].getSimpleName(), ScriptTarget.DB_OBJECT_TYPE_TABLE));
					}
				}
				else if(dbObjs[i].getDatabaseObjectType().equals(DatabaseObjectType.PROCEDURE))
				{
					targets.add(new ScriptTarget(dbObjs[i].getSimpleName(), ScriptTarget.DB_OBJECT_TYPE_PROCEDURE));
				}
				else if(dbObjs[i].getDatabaseObjectType().equals(DatabaseObjectType.SESSION))
				{
					targets.add(new ScriptTarget(dbObjs[i].getSimpleName(), ScriptTarget.DB_OBJECT_TYPE_CONNECTION));
				}
			}
			return targets;


		}
		else // targetType == TARGET_TYPE_SQL
		{
			ScriptTargetCollection targets = new ScriptTargetCollection();

         //String sql = m_session.getSQLPanelAPI(m_plugin).getSQLScriptToBeExecuted();
         String sql = FrameWorkAcessor.getSQLPanelAPI(m_session, m_plugin).getSQLScriptToBeExecuted();

         targets.add(new ScriptTarget(sql, ScriptTarget.DB_OBJECT_TYPE_SQL_STATEMENT));
			return targets;
		}
	}

	public UserScriptPlugin getPlugin()
	{
		return m_plugin;
	}

	public ISession getSession()
	{
		return m_session;
	}

	public void writeScriptProps(ScriptProps scriptProps)
	{
		try
		{
			XMLBeanWriter bw = new XMLBeanWriter(scriptProps);
			bw.save(getScriptPropertiesFile());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void refreshExtraClassPath()
	{
		initUserScriptClassLoader(readScriptProps());
	}

	public void executeScript(JFrame ownerFrame, Script script, ScriptTargetCollection targets)
	{
		//ScriptEnvironment env = new ScriptEnvironment(m_session.getSQLPanelAPI(m_plugin), ownerFrame);
		ScriptEnvironment env = new ScriptEnvironment(FrameWorkAcessor.getSQLPanelAPI(m_session, m_plugin), ownerFrame);

		try
		{
			ClassLoader loader = m_plugin.getUserScriptClassLoader();
			Class<?> scriptClass = 
				Class.forName(script.getScriptClass(), false, loader);
			Object scriptInst = scriptClass.newInstance();

			Field f = scriptInst.getClass().getField("environment");
			f.set(scriptInst, env);
			Method m = scriptInst.getClass().getMethod("execute", new Class[]{String.class, String.class, Connection.class});

			ScriptTarget[] buf = targets.getAll();
			for (int i = 0; i < buf.length; i++)
			{
				m.invoke(scriptInst, new Object[]{buf[i].getTargetType(), buf[i].getTargetInfo(), m_session.getSQLConnection().getConnection()});
				env.flushAll();
			}
			env.setExecutionFinished(true);
		}
		catch (Exception e)
		{
			// i18n[userscript.scriptAdminErr=Err Msg]
			e.printStackTrace(env.createPrintStream(s_stringMgr.getString("userscript.scriptAdminErr")));
			env.flushAll();
			env.setExecutionFinished(false);
			throw new RuntimeException(e);
		}

	}

}
