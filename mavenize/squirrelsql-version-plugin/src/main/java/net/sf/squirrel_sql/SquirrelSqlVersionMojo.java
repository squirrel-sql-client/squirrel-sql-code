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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which sets the System property "squirrelsql.version" based on the value of project version. Sets the
 * System property "squirrelsql.version" so that it can be used globally by the installers and the update-site
 * projects. It accepts the project version optionally as an argument which it uses to decide what the
 * squirrelsql.version should be. If this property is not configured, then the version of the project in which
 * this plugin is configured will be used. If the project version ends with "-SNAPSHOT", then the
 * squirrelsql.version will be set to Snapshot-{timestamp} where {timestamp} is the current timestamp in the
 * form of YYYYMMDD_HHMM. If however, the project version does not end with "-SNAPSHOT", then
 * squirrelsql.version will be set to the value of the project version. In case this mojo is configured and
 * executed more than once in a build, the squirrelsqlVersion property is set to the value that it was 
 * initially set to during the first execution of this plugin.
 * 
 * @goal set-version
 * @phase initialize
 */
public class SquirrelSqlVersionMojo extends AbstractMojo
{

	private org.apache.maven.plugin.logging.Log log = getLog();

	/** This is the property that will be set for use in the pom */
	public static String VERSION_PROPERTY_KEY = "squirrelsql.version";

	/** The format of the timestamp that follows the prefix SNAPSHOT- in the version string */
	public static String TIMESTAMP_PATTERN = "yyyyMMdd_kkmm";

	/** A place to keep the version after it has been generated. */
	private static String squirrelsqlVersion = null;

	/**
	 * The maven project in which this plugin is configured.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The version for the release.  This is optional and if it is not specified then the project.version 
	 * of the project in which this plugin is configured is used instead.
	 * 
	 * @parameter expression="${projectVersion}"
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
		// Skip creating a new version if we have already done so in the past.
		if (squirrelsqlVersion == null)
		{
			if (project == null) { throw new MojoExecutionException("project cannot be null."); }

			squirrelsqlVersion = project.getVersion();

			// override the project's version with the value of "projectVersion" if it is configured
			if (projectVersion != null && !"".equals(projectVersion))
			{
				squirrelsqlVersion = projectVersion;
			}

			// If the squirrelsqlVersion ends with -snapshot, then we need to generate a timestamp
			if (squirrelsqlVersion.toLowerCase().endsWith("-snapshot"))
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
					String timestampStr = sdf.format(new Date());
					squirrelsqlVersion = "Snapshot-" + timestampStr;
				}
				catch (IllegalStateException e)
				{

					log.error("Could not convert date format pattern " + TIMESTAMP_PATTERN);
					throw e;
				}
			}
		}
		
		// We set this as a property in the current project where the plugin is configured.  This is probably 
		// unnecessary.
		Properties props = project.getProperties();
		props.put(VERSION_PROPERTY_KEY, squirrelsqlVersion);
		
		// Also a global system property so that this is accessible from any pom as a pom property.
		System.setProperty(VERSION_PROPERTY_KEY, squirrelsqlVersion);

	}

}
