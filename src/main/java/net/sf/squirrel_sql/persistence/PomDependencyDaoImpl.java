package net.sf.squirrel_sql.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class PomDependencyDaoImpl implements PomDependencyDao
{
	private static final Logger log = LoggerFactory.getLogger(PomDependencyDaoImpl.class);
	
	private static final String CROSS_TREE_DEPENDENCY_QUERY = 
		"select " +
			"p1.TREEROOTDIR, " +
			"p2.TREEROOTDIR as dependuponrootdir " +
			"from DEPENDENCY d, POMFILE p1, POMFILE p2 " +
			"where d.POMFILEID = p1.ID " +
			"and d.DEPENDSUPONPOMFILEID = p2.ID " +
			"and p1.TREEROOTDIR != p2.TREEROOTDIR " +
			"group by p1.TREEROOTDIR, " +
			"p2.TREEROOTDIR ";
	
	private static final String FIND_DEPENDENCY_BY_TREE_ROOT_DIR = 
	"SELECT d " +
	"FROM PomDependencyImpl d " +
	"WHERE d.pomFile.treeRootDir = :treeRootDir ";
	
	@PersistenceContext
	private EntityManager em;
	
	private CrossSourceTreeDependencyFactory crossTreeDependencyFactory;
	
	/**
	 * @param crossTreeDependencyFactory the crossTreeDependencyFactory to set
	 */
	@Required
	public void setCrossTreeDependencyFactory(CrossSourceTreeDependencyFactory crossTreeDependencyFactory)
	{
		this.crossTreeDependencyFactory = crossTreeDependencyFactory;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependencyDao#insertDependency(net.sf.squirrel_sql.DependencyImpl)
	 */
	@Override
	public void insertDependency(PomDependency d) {
		if (log.isDebugEnabled()) {
			log.debug("Persisting pom dependency: "+d);
		}
		em.persist(d);
	}
	
	public List<CrossSourceTreeDependency> findCrossTreeDependencies() {
		if (log.isDebugEnabled()) {
			log.debug("Finding cross-tree dependencies");
		}
		List<CrossSourceTreeDependency> result = new ArrayList<CrossSourceTreeDependency>();
		Query query = em.createNativeQuery(CROSS_TREE_DEPENDENCY_QUERY);
		Iterator sourceTreeRoots = query.getResultList().iterator();
		while (sourceTreeRoots.hasNext()) {
			Object[] tuple = (Object[]) sourceTreeRoots.next();
			result.add(crossTreeDependencyFactory.create((String)tuple[0], (String)tuple[1]));
		}
		return result;
	}
	
	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependencyDao#findDependenciesBySourceTreePath(java.lang.String)
	 */
	@Override
	public List<PomDependency> findDependenciesBySourceTreePath(String treeRootDir) {
		if (log.isTraceEnabled())
		{
			log.trace("Searching for dependencies using treeRootDir=" + treeRootDir);
		}
		Query query = em.createQuery(FIND_DEPENDENCY_BY_TREE_ROOT_DIR);
		query.setParameter("treeRootDir", treeRootDir);
		List<PomDependency> result = null;
		try
		{
			result = query.getResultList();
		}
		catch (Exception e)
		{
			if (log.isTraceEnabled()) {
				log.error("Couldn't locate dependencies using treeRootDir=" + treeRootDir
					+ " : " + e.getMessage(), e);				
			}
			else if (log.isDebugEnabled())
			{
				log.error("Couldn't locate dependencies using treeRootDir=" + treeRootDir
					+ " : " + e.getMessage());
			}
		}
		return result;

	}
}
