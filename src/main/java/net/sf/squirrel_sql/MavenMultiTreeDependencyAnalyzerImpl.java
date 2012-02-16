package net.sf.squirrel_sql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.squirrel_sql.persistence.CrossSourceTreeDependency;
import net.sf.squirrel_sql.persistence.PomDependency;
import net.sf.squirrel_sql.persistence.PomDependencyDao;
import net.sf.squirrel_sql.persistence.PomDependencyFactory;
import net.sf.squirrel_sql.persistence.PomFile;
import net.sf.squirrel_sql.persistence.PomFileDao;
import net.sf.squirrel_sql.persistence.PomFileFactory;
import net.sf.squirrel_sql.persistence.PomFileImpl;
import net.sf.squirrel_sql.util.PomFileUtils;
import net.sf.squirrel_sql.util.PomFileUtilsFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.model.Dependency;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class MavenMultiTreeDependencyAnalyzerImpl implements MavenMultiTreeDependencyAnalyzer
{
	private static final Logger log = LoggerFactory.getLogger(MavenMultiTreeDependencyAnalyzerImpl.class);

	private final IOFileFilter pomFileFilter = new NameFileFilter("pom.xml");

	private List<String> sourceTreePaths;

	private PomFileDao pomFileDao = null;

	private PomDependencyDao pomDependencyDao = null;

	private PomDependencyFactory pomDependencyFactory = null;

	private PomFileFactory pomFileFactory = null;

	private PomFileUtilsFactory pomFileUtilsFactory = null;

	private List<PomFileUtils> pomFileUtilsList = null;

	/**
	 * @throws IOException
	 * @see net.sf.squirrel_sql.MavenMultiTreeDependencyAnalyzer#analyzePaths()
	 */
	@Transactional
	public void analyzePaths() throws IOException
	{
		for (String sourceTreePath : sourceTreePaths)
		{
			File pathFile = new File(sourceTreePath);
			findPoms(pathFile);
		}
		for (String sourceTreePath : sourceTreePaths)
		{
			File pathFile = new File(sourceTreePath);
			findDependencies(pathFile);
		}
	}

	public void detectCycles() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Attempting to detect cycles.");
		}
		DirectedGraph<String, DefaultEdge> g =
         new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		for (String sourceTreePath : sourceTreePaths) {
			g.addVertex(sourceTreePath);
		}
		for (CrossSourceTreeDependency dep : pomDependencyDao.findCrossTreeDependencies()) {
			g.addEdge(dep.getTreeRootDir(), dep.getDependUponTreeRootDir());
		}
		CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
		if (cycleDetector.detectCycles()) {
			log.info("A cycle was detected between the following source trees:");
			for (String sourceTreePath : cycleDetector.findCycles()) {
				log.info("\t"+sourceTreePath);
			}
			log.info("The following pom dependencies create the cycle:");
			for (String sourceTreePath : cycleDetector.findCycles()) {
				for (PomDependency dep : pomDependencyDao.findDependenciesBySourceTreePath(sourceTreePath)) {
					PomFile pomFile = dep.getPomFile();
					PomFile dependsUponPomFile = dep.getDependsUponPomFile();
					log.info("\t"+pomFile.getProjectGroupId()+":"+pomFile.getProjectArtifactId()+" => "+
						dependsUponPomFile.getProjectGroupId()+":"+dependsUponPomFile.getProjectArtifactId());
				}
			}
		}
	}
	
	private void findPoms(File sourceTreePathFile) throws IOException
	{
		Iterator<File> pomFiles =
			FileUtils.iterateFiles(sourceTreePathFile, pomFileFilter, TrueFileFilter.INSTANCE);
		pomFileUtilsList = new ArrayList<PomFileUtils>();

		// First pass will create a PomFileUtils object for each pom file and insert all pom file records, in
		// preparation for the second pass.
		while (pomFiles.hasNext())
		{
			File file = pomFiles.next();
			if (file.getAbsolutePath().contains("/target/"))
			{
				continue;
			}
			if (log.isDebugEnabled())
			{
				log.debug("Processing file: " + file);
			}
			PomFileUtils pomFileUtils = pomFileUtilsFactory.create(file);
			pomFileUtilsList.add(pomFileUtils);

			PomFile pomFile =
				pomFileFactory.createPomFile(sourceTreePathFile.getAbsolutePath(), file.getAbsolutePath(),
					pomFileUtils.getGroupId(), pomFileUtils.getArtifactId(), pomFileUtils.getProjectName());
			pomFileDao.insertPom(pomFile);
		}

	}

	private void findDependencies(File sourceTreePathFile) throws IOException
	{
		Iterator<File> pomFiles =
			FileUtils.iterateFiles(sourceTreePathFile, pomFileFilter, TrueFileFilter.INSTANCE);
		pomFileUtilsList = new ArrayList<PomFileUtils>();

		// First pass will create a PomFileUtils object for each pom file and insert all pom file records, in
		// preparation for the second pass.
		while (pomFiles.hasNext())
		{
			File file = pomFiles.next();
			if (file.getAbsolutePath().contains("/target/"))
			{
				continue;
			}
			if (log.isDebugEnabled())
			{
				log.debug("Processing file: " + file);
			}
			PomFileUtils pomFileUtils = pomFileUtilsFactory.create(file);

			// Second pass gets dependencies and if a dependency is on a pom from the first pass,
			// it gets stored as a dependency.
			PomFile pomFile =
				pomFileDao.findByGroupIdAndArtifactId(pomFileUtils.getGroupId(), pomFileUtils.getArtifactId());
			if (log.isDebugEnabled())
			{
				log.debug("Processing artifact: " + pomFileUtils.getArtifactId());
			}

			for (Dependency dependency : pomFileUtils.getDependencies())
			{
				if (log.isDebugEnabled())
				{
					log.debug("Processing artifact dependency: " + dependency);
				}
				PomFile dependsUponPomFile =
					pomFileDao.findByGroupIdAndArtifactId(dependency.getGroupId(), dependency.getArtifactId());
				if (dependsUponPomFile != null)
				{
					PomDependency pomDependency =
						pomDependencyFactory.createDependency(pomFile, dependsUponPomFile, dependency.getType());
					pomDependencyDao.insertDependency(pomDependency);
				}
			}

		}

	}

	@Override
	public void setSourceTreePaths(List<String> sourceTreePaths)
	{
		this.sourceTreePaths = sourceTreePaths;
	}

	@Required
	public void setPomFileDao(PomFileDao pomFileDao)
	{
		if (pomFileDao == null) { throw new IllegalArgumentException("pomFileDao cannot be null"); }
		this.pomFileDao = pomFileDao;
	}

	@Required
	public void setDependencyDao(PomDependencyDao pomDependencyDao)
	{
		if (pomDependencyDao == null) { throw new IllegalArgumentException("pomDependencyDao cannot be null"); }
		this.pomDependencyDao = pomDependencyDao;
	}

	@Required
	public void setDependencyFactory(PomDependencyFactory pomDependencyFactory)
	{
		if (pomDependencyFactory == null) { throw new IllegalArgumentException(
			"pomDependencyFactory cannot be null"); }
		this.pomDependencyFactory = pomDependencyFactory;
	}

	@Required
	public void setPomFileFactory(PomFileFactory pomFileFactory)
	{
		if (pomFileFactory == null) { throw new IllegalArgumentException("pomFileFactory cannot be null"); }
		this.pomFileFactory = pomFileFactory;
	}

	@Required
	public void setPomFileUtilsFactory(PomFileUtilsFactory pomFileUtilsFactory)
	{
		if (pomFileUtilsFactory == null) { throw new IllegalArgumentException(
			"pomFileUtilsFactory cannot be null"); }
		this.pomFileUtilsFactory = pomFileUtilsFactory;
	}
}
