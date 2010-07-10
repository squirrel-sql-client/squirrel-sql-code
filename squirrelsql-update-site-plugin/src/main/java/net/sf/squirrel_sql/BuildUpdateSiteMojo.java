package net.sf.squirrel_sql;

/*
 * Copyright (C) 2009 Rob Manning
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
 * Goal which builds the update site from a directory which is specified as an argument.
 * 
 * @goal build-update-site
 * @phase package
 */
public class BuildUpdateSiteMojo extends AbstractMojo
{

	private org.apache.maven.plugin.logging.Log log = getLog();

	private IOUtilities _iou = new IOUtilitiesImpl();

	/**
	 * Transitive dependencies that are not used directly by SQuirreL are excluded so that the update site
	 * doesn't become a poor man's maven repository. We only want artifacts that are used directly by SQuirreL
	 * or plugin code - no artifacts that are needed solely for build purposes should be included. Also, we
	 * exclude look and feel jars, since these need to be in a special directory beneath the LAF plugin
	 * directory. Lastly, we also exclude the translation jars artifact, since this is already unpacked by
	 * maven into the i18n directory, so they don't need to appear in core.  Note: any files found in the 
	 * release directory that match any pattern in this list will not be included and will also be deleted.
	 */
	private static final String[] excludedPatterns =
		new String[] { "maven", "plexus", "ant.jar", "ant-", "aopalliance", "axis", "classworlds",
				"commons-beanutils", "commons-digester", "commons-lang", "commons-validator", "doxia", "ehcache",
				"file-management", "tests", "hibernate-validator", "ilf-gpl", "javassist", "jboss", "jsch",
				"jtidy", "junit", "kunststoff", "looks", "metouia", "napkinlaf", "nimrodlf", "oalnf", "oro.jar",
				"persistence.jar", "skinlf", "squirrelsql-translations", "substance", "swingsetthemes",
				"tinylaf", "toniclf", "velocity", "wagon", "xml-apis" };

	/**
	 * Location of the directory which contains the release artifacts
	 * 
	 * @parameter expression="${releaseDirectory}"
	 * @required
	 */
	private String releaseDirectory;

	public void setReleaseDirectory(String releaseDirectory)
	{
		this.releaseDirectory = releaseDirectory;
	}

	/**
	 * The version for the release.
	 * 
	 * @parameter expression="${releaseVersion}"
	 * @required
	 */
	private String releaseVersion;

	public void setReleaseVersion(String releaseVersion)
	{
		this.releaseVersion = releaseVersion;
	}

	/**
	 * Does the main work provided by this plugin.
	 * 
	 * @throws MojoExecutionException
	 *            if the releaseDirectory isn't specified, doesn't exist or cannot be read.
	 */
	public void execute() throws MojoExecutionException
	{
		String channelName = "snapshot";
		String releaseName = "snapshot";

		if (!releaseVersion.toLowerCase().startsWith("snapshot"))
		{
			channelName = "stable";
			releaseName = "stable";
		}

		if (releaseDirectory == null) { throw new MojoExecutionException("releaseDirectory cannot be null."); }
		File releaseDir = new File(releaseDirectory);
		if (!releaseDir.exists()) { throw new MojoExecutionException("the specified releaseDirectory ("
			+ releaseDirectory + ") doesn't appear to exist."); }
		if (!releaseDir.canRead()) { throw new MojoExecutionException(
			"Cannot read the specified releaseDirectory: " + releaseDirectory); }

		File f = new File(releaseDirectory, UpdateUtil.RELEASE_XML_FILENAME);

		try
		{
			ChannelXmlBean channelBean =
				buildChannelRelease(channelName, releaseName, releaseVersion, releaseDir.getAbsolutePath());
			UpdateXmlSerializer serializer = new UpdateXmlSerializerImpl();
			if (log.isInfoEnabled())
			{
				log.info("Writing channel release bean to " + f);
			}
			serializer.write(channelBean, f.getAbsolutePath());
		}
		catch (IOException e)
		{
			throw new MojoExecutionException("Failed to create update site", e);
		}
	}

	/**
	 * This will create a ChannelXmlBean that describes a release as it is found in the specified directory,
	 * using the specified releaseName and version. This will excluded files that match any patterns in the
	 * excludePatterns array. Files that are excluded are also deleted from the release directory as they will
	 * not be shipped with the update site, and are therefore not required.
	 * 
	 * @param channelName
	 *           the name of the channel.
	 * @param releaseName
	 *           the name of the release.
	 * @param version
	 *           the version of the release.
	 * @param directory
	 *           the directory to use as the top-level of the release.
	 * @return a ChannelXmlBean that represents the specified parameters and files found in the release
	 *         directory that don't match any exclude pattern.
	 */
	private ChannelXmlBean buildChannelRelease(String channelName,
			String releaseName, String version, String directory)
			throws IOException {
		ChannelXmlBean result = new ChannelXmlBean();
		result.setName(channelName);
		ReleaseXmlBean releaseBean = new ReleaseXmlBean(releaseName, version);
		releaseBean.setCreateTime(new Date());
		File dir = new File(directory);
		for (File f : dir.listFiles()) {
			if (log.isInfoEnabled()) {
				log.info("Processing module directory: " + f);
			}
			if (f.isDirectory()) {
				// f is a module
				ModuleXmlBean module = new ModuleXmlBean();
				module.setName(f.getName());
				for (File a : f.listFiles()) {
					String filename = a.getName();
					if (isExcluded(filename)) {
						a.delete();
						continue;
					}
					if (log.isDebugEnabled()) {
						log.debug("Processing artifact file: " + filename);
					}
					String type = filename.substring(filename.indexOf(".") + 1);
					ArtifactXmlBean artifact = new ArtifactXmlBean();
					artifact.setName(a.getName());
					artifact.setType(type);
					artifact.setVersion(version);
					artifact.setSize(a.length());
					artifact.setChecksum(_iou.getCheckSum(a));
					module.addArtifact(artifact);
				}
				releaseBean.addmodule(module);
			}
		}
		result.setCurrentRelease(releaseBean);
		return result;
	}

	/**
	 * Searches for the specified filename in the excludedPatterns array and compares to see if any pattern
	 * matches.
	 * 
	 * @param filename
	 *           the name of the artifact to look for.
	 * @return true if the filename matches any exclude pattern; false otherwise.
	 */
	private boolean isExcluded(String filename) {
		boolean result = false;
		for (String pattern : excludedPatterns) {
			if (filename.matches(".*" + pattern + ".*")) {
				if (log.isDebugEnabled()) {
					log.debug("Excluding filename (" + filename
							+ " because it matched an exclude pattern: "
							+ pattern);
				}
				result = true;
				break;
			}
		}
		return result;
	}
}
