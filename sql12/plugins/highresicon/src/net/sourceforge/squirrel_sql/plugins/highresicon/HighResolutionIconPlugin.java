package net.sourceforge.squirrel_sql.plugins.highresicon;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.File;
import java.io.IOException;

public class HighResolutionIconPlugin extends DefaultPlugin
{
	static final String HIGH_RES_PREFS_FILE_NAME = "HiResPrefs.json";


	private HighResIconHandler _iconHandler;

	private HighResPrefJsonBean _highResPrefJsonBean = new HighResPrefJsonBean();

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "highresicon";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "High resolution icon";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.01";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Stanimir Stamenkov";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "doc/readme.txt";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @return Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "";
	}

	@Override
	public void load(IApplication app) throws PluginException
	{
		final File prefsFile = getHighResPrefsFile();

		if (prefsFile.exists())
		{
			_highResPrefJsonBean = JsonMarshalUtil.readObjectFromFileSave(prefsFile, HighResPrefJsonBean.class, new HighResPrefJsonBean());
		}

		IconScale.setFollowTextSize(_highResPrefJsonBean.isScaleIconsWithText());
		_iconHandler = new HighResIconHandler();
		app.setIconHandler(_iconHandler);
	}

	public File getHighResPrefsFile()
	{
		try
		{
			return new File(getPluginUserSettingsFolder().getFile(), HIGH_RES_PREFS_FILE_NAME);
		}
		catch (IOException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]{new HighResGlobalPreferencesPanel(this)};
	}

	public HighResPrefJsonBean getHighResPrefs()
	{
		return _highResPrefJsonBean;
	}
}
