/*
 * Copyright (C) 2011 Rob Manning
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

package net.sf.squirrel_sql;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.dependency.CopyDependenciesMojo;
import org.apache.maven.project.MavenProject;

/**
 * This mojo extends the functionality of the copy dependencies mojo to all for specifying included artifacts 
 * using both the groupId and artifactId for finer precision. 
 * 
 * @goal copy-dependencies
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class CopySquirrelDependenciesMojo extends CopyDependenciesMojo
{
	private org.apache.maven.plugin.logging.Log log = getLog();

   /**
    * POM
    *
    * @parameter expression="${project}"
    * @readonly
    * @required
    */
   protected MavenProject project;

   public void setProject(MavenProject project) {
   	super.project = project;
   	this.project = project;
   }
	
   /**
    * @parameter property="stripVersion"
    */
   private boolean stripVersion;
   
   public void setStripVersion(boolean stripVersion) {
   	super.stripVersion = stripVersion;
   	this.stripVersion = stripVersion;
   }
   
   /**
    * @parameter property="outputDirectory"
    */
   private File outputDirectory;
   
   public void setOutputDirectory(File outputDirectory) {
   	super.outputDirectory = outputDirectory;
   	this.outputDirectory = outputDirectory;
   }
   
   /**
    * @parameter property="excludeGroupIds"
    */
   private String excludeGroupIds;
   
   public void setExcludeGroupIds(String excludeGroupIds) {
   	super.excludeGroupIds = excludeGroupIds;
   	this.excludeGroupIds = excludeGroupIds;
   }   
   
   /**
    * Used to look up Artifacts in the remote repository.
    *
    * @component
    */
   protected org.apache.maven.artifact.factory.ArtifactFactory factory;

   public void setFactory(org.apache.maven.artifact.factory.ArtifactFactory factory) {
   	this.factory = factory;
   	super.factory = factory;
   }
   
   
	/**
	 * This is expected to be a list of artifacts (/groupId/:/artifactId/) and should look like the following:
	 * 
	 * <pre>
	 * <includedArtifacts>
	 * 	<includedArtifact>net.sf.squirrel-sql:fw</includedArtifact>
	 *    <includedArtifact>log4j:log4j</includedArtifact>
	 *    ... 
	 * </includedArtifacts>
	 * </pre>
	 * 
	 * @parameter
	 */
	@SuppressWarnings("unchecked")
	private List includedArtifacts;
	
	private Set<String> artifactSet = new HashSet<String>();
	
	@SuppressWarnings("unchecked")
	public void setIncludedArtifacts(List includedArtifacts) {
		this.includedArtifacts = includedArtifacts;
		
		for (Object item : this.includedArtifacts) {
			String itemStr = (String)item;
			artifactSet.add(itemStr.toLowerCase());
		}
	}
	
   /**
    * Main entry into mojo. Gets the list of dependencies and iterates through
    * calling copyArtifact.
    *
    * @throws MojoExecutionException
    *             with a message if an error occurs.
    *
    * @see #getDependencies
    * @see #copyArtifact(Artifact, boolean)
    */
	@Override
   public void execute()
       throws MojoExecutionException
   {
		super.execute();
   }
	/**
	 * @see org.apache.maven.plugin.dependency.CopyDependenciesMojo#copyArtifact(org.apache.maven.artifact.Artifact, boolean)
	 */
	@Override
	protected void copyArtifact(Artifact arg0, boolean arg1, boolean arg2) throws MojoExecutionException
	{
		String key = arg0.getGroupId().trim().toLowerCase() + ":" + arg0.getArtifactId().trim().toLowerCase();
		if (artifactSet.contains(key)) {
			super.copyArtifact(arg0, arg1, arg2);
		}
	}
	
}
