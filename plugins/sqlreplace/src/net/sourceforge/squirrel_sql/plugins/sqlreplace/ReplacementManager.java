/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

/**
 * Manages replacements. Including loading and saving to a properties file.
 * 
 * @author Dieter
 */
public class ReplacementManager
{

	/**
	 * The file to save/load replacements to/from
	 */
	private File replacementFile;

	private ArrayList<Replacement> replacements = new ArrayList<Replacement>();

	IMessageHandler mpan;

	private final static ILogger log = LoggerController.createLogger(SQLReplacePlugin.class);

	/**
	 * @param _plugin
	 */
	public ReplacementManager(SQLReplacePlugin _plugin)
	{
		try
		{
			replacementFile = new File(_plugin.getPluginUserSettingsFolder(), "sqlreplacement.xml");
			mpan = _plugin.getApplication().getMessageHandler();
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load the stored replacement.
	 */
	protected void load() throws IOException
	{
		replacements.clear();
		try
		{
			final XMLBeanReader xmlin = new XMLBeanReader();

			if (replacementFile.exists())
			{
				xmlin.load(replacementFile, getClass().getClassLoader());
				for (final Object bean : xmlin)
				{
					if (bean instanceof Replacement)
					{
						replacements.add((Replacement) bean);
					}
				}
			}
		}
		catch (final XMLException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save the replacement.
	 */
	protected void save()
	{
		try
		{
			final XMLBeanWriter xmlout = new XMLBeanWriter();

			for (final Replacement rep : replacements)
			{
				xmlout.addToRoot(rep);
			}

			xmlout.save(replacementFile);
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected Iterator<Replacement> iterator()
	{
		return replacements.iterator();
	}

	public void removeAll()
	{
		replacements = new ArrayList<Replacement>();
	}

	/**
	 * Sets the content from the editor section and add it to the replacementarray
	 * 
	 * @param content
	 */
	public void setContentFromEditor(String content)
	{
		final String cont = content;
		final String[] lines = cont.split("\n");
		replacements.clear();
		for (final String line : lines)
		{
			if (line != null && line.length() != 0)
			{
				final String[] s = line.split("=");
				if (s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0)
				{
					final Replacement ro = new Replacement(s[0].trim(), s[1].trim());
					replacements.add(ro);
				}
			}
		}
		this.save();
	}

	/*
	 * 
	 */
	public String getContent()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<Replacement> it = replacements.iterator();
		while (it.hasNext())
		{
			final Replacement r = it.next();
			sb.append(r.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Here we replace the original SQL Statement with the Set of Vars from our ReplamentList
	 * 
	 * @param buffer
	 * @return
	 */
	public String replace(StringBuffer buffer)
	{
		String toReplace = buffer.toString();
		final Iterator<Replacement> it = replacements.iterator();
		while (it.hasNext())
		{
			final Replacement r = it.next();
			if (toReplace.indexOf(r.getVariable()) > -1)
			{
				String replacementMsg = "Replace-Rule: " + r.toString();
				if (log.isInfoEnabled()) {
					log.info(replacementMsg);
				}
				mpan.showMessage(replacementMsg);
				
				toReplace = toReplace.replace(r.getVariable(), r.getValue());
			}
		}

		return toReplace;
	}
}
