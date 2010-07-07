package net.sf.squirrel_sql;

/*
 * Copyright (C) 2010 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializerImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.XmlBeanUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which sets the System property "squirrelsql.version" based on the value of project version.  Sets the 
 * System property "squirrelsql.version" so that it can be used globally by the installers and the 
 * update-site projects.  It accepts the project version as an argument which it uses to decide what the 
 * squirrelsql.version should be.  If the project version ends with "-SNAPSHOT", then the squirrelsql.version 
 * will be set to Snapshot-{timestamp} where {timestamp} is the current timestamp in the form of 
 * YYYYMMDD_HHMM.  If however, the project version does not end with "-SNAPSHOT", then squirrelsql.version 
 * will be set to the value of the project version.  This mojo should only be executed once during the 
 * build since it manipulates globally-accessible properties.  This is particularly important in the case 
 * of snapshot project versions.
 * 
 * @goal set-version
 * @phase initialize
 */
public class SquirrelSqlVersionMojo extends AbstractMojo
{

	private org.apache.maven.plugin.logging.Log log = getLog();

	public static String VERSION_PROPERTY_KEY = "squirrelsql.version";
	
	/**
	 * The version for the release.
	 * 
	 * @parameter expression="${projectVersion}"
	 * @required
	 */
	private String projectVersion;

	public void setprojectVersion(String projectVersion)
	{
		this.projectVersion = projectVersion;
	}

	/**
	 * Does the main work provided by this plugin.
	 * 
	 * @throws MojoExecutionException
	 *            if the projectVersion isn't specified.
	 */
	public void execute() throws MojoExecutionException
	{
		if (projectVersion == null) { throw new MojoExecutionException("projectVersion cannot be null."); }

		String squirrelsqlVersion = projectVersion;
		
		if (!projectVersion.toLowerCase().endsWith("-snapshot"))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_kkmm");
			try
			{
				String date = sdf.format(new Date());
				squirrelsqlVersion = "Snapshot-" + date;
			}
			catch (IllegalStateException e)
			{
				log.error("Could not convert date format pattern " + timestampPattern);
				throw e;
			}
		}

		Properties props = project.getProperties();
		props.put(VERSION_PROPERTY_KEY, squirrelsqlVersion);
		System.setProperty(VERSION_PROPERTY_KEY, squirrelsqlVersion)

	}

}
